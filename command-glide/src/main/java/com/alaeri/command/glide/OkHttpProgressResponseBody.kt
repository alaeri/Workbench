package com.alaeri.command.glide

import android.util.Log
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.BufferedSource
import okio.Okio

class OkHttpProgressResponseBody(
    progressListener: ProgressListener,
    url: HttpUrl, private val responseBody: ResponseBody
) : ResponseBody() {

    init{
        Log.d("CATS", "url: $url")
    }

    override fun contentLength(): Long = responseBody.contentLength()

    override fun contentType(): MediaType? = responseBody.contentType()

    override fun source(): BufferedSource = bufferedSource

    private val bufferedSource: BufferedSource =
        Okio.buffer(
            UpdatingSource(
                responseBody.source(),
                responseBody.contentLength(),
                url,
                progressListener
            )
        )
}