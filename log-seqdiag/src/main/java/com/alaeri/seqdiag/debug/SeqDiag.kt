package com.alaeri.seqdiag

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.alaeri.log.repository.GraphRepresentation
import com.alaeri.log.repository.GroupKey
import com.alaeri.log.repository.HistoryItem
import com.zachklipp.seqdiag.LineStyle
import com.zachklipp.seqdiag.Note
import com.zachklipp.seqdiag.Participant
import com.zachklipp.seqdiag.SequenceDiagram
import kotlin.math.pow

fun fadedColor(lastElementIndex: Int, overallLastIndex: Int, baseColor: Color): Color{
    return baseColor.copy( alpha = 0.99f.pow(overallLastIndex - lastElementIndex).coerceAtLeast(0.1f))
}

@Preview
@Composable
fun seqDiag(){
    val state : State<GraphRepresentation> = graphRepository.graph.collectAsState(
        GraphRepresentation(emptyList())
    )
    val graphRepresentation = state.value

//    val robot = Robot()
//
//
//    val img = robot.createScreenCapture(0.window.bounds)
//    //val subImg = img.getSubimage(0, 0, xEnd, yEnd) // <- take a sub image from the main one
//    val outputfile = File("screenshot-compose.png")
//    ImageIO.write(img, "png", outputfile)


    SequenceDiagram(
        modifier = Modifier

//            .border(2.dp, Color.Green)
        //.background(Color.Green)
    ) {

        val participantsMap = mutableMapOf<GroupKey, Participant>()
        graphRepresentation.items.forEach { item ->
            when (item) {
                is HistoryItem.Actor -> {
                    val modifier = when (item.actorType) {
                        HistoryItem.ActorType.Receiver -> Modifier.background(Color.LightGray)
                            .padding(10.dp)
                        HistoryItem.ActorType.Log -> Modifier.background(Color(0xFFBBBBEE))
                            .padding(10.dp)
                    }
                    participantsMap[item.id] =
                        createParticipant(
                            topLabel = { Text(item.name, modifier) },
                            bottomLabel = {})
                }
                is HistoryItem.Receiver -> {
                    noteOver(item.contained.mapNotNull { participantsMap[it] }){
                        Note(item.name)
                    }
                }
                is HistoryItem.Line -> {
                    val from = participantsMap[item.from]
                    val to = participantsMap[item.to]
                    if (from != null && to != null) {
                        val lineStyle = when (item.lineType) {
                            HistoryItem.LineType.FromReceiver -> LineStyle(
                                arrowHeadType = null,
                                dashIntervals = 2.dp to 5.dp,
                                brush = SolidColor(Color.LightGray)
                            )
                            HistoryItem.LineType.FromParentStart -> LineStyle(
                                brush = SolidColor(fadedColor(
                                    item.index,
                                    graphRepresentation.lastIndex,
                                    if(item.isActive) { Color.Red }else{Color.DarkGray}
                                ))
                            )
                            HistoryItem.LineType.ToParentEnd -> LineStyle(
                                brush = SolidColor(fadedColor(
                                    item.index,
                                    graphRepresentation.lastIndex,
                                    if(item.isActive) { Color.Red }else{Color.DarkGray}
                                ))
                            )
                            HistoryItem.LineType.OnEach -> LineStyle(
                                width = 5.dp,
                                dashIntervals = 2.dp to 5.dp,
                                brush = SolidColor(fadedColor(
                                    item.index,
                                    graphRepresentation.lastIndex,
                                    if(item.isActive) { Color.Red }else{Color.DarkGray}
                                ))
                            )
                        }
                        when (item.lineType) {
                            HistoryItem.LineType.OnEach,
                            HistoryItem.LineType.ToParentEnd -> to.lineTo(from)
                                .label { Text(item.name.take(40)) }.style(lineStyle)
                            else -> from.lineTo(to).label { Text(item.name.take(40)) }
                                .style(lineStyle)
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