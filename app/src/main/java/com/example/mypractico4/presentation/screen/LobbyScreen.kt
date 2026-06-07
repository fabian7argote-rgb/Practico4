package com.example.mypractico4.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LobbyScreen(
    roomId: String,
    isWaiting: Boolean,
    errorMessage: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Lobby")
            Spacer(modifier = Modifier.height(16.dp))
            Text("Código de sala: $roomId")
            Spacer(modifier = Modifier.height(16.dp))
            Text("Jugador 1: conectado")

            if (isWaiting) {
                Text("Jugador 2: esperando...")
            } else {
                Text("Jugador 2: conectado")
            }

            if (errorMessage.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Mensaje: $errorMessage")
            }
        }
    }
}