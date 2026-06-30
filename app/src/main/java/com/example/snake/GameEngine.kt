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

private const val TAG = "GameEngine"
class GameEngine(application: Application) : AndroidViewModel(application) {
    enum class GameScreen { MENU, STARTED }

    private val _screen = MutableStateFlow(GameScreen.MENU)
    val screen: StateFlow<GameScreen> = _screen

    private val _gameState = MutableStateFlow(GameState(0, 1, Direction.RIGHT, 0, 0))
    val gameState: StateFlow<GameState> = _gameState

    private val gameGrid = GameGrid(GRID_SIZE)

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
    }

    private fun movePlayer() {
        _gameState.update { state ->
            when(state.direction) {
                Direction.UP    -> state.copy(y = state.y + MOVE_SPEED)
                Direction.DOWN  -> state.copy(y = state.y - MOVE_SPEED)
                Direction.LEFT  -> state.copy(x = state.x - MOVE_SPEED)
                Direction.RIGHT -> state.copy(x = state.x + MOVE_SPEED)
            }
        }
        Log.i(TAG, "movePlayer: New player Object: ${gameState.value}")
    }

    private suspend fun tick() {
        Log.i(TAG, "startGame: Ticking...")
        movePlayer()
        // checkWin()
        delay(500L)
    }
}