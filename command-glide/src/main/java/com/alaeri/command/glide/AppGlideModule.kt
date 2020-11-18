package com.alaeri.command.glide

import android.content.Context
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import org.koin.core.KoinComponent
import java.io.InputStream

/**
 * Created by Emmanuel Requier on 25/04/2020.
 */
@GlideModule
class AppGlideModule : AppGlideModule(), KoinComponent{

    override fun registerComponents(context : Context, glide: Glide, registry: Registry) {
        val flowImageLoader : FlowImageLoader = getKoin().get()
        val client = OkHttpClient.Builder()
            .addNetworkInterceptor{chain ->
                val request = chain.request()
                val response = chain.proceed(request)
                val body = OkHttpProgressResponseBody(
                    flowImageLoader,
                    request.url(),
                    response.body()!!
                )
                response.newBuilder().body(body).build()
            }
            .build()
        registry.replace(
            GlideUrl::class.java, InputStream::class.java,
            OkHttpUrlLoader.Factory(client)
        );
        super.registerComponents(context, glide, registry);
    }

    private val debugProgressListener = object :ProgressListener {
        override fun onProgress(
            url: HttpUrl,
            bytesRead: Long,
            expectedLength: Long
        ) {
            Log.d("CATS", "$bytesRead/$expectedLength - ${url.uri().toString()}")
        }

    }

}
