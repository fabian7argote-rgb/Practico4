package com.example.mypractico4.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypractico4.di.AppModule
import com.example.mypractico4.domain.model.Cell
import com.example.mypractico4.domain.model.GameState
import com.example.mypractico4.domain.model.TetrominoType
import com.example.mypractico4.domain.model.randomPiece
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import com.example.mypractico4.domain.logic.AttackLogic
import com.example.mypractico4.domain.logic.BoardLogic
import com.example.mypractico4.domain.logic.ScoreLogic

data class LobbyState(
    val roomId: String = "",
    val isConnected: Boolean = false,
    val isWaiting: Boolean = false,
    val gameStarted: Boolean = false,
    val errorMessage: String = "",
    val victory: Boolean = false,
    val matchFinished: Boolean = false,
    val opponentStatus: String = "Esperando oponente..."
)

class GameViewModel : ViewModel() {

    private val repository = AppModule.gameRepository

    private val _lobbyState = MutableStateFlow(LobbyState())
    val lobbyState: StateFlow<LobbyState> = _lobbyState

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState

    private var gameLoopJob: Job? = null
    private var turbo37Job: Job? = null//modo 37
    private var timerJob: Job? = null

    init {
        repository.connect()

        repository.onRoomCreated { roomId ->
            viewModelScope.launch {
                _lobbyState.value = _lobbyState.value.copy(
                    roomId = roomId,
                    isConnected = true,
                    isWaiting = true,
                    errorMessage = ""
                )
            }
        }

        repository.onGameStart {
            viewModelScope.launch {
                _lobbyState.value = _lobbyState.value.copy(
                    gameStarted = true,
                    isWaiting = false,
                    opponentStatus = "Oponente conectado"
                )
            }
        }

        repository.onErrorMessage { message ->
            viewModelScope.launch {
                _lobbyState.value = _lobbyState.value.copy(
                    errorMessage = message
                )
            }
        }

        repository.onOpponentDisconnected {
            viewModelScope.launch {
                _lobbyState.value = _lobbyState.value.copy(
                    errorMessage = "El oponente se desconectó",
                    victory = true,
                    matchFinished = true,
                    opponentStatus = "Oponente desconectado"
                )
                gameLoopJob?.cancel()
            }
        }

        repository.onReceiveAttack { garbageLines ->
            viewModelScope.launch {
                addGarbageLines(garbageLines)
            }
        }

        repository.onVictory {
            viewModelScope.launch {
                _lobbyState.value = _lobbyState.value.copy(
                    victory = true,
                    matchFinished = true
                )
                gameLoopJob?.cancel()
            }
        }
    }
    fun resetMatch() {
        gameLoopJob?.cancel()
        gameLoopJob = null

        _lobbyState.value = LobbyState()
        _gameState.value = GameState()

        repository.connect()
        timerJob?.cancel() //modo 37
        timerJob = null
        turbo37Job?.cancel()//modo 37
        turbo37Job = null
    }

    fun createRoom() {
        resetMatch()
        repository.createRoom()
    }

    fun joinRoom(roomId: String) {
        resetMatch()

        _lobbyState.value = _lobbyState.value.copy(
            roomId = roomId,
            isConnected = true,
            isWaiting = true,
            errorMessage = ""
        )

        repository.joinRoom(roomId)
    }

    fun moveLeft() {
        if (_gameState.value.isGameOver) return

        val state = _gameState.value
        val moved = BoardLogic.movePiece(state.currentPiece, dx = -1, dy = 0)

        if (BoardLogic.isValid(moved, state.board)) {
            _gameState.value = state.copy(currentPiece = moved)
        }
    }

    fun moveRight() {
        if (_gameState.value.isGameOver) return

        val state = _gameState.value
        val moved = BoardLogic.movePiece(state.currentPiece, dx = 1, dy = 0)

        if (BoardLogic.isValid(moved, state.board)) {
            _gameState.value = state.copy(currentPiece = moved)
        }
    }

    fun moveDown() {
    if (_gameState.value.isGameOver) return

    val state = _gameState.value
    val moved = BoardLogic.movePiece(state.currentPiece, dx = 0, dy = 1)

    if (BoardLogic.isValid(moved, state.board)) {
        _gameState.value = state.copy(currentPiece = moved)
    } else {
        lockPiece()
    }
    }
    fun rotatePiece() {
    if (_gameState.value.isGameOver) return

    val state = _gameState.value

    val rotated = state.currentPiece.copy(
        rotation = state.currentPiece.rotation + 1
    )

    if (BoardLogic.isValid(rotated, state.board)) {
        _gameState.value = state.copy(currentPiece = rotated)
    }
}
    fun hardDrop() {
    if (_gameState.value.isGameOver) return

    var state = _gameState.value
    var moved = BoardLogic.movePiece(state.currentPiece, dx = 0, dy = 1)

    while (BoardLogic.isValid(moved, state.board)) {
        _gameState.value = state.copy(currentPiece = moved)
        state = _gameState.value
        moved = BoardLogic.movePiece(state.currentPiece, dx = 0, dy = 1)
    }

    lockPiece()
}
    private fun lockPiece() {
    val state = _gameState.value

    val boardWithPiece = BoardLogic.lockPieceOnBoard(
        board = state.board,
        piece = state.currentPiece
    )

    val result = BoardLogic.clearLines(boardWithPiece)

    val attackLines = AttackLogic.calculateAttack(result.linesCleared)

    if (attackLines > 0 && _lobbyState.value.roomId.isNotBlank()) {
        repository.sendAttack(
            roomId = _lobbyState.value.roomId,
            garbageLines = attackLines
        )
    }

    val newPiece = state.nextPiece.copy(
        position = Cell(4, 0)
    )

    if (!BoardLogic.isValid(newPiece, result.board)) {
        loseGame(result.board)
        return
    }

    _gameState.value = state.copy(
        board = result.board,
        currentPiece = newPiece,
        nextPiece = randomPiece(),
        score = state.score + ScoreLogic.calculateScore(result.linesCleared),
        lines = state.lines + result.linesCleared
    )
}

    private fun addGarbageLines(amount: Int) {
    if (_gameState.value.isGameOver) return

    val state = _gameState.value

    val newBoard = BoardLogic.addGarbageLines(
        board = state.board,
        amount = amount
    )

    if (!BoardLogic.isValid(state.currentPiece, newBoard)) {
        loseGame(newBoard)
        return
    }

    _gameState.value = state.copy(
        board = newBoard
    )
    }

    private fun loseGame(board: List<List<TetrominoType?>>) {
        _gameState.value = _gameState.value.copy(
            board = board,
            isGameOver = true
        )

        _lobbyState.value = _lobbyState.value.copy(
            victory = false,
            matchFinished = true
        )

        gameLoopJob?.cancel()

        if (_lobbyState.value.roomId.isNotBlank()) {
            repository.sendGameOver(_lobbyState.value.roomId)
        }
    }

    fun startGameLoop() {
        if (gameLoopJob != null) return

        startTimer()
        startTurbo37Loop()

        gameLoopJob = viewModelScope.launch {
            while (!_gameState.value.isGameOver) {
                val delayTime = if (_gameState.value.turbo37Active) {
                    441L
                } else {
                    700L
                }

                delay(delayTime)
                moveDown()
            }
        }
    }
    private fun startTimer() {
        if (timerJob != null) return

        timerJob = viewModelScope.launch {
            while (!_gameState.value.isGameOver) {
                delay(1000)

                _gameState.value = _gameState.value.copy(
                    elapsedSeconds = _gameState.value.elapsedSeconds + 1
                )
            }
        }
    }
    private fun startTurbo37Loop() {
        if (turbo37Job != null) return

        turbo37Job = viewModelScope.launch {
            while (!_gameState.value.isGameOver) {
                delay(37_000)

                if (_gameState.value.isGameOver) return@launch

                _gameState.value = _gameState.value.copy(
                    turbo37Active = true
                )

                delay(5_000)

                _gameState.value = _gameState.value.copy(
                    turbo37Active = false
                )
            }
        }
    }
    //aqui acaba el modo 37


    override fun onCleared() {
        super.onCleared()
        gameLoopJob?.cancel()
        timerJob?.cancel()//modo 37
        turbo37Job?.cancel()//modo 37
        repository.disconnect()
    }
    fun getDurationSeconds(): Long {
        return (System.currentTimeMillis() - _gameState.value.startTime) / 1000
    }
    fun leaveMatch() {
        _lobbyState.value = _lobbyState.value.copy(
            victory = false,
            matchFinished = true,
            opponentStatus = "Abandonaste la partida"
        )

        _gameState.value = _gameState.value.copy(
            isGameOver = true
        )

        gameLoopJob?.cancel()

        if (_lobbyState.value.roomId.isNotBlank()) {
            repository.sendGameOver(_lobbyState.value.roomId)
        }
    }
}