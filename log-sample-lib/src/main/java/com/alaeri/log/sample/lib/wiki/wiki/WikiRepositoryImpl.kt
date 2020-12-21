package com.alaeri.log.sample.lib.wiki.wiki


import com.alaeri.domain.wiki.*
import com.alaeri.log.sample.lib.logLib
import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import org.sweble.wikitext.parser.WikitextParser
import org.sweble.wikitext.parser.WtEntityMap
import org.sweble.wikitext.parser.nodes.*
import org.sweble.wikitext.parser.preprocessor.PreprocessedWikitext
import org.sweble.wikitext.parser.utils.SimpleParserConfig
import java.io.StringReader

/**
 * TODO find why IDE displays warnings about "wtParsedWikitextPage.propertyIterator()"
 * cannot access "at.de...AstNode..." check classpath for conflicting dependencies
 * The project builds even these warnings appear.
 */
class WikiRepositoryImpl : WikiRepository {

    override suspend fun loadWikiArticle(searchTerm: String?): Flow<LoadingStatus> = logLib("load article") {
        flow {
            supervisorScope {
                if (searchTerm != null) {
                    emit(LoadingStatus.Loading(searchTerm))
                    val responseBody = run {
                        val deferredString = async {
                            withContext(Dispatchers.IO) {
                                Fuel.get("https://en.wikipedia.org/w/api.php?action=query&prop=revisions&titles=$searchTerm&rvslots=*&rvprop=content&formatversion=2&format=json")
                                    .awaitString()
                            }
                        }
                        deferredString.await()
                    }
                    emit(LoadingStatus.Parsing(responseBody.length.toLong()))

                    val apiWikiArticle = withContext(Dispatchers.Default) {
                        logLib("httpResponse:","http") { responseBody }
                        async {
                            Klaxon().parse<ApiWikiArticle>(JsonReader(StringReader(responseBody)))
                                ?: throw RuntimeException("pas de bol")
                        }.await()
                    }
                    emit(LoadingStatus.Filtering("ok"))
                    val result = withContext(Dispatchers.Default) {
                        val parsed = WikitextParser(SimpleParserConfig()).parseArticle(
                            PreprocessedWikitext(
                                apiWikiArticle.content,
                                WtEntityMap.EMPTY_ENTITY_MAP
                            ),
                            "title"
                        )
                        val wtParsedWikitextPage: WtParsedWikitextPage =
                            parsed as WtParsedWikitextPage
                        wtParsedWikitextPage.propertyCount
                        val iterator = wtParsedWikitextPage.propertyIterator()
                        val mutableList = mutableListOf<String>()
                        while (iterator.next()) {
                            mutableList.add("${iterator.name} to ${iterator.value}")
                        }
                        val wikiArticle = wtParsedWikitextPage.fold(
                            WikiArticle(
                                null,
                                null,
                                mutableListOf(mutableListOf())
                            )
                        ) { acc, wtNode ->
                            when (wtNode) {
                                is WtText -> when {
                                    wtNode.content?.startsWith("{{About|") == true -> acc.copy(about = wtNode.content)
                                    wtNode.content?.startsWith("{{short description|") == true -> acc.copy(
                                        shortDescription = wtNode.content
                                    )
                                    wtNode.content?.trim()?.startsWith("{{") == true -> acc
                                    wtNode.content?.trim()?.startsWith("|") == true -> acc
                                    else -> {
                                        acc.lines.last().add(WikiText.NormalText(wtNode.content))
                                        acc
                                    }
                                }
                                is WtInternalLink -> {
                                    val wtPageName: WtPageName =
                                        wtNode.mapNotNull { node -> node as? WtPageName }.first()
                                    val wtText: WtText = wtPageName
                                        .mapNotNull { node -> node as? WtText }.first()
                                    acc.lines.last().add(
                                        WikiText.InternalLink(
                                            wtText.content,
                                            wtText.content,
                                        )
                                    )
                                    acc
                                }
                                is WtNewline -> {
                                    acc.lines.add(mutableListOf())
                                    acc
                                }
                                else -> acc
                            }

                        }
                        wikiArticle.copy(lines = wikiArticle.lines.filter { it.isNotEmpty() }
                            .toMutableList())

                    }
                    emit(LoadingStatus.Done(result))
                } else {
                    emit(
                        LoadingStatus.Done(
                            WikiArticle(
                                "empty search page",
                                "this should never be visible",
                                mutableListOf()
                            )
                        )
                    )
                }
            }
        }.catch { it ->
            logLib("error wiki") { it }; emit(
            LoadingStatus.Error(
                "could not load data",
                it
            )
        )
        }
    }

}