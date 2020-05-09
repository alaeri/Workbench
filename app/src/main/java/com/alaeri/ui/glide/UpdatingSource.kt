package com.alaeri.ui.glide

import okhttp3.HttpUrl
import okio.Buffer
import okio.ForwardingSource
import okio.Source

class UpdatingSource(source: Source, private val fullLength: Long,
                             private val url : HttpUrl,
                             private val progressListener: ProgressListener
): ForwardingSource(source) {
    var totalBytesRead = 0L
    override fun read(sink: Buffer, byteCount: Long): Long {
        val bytesRead = super.read(sink, byteCount)
        if (bytesRead == -1L) { // this source is exhausted
            totalBytesRead = fullLength;
        } else {
            totalBytesRead += bytesRead;
        }
        progressListener.onProgress(url, totalBytesRead, fullLength);
        return bytesRead
    }
}