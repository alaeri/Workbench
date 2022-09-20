package com.alaeri

import kotlinx.coroutines.*
import com.alaeri.log.sample.lib.wiki.wiki.WikiRepositoryImpl
import kotlinx.coroutines.flow.collectLatest


fun main() = runBlocking{
    println("type anything and it will quit later")
    val input = readln()
    val wikiRepositoryImpl = WikiRepositoryImpl()
    wikiRepositoryImpl.loadWikiArticle(input ?: "").collect {
        println(it)
    }
    println("input: $input")
    delay(1000)
    println("quit")
}