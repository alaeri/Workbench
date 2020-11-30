package com.alaeri.presentation.wiki

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow

class QueryRepository{
    private val mutableQuery = MutableStateFlow<String>("")
    fun updateQuery(newQuery: String){
        mutableQuery.value = newQuery
    }
    val queryFlow: SharedFlow<String> = mutableQuery
}