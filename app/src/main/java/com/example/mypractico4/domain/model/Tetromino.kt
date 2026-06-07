package com.example.mypractico4.domain.model

import com.example.mypractico4.domain.model.TetrominoType

data class Tetromino(
    val type: TetrominoType,
    val position: Cell = Cell(4, 0),
    val rotation: Int = 0
) {
    fun blocks(): List<Cell> {
        val shape = when (type) {
            TetrominoType.I -> listOf(Cell(-1, 0), Cell(0, 0), Cell(1, 0), Cell(2, 0))
            TetrominoType.O -> listOf(Cell(0, 0), Cell(1, 0), Cell(0, 1), Cell(1, 1))
            TetrominoType.T -> listOf(Cell(-1, 0), Cell(0, 0), Cell(1, 0), Cell(0, 1))
            TetrominoType.S -> listOf(Cell(0, 0), Cell(1, 0), Cell(-1, 1), Cell(0, 1))
            TetrominoType.Z -> listOf(Cell(-1, 0), Cell(0, 0), Cell(0, 1), Cell(1, 1))
            TetrominoType.J -> listOf(Cell(-1, 0), Cell(-1, 1), Cell(0, 1), Cell(1, 1))
            TetrominoType.L -> listOf(Cell(1, 0), Cell(-1, 1), Cell(0, 1), Cell(1, 1))
        }

        val rotated = if (type == TetrominoType.O) {
            shape
        } else {
            repeat(rotation % 4, shape) { blocks ->
                blocks.map { Cell(-it.y, it.x) }
            }
        }

        return rotated.map {
            Cell(it.x + position.x, it.y + position.y)
        }
    }
}

private fun <T> repeat(times: Int, value: T, action: (T) -> T): T {
    var result = value
    repeat(times) {
        result = action(result)
    }
    return result
}