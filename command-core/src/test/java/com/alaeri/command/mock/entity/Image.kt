package com.alaeri.command.mock.entity

import com.alaeri.command.core.suspend.suspendingCommand
import kotlinx.coroutines.delay

class Image{

    suspend fun downloadData() = suspendingCommand<ImgData>{
        delay(100)
        ImgData("bla")
    }
}