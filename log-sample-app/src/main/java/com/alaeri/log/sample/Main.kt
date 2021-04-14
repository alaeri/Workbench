package com.alaeri.log.sample

import com.alaeri.log.core.LogConfig
import com.alaeri.log.core.child.ChildLogEnvironmentFactory
import com.alaeri.log.sample.lib.wiki.wiki.WikiRepositoryImpl
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.util.concurrent.Executors

/**
 * Using logBlocking rather than log as
 * This code may throw an exception
 * @see SuspendInlineErrorRepro
 *
 */
object Main {

    init {
        LogConfig.logEnvironmentFactory = ChildLogEnvironmentFactory
    }

    @JvmStatic
    fun main(args: Array<String>) {

        logBlocking("await Job") {
            runBlocking {
                val job = SampleLogServer.start()
                println("type enter to continue: server should be up at http://localhost:8080/")
                readLine()
                withContext(Dispatchers.IO){
                    loadWikiArticleSuspend()
                }
                println("type enter to quit")
                readLine()
                SampleLogServer.quit()
                println("grace period for server")
                delay(1000)

            }
        }

    }

    private suspend fun loadWikiArticleSuspend() {
        this@Main.logBlocking("load wiki article", 1) {
            val wikiRepository = this@Main.logBlocking("initialize Wiki Repo") { WikiRepositoryImpl() }
            //val flow =
            wikiRepository.loadWikiArticle("fun").collect {
                    it -> println(it)
            }
            println("done")
//            this@Main.logCollect("collect wiki flow", flow) { it ->
//                println(it)
//            }
        }
    }

}