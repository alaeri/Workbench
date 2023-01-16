package com.alaeri.log.sample.lib.wiki.wiki


import com.alaeri.domain.wiki.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext

/**
 * TODO find why IDE displays warnings about "wtParsedWikitextPage.propertyIterator()"
 * cannot access "at.de...AstNode..." check classpath for conflicting dependencies
 * The project builds even these warnings appear.
 */
class WikiRepositoryImpl : WikiRepository {
//    init {
//        LogConfig.logEnvironmentFactory = ChildLogEnvironmentFactory
//    }

    val instance = this
    override suspend fun loadWikiArticle(searchTerm: String?): Flow<LoadingStatus> = flow{//logFlow<LoadingStatus>("load article", searchTerm) {
        supervisorScope {
            try {

                if (searchTerm != null) {
                    emit(LoadingStatus.Loading(searchTerm))
                    val result1 = //run {
                        supervisorScope {
                            val result001 = kotlin.runCatching {
                                supervisorScope {
//                                    Fuel.get("https://en.wikipedia.org/w/api.php?action=query&prop=revisions&titles=$searchTerm&rvslots=*&rvprop=content&formatversion=2&format=json")
//                                        .awaitString(scope = SupervisorJob())
                                    "......."

                                }
                            }
                            result001
                    }
                    val responseBody = result1.getOrThrow()
                    emit(LoadingStatus.Parsing(responseBody.length.toLong()))

                    val apiWikiArticle = withContext(Dispatchers.Default) {

                        async {
                          ApiWikiArticle("")
                        }.await()
                    }
                    emit(LoadingStatus.Filtering("ok"))
                    val result = withContext(Dispatchers.Default) {
                       val wikiArticle = WikiArticle(null, null, mutableListOf(mutableListOf(WikiText.NormalText("$searchTerm dsfsdf"))))

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
            }catch (e: Exception) {
                println("error: $e")
                emit(LoadingStatus.Error("could not load data", e))
            }
        }
    }
}