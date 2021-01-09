package com.alaeri.log.sample

import com.alaeri.log.core.LogConfig
import com.alaeri.log.core.child.ChildLogEnvironmentFactory
import com.alaeri.log.sample.lib.wiki.wiki.WikiRepositoryImpl
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

object Main {

    init {
        LogConfig.logEnvironmentFactory = ChildLogEnvironmentFactory
    }


    @JvmStatic
    fun main(args: Array<String>) {
        logBlocking("print args", args.joinToString(",")){}


        logBlocking("await Job") {
            runBlocking {
                SampleLogServer.start()
                println("type enter to continue: server should be up at http://localhost:8080/")
                readLine()
                withContext(Dispatchers.IO){
                    loadWikiArticleSuspend()
                }
                println("type enter to quit")
                readLine()
                SampleLogServer.quit()

            }
        }

    }

    private suspend fun loadWikiArticleSuspend() {
        this@Main.log("load wiki article", 1) {
            val wikiRepository = this@Main.log("initialize Wiki Repo") { WikiRepositoryImpl() }
            val flow = wikiRepository.loadWikiArticle("fun")
            this@Main.logCollect("collect wiki flow", flow) { it ->
                println(it)
            }
        }
    }
}