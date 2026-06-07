package com.example.mypractico4.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ResultScreen(
    isWinner: Boolean,
    score: Int,
    lines: Int,
    durationSeconds: Long,
    onBackHome: () -> Unit
) {
    val minutes = durationSeconds / 60
    val seconds = durationSeconds % 60

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isWinner) "Ganaste" else "Perdiste"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Puntaje: $score")
            Text("Líneas eliminadas: $lines")
            Text("Duración: ${minutes}:${seconds.toString().padStart(2, '0')}")

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = onBackHome) {
                Text("Volver al inicio")
            }
        }
    }
}