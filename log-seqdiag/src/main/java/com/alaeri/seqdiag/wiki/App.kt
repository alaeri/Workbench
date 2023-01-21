package com.alaeri.seqdiag.wiki

import com.alaeri.log.core.LogConfig
import com.alaeri.log.core.child.ChildLogEnvironmentFactory
import kotlinx.coroutines.CoroutineScope

object App{
    init {
        LogConfig.logEnvironmentFactory = ChildLogEnvironmentFactory
    }

    lateinit var scope : CoroutineScope
    lateinit var vm: WikiViewModel
}