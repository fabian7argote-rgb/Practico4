package com.example.mypractico4.presentation.components


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mypractico4.domain.model.Tetromino

@Composable
fun NextPieceView(piece: Tetromino) {
    Canvas(
        modifier = Modifier
            .size(120.dp)
            .border(1.dp, Color.Black)
    ) {
        val cellSize = size.width / 6

        piece.blocks().forEach { cell ->
            val x = (cell.x - piece.position.x + 2) * cellSize
            val y = (cell.y - piece.position.y + 2) * cellSize

            drawRect(
                color = colorFor(piece.type),
                topLeft = Offset(x, y),
                size = Size(cellSize, cellSize)
            )

            drawRect(
                color = Color.Black,
                topLeft = Offset(x, y),
                size = Size(cellSize, cellSize),
                style = androidx.compose.ui.graphics.drawscope.Stroke()
            )
        }
    }
}