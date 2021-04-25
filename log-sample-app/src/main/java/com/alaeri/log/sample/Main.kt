package com.alaeri.log.sample

import com.alaeri.log.core.LogConfig
import com.alaeri.log.core.child.ChildLogEnvironmentFactory
import com.alaeri.log.sample.lib.wiki.wiki.WikiRepositoryImpl
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

/**
 * Using logBlocking rather than log as
 * This code may throw an exception
 * @see SuspendInlineErrorRepro
 *
 */
object Main {
    const val defaultSearchTerm = "Coroutine"

    init {
        LogConfig.logEnvironmentFactory = ChildLogEnvironmentFactory
    }

    @JvmStatic
    fun main(args: Array<String>) {
        logBlocking("Application") {
            logBlocking("printArgs"){
                args.forEach{
                    println(it)
                }
            }
            runBlocking {
                SampleLogServer.start()
                println("Server should be up at http://localhost:8080/")
                print("Enter a search term to continue [$defaultSearchTerm]:")
                val searchTerm = readLine() ?: defaultSearchTerm
                withContext(Dispatchers.IO){
                    loadWikiArticleSuspend(searchTerm)
                }
                println("type enter to quit")
                readLine()
                SampleLogServer.quit()
                delay(1000)
            }
        }

    }

    private suspend fun loadWikiArticleSuspend(searchTerm: String) {
        this@Main.log("load wiki article", 1) {
            val wikiRepository = this@Main.log("initialize Wiki Repo") { WikiRepositoryImpl() }
            wikiRepository.loadWikiArticle(searchTerm).collect {
                    it -> println(it)
            }
        }
    }

}