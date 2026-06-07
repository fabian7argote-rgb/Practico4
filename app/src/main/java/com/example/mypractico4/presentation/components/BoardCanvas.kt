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
import com.example.mypractico4.domain.model.GameState
import com.example.mypractico4.domain.model.TetrominoType


@Composable
fun BoardCanvas(gameState: GameState) {
    Canvas(
        modifier = Modifier
            .size(width = 250.dp, height = 500.dp)
            .border(1.dp, Color.Black)
    ) {
        val cellSize = size.width / 10

        for (y in 0 until 20) {
            for (x in 0 until 10) {
                drawRect(
                    color = Color.LightGray,
                    topLeft = Offset(x * cellSize, y * cellSize),
                    size = Size(cellSize, cellSize),
                    style = androidx.compose.ui.graphics.drawscope.Stroke()
                )

                val type = gameState.board[y][x]
                if (type != null) {
                    drawRect(
                        color = colorFor(type),
                        topLeft = Offset(x * cellSize, y * cellSize),
                        size = Size(cellSize, cellSize)
                    )
                }
            }
        }

        gameState.currentPiece.blocks().forEach { cell ->
            drawRect(
                color = colorFor(gameState.currentPiece.type),
                topLeft = Offset(cell.x * cellSize, cell.y * cellSize),
                size = Size(cellSize, cellSize)
            )
        }
    }
}

fun colorFor(type: TetrominoType): Color {
    return when (type) {
        TetrominoType.I -> Color.Cyan
        TetrominoType.O -> Color.Yellow
        TetrominoType.T -> Color.Magenta
        TetrominoType.S -> Color.Green
        TetrominoType.Z -> Color.Red
        TetrominoType.J -> Color.Blue
        TetrominoType.L -> Color(0xFFFF9800)
    }
}