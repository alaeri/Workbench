package com.alaeri.log.android.ui

import com.alaeri.log.android.ui.focus.FocusLogRepository
import com.alaeri.log.android.ui.focus.LogFocusViewModel
import com.alaeri.log.android.ui.list.LogAdapter
import com.alaeri.log.android.ui.list.LogListFragment
import com.alaeri.log.android.ui.list.LogListViewModel
import com.alaeri.log.repository.LogRepository
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

object LogAndroidUiModule{

    fun logAndroidUiModule(logRepository: LogRepository) = module {

        single<LogRepository> { logRepository }
        single<FocusLogRepository> { FocusLogRepository(get()) }
        factory { LogAdapter() }

        viewModel<LogFocusViewModel>{
            LogFocusViewModel(get())
        }
    }
}