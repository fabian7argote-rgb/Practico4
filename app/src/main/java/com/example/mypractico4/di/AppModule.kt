package com.example.mypractico4.di

import com.example.mypractico4.data.remote.SocketManager
import com.example.mypractico4.data.repository.GameRepository

object AppModule {

    private val socketManager: SocketManager by lazy {
        SocketManager()
    }

    val gameRepository: GameRepository by lazy {
        GameRepository(socketManager)
    }
}