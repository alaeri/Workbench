package com.alaeri.log.sample

import kotlinx.coroutines.*

/**
 * Created by Emmanuel Requier on 15/04/2021.
 */
object SuspendInlineErrorWorkAround {


    private suspend fun <T> suspendInlineFunction(body: suspend ()->T): T{
        return supervisorScope {
            body.invoke()
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            val res = suspendInlineFunction {
                withContext(Dispatchers.IO) {
                    println("Apres moi, le deluge")
                    "ggggg"
                }
            }
            println(res)
        }
    }
}