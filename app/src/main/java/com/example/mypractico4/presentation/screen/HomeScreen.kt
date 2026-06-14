package com.example.mypractico4.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onCreateRoom: () -> Unit,
    onJoinRoom: (String) -> Unit
) {
    var roomCode by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Tetris Duel ",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Juego Multijugador",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onCreateRoom,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crear Sala")
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = roomCode,
            onValueChange = {
                roomCode = it.uppercase()
            },
            label = {
                Text("Código de sala")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (roomCode.isNotBlank()) {
                    onJoinRoom(roomCode.trim())
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Unirse")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Ingrese un código para unirse a una partida.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}