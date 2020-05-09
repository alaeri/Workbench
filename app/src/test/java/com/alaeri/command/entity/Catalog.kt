package com.alaeri.command.entity

import com.alaeri.command.core.suspend.SuspendingCommand
import com.alaeri.command.core.suspendInvokeAndFold
import com.alaeri.command.core.command
import com.alaeri.command.core.suspend.suspendingCommand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Catalog {
    val images = listOf(Image())

    suspend fun downloadAll() : SuspendingCommand<Unit> = suspendingCommand<Unit> {
        images.forEach { suspendInvokeAndFold { download(it) } }
    }

    suspend fun download(image: Image) = suspendingCommand<Unit> {
        val data = suspendInvokeAndFold {
            val suspendingCommand: SuspendingCommand<ImgData> = image.downloadData()
            suspendingCommand
        }
        Unit
    }

    fun refresh() = command<Unit> {
        CoroutineScope(Dispatchers.IO)
            .launch {
            images.forEach {
                suspendInvokeAndFold {
                    download(it)
                }
            }
        }
    }

    fun count() = command<Int> {
        images.size
    }
}