package com.example.mypractico4.domain.logic

object ScoreLogic {

    fun calculateScore(linesCleared: Int): Int {
        return when (linesCleared) {
            1 -> 100
            2 -> 300
            3 -> 500
            4 -> 800
            else -> 0
        }
    }
}