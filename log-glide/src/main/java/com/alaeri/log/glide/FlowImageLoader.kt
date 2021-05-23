package com.alaeri.log.glide

import android.content.Context
import com.bumptech.glide.Glide
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.HttpUrl
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

/**
 * Created by Emmanuel Requier on 23/04/2020.
 * This class is a hack to show the potential of the command design pattern with a visual example.
 * Ideally we should use an image loading library that implements natively our command design pattern.
 *
 */
class FlowImageLoader(private val applicationContext: Context,
                      private val flowContext: CoroutineContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher()):
    ProgressListener {

    val bgScope = CoroutineScope(flowContext)

    data class ChannelAndObserverCount(val channel: MutableStateFlow<ImageLoadingState>, val observerCount : Int)

    private val hashMap = hashMapOf<HttpUrl, ChannelAndObserverCount>()

    fun loadImage(url: String, size: Size): Flow<ImageLoadingState> = logBlockingFlow<ImageLoadingState>("loadImage") {

        flow<Flow<ImageLoadingState>>{

            val originalContext = currentCoroutineContext()

            val httpUrl = HttpUrl.parse(url)!!
            val channel = hashMap.getOrPut(httpUrl) {
                log("createNewChannel"){
                    ChannelAndObserverCount(MutableStateFlow(ImageLoadingState.AwaitingLoad), 1)
                }
            }.channel
            val request = Glide.with(applicationContext).load(url).submit(size.width, size.height)
            val flowContextScope = CoroutineScope(originalContext + flowContext)
            flowContextScope.launch {
                try {
                    log("loading image"){
                        val drawable = withContext(Dispatchers.IO) {
                            request.get()
                        }
                        channel.value = ImageLoadingState.Completed(drawable)
                    }
                } catch (e: Exception) {
                    log("processing exception during imageLoad"){
                        if (!request.isCancelled || request.isDone) {
                            request.cancel(true)
                        }
                        channel.value = ImageLoadingState.Failed(e)
                    }
                } finally {
                    val channelAndObserverCount = hashMap[httpUrl]
                    if (channelAndObserverCount != null) {
                        val count = channelAndObserverCount.observerCount
                        if (count == 1) {
                            log("clearChannel as it has 0 observers"){
                                hashMap.remove(httpUrl)
                            }
                        } else {
                            log("remove observer from channel"){
                                hashMap[httpUrl] = channelAndObserverCount.copy(observerCount = count - 1)
                            }
                        }
                    }
                }
            }
            emit(channel)
        }.flatMapLatest { logBlockingFlow("imageLoadingChannel"){ it }.flowOn(Dispatchers.IO) }
    }

    override fun onProgress(url: HttpUrl, bytesRead: Long, expectedLength: Long) {
        bgScope.launch {
            hashMap[url]?.channel?.value = (ImageLoadingState.Loading(bytesRead, expectedLength))
        }
    }


}
