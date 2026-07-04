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

    private val gameGrid = GameGrid(GRID_SIZE)

    private val _gameState = MutableStateFlow(GameState(Direction.RIGHT, listOf(Point(1,1)), Point(5, 5))) // need to init apple after
    val gameState: StateFlow<GameState> = _gameState
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

        spawnApple()

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
        highscore = max(highscore, gameState.value.body.size)
        _gameState.update { state ->
            state.copy(direction = Direction.RIGHT, body = listOf(Point(1,1)))
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
            tryCollectApple(newX, newY, _gameState.value.apple.x, _gameState.value.apple.y)
            val newHead = Point(newX, newY)
            val newBody = listOf(newHead) + state.body.take(state.body.size - 1)
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

    private fun tryCollectApple(playerX: Int, playerY: Int, appleX: Int, appleY: Int) {
        if (playerX == appleX && playerY == appleY) eatApple()
    }

    /*
    * Extending body means creating a new point at current x,y
    * Functions the same as moving the snake but we don't remove the last segment
    * */
    private fun eatApple() {
        Log.i(TAG, "eatApple: Increasing body length")
        _gameState.update { state ->
            val newSegment = Point(state.body[0].x, state.body[0].y)
            val newBody = listOf(newSegment) + state.body
            state.copy(body = newBody)
        }
        spawnApple()
    }

    private fun spawnApple() {
        _gameState.update { state ->
            val newApple = gameGrid.spawnApple(state.body)
            state.copy(apple = newApple)
        }
    }

    private suspend fun tick() {
        Log.i(TAG, "startGame: Ticking...")
        movePlayer()
        // checkWin()
        delay(250L)
    }
}