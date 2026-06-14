package com.example.mypractico4.domain.model

import com.example.mypractico4.domain.model.TetrominoType

data class GameState(
    val board: List<List<TetrominoType?>> = List(20) { List(10) { null } },
    val currentPiece: Tetromino = randomPiece(),
    val nextPiece: Tetromino = randomPiece(),
    val score: Int = 0,
    val lines: Int = 0,
    val isGameOver: Boolean = false,
    val startTime: Long = System.currentTimeMillis(),
    val turbo37Active: Boolean = false
)

fun randomPiece(): Tetromino {
    return Tetromino(
        type = TetrominoType.entries.random()
    )
}