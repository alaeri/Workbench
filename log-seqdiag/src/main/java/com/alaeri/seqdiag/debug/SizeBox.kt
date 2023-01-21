package com.alaeri.seqdiag

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun sizeBox(){
//    ZoomableBox {
    Box(
        modifier =
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
        propagateMinConstraints = true
    ) {
        seqDiag()
    }
//    }
}