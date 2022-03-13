package com.alaeri.data

import com.alaeri.domain.ILogger
import com.alaeri.domain.wiki.*
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
import org.commonmark.Extension
import org.commonmark.node.*
import org.commonmark.parser.Parser
import org.commonmark.renderer.Renderer
import org.commonmark.renderer.text.TextContentRenderer
//import org.sweble.wikitext.parser.WikitextParser
//import org.sweble.wikitext.parser.WtEntityMap
//import org.sweble.wikitext.parser.nodes.*
//import org.sweble.wikitext.parser.preprocessor.PreprocessedWikitext
//import org.sweble.wikitext.parser.utils.SimpleParserConfig
import java.io.*
import java.lang.Appendable

/**
 * TODO find why IDE displays warnings about "wtParsedWikitextPage.propertyIterator()"
 * cannot access "at.de...AstNode..." check classpath for conflicting dependencies
 * The project builds even these warnings appear.
 */
class WikiRepositoryImpl(val logger: ILogger? = null) : WikiRepository {

    override fun loadWikiArticle(searchTerm: String?): Flow<LoadingStatus> = //flowCommand<LoadingStatus> {
        flow {
            supervisorScope {
                if (searchTerm != null) {
                    emit(LoadingStatus.Loading(searchTerm))
                    val responseBody = run {
                        val deferredString = async {
                            withContext(Dispatchers.IO) {
                                val url4 = "https://en.wikipedia.org/w/api.php?action=parse&page=$searchTerm&format=json&formatversion=2"
                                //val url3 = "https://en.wikipedia.org/w/api.php?action=parse&page=$searchTerm&prop=wikitext&format=xml"
                                val url = "https://en.wikipedia.org/w/api.php?action=parse&page=$searchTerm&prop=wikitext&format=json&formatversion=2"
                                //val url2 = "https://en.wikipedia.org/w/api.php?action=query&prop=revisions&titles=$searchTerm&rvslots=*&rvprop=content&formatversion=2&format=json"
                                Fuel.get(url)
                                    .awaitString()
                            }
                        }
                        deferredString.await()
                    }
                    emit(LoadingStatus.Parsing(responseBody.length.toLong()))


                    val apiWikiArticle = withContext(Dispatchers.Default) {
                        logger?.println(responseBody)
                        async {
                            Klaxon().parse<ApiWikiArticle>(JsonReader(StringReader(responseBody)))
                                ?: throw RuntimeException("pas de bol")
                        }.await()
                    }
                    println("##########response: $responseBody")

                    emit(LoadingStatus.Filtering("ok"))
                    val result = withContext(Dispatchers.Default) {

                        val markdown = convertToMarkdown("${apiWikiArticle.content}") //.replace("\n","\n\n")
                        return@withContext cleanMarkdown(markdown)
//                        cleanMarkdown(markdown)
//                        println("##########mardkdown: $markdown")
//                        val paragraphes =markdown
//                            .replace("""```.*?```""".toRegex(),"")
//                            .replace("""!\[.*?]\(.*?\)""".toRegex(),"")
//                            .replace("""\{.*?}""".toRegex(),"")
//                            .replace("#","\n")
//                            .replace(":","\n")
//                            .replace("\\n","\n")
////                            .split("\n")
////                            .filter{ it.isNotEmpty() }
////                            .map { line -> "$line\n\n"}
//                            .let{ listOf<String>(it) }
//                            .map{ line ->
//                               line.split("[").flatMap{ firstSplit ->
//                                   if(firstSplit.contains("]")){
//                                       val splitAtEndOfLabel = firstSplit.split("]")
//                                       println("#><#endOfLabel $splitAtEndOfLabel ")
//                                       val splitAfterLink = splitAtEndOfLabel[1].split(")")
//                                       println("#><#afterLink $splitAfterLink ")
//                                       val linkTarget = splitAfterLink[0].replace("(","")
//                                           .replace(""" "wikilink"""" ,"")
//                                       println("linkTargetk $linkTarget ")
//                                       val linkLabel = " ${splitAtEndOfLabel[0].trim()} "
//                                       if(splitAfterLink.size > 1){
//                                           listOf(WikiText.InternalLink(linkLabel, linkTarget), WikiText.NormalText(splitAfterLink[1]))
//                                       }else{
//                                           listOf(WikiText.InternalLink(linkLabel, linkTarget))
//                                       }
//                                   }else{
//                                       listOf(WikiText.NormalText(firstSplit))
//                                   }
//                                }.toMutableList()
//                        }
//                        paragraphes.forEach { println(it) }
//                        return@withContext WikiArticle(lines = paragraphes.toMutableList(), about = "", shortDescription = "")

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
        }.catch { it -> logger?.println(it); emit(LoadingStatus.Error("could not load data", it)) }

    private fun parseWikiTextLines() {
//        val parsed = WikitextParser(SimpleParserConfig()).parseArticle(
//            PreprocessedWikitext(
//                apiWikiArticle.content,
//                WtEntityMap.EMPTY_ENTITY_MAP
//            ),
//            "title"
//        )
//        val wtParsedWikitextPage: WtParsedWikitextPage =
//            parsed as WtParsedWikitextPage
//        wtParsedWikitextPage.propertyCount
//        val iterator = wtParsedWikitextPage.propertyIterator()
//        val mutableList = mutableListOf<String>()
//        while (iterator.next()) {
//            mutableList.add("${iterator.name} to ${iterator.value}")
//        }
//        val wikiArticle = wtParsedWikitextPage.fold(
//            WikiArticle(
//                null,
//                null,
//                mutableListOf(mutableListOf())
//            )
//        ) { acc, wtNode ->
//            when (wtNode) {
//                is WtText -> when {
//                    wtNode.content?.startsWith("{{About|") == true -> acc.copy(about = wtNode.content)
//                    wtNode.content?.startsWith("{{short description|") == true -> acc.copy(
//                        shortDescription = wtNode.content
//                    )
//                    wtNode.content?.trim()?.startsWith("{{") == true -> acc
//                    wtNode.content?.trim()?.startsWith("|") == true -> acc
//                    else -> {
//                        acc.lines.last().add(WikiText.NormalText(wtNode.content))
//                        acc
//                    }
//                }
//                is WtInternalLink -> {
//                    val wtPageName: WtPageName =
//                        wtNode.mapNotNull { node -> node as? WtPageName }.first()
//                    val wtText: WtText = wtPageName
//                        .mapNotNull { node -> node as? WtText }.first()
//                    acc.lines.last().add(
//                        WikiText.InternalLink(
//                            wtText.content,
//                            wtText.content,
//                        )
//                    )
//                    acc
//                }
//                is WtNewline -> {
//                    acc.lines.add(mutableListOf())
//                    acc
//                }
//                else -> acc
//            }
//
//        }
//        wikiArticle.copy(lines = wikiArticle.lines.filter { it.isNotEmpty() }
//            .toMutableList())
    }

    fun convertToMarkdown(mediaWikiDoc: String): String{
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
        return result.trim()
    }

    data class ParagraphBuilder(val segments: MutableList<WikiText> = mutableListOf())
    data class ArticleBuilder(val paragraphs: MutableList<ParagraphBuilder> = mutableListOf()){
        val lastParagraph  = paragraphs.last()

        fun build(): WikiArticle {
            val mutableList = paragraphs.map{
                it.segments.toMutableList()
            }.toMutableList()
            return WikiArticle("", "", mutableList)
        }
    }

    class Renderer {

        val currentBuilder = ArticleBuilder(mutableListOf(ParagraphBuilder()))

        fun renderMarkdown(node: Node){
//            println("node: $node")
            renderCurrent(node)
            var child = node.firstChild
            while(child != null){
                renderMarkdown(child)
                child = child.next
            }
        }


        fun renderCurrent(node: Node){
            when(node){
                is Heading -> currentBuilder.paragraphs.add(ParagraphBuilder())
                is Paragraph -> currentBuilder.paragraphs.add(ParagraphBuilder())
                is Link -> currentBuilder.lastParagraph.segments.add(WikiText.InternalLink(node.title ?: "" , node.destination ?: ""))

                is Text -> {
                    val parent = node.parent
                    if(parent is Link){
                        currentBuilder.lastParagraph.segments.removeLast()
                        currentBuilder.lastParagraph.segments.add(WikiText.InternalLink(node.literal ?: "" , parent.destination ?: ""))
                    }else{
                        currentBuilder.lastParagraph.segments.add(WikiText.NormalText(node.literal))
                    }
                }
            }
        }
    }


    private fun cleanMarkdown(markDownString: String): WikiArticle {
        val render = Renderer()
        val parser = Parser.builder().build();
        val document = parser.parse(markDownString)
        render.renderMarkdown(document)
        return render.currentBuilder.build()
    }
}