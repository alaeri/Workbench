package com.alaeri.log.android.ui.filter

import com.alaeri.log.extra.identity.IdentityRepresentation
import com.alaeri.log.repository.LogRepository
import com.alaeri.log.serialize.serialize.SerializedLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

/**
 * Created by Emmanuel Requier on 18/05/2021.
 */
class FilterLogRepository(private val logRepository: LogRepository){

    private val mutableOptions = MutableStateFlow<FilterOptions?>(null)

    val options : Flow<FilterOptions?> = mutableOptions
    fun update(options: FilterOptions){
        mutableOptions.value = options
    }

    val filteredList: Flow<List<SerializedLog<IdentityRepresentation>>> =
        combine(logRepository.listAsFlow, mutableOptions) {
            list, options -> list
        }

}
