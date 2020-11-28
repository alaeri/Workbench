package com.alaeri.command.android.visualizer.option

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class VisualizationOptionsFragmentViewModel(private val filterCommandRepository: FilterCommandRepository): ViewModel(){
    fun onInjectionSwitched(isChecked: Boolean) {
        viewModelScope.launch {
            val currentOptions = filterCommandRepository.options.first()
            val updatedOptions = currentOptions.copy(showInjection = isChecked)
            filterCommandRepository.update(updatedOptions)

        }
    }

    fun onLifecycleSwitched(isChecked: Boolean) {
        viewModelScope.launch {
            val currentOptions = filterCommandRepository.options.first()
            val updatedOptions = currentOptions.copy(showLifecycle = isChecked)
            filterCommandRepository.update(updatedOptions)
        }
    }

    val currentOptionsLiveData: LiveData<VisualizationOptions> = filterCommandRepository.options.asLiveData()

}