package com.example.mypractico4.domain.logic

import com.example.mypractico4.domain.model.Cell
import com.example.mypractico4.domain.model.Tetromino
import com.example.mypractico4.domain.model.TetrominoType

data class ClearResult(
    val board: List<List<TetrominoType?>>,
    val linesCleared: Int
)

object BoardLogic {

    fun isValid(
        piece: Tetromino,
        board: List<List<TetrominoType?>>
    ): Boolean {
        return piece.blocks().all { cell ->
            cell.x in 0 until 10 &&
                    cell.y in 0 until 20 &&
                    board[cell.y][cell.x] == null
        }
    }

    fun movePiece(
        piece: Tetromino,
        dx: Int,
        dy: Int
    ): Tetromino {
        return piece.copy(
            position = Cell(
                x = piece.position.x + dx,
                y = piece.position.y + dy
            )
        )
    }

    fun lockPieceOnBoard(
        board: List<List<TetrominoType?>>,
        piece: Tetromino
    ): MutableList<MutableList<TetrominoType?>> {
        val newBoard = board.map { row ->
            row.toMutableList()
        }.toMutableList()

        piece.blocks().forEach { cell ->
            if (cell.y in 0 until 20 && cell.x in 0 until 10) {
                newBoard[cell.y][cell.x] = piece.type
            }
        }

        return newBoard
    }

    fun clearLines(
        board: MutableList<MutableList<TetrominoType?>>
    ): ClearResult {
        val remainingRows = board.filter { row ->
            row.any { cell -> cell == null }
        }

        val linesCleared = 20 - remainingRows.size

        val emptyRows = List(linesCleared) {
            List<TetrominoType?>(10) { null }
        }

        return ClearResult(
            board = emptyRows + remainingRows,
            linesCleared = linesCleared
        )
    }

    fun addGarbageLines(
        board: List<List<TetrominoType?>>,
        amount: Int
    ): List<List<TetrominoType?>> {
        val currentBoard = board.toMutableList()

        repeat(amount) {
            val hole = (0 until 10).random()

            val garbageRow = List<TetrominoType?>(10) { index ->
                if (index == hole) null
                else TetrominoType.Z
            }

            currentBoard.removeAt(0)
            currentBoard.add(garbageRow)
        }

        return currentBoard
    }
}