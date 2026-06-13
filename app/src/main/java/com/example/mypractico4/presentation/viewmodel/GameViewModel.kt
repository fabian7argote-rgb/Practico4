package com.example.mypractico4.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypractico4.di.AppModule
import com.example.mypractico4.domain.model.Cell
import com.example.mypractico4.domain.model.GameState
import com.example.mypractico4.domain.model.Tetromino
import com.example.mypractico4.domain.model.TetrominoType
import com.example.mypractico4.domain.model.randomPiece
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay


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

data class ClearResult(
    val board: List<List<TetrominoType?>>,
    val linesCleared: Int
)

class GameViewModel : ViewModel() {

    private val repository = AppModule.gameRepository

    private val _lobbyState = MutableStateFlow(LobbyState())
    val lobbyState: StateFlow<LobbyState> = _lobbyState

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState

    private var gameLoopJob: Job? = null

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
        if (!_gameState.value.isGameOver) {
            movePiece(dx = -1, dy = 0)
        }
    }

    fun moveRight() {
        if (!_gameState.value.isGameOver) {
            movePiece(dx = 1, dy = 0)
        }
    }

    fun moveDown() {
        if (_gameState.value.isGameOver) return

        if (!movePiece(dx = 0, dy = 1)) {
            lockPiece()
        }
    }

    fun rotatePiece() {
        if (_gameState.value.isGameOver) return

        val state = _gameState.value

        val rotated = state.currentPiece.copy(
            rotation = state.currentPiece.rotation + 1
        )

        if (isValid(rotated, state.board)) {
            _gameState.value = state.copy(currentPiece = rotated)
        }
    }

    fun hardDrop() {
        if (_gameState.value.isGameOver) return

        while (movePiece(dx = 0, dy = 1)) {
        }

        lockPiece()
    }

    private fun movePiece(dx: Int, dy: Int): Boolean {
        val state = _gameState.value

        val moved = state.currentPiece.copy(
            position = Cell(
                x = state.currentPiece.position.x + dx,
                y = state.currentPiece.position.y + dy
            )
        )

        return if (isValid(moved, state.board)) {
            _gameState.value = state.copy(currentPiece = moved)
            true
        } else {
            false
        }
    }

    private fun isValid(
        piece: Tetromino,
        board: List<List<TetrominoType?>>
    ): Boolean {
        return piece.blocks().all { cell ->
            cell.x in 0 until 10 &&
                    cell.y in 0 until 20 &&
                    board[cell.y][cell.x] == null
        }
    }

    private fun lockPiece() {
        val state = _gameState.value

        val newBoard = state.board.map { row ->
            row.toMutableList()
        }.toMutableList()

        state.currentPiece.blocks().forEach { cell ->
            if (cell.y in 0 until 20 && cell.x in 0 until 10) {
                newBoard[cell.y][cell.x] = state.currentPiece.type
            }
        }

        val result = clearLines(newBoard)

        val attackLines = calculateAttack(result.linesCleared)

        if (attackLines > 0 && _lobbyState.value.roomId.isNotBlank()) {
            repository.sendAttack(
                roomId = _lobbyState.value.roomId,
                garbageLines = attackLines
            )
        }

        val newPiece = state.nextPiece.copy(
            position = Cell(4, 0)
        )

        if (!isValid(newPiece, result.board)) {
            loseGame(result.board)
            return
        }

        _gameState.value = state.copy(
            board = result.board,
            currentPiece = newPiece,
            nextPiece = randomPiece(),
            score = state.score + calculateScore(result.linesCleared),
            lines = state.lines + result.linesCleared
        )
    }

    private fun clearLines(
        board: MutableList<MutableList<TetrominoType?>>
    ): ClearResult {
        val remainingRows = board.filter { row ->
            row.any { cell -> cell == null }
        }

        val linesCleared = 20 - remainingRows.size

        val emptyRows = List(linesCleared) {
            List<TetrominoType?>(10) { null }
        }

        val newBoard = emptyRows + remainingRows

        return ClearResult(
            board = newBoard,
            linesCleared = linesCleared
        )
    }

    private fun calculateAttack(linesCleared: Int): Int {
        return when (linesCleared) {
            2 -> 1
            3 -> 2
            4 -> 4
            else -> 0
        }
    }

    private fun calculateScore(linesCleared: Int): Int {
        return when (linesCleared) {
            1 -> 100
            2 -> 300
            3 -> 500
            4 -> 800
            else -> 0
        }
    }

    private fun addGarbageLines(amount: Int) {
        if (_gameState.value.isGameOver) return

        val state = _gameState.value
        val currentBoard = state.board.toMutableList()

        repeat(amount) {
            val hole = (0 until 10).random()

            val garbageRow = List<TetrominoType?>(10) { index ->
                if (index == hole) null else TetrominoType.Z
            }

            currentBoard.removeAt(0)
            currentBoard.add(garbageRow)
        }

        if (!isValid(state.currentPiece, currentBoard)) {
            loseGame(currentBoard)
            return
        }

        _gameState.value = state.copy(
            board = currentBoard
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

        gameLoopJob = viewModelScope.launch {
            while (!_gameState.value.isGameOver) {
                delay(700)
                moveDown()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        gameLoopJob?.cancel()
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