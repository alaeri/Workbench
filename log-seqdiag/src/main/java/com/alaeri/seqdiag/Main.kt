package com.alaeri.seqdiag

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.alaeri.domain.wiki.LoadingStatus
import com.alaeri.log.core.LogConfig
import com.alaeri.log.core.LogConfig.logBlocking
import com.alaeri.log.core.child.ChildLogEnvironmentFactory
import com.alaeri.log.repository.GraphNode
import com.alaeri.log.repository.GraphRepresentation
import com.alaeri.log.sample.graphRepository
import com.alaeri.log.sample.lib.wiki.wiki.WikiRepositoryImpl
import com.alaeri.log.sample.log
import com.alaeri.log.sample.logBlocking
import com.zachklipp.seqdiag.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*

object DI{
    init {
        LogConfig.logEnvironmentFactory = ChildLogEnvironmentFactory
        logBlocking("init"){
            println("ok")
        }
    }


    lateinit var scope : CoroutineScope
    var wikiRepository  = WikiRepositoryImpl()
    lateinit var vm: MainViewModel
}
@Composable
@Preview
fun RowWithWikiAppAndDebugScreen(){
    Row(modifier = Modifier.fillMaxSize()){
        WikiApp()
        debugScreen()
    }
}
class MainViewModel(val sharingScope: CoroutineScope) {
    val entry : MutableStateFlow<String> = MutableStateFlow("initial")
    fun setValue(value: String){
        entry.value = value
    }

    val text = entry
        .log("entry")
        .flatMapLatest {
        println("loading")
        DI.wikiRepository.loadWikiArticle(it)
//            .log("magic load")
            .onEach {
            println("loading: $it")
        }

    }.stateIn(sharingScope, SharingStarted.Eagerly, LoadingStatus.Loading(""))
}
@Composable
@Preview
fun WikiApp(){

    val wikiText: State<LoadingStatus> = DI.vm.text.collectAsState(LoadingStatus.Loading(""))
    val inputText: State<String> = DI.vm.entry.collectAsState("")


    Column(){
        Text(wikiText.value.let { it.toString() })
        TextField(inputText.value, onValueChange = DI.vm::setValue)
    }
}
@Composable
@Preview
fun debugScreen(){
    scrollBox()
}
@Composable
@Preview
fun App(){
    RowWithWikiAppAndDebugScreen()
//
//
//    val column = Column(
//        modifier = Modifier
////            .weight(1f, true)
//            .padding(10.dp)
//            .background(Color.Gray)
//            .fillMaxSize()
//            //.weight(1f, false)
//
//    ) {
//        scrollBox()
//    }
//    return column
}
@Preview
@Composable
fun scrollBox(){
    val box = Box(modifier =
    Modifier
        .border(3.dp, Color.Green)
        .verticalScroll(rememberScrollState())
        .horizontalScroll(rememberScrollState())
        .padding(10.dp)
        .background(Color.Blue), propagateMinConstraints = true){
        sizeBox()
    }
}
@Preview
@Composable
fun sizeBox(){
    val box = Box(modifier =
    Modifier.requiredSize(Dp(1000.0f), Dp(1000.0f))
        .border(3.dp, Color.Green)

        .padding(10.dp)
        .background(Color.Yellow), propagateMinConstraints = true){
        seqDiag()
    }
}
@Preview
@Composable
fun seqDiag(){
    val state :State<GraphRepresentation> = graphRepository.graph.collectAsState(
        GraphRepresentation(emptyList())
    )
    val graphRepresentation = state.value

    SequenceDiagram(modifier = Modifier
        .border(2.dp, Color.Green)
        //.background(Color.Green)
    ) {
        graphRepresentation.levels.forEach { aLevel ->
            aLevel.forEach {
                graphNode: GraphNode ->  graphNode.label
                createParticipant(topLabel = {Note(graphNode.label)}, bottomLabel = {})
            }

        }
//
//        val alice = createParticipant(topLabel = { Note("Alice") }, bottomLabel = {})
//        val bob = createParticipant(topLabel = { Note("Bob") }, bottomLabel = {})
//        val carlos = createParticipant(topLabel = { Note("Carlos")}, bottomLabel = {} )
//
//        noteToStartOf(alice) { Note("Note to the start of Alice") }
//        noteOver(listOf(alice)) { Note("Note over Alice") }
//        noteToEndOf(alice) { Note("Note to the end of Alice") }
//
//        noteOver(listOf(alice, carlos)){ Note("Note over multiple participants") }
//
//        alice.lineTo(bob)
//            .label { Label("Hello!") }
//        bob.lineTo(carlos)
//            .label { Label("Alice says hi") }
//
//        // Lines don't need to have labels, and they can be styled.
//        carlos.lineTo(bob)
//            .color(Color.Blue)
//            .arrowHeadType(ArrowHeadType.Outlined)
//
//        // Lines can span multiple participants.
//        carlos.lineTo(alice)
//            .label { Label("Hello back!") }
    }
}
fun main() = logBlocking() {
    application {
        DI.scope = CoroutineScope(GlobalScope.coroutineContext)
        DI.vm = MainViewModel(DI.scope)
        Window(onCloseRequest = ::exitApplication, title = "SeqDiag Compose test") {

            App()
        }
    }
}
