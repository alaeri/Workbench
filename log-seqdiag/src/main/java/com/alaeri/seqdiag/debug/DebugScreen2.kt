package com.alaeri.seqdiag

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.map

@Composable
@Preview
fun debugScreen2(){
    val wikiText: State<List<String>> = //logRepository.listAsFlow.map { l -> l.map { it.toString() }  }
        graphRepository.graph.map { it.items.map { it.toString() } }
        .collectAsState(
        emptyList()
    )
    LazyColumn(Modifier.fillMaxHeight().fillMaxWidth(1f)) {
        items(items = wikiText.value, itemContent = { t: String ->
            Text(modifier = Modifier.padding(10.dp), text = t)
        })
    }
}