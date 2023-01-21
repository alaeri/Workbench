package com.alaeri.seqdiag

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.alaeri.domain.wiki.LoadingStatus
import com.alaeri.domain.wiki.WikiText
import com.alaeri.log.core.LogConfig
import com.alaeri.log.core.child.ChildLogEnvironmentFactory
import com.alaeri.log.extra.tag.receiver.ReceiverTag
import com.alaeri.log.repository.GraphRepresentation
import com.alaeri.log.repository.GroupedLogsGraphMapper
import com.alaeri.log.repository.HistoryItem
import com.alaeri.log.sample.lib.wiki.wiki.WikiRepositoryImpl
import com.zachklipp.seqdiag.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*

object App{
    val app = this
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
//        debugScreen2()
    }
}
@Composable
@Preview
fun debugScreen2(){
    val wikiText: State<List<String>> = //logRepository.listAsFlow.map { l -> l.map { it.toString() }  }
        graphRepository.graph.map { it.items.map { it.toString() } }
        .collectAsState(
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
        logBlocking("setInputValue", receiverTag){
            entry.value = value
        }
    }


    private val inputFlow = entry.log("inputFlow", receiverTag)

    private val text = inputFlow
        .flatMapLatest {
            log("load wiki article", receiverTag){
                App.wikiRepository.loadWikiArticle(it)
            }
        }
        .log("flowWikiLoad", receiverTag)
        .flowOn(Dispatchers.IO)
        .distinctUntilChanged()

    data class State(val input: String, val text: LoadingStatus)
    val state = combine(inputFlow, text){ e, t -> State( e, t )}
        .logShareIn(
            name = "sharedState",
            receiverTag = receiverTag,
            coroutineScope = sharingScope,
            sharingStarted = SharingStarted.Eagerly,
            replayCount = 1
        )
}

@Composable
@Preview
fun WikiApp(){
    val receiverTag = ReceiverTag(App.app)
    logBlocking("update screen", receiverTag){
        val state: State<MainViewModel.State> = App.vm.state.log("appState", ReceiverTag(App)).collectAsState(MainViewModel.State("initial", LoadingStatus.Loading("")))
        Column(modifier = Modifier.fillMaxWidth(0.4f)){
            Text(modifier = Modifier.weight(1f, true),
                text =
                //"ok",
                state.value.let { it.text.toString() }
            )
            TextField(modifier = Modifier,
                value = state.value.input,
                onValueChange = {
                logBlocking("onInputUpdated", receiverTag){
                    App.vm.setValue(it)
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
        .fillMaxWidth(1f)
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
        //.requiredSize(800.dp, 800.dp)
        .fillMaxSize()
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

        val participantsMap = mutableMapOf<GroupedLogsGraphMapper.GroupKey, Participant>()
        graphRepresentation.items.forEach { item ->
            when (item) {
                is HistoryItem.Actor -> {
                    val modifier = when(item.actorType){
                        HistoryItem.ActorType.Receiver -> Modifier.background(Color.LightGray).padding(10.dp)
                        HistoryItem.ActorType.Log -> Modifier.background(Color(0xFFBBBBEE)).padding(10.dp)
                    }
                    participantsMap[item.id] =
                        createParticipant(topLabel = { Text(item.name, modifier) }, bottomLabel = {})
                }
                is HistoryItem.Line -> {
                    val from = participantsMap[item.from]
                    val to = participantsMap[item.to]
                    if (from != null && to != null && item.lineType == HistoryItem.LineType.OnEach) {
                        val lineStyle = when(item.lineType){
                            HistoryItem.LineType.FromReceiver -> LineStyle(
                                arrowHeadType = null,
                                dashIntervals = 2.dp to 5.dp,
                                brush = SolidColor(Color.LightGray))
                            HistoryItem.LineType.FromParentStart -> LineStyle()
                            HistoryItem.LineType.ToParentEnd -> LineStyle()
                            HistoryItem.LineType.OnEach -> LineStyle(
                                width = 3.dp,
                                brush = Brush.horizontalGradient(listOf( Color.Green, Color.Red))
                            )
                        }
                        when(item.lineType){
                            HistoryItem.LineType.OnEach,
                            HistoryItem.LineType.ToParentEnd -> to.lineTo(from).label { Text(item.name.take(40)) }.style(lineStyle)
                            else -> from.lineTo(to).label { Text(item.name.take(40)) }.style(lineStyle)
                        }

                    } else {
                        println("missing from: ${item.from} = $from or to: ${item.to} = $to in log")
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
    val receiverTag = ReceiverTag(App)
    logBlocking("main", receiverTag) {
        val a = this
        application {
            val appCoroutineScope = rememberCoroutineScope()
            App.scope = appCoroutineScope
            App.vm = MainViewModel(App.scope)
            Window(onCloseRequest = ::exitApplication, title = "SeqDiag Compose test") {
                logBlocking("populate and run app window", receiverTag){
                    println(Thread.currentThread().name)
                    App()
                }
            }
        }
    }
}
