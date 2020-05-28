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
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.koin.core.KoinComponent
import retrofit2.converter.moshi.MoshiConverterFactory

//import sun.jvm.hotspot.utilities.IntArray


/**
 * Created by Emmanuel Requier on 09/05/2020.
 */
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
        val ids = filteredList.flatMap { listOf(it.context.invokationContext, it.context.executionContext) }
        val dedupedContexts = ids.distinctBy { it.id }
        Log.d("CATS","deduped ids: ${dedupedContexts.size}")
        val level0Contexts = dedupedContexts.filter { dedupedContext ->
            val id = dedupedContext.id
            filteredList.none { it.context.executionContext.id == id && it.context.invokationContext.id != it.context.executionContext.id  }
        }
        val level0data = level0Contexts.map { context ->
             "{\"id\": \"${context.id}-${context.serializedClass}\", \"parents\":[]}"
        }.onEach {
            Log.d("CATS",it)
        }

        val level0ids = level0Contexts.map { it.id }
        Log.d("CATS","level0 ids: ${level0ids.size}")
        val nextLevelContexts = dedupedContexts
            .filterNot{  it.id in level0ids }
            .filter { context ->
                filteredList.any { it.context.executionContext.id == context.id && it.context.invokationContext.id in level0ids }
            }
        Log.d("CATS","nextLevelContexts: ${nextLevelContexts.size}")
        val nextLevel = nextLevelContexts.map {
            context ->
            val parents = filteredList.mapNotNull {
                if(it.context.executionContext.id == context.id && it.context.invokationContext.id in level0ids){
                    val invokationContext = it.context.invokationContext
                    "\"${invokationContext.id}-${invokationContext.serializedClass}\""
                }else{
                    null
                }
            }
            "{\"id\": \"${context.id}-${context.serializedClass}\", \"parents\":[${parents.distinct().joinToString(", ")}]}"
        }.onEach {
            Log.d("CATS",it)
        }.joinToString(",")

        val level1Ids = nextLevelContexts.map { it.id }
        val secondLevelContexts = dedupedContexts
            .filterNot{  it.id in level1Ids }
            .filterNot{  it.id in level0ids }
            .filter { context ->
                filteredList.any { it.context.executionContext.id == context.id && it.context.invokationContext.id in level1Ids }
            }
        Log.d("CATS","nextLevelContexts: ${nextLevelContexts.size}")
        val secondLevel = secondLevelContexts.map {
                context ->
            val parents = filteredList.mapNotNull {
                if(it.context.executionContext.id == context.id && it.context.invokationContext.id in level1Ids){
                    val invokationContext = it.context.invokationContext
                    "\"${invokationContext.id}-${invokationContext.serializedClass}\""
                }else{
                    null
                }
            }
            "{\"id\": \"${context.id}-${context.serializedClass}\", \"parents\":[${parents.distinct().joinToString(", ")}]}"
        }.onEach {
            Log.d("CATS",it)
        }.joinToString(",")

//        val  dataset = intArrayOf(5,10,15,20,35)
        // use java.util.Arrays to format
        // the array as text
        Log.d("CATS",nextLevel)
        val text = "{\"levels\":[[${level0data.joinToString(",")}],[$nextLevel],[$secondLevel]]}"//dataset.toString()
        Log.d("CATS",text)


        val converterFactory = MoshiConverterFactory.create()
        val moshi = Moshi.Builder().build();
        val jsonAdapter : JsonAdapter<Levels> = moshi.adapter(Levels::class.java)
//        val levelType = (
//            MutableList::class.java,
//            MyData::class.java
//        )
//        val adapter: JsonAdapter<List<MyData>> =
//            moshi.adapter<Any>(listMyData)
        val json = jsonAdapter.fromJson(text)

        binding.apply {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                webView.evaluateJavascript("loadPieChart('$text');", null);
            } else {
                webView.loadUrl("javascript:loadPieChart('$text');");
            }
        }
    }

}