package com.alaeri.presentation.wiki

import com.alaeri.log
import com.alaeri.logBlockingFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class PathRepository(){
    private val mutablePath = MutableStateFlow<String?>(null)
    suspend fun select(link: String?) = log(name = "set path"){
        mutablePath.value = link
    }
    val pathFlow: Flow<String?> = logBlockingFlow("pathFlow"){ mutablePath }

}