package com.alaeri.ui.glide

import android.content.Context
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.LibraryGlideModule
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import org.koin.core.KoinComponent
import java.io.InputStream

//@GlideModule
//class ProgressGlideModule : LibraryGlideModule(), KoinComponent {
//
//    //private val listener: ProgressListener = getKoin().get()
//
//
//}