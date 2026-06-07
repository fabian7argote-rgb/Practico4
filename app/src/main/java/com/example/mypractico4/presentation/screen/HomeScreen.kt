package com.example.mypractico4.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onCreateRoom: () -> Unit,
    onJoinRoom: (String) -> Unit
) {
    var roomCode by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Tetris Duel Online")

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onCreateRoom,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear sala")
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = roomCode,
                onValueChange = { roomCode = it.uppercase() },
                label = { Text("Código de sala") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (roomCode.isNotBlank()) {
                        onJoinRoom(roomCode)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Unirse")
            }
        }
    }
}