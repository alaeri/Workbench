package com.alaeri.log.android.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.alaeri.log.repository.LogRepository

/**
 * Created by Emmanuel Requier on 18/05/2021.
 */
class LogListViewModel(private val logRepository: LogRepository): ViewModel() {

    val logListLiveData = logRepository.listAsFlow.asLiveData()

}