package com.alaeri.log.sample

import com.alaeri.log.sample.lib.wiki.wiki.WikiRepositoryImpl
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

object Main {


    @JvmStatic
    fun main(args: Array<String>) {
        logBlocking("print args", args.joinToString(",")){}
        val job = logBlocking<Job>("createJob", *args) {
            GlobalScope.launch {
                log("coroutine", 1) {
                    println("fun")
                    val wikiRepository = log("initialize Wiki Repo") { WikiRepositoryImpl() }
                    val flow = wikiRepository.loadWikiArticle("fun")
                    log("collect flow", flow) {
                        flow.collect { it ->
                            log("update flow", "flowupdate", it) {
                                println(it)
                            }
                        }
                    }
                }
            }
        }
        logBlocking("await Job", job) {
            runBlocking { job.join() }
        }
    }
}