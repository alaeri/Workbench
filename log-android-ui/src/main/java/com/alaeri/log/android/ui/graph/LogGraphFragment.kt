package com.alaeri.log.android.ui.graph

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
import androidx.lifecycle.MutableLiveData
import com.alaeri.log.android.ui.databinding.GraphFragmentBinding
import org.koin.core.KoinComponent


/**
 * Created by Emmanuel Requier on 09/05/2020.
 */
class LogGraphFragment: Fragment(), KoinComponent {

    private lateinit var binding: GraphFragmentBinding

    val mutableLiveDataIsPageReady = MutableLiveData<Boolean>(false)

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
                //This the the enabling of the zoom controls
//                webView.settings.builtInZoomControls = true;

//                //This will zoom out the WebView
//                webView.settings.useWideViewPort = true;
//                webView.settings.loadWithOverviewMode = true;
//                webView.setInitialScale(1);


                settings.javaScriptEnabled = true
                webChromeClient = WebChromeClient()
                webViewClient =  object: WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        Log.d("CATS","readying webview to commands after loading: $url")
                        mutableLiveDataIsPageReady.value = true
                    }
                }
                loadUrl("http://localhost:8080/")
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        mutableLiveDataIsPageReady.switchMap {
//            Log.d("CATS","switching to commands")
//            commandRepository.state.map { it.history?.list }.filterNotNull().map {
//                it.mapNotNull { focusedCommandOrBreak ->
//                    val focused =  focusedCommandOrBreak as? FocusedLogOrBreak.Focused
//                    focused?.serializableLog
//                }
//            }.asLiveData(lifecycleScope.coroutineContext)
//        }.observe(this.viewLifecycleOwner, Observer {
//            val levelsToJson = ComponentsGraphMapper.buildGraph(it)
//            //val converterFactory = MoshiConverterFactory.create()
//            val moshi = Moshi.Builder().build();
//            val jsonAdapter: JsonAdapter<GraphRepresentation> = moshi.adapter(GraphRepresentation::class.java)
//            //val oldText = createJsonManually(filteredList)
//            val text = jsonAdapter.toJson(levelsToJson)
//            Log.d("CATS","json text: $text")
////        val levelType = (
////            MutableList::class.java,
////            MyData::class.java
////        )
////        val adapter: JsonAdapter<List<MyData>> =
////            moshi.adapter<Any>(listMyData)
//            //val json = jsonAdapter.fromJson(text)
//
//            binding.apply {
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//                    webView.evaluateJavascript("loadPieChart('$text');", null);
//                } else {
//                    webView.loadUrl("javascript:loadPieChart('$text');");
//                }
//            }
//            Log.d("CATS","size of commands to build graph: ${it.size}")
//        })
//    }


//    private fun createJsonManually(
//        filteredList: List<SerializableCommandStateAndContext<IndexAndUUID>>
//    ): String {
//        val allContexts = filteredList.flatMap {
//            listOf(
//                it.context.invokationContext,
//                it.context.executionContext
//            )
//        }
//        val dedupedContexts = allContexts.distinctBy { it.id }
//        Log.d("CATS", "deduped ids: ${dedupedContexts.size}")
//        val level0Contexts = dedupedContexts.filter { dedupedContext ->
//            val id = dedupedContext.id
//            filteredList.none { it.context.executionContext.id == id && it.context.invokationContext.id != it.context.executionContext.id }
//            filteredList.none { it.state is IdOwner<*> && it.state.id == id }
//        }
//        val level0data = level0Contexts.map { context ->
//            "{\"id\": \"${context.id}-${context.serializedClass}\", \"parents\":[]}"
//        }.onEach {
//            Log.d("CATS", it)
//        }
//
//        val level0ids = level0Contexts.map { it.id }
//        Log.d("CATS", "level0 ids: ${level0ids.size}")
//        val nextLevelContexts = dedupedContexts
//            .filterNot { it.id in level0ids }
//            .filter { context ->
//                filteredList.any { it.context.executionContext.id == context.id && it.context.invokationContext.id in level0ids }
//            }
//        Log.d("CATS", "nextLevelContexts: ${nextLevelContexts.size}")
//        val returnedObjectsList = filteredList.mapNotNull {
//            if (it.state is IdOwner<*>) {
//                it.context to it.state as IdOwner<IndexAndUUID>
//            } else {
//                null
//            }
//        }.groupBy { it.second }.entries.map {
//            val parents = it.value.mapNotNull {
//                if (it.first.executionContext.id in level0ids) {
//                    "\"${it.first.executionContext.id}-${it.first.executionContext.serializedClass}\""
//                } else {
//                    null
//                }
//            }
//            "{\"id\": \"${it.key.id}-${it.key.clazz}\", \"parents\":[${parents.distinct()
//                .joinToString(", ")}]}"
//        }
//        Log.d("CATS", "returnedObjectsList: ${returnedObjectsList}")
//        val nextLevelList = nextLevelContexts.map { context ->
//            val parents = filteredList.mapNotNull {
//                if (it.context.executionContext.id == context.id && it.context.invokationContext.id in level0ids) {
//                    val invokationContext = it.context.invokationContext
//                    "\"${invokationContext.id}-${invokationContext.serializedClass}\""
//                } else {
//                    null
//                }
//            }
//            "{\"id\": \"${context.id}-${context.serializedClass}\", \"parents\":[${parents.distinct()
//                .joinToString(", ")}]}"
//        }.onEach {
//            Log.d("CATS", it)
//        }
//
//        val nextLevel = (returnedObjectsList + nextLevelList).joinToString(",")
//
//        val level1Ids = nextLevelContexts.map { it.id }
//        val secondLevelContexts = dedupedContexts
//            .filterNot { it.id in level1Ids }
//            .filterNot { it.id in level0ids }
//            .filter { context ->
//                filteredList.any { it.context.executionContext.id == context.id && it.context.invokationContext.id in level1Ids }
//            }
//        Log.d("CATS", "nextLevelContexts: ${nextLevelContexts.size}")
//        val secondLevel = secondLevelContexts.map { context ->
//            val parents = filteredList.mapNotNull {
//                if (it.context.executionContext.id == context.id && it.context.invokationContext.id in level1Ids) {
//                    val invokationContext = it.context.invokationContext
//                    "\"${invokationContext.id}-${invokationContext.serializedClass}\""
//                } else {
//                    null
//                }
//            }
//            "{\"id\": \"${context.id}-${context.serializedClass}\", \"parents\":[${parents.distinct()
//                .joinToString(", ")}]}"
//        }.onEach {
//            Log.d("CATS", it)
//        }.joinToString(",")
//
//        //        val  dataset = intArrayOf(5,10,15,20,35)
//        // use java.util.Arrays to format
//        // the array as text
//        Log.d("CATS", nextLevel)
//        val text =
//            "{\"levels\":[[${level0data.joinToString(",")}],[$nextLevel],[$secondLevel]]}"//dataset.toString()
//        Log.d("CATS", text)
//
//
//
//        return text
//    }
    }

}
