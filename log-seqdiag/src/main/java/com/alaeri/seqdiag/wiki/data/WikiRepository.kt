package com.alaeri.seqdiag.wiki.data

import com.alaeri.log.extra.tag.receiver.ReceiverTag
import com.alaeri.seqdiag.log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import kotlinx.serialization.decodeFromString

enum class LoadingStep{
    Fetching,
    Parsing,
    ConvertingToMarkdown
}
sealed class LoadingStatus{
    data class Loading(val input: String, val step: LoadingStep): LoadingStatus()
    data class Error(val input: String, val step: LoadingStep, val error: Exception): LoadingStatus()
    data class Success(val input: String, val markdown: String): LoadingStatus()
}
class WikiRepository {

    private val receiverTag =  ReceiverTag(this)
    val client = HttpClient(CIO)
    fun loadWikiArticle(searchTerm: String) : Flow<LoadingStatus> = flow{
        var currentStep = LoadingStep.Fetching
        try{
            emit(LoadingStatus.Loading(searchTerm, currentStep))
            val jsonString = log("fetchPage", receiverTag){
                fetchPage(searchTerm)
            }
            currentStep = LoadingStep.Parsing
            emit(LoadingStatus.Loading(searchTerm, currentStep))
            val markupString = log("parse", receiverTag){
                extractWikiTextFromJsonString(jsonString)
            }
            currentStep = LoadingStep.ConvertingToMarkdown
            emit(LoadingStatus.Loading(searchTerm, currentStep))
            val markdown = log("convert", receiverTag){
                convertToMarkdown(markupString)
            }
            emit(LoadingStatus.Success(searchTerm, markdown))
        }catch (e: Exception){
            emit(LoadingStatus.Error(searchTerm, currentStep, e))
        }

    }.log("loadWikiArticle", receiverTag)

    @Serializable
    data class WikiJson(val parse: Parse)
    @Serializable
    data class Parse(val wikitext: String)

    private suspend fun extractWikiTextFromJsonString(jsonString: String): String{
        val json = Json { ignoreUnknownKeys = true }
        val wikiJson = json.decodeFromString<WikiJson>(jsonString)
        delay(1000)
        return wikiJson.parse.wikitext
    }

    private suspend fun fetchPage(searchTerm: String): String {

        val url =
            "https://en.wikipedia.org/w/api.php?action=parse&page=$searchTerm&prop=wikitext&format=json&formatversion=2"

        val response: HttpResponse = client.request(url) {
            // Configure request parameters exposed by HttpRequestBuilder
        }
        val string = response.body<String>()
        delay(1000)
        return string


    }

    private suspend fun convertToMarkdown(mediaWikiDoc: String): String{
        val command = arrayOf(
            "pandoc", "-f", "mediawiki", "-t", "markdown_strict"

        )
        val pb = ProcessBuilder(*command)
        pb.redirectErrorStream(true)
        val p = pb.start()
        val reader = BufferedReader(InputStreamReader(p.inputStream))
        val writer = BufferedWriter(OutputStreamWriter(p.outputStream))

        writer.write(mediaWikiDoc)
        writer.newLine()
        writer.close()

        var line: String?


        var result = ""
        while (reader.readLine().also { line = it } != null) {
            result += "$line "
        }
        delay(1000)
        return result.trim()
    }
}