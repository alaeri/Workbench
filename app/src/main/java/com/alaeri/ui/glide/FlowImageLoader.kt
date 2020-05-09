package com.alaeri.ui.glide

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.Size
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.HttpUrl
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * Created by Emmanuel Requier on 23/04/2020.
 */
class FlowImageLoader(private val applicationContext: Context, private val flowContext: CoroutineContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher()):
    ProgressListener {

    private val flowContextScope = CoroutineScope(flowContext)

    sealed class ImageLoadingState{
        object AwaitingLoad : ImageLoadingState()
        data class Loading(val readCount: Long, val totalReadCount: Long): ImageLoadingState()
        data class Completed(val bitmap: Drawable) : ImageLoadingState()
        data class Failed(val exception: Exception) : ImageLoadingState()
    }
    class ChannelTarget(private val channel: ConflatedBroadcastChannel<ImageLoadingState>, view: ImageView): CustomViewTarget<ImageView, Drawable>(view){
        override fun onLoadFailed(errorDrawable: Drawable?) {
            if(!channel.isClosedForSend){
                channel.offer(ImageLoadingState.Failed(Exception("load failed")))
            }
        }

        override fun onResourceCleared(placeholder: Drawable?) {
            //channel.offer(ImageLoadingState.Failed(Exception("resource cleared")))
        }

        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
            if(!channel.isClosedForSend){
                channel.offer(ImageLoadingState.Completed(resource))
            }
        }
    }

    data class ChannelAndObserverCount(val channel: ConflatedBroadcastChannel<ImageLoadingState>, val observerCount : Int)

    private val hashMap = hashMapOf<HttpUrl, ChannelAndObserverCount>()

    data class Size(val width: Int, val height: Int)
    fun loadImage(url: String, size: Size): Flow<ImageLoadingState> = flow{
        val emissionContext = coroutineContext

        withContext(flowContext){
            val httpUrl = HttpUrl.parse(url)!!
            val channel = hashMap.getOrPut(httpUrl) {
                ChannelAndObserverCount(ConflatedBroadcastChannel(ImageLoadingState.AwaitingLoad), 1)
            }.channel
            val request = Glide.with(applicationContext).load(url).submit(size.width, size.height)
            try{
                val j = launch {
                    for(loading in channel.openSubscription()){
                        withContext(emissionContext){ emit(loading) }
                    }
                }
//                val j = launch {
//                    internalAdapter
//                }
                val drawable = withContext(Dispatchers.IO){
                    request.get()
                }
                j.cancel()
                withContext(emissionContext){
                    emit(ImageLoadingState.Completed(drawable))
                }
            }catch (e: Exception){
                if(!request.isCancelled || request.isDone){
                    request.cancel(true)
                }
                withContext(emissionContext){ emit(ImageLoadingState.Failed(exception = e)) }
            }finally {
                val channelAndObserverCount = hashMap[httpUrl]
                if(channelAndObserverCount != null) {
                    val count = channelAndObserverCount.observerCount
                    channelAndObserverCount.channel.close()
                    if (count == 1) {
                        hashMap.remove(httpUrl)
                    } else {
                        hashMap[httpUrl] = channelAndObserverCount.copy(observerCount = count - 1)
                    }
                }
            }
        }
    }

    override fun onProgress(url: HttpUrl, bytesRead: Long, expectedLength: Long) {
        flowContextScope.launch {
            hashMap[url]?.channel?.offer(ImageLoadingState.Loading(bytesRead, expectedLength))
        }
    }


}
