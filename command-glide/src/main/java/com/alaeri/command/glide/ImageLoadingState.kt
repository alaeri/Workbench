package com.alaeri.command.glide

import android.graphics.drawable.Drawable

sealed class ImageLoadingState{
    object AwaitingLoad : ImageLoadingState()
    data class Loading(val readCount: Long, val totalReadCount: Long): ImageLoadingState()
    data class Completed(val bitmap: Drawable) : ImageLoadingState()
    data class Failed(val exception: Exception) : ImageLoadingState()
}