package com.example.mypractico4.navigation

object Routes {
    const val HOME = "home"
    const val LOBBY = "lobby"
    const val GAME = "game"
    const val RESULT = "result"

    fun lobby(roomId: String): String {
        return LOBBY
    }

}