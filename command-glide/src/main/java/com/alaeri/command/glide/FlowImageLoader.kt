package com.alaeri.command.glide

import android.content.Context
import com.alaeri.command.core.command
import com.alaeri.command.core.flow.FlowCommand
import com.alaeri.command.core.flow.flowCommand
import com.bumptech.glide.Glide
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
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

    private val flowContextScope = CoroutineScope(flowContext)

    data class ChannelAndObserverCount(val channel: MutableStateFlow<ImageLoadingState>, val observerCount : Int)

    private val hashMap = hashMapOf<HttpUrl, ChannelAndObserverCount>()

    fun loadImage(url: String, size: Size): FlowCommand<ImageLoadingState> = flowCommand {

        //withContext(flowContext){
        val httpUrl = HttpUrl.parse(url)!!
        val channel = hashMap.getOrPut(httpUrl) {
            ChannelAndObserverCount(MutableStateFlow(ImageLoadingState.AwaitingLoad), 1)
        }.channel
        val request = Glide.with(applicationContext).load(url).submit(size.width, size.height)


//                val j = launch {
//                    internalAdapter
//                }
        flowContextScope.launch {
            try {
                val drawable = withContext(Dispatchers.IO) {
                    request.get()
                }
                channel.value = ImageLoadingState.Completed(drawable)
            } catch (e: Exception) {
                if (!request.isCancelled || request.isDone) {
                    request.cancel(true)
                }
                channel.value = ImageLoadingState.Failed(e)
            } finally {
                val channelAndObserverCount = hashMap[httpUrl]
                if (channelAndObserverCount != null) {
                    val count = channelAndObserverCount.observerCount
                    if (count == 1) {
                        hashMap.remove(httpUrl)
                    } else {
                        hashMap[httpUrl] = channelAndObserverCount.copy(observerCount = count - 1)
                    }
                }
            }
        }
        channel
    }

    override fun onProgress(url: HttpUrl, bytesRead: Long, expectedLength: Long) {
        flowContextScope.launch {
            hashMap[url]?.channel?.value = (ImageLoadingState.Loading(bytesRead, expectedLength))
        }
    }


}
