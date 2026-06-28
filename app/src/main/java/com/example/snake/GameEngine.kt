package com.example.snake

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameEngine(application: Application) : AndroidViewModel(application) {
    enum class GameScreen { MENU, STARTED }

    private val _screen = MutableStateFlow(GameScreen.MENU)
    val screen: StateFlow<GameScreen> = _screen

    private val _gameState = MutableStateFlow(GameState(0, 1, Direction.RIGHT, 0, 0))
    val gameState: StateFlow<GameState> = _gameState

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
                Direction.UP    -> state.copy(y = state.y + 1)
                Direction.DOWN  -> state.copy(y = state.y - 1)
                Direction.LEFT  -> state.copy(x = state.x - 1)
                Direction.RIGHT -> state.copy(x = state.x + 1)
            }
        }
    }

    private suspend fun tick() {
        movePlayer()
        // checkWin()
        delay(500L)
    }
}