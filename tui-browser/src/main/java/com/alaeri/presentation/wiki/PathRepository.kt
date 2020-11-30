package com.alaeri.presentation.wiki

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow

class PathRepository(){
    private val mutablePath = MutableStateFlow<String?>(null)
    suspend fun select(link: String?){
        mutablePath.value = link
    }
    val pathFlow: SharedFlow<String?> = mutablePath
}