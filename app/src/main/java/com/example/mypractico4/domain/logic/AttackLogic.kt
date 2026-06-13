package com.example.mypractico4.domain.logic

object AttackLogic {

    fun calculateAttack(linesCleared: Int): Int {
        return when (linesCleared) {
            2 -> 1
            3 -> 2
            4 -> 4
            else -> 0
        }
    }
}