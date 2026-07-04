package com.example.snake

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max

private const val TAG = "GameEngine"
class GameEngine(application: Application) : AndroidViewModel(application) {
    enum class GameScreen { MENU, STARTED }

    private val _screen = MutableStateFlow(GameScreen.MENU)
    val screen: StateFlow<GameScreen> = _screen

    private val _gameState = MutableStateFlow(GameState(1, Direction.RIGHT, listOf(Point(1,1))), )
    val gameState: StateFlow<GameState> = _gameState

    private val gameGrid = GameGrid(GRID_SIZE)

    var highscore = 0
    /*
    Potential danger is this would trigger a render before tick() is called. need a queue system instead
     */
    fun changeDirection(newDirection: Direction) {
        _gameState.update { currentState ->
            currentState.copy(direction = newDirection)
        }
    }

    var gameJob: Job? = null

    fun startGame() {
        Log.i(TAG, "startGame: Starting game")
        _screen.value = GameScreen.STARTED
        if (gameJob != null) return

        gameJob = viewModelScope.launch {
            while (screen.value == GameScreen.STARTED) {
                tick()
                delay(500L)
            }
        }
    }

    fun stopGame() {
        _screen.value = GameScreen.MENU
        gameJob?.cancel()
        gameJob = null
        resetState()
    }

    fun resetState() {
        highscore = max(highscore, gameState.value.snakeLen)
        _gameState.update { state ->
            state.copy(snakeLen = 1, direction = Direction.RIGHT, body = listOf(Point(1,1)))
        }
    }

    private fun movePlayer() {
        // call ui move which would also check collision
        var newX = _gameState.value.body[0].x
        var newY = _gameState.value.body[0].y
        _gameState.update { state ->
            when(state.direction) {
                Direction.UP    -> newY -= 1
                Direction.DOWN  -> newY += 1
                Direction.LEFT  -> newX -= 1
                Direction.RIGHT -> newX += 1
            }
            if (checkCollisionOnNextMove(newX, newY)) return
            val newHead = Point(newX, newY)
            val newBody = listOf(newHead) + state.body.take(state.snakeLen - 1)
            state.copy(body = newBody)
        }
        Log.i(TAG, "movePlayer: New player body: ${gameState.value.body}")
    }

    private fun checkCollisionOnNextMove(nextX: Int, nextY: Int): Boolean {
        Log.i(TAG, "checkCollisionOnNextMove: nextX: $nextX")
        Log.i(TAG, "checkCollisionOnNextMove: nextY: $nextY")
        Log.i(TAG, "checkCollisionOnNextMove: Direction: ${_gameState.value.direction}")
        if(gameGrid.checkCollision(nextX, nextY)) {
            Log.i(TAG, "checkCollisionOnNextMove: stopping game")
            stopGame()
            return true
        }
        return false
    }

    private suspend fun tick() {
        Log.i(TAG, "startGame: Ticking...")
        movePlayer()
        // checkWin()
        delay(500L)
    }
}