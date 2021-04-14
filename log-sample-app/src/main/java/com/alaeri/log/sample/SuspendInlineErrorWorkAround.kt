package com.alaeri.log.sample

import kotlinx.coroutines.*

/**
 * Created by Emmanuel Requier on 15/04/2021.
 */
object SuspendInlineErrorWorkAround {


    private suspend fun suspendInlineFunction(body: suspend CoroutineScope.()->Unit){
        supervisorScope {
            body.invoke(this)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            suspendInlineFunction {
                withContext(Dispatchers.IO) {
                    println("Apres moi, le deluge")
                }
            }
        }
    }
}