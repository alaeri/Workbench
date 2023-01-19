package com.alaeri.seqdiag

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.alaeri.domain.wiki.LoadingStatus
import com.alaeri.log.core.LogConfig
import com.alaeri.log.core.LogConfig.logBlocking
import com.alaeri.log.core.child.ChildLogEnvironmentFactory
import com.alaeri.log.extra.identity.IdentityRepresentation
import com.alaeri.log.extra.tag.receiver.ReceiverTag
import com.alaeri.log.repository.GraphNode
import com.alaeri.log.repository.GraphRepresentation
import com.alaeri.log.repository.HistoryItem
import com.alaeri.log.sample.graphRepository
import com.alaeri.log.sample.lib.wiki.wiki.WikiRepositoryImpl
import com.alaeri.log.sample.log
import com.alaeri.log.sample.logBlocking
import com.alaeri.log.sample.logRepository
import com.zachklipp.seqdiag.LineStyle
import com.zachklipp.seqdiag.Note
import com.zachklipp.seqdiag.Participant
import com.zachklipp.seqdiag.SequenceDiagram
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*

object DI{
    val app = Any()
    init {
        LogConfig.logEnvironmentFactory = ChildLogEnvironmentFactory
    }

    lateinit var scope : CoroutineScope
    var wikiRepository  = WikiRepositoryImpl()
    lateinit var vm: MainViewModel
}
//https://stackoverflow.com/questions/66005066/android-jetpack-compose-how-to-zoom-a-image-in-a-box
@Composable
fun ZoomableBox(
    modifier: Modifier = Modifier,
    minScale: Float = 0.1f,
    maxScale: Float = 5f,
    content: @Composable ZoomableBoxScope.() -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var size by remember { mutableStateOf(IntSize.Zero) }
    Box(
        modifier = modifier
            .clip(RectangleShape)
            .onSizeChanged { size = it }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = maxOf(minScale, minOf(scale * zoom, maxScale))
                    val maxX = (size.width * (scale - 1)) / 2
                    val minX = -maxX
                    offsetX = maxOf(minX, minOf(maxX, offsetX + pan.x))
                    val maxY = (size.height * (scale - 1)) / 2
                    val minY = -maxY
                    offsetY = maxOf(minY, minOf(maxY, offsetY + pan.y))
                }
            }
    ) {
        val scope = ZoomableBoxScopeImpl(scale, offsetX, offsetY)
        scope.content()
    }
}

interface ZoomableBoxScope {
    val scale: Float
    val offsetX: Float
    val offsetY: Float
}

private data class ZoomableBoxScopeImpl(
    override val scale: Float,
    override val offsetX: Float,
    override val offsetY: Float
) : ZoomableBoxScope
@Composable
@Preview
fun RowWithWikiAppAndDebugScreen(){
    Row(modifier = Modifier.fillMaxSize()){
        WikiApp()
        debugScreen()
        debugScreen2()
    }
}
@Composable
@Preview
fun debugScreen2(){
    val wikiText: State<List<String>> = logRepository.listAsFlow.map { l -> l.map { it.toString() }  }.collectAsState(
        emptyList()
    )
    LazyColumn(Modifier.fillMaxHeight().fillMaxWidth(1f)) {
        items(items = wikiText.value, itemContent = { t : String->
            Text(modifier = Modifier.padding(10.dp), text = t)
        })
    }
}
class MainViewModel(val sharingScope: CoroutineScope) {
    val receiverTag = ReceiverTag(this)
    val entry : MutableStateFlow<String> = MutableStateFlow("initial")
    fun setValue(value: String){
        logBlocking("updateInput2", receiverTag){
            entry.value = value
        }
    }

    val text = entry
        .log("entry", receiverTag)
        .flatMapLatest {
        println("loading")
        DI.wikiRepository.loadWikiArticle(it)
            .log("magic load", ReceiverTag(this))
            .onEach {
            println("loading: $it")
        }

    }.stateIn(sharingScope, SharingStarted.Eagerly, LoadingStatus.Loading(""))
}
@Composable
@Preview
fun WikiApp(){
    val receiverTag = ReceiverTag(DI.app)
    logBlocking("update screen", receiverTag){
        val wikiText: State<LoadingStatus> = DI.vm.text.log("updateText", ReceiverTag(DI.app)).collectAsState(LoadingStatus.Loading(""))
        val inputText: State<String> = DI.vm.entry.log("updateText", ReceiverTag(DI.app)).collectAsState("")

        Column(modifier = Modifier.fillMaxWidth(0.4f)){
            Text(modifier = Modifier.weight(1f, true), text = wikiText.value.let { it.toString() })
            TextField(modifier = Modifier, value = inputText.value, onValueChange = {
                logBlocking("updateInput", receiverTag){
                    DI.vm.setValue(it)
                }
            })
        }
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
        .fillMaxHeight()
        .fillMaxWidth(0.5f)
        .verticalScroll(rememberScrollState())
        .horizontalScroll(rememberScrollState())
        .padding(10.dp)
//        .background(Color.Blue)
        , propagateMinConstraints = true){
        sizeBox()
    }
}
@Preview
@Composable
fun sizeBox(){
//    ZoomableBox {
         Box(modifier =
    Modifier
        .requiredSize(800.dp, 10000.dp)
        .border(3.dp, Color.Green)
        .padding(10.dp)
//        .graphicsLayer(
//            scaleX = scale,
//            scaleY = scale,
//            translationX = offsetX,
//            translationY = offsetY
//        )
        //.background(Color.Yellow)
        ,
        propagateMinConstraints = true) {
             seqDiag()
         }
//    }
}
@Preview
@Composable
fun seqDiag(){
    val state :State<GraphRepresentation> = graphRepository.graph.collectAsState(
        GraphRepresentation(emptyList())
    )
    val graphRepresentation = state.value

    SequenceDiagram(
        modifier = Modifier

            .border(2.dp, Color.Green)
        //.background(Color.Green)
    ) {

        val participantsMap = mutableMapOf<IdentityRepresentation, Participant>()
        graphRepresentation.items.forEach { item ->
            when (item) {
                is HistoryItem.Actor -> participantsMap[item.id] =
                    createParticipant(topLabel = { Note(item.name) }, bottomLabel = {})
                is HistoryItem.Line -> {
                    val from = participantsMap[item.from]
                    val to = participantsMap[item.to]
                    if (from != null && to != null) {
                        from.lineTo(to).label { Note(item.name) }.style(LineStyle())
                    } else {
                        println("missing from: $from or to: $to in log")
                    }
                }
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
fun main() {
    val receiverTag = ReceiverTag(DI.app)
    logBlocking("main", receiverTag) {
        val logScope = this
        application {
            DI.scope = CoroutineScope(GlobalScope.coroutineContext)
            logBlocking("build VM", receiverTag) {
                DI.vm = MainViewModel(DI.scope)
                Window(onCloseRequest = ::exitApplication, title = "SeqDiag Compose test") {
                    logBlocking("app", receiverTag){
                        App()
                    }
                }
            }
        }
    }
}
