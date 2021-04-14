package com.alaeri.log.sample

import kotlinx.coroutines.*

/**
 * Created by Emmanuel Requier on 15/04/2021.
 *
 * CoroutinesInternalError: Fatal exception in coroutines machinery for...
 * I think the issue is similar to the one described here.
 * https://youtrack.jetbrains.com/issue/KT-41563
 * https://github.com/Kotlin/kotlinx.coroutines/issues/2221
 *
 * I spent too much time trying to zoom in this issue.
 */
object SuspendInlineErrorRepro {

    private suspend inline fun suspendInlineFunction(crossinline body: suspend ()->Unit){
        supervisorScope {
            body.invoke()
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            suspendInlineFunction {
                withContext(Dispatchers.IO) {
                    println("Guess what happens next")
                }
            }
        }
    }
}