package com.alaeri.presentation.wiki

import com.alaeri.log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow

class PathRepository(){
    private val mutablePath = MutableStateFlow<String?>(null)
    suspend fun select(link: String?) = log(name = "set path"){
        mutablePath.value = link
    }
    val pathFlow: SharedFlow<String?> = mutablePath

}