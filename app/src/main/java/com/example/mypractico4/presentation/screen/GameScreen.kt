package com.example.mypractico4.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mypractico4.presentation.components.BoardCanvas
import com.example.mypractico4.presentation.components.NextPieceView
import com.example.mypractico4.presentation.viewmodel.GameViewModel

@Composable
fun GameScreen(
    roomId: String,
    viewModel: GameViewModel
) {
    val gameState by viewModel.gameState.collectAsState()
    val lobbyState by viewModel.lobbyState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startGameLoop()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Tetris Duel Online")
        Text("Sala: $roomId")

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text("Conexión: ${if (lobbyState.isConnected) "Conectado" else "Desconectado"}")
                Text("Oponente: ${lobbyState.opponentStatus}")
                Text("Referencia 37: Modo 37 activo")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.Top
        ) {
            BoardCanvas(gameState)

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text("Siguiente:")
                NextPieceView(gameState.nextPiece)

                Spacer(modifier = Modifier.height(16.dp))

                Text("Puntaje: ${gameState.score}")
                Text("Líneas: ${gameState.lines}")

                Spacer(modifier = Modifier.height(16.dp))

                if (gameState.isGameOver) {
                    Text("Partida finalizada")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row {
            Button(onClick = { viewModel.moveLeft() }) {
                Text("←")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = { viewModel.rotatePiece() }) {
                Text("↻")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = { viewModel.moveRight() }) {
                Text("→")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            Button(onClick = { viewModel.moveDown() }) {
                Text("↓")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = { viewModel.hardDrop() }) {
                Text("Caída")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { viewModel.leaveMatch() }
        ) {
            Text("Abandonar partida")
        }
    }
}