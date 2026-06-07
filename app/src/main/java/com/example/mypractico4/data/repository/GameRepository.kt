package com.example.mypractico4.data.repository

import com.example.mypractico4.data.remote.SocketManager

class GameRepository(
    private val socketManager: SocketManager
) {

    fun connect() {
        socketManager.connect()
    }

    fun disconnect() {
        socketManager.disconnect()
    }

    fun createRoom() {
        socketManager.createRoom()
    }

    fun joinRoom(roomId: String) {
        socketManager.joinRoom(roomId)
    }

    fun sendAttack(roomId: String, garbageLines: Int) {
        socketManager.sendAttack(roomId, garbageLines)
    }

    fun sendGameOver(roomId: String) {
        socketManager.sendGameOver(roomId)
    }

    fun onRoomCreated(callback: (String) -> Unit) {
        socketManager.onRoomCreated(callback)
    }

    fun onGameStart(callback: () -> Unit) {
        socketManager.onGameStart(callback)
    }

    fun onErrorMessage(callback: (String) -> Unit) {
        socketManager.onErrorMessage(callback)
    }

    fun onOpponentDisconnected(callback: () -> Unit) {
        socketManager.onOpponentDisconnected(callback)
    }

    fun onReceiveAttack(callback: (Int) -> Unit) {
        socketManager.onReceiveAttack(callback)
    }

    fun onVictory(callback: () -> Unit) {
        socketManager.onVictory(callback)
    }
}