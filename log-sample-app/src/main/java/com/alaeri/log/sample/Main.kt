package com.alaeri.log.sample

import com.alaeri.domain.wiki.LoadingStatus
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

    private const val defaultSearchTerm = "Coroutine"

    init {
        LogConfig.logEnvironmentFactory = ChildLogEnvironmentFactory
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        log("Application") {
            log("printArgs"){
                val argsToPrint = "[${args.joinToString(",")}]"
                println("Sample launched with args: $argsToPrint")
            }
            launch {
                log("startServer"){
                    withContext(Dispatchers.IO){
                        SampleLogServer.start()
                    }
                }
                println("Server should be up at http://localhost:8080/")
            }
            print("Enter a search term to continue [$defaultSearchTerm]:")
            val input = log("readLine"){ readLine() }
            val searchTerm = if(input.isNullOrEmpty()){
                defaultSearchTerm
            }else{
                input
            }
            val wikiRepository = log("initialize Wiki Repo") { WikiRepositoryImpl() }
            withContext(Dispatchers.IO){
                log("load wiki article", 1) {
                    wikiRepository.loadWikiArticle(searchTerm).log("wiki article flow").collect {
                        if(it !is LoadingStatus.Done){
                            println(it)
                        } else{
                            it.result.lines.forEach { l ->
                                log("printline"){
                                    val lineElements = l.map { el -> el.text }
                                    val line = lineElements.joinToString(" ")
                                    println(line)
                                }
                            }
                        }
                    }
                }
            }
            println("type enter to quit")
            readLine()
            SampleLogServer.quit()
            delay(1000)
        }
    }
}