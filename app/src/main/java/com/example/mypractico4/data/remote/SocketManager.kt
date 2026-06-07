package com.example.mypractico4.data.remote


import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject

class SocketManager {

    private var socket: Socket? = null

    fun connect() {
        if (socket == null) {
            socket = IO.socket("http://192.168.0.101:3000")
        }

        socket?.connect()
    }

    fun disconnect() {
        socket?.disconnect()
    }

    fun createRoom() {
        socket?.emit("create_room")
    }

    fun joinRoom(roomId: String) {
        val data = JSONObject()
        data.put("roomId", roomId)
        socket?.emit("join_room", data)
    }

    fun onRoomCreated(callback: (String) -> Unit) {
        socket?.on("room_created") { args ->
            val data = args[0] as JSONObject
            val roomId = data.getString("roomId")
            callback(roomId)
        }

    }

    fun onGameStart(callback: () -> Unit) {
        socket?.on("game_start") {
            callback()
        }
    }

    fun onErrorMessage(callback: (String) -> Unit) {
        socket?.on("error_message") { args ->
            val data = args[0] as JSONObject
            val message = data.getString("message")
            callback(message)
        }
    }

    fun onOpponentDisconnected(callback: () -> Unit) {
        socket?.on("opponent_disconnected") {
            callback()
        }
    }
    fun sendAttack(roomId: String, garbageLines: Int) {
        val data = JSONObject()
        data.put("roomId", roomId)
        data.put("garbageLines", garbageLines)
        socket?.emit("send_attack", data)
    }

    fun onReceiveAttack(callback: (Int) -> Unit) {
        socket?.on("receive_attack") { args ->
            val data = args[0] as JSONObject
            val garbageLines = data.getInt("garbageLines")
            callback(garbageLines)
        }
    }

    fun sendGameOver(roomId: String) {
        val data = JSONObject()
        data.put("roomId", roomId)
        socket?.emit("game_over", data)
    }

    fun onVictory(callback: () -> Unit) {
        socket?.on("victory") {
            callback()
        }
    }
}