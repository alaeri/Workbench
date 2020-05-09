package com.alaeri.ui.glide

import okhttp3.HttpUrl

interface ProgressListener {
    fun onProgress(url: HttpUrl, bytesRead: Long, expectedLength: Long)
}