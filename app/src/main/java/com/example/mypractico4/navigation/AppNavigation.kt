package com.example.mypractico4.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.Composable
import com.example.mypractico4.presentation.screen.GameScreen
import com.example.mypractico4.presentation.screen.HomeScreen
import com.example.mypractico4.presentation.screen.LobbyScreen
import com.example.mypractico4.presentation.screen.ResultScreen
import com.example.mypractico4.presentation.viewmodel.GameViewModel


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val gameViewModel: GameViewModel = viewModel()
    val lobbyState by gameViewModel.lobbyState.collectAsState()

    LaunchedEffect(lobbyState.gameStarted) {
        if (lobbyState.gameStarted) {
            navController.navigate(Routes.GAME)
        }
    }

    LaunchedEffect(lobbyState.matchFinished) {
        if (lobbyState.matchFinished) {
            navController.navigate(Routes.RESULT)
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onCreateRoom = {
                    gameViewModel.createRoom()
                    navController.navigate(Routes.LOBBY)
                },
                onJoinRoom = { roomId ->
                    gameViewModel.joinRoom(roomId)
                    navController.navigate(Routes.LOBBY)
                }
            )
        }

        composable(Routes.LOBBY) {
            LobbyScreen(
                roomId = lobbyState.roomId,
                isWaiting = lobbyState.isWaiting,
                errorMessage = lobbyState.errorMessage
            )
        }

        composable(Routes.GAME) {
            GameScreen(
                roomId = lobbyState.roomId,
                viewModel = gameViewModel
            )
        }

        composable(Routes.RESULT) {
            val gameState by gameViewModel.gameState.collectAsState()

            ResultScreen(
                isWinner = lobbyState.victory,
                score = gameState.score,
                lines = gameState.lines,
                durationSeconds = gameViewModel.getDurationSeconds(),
                onBackHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(0)
                    }
                }
            )
        }
    }
}