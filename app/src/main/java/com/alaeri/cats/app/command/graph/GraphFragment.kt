package com.alaeri.cats.app.command.graph

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.alaeri.cats.app.command.CommandRepository
import com.alaeri.cats.app.databinding.GraphFragmentBinding
import com.alaeri.command.history.*
import com.alaeri.command.history.id.IndexAndUUID
import com.alaeri.command.history.serialization.SerializableCommandStateAndContext
import com.alaeri.command.history.serialization.SerializableInvokationContext
import com.alaeri.command.history.serialization.SerializedClass
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.koin.core.KoinComponent
import retrofit2.converter.moshi.MoshiConverterFactory



/**
 * Created by Emmanuel Requier on 09/05/2020.
 */
val serializedUnit = Unit.toSerializedClass()
fun <Key> SerializableInvokationContext<Key>.toElement() = Element(id, serializedClass)
fun <Key> IdOwner<Key>.toElement() : Element<Key>? = if(clazz != serializedUnit) { id?.let { id -> clazz?.let { Element(id, it) } } } else null
fun Element<IndexAndUUID>.toStr() = "$id $clazz"
data class Element<Key>(val id: Key, val clazz: SerializedClass)
data class ElementConnection<Key>(val child: Element<Key>, val parent: Element<Key>)
data class ElementAndParents<Key>(val element: Element<Key>, val parents: List<Element<Key>>)

data class IdAndParents(val id: String, val parents: List<String>)
data class Levels(val levels: List<List<IdAndParents>>)

class GraphFragment: Fragment(), KoinComponent {

    private lateinit var binding: GraphFragmentBinding

    val commandRepository : CommandRepository by lazy { getKoin().get<CommandRepository>() }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        GraphFragmentBinding.inflate(inflater).apply {
            binding = this
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true)
            }
            webView.apply {
                settings.javaScriptEnabled = true
                webChromeClient = WebChromeClient()
                webViewClient =  object: WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        loadPieChart()
                    }
                }
                loadUrl("file:///android_asset/" + "d3graph.html")
            }
        }
        return binding.root
    }

    fun loadPieChart() {
        //TODO use proper json serializing

        val filteredList = commandRepository.list//.filterNot{ it.context.executionContext.id == it.context.invokationContext.id }
        Log.d("CATS","$filteredList")
        val connections = filteredList.flatMap {
            val childContextElement = it.context.executionContext.toElement()
            val parentContextElement = it.context.invokationContext.toElement()
            val contextConnection = ElementConnection(
                child = childContextElement,
                parent = parentContextElement)
            val idOwnerState = it.state as? IdOwner<IndexAndUUID>
            val resultElement = idOwnerState?.toElement() as? Element<IndexAndUUID>
            val stateConnections = resultElement?.let {
                listOf(
                    ElementConnection<IndexAndUUID>(
                        child = it,
                        parent = childContextElement
                    )
                )
//                if (childContextElement == parentContextElement) {
//
//                } else {
//                    listOf(
//                        ElementConnection<IndexAndUUID>(
//                            child = it,
//                            parent = parentContextElement
//                        ),
//                        ElementConnection<IndexAndUUID>(
//                            child = childContextElement,
//                            parent = it
//                        )
//                    )
//                }
            } ?: emptyList<ElementConnection<IndexAndUUID>>()
            return@flatMap stateConnections + contextConnection
        }
        val distinctElements = connections.flatMap {
            listOf(it.child, it.parent)
        }.distinct()
        val levels = mutableListOf<List<ElementAndParents<IndexAndUUID>>>()
        val cleanedConnections = connections.filter { it.child != it.parent }
        val connectionsGroupedByChild = cleanedConnections.groupBy { it.child }
        val connectionsGroupedByParent =  cleanedConnections.groupBy { it.parent }
        val previousLevelElements = listOf<Element<IndexAndUUID>>()
        var remainingElements = distinctElements
        while (remainingElements.size > 0){
            val nextElements = remainingElements.filter {
                val parents = connectionsGroupedByChild.get(it)?.map { it.parent }
                Log.d("CATS","it: $it parents: ${parents}")
                parents?.none { it in  remainingElements } ?: true
            }.map {
                ElementAndParents(it, connectionsGroupedByChild[it]?.map { it.parent }?: listOf())
            }
            if(nextElements.isEmpty()){
                Log.d("CATS", "chain is broken...")
                break
            }
            levels += nextElements
            remainingElements = remainingElements.filterNot { it in nextElements.map { it.element } }
            Log.d("CATS","next: ${nextElements.size} remaining: ${remainingElements.size} depth: ${levels.size}")
        }
        val levelsToJson = Levels(levels.map { level ->
            level.map { elementAndParents ->
                IdAndParents(
                    elementAndParents.element.toStr(),
                    elementAndParents.parents.map { it.toStr() })
            }
        })





        val converterFactory = MoshiConverterFactory.create()
        val moshi = Moshi.Builder().build();
        val jsonAdapter: JsonAdapter<Levels> = moshi.adapter(Levels::class.java)
        //val oldText = createJsonManually(filteredList)
        val text = jsonAdapter.toJson(levelsToJson)
        Log.d("CATS","json text: $text")
//        val levelType = (
//            MutableList::class.java,
//            MyData::class.java
//        )
//        val adapter: JsonAdapter<List<MyData>> =
//            moshi.adapter<Any>(listMyData)
        //val json = jsonAdapter.fromJson(text)

        binding.apply {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                webView.evaluateJavascript("loadPieChart('$text');", null);
            } else {
                webView.loadUrl("javascript:loadPieChart('$text');");
            }
        }
    }

    private fun createJsonManually(
        filteredList: List<SerializableCommandStateAndContext<IndexAndUUID>>
    ): String {
        val allContexts = filteredList.flatMap {
            listOf(
                it.context.invokationContext,
                it.context.executionContext
            )
        }
        val dedupedContexts = allContexts.distinctBy { it.id }
        Log.d("CATS", "deduped ids: ${dedupedContexts.size}")
        val level0Contexts = dedupedContexts.filter { dedupedContext ->
            val id = dedupedContext.id
            filteredList.none { it.context.executionContext.id == id && it.context.invokationContext.id != it.context.executionContext.id }
            filteredList.none { it.state is IdOwner<*> && it.state.id == id }
        }
        val level0data = level0Contexts.map { context ->
            "{\"id\": \"${context.id}-${context.serializedClass}\", \"parents\":[]}"
        }.onEach {
            Log.d("CATS", it)
        }

        val level0ids = level0Contexts.map { it.id }
        Log.d("CATS", "level0 ids: ${level0ids.size}")
        val nextLevelContexts = dedupedContexts
            .filterNot { it.id in level0ids }
            .filter { context ->
                filteredList.any { it.context.executionContext.id == context.id && it.context.invokationContext.id in level0ids }
            }
        Log.d("CATS", "nextLevelContexts: ${nextLevelContexts.size}")
        val returnedObjectsList = filteredList.mapNotNull {
            if (it.state is IdOwner<*>) {
                it.context to it.state as IdOwner<IndexAndUUID>
            } else {
                null
            }
        }.groupBy { it.second }.entries.map {
            val parents = it.value.mapNotNull {
                if (it.first.executionContext.id in level0ids) {
                    "\"${it.first.executionContext.id}-${it.first.executionContext.serializedClass}\""
                } else {
                    null
                }
            }
            "{\"id\": \"${it.key.id}-${it.key.clazz}\", \"parents\":[${parents.distinct()
                .joinToString(", ")}]}"
        }
        Log.d("CATS", "returnedObjectsList: ${returnedObjectsList}")
        val nextLevelList = nextLevelContexts.map { context ->
            val parents = filteredList.mapNotNull {
                if (it.context.executionContext.id == context.id && it.context.invokationContext.id in level0ids) {
                    val invokationContext = it.context.invokationContext
                    "\"${invokationContext.id}-${invokationContext.serializedClass}\""
                } else {
                    null
                }
            }
            "{\"id\": \"${context.id}-${context.serializedClass}\", \"parents\":[${parents.distinct()
                .joinToString(", ")}]}"
        }.onEach {
            Log.d("CATS", it)
        }

        val nextLevel = (returnedObjectsList + nextLevelList).joinToString(",")

        val level1Ids = nextLevelContexts.map { it.id }
        val secondLevelContexts = dedupedContexts
            .filterNot { it.id in level1Ids }
            .filterNot { it.id in level0ids }
            .filter { context ->
                filteredList.any { it.context.executionContext.id == context.id && it.context.invokationContext.id in level1Ids }
            }
        Log.d("CATS", "nextLevelContexts: ${nextLevelContexts.size}")
        val secondLevel = secondLevelContexts.map { context ->
            val parents = filteredList.mapNotNull {
                if (it.context.executionContext.id == context.id && it.context.invokationContext.id in level1Ids) {
                    val invokationContext = it.context.invokationContext
                    "\"${invokationContext.id}-${invokationContext.serializedClass}\""
                } else {
                    null
                }
            }
            "{\"id\": \"${context.id}-${context.serializedClass}\", \"parents\":[${parents.distinct()
                .joinToString(", ")}]}"
        }.onEach {
            Log.d("CATS", it)
        }.joinToString(",")

        //        val  dataset = intArrayOf(5,10,15,20,35)
        // use java.util.Arrays to format
        // the array as text
        Log.d("CATS", nextLevel)
        val text =
            "{\"levels\":[[${level0data.joinToString(",")}],[$nextLevel],[$secondLevel]]}"//dataset.toString()
        Log.d("CATS", text)



        return text
    }

}