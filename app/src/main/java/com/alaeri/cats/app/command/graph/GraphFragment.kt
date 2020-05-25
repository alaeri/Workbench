package com.alaeri.cats.app.command.graph

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.alaeri.cats.app.databinding.GraphFragmentBinding

/**
 * Created by Emmanuel Requier on 09/05/2020.
 */
class GraphFragment: Fragment() {

    private lateinit var binding: GraphFragmentBinding

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
        val  dataset = intArrayOf(5,10,15,20,35)
        // use java.util.Arrays to format
        // the array as text
        val text = dataset.toString()
        binding.apply {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                webView.evaluateJavascript("loadPieChart('$text');", null);
            } else {
                webView.loadUrl("javascript:loadPieChart('$text');");
            }
        }
    }

}