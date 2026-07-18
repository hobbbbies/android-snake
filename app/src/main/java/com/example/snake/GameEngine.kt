package com.example.snake

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max

private const val TAG = "GameEngine"
class GameEngine(application: Application) : AndroidViewModel(application) {
    enum class GameScreen { MENU, STARTED }

    private val _screen = MutableStateFlow(GameScreen.MENU)
    val screen = _screen.asStateFlow()

    private val gameGrid = GameGrid(GRID_SIZE)

    private val _gameState = MutableStateFlow(GameState(Direction.RIGHT, listOf(Point(1,1)), Point(5, 5))) // need to init apple after
    val gameState = _gameState.asStateFlow()

    // localState will act as the main configurer for state flow, and it will be applied to gameState on every tick()
    private var localState: GameState = _gameState.value // starts as a shallow copy    
    var highscore = 0

    fun changeDirection(newDirection: Direction) {
        when(newDirection) { // Make sure we aren't going in the opposite direction
            Direction.UP -> if (localState.direction == Direction.DOWN) return
            Direction.DOWN -> if (localState.direction == Direction.UP) return
            Direction.LEFT -> if (localState.direction == Direction.RIGHT) return
            Direction.RIGHT -> if (localState.direction == Direction.LEFT) return
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
                delay(250L)
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

    private fun moveSegments() {
        var newX = localState.body[0].x
        var newY = localState.body[0].y
        when(localState.direction) {
            Direction.UP    -> newY -= 1
            Direction.DOWN  -> newY += 1
            Direction.LEFT  -> newX -= 1
            Direction.RIGHT -> newX += 1
        }
        if (checkCollisionOnNextMove(newX, newY)) return
        tryCollectApple(newX, newY, localState.apple.x, localState.apple.y)
        val newHead = Point(newX, newY)
        val newBody = listOf(newHead) + localState.body.take(localState.body.size - 1)
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
        addSegment()
        Log.i(TAG, "eatApple: ATE APPLE. NEW BODY: ${_gameState.value.body}")
        spawnApple()
    }

    private fun addSegment() {
        val tail = localState.body[localState.body.size-1]
        var newX = tail.x
        var newY = tail.y
        when(localState.direction) { // all values reversed from movePlayer
            Direction.UP    -> newY += 1
            Direction.DOWN  -> newY -= 1
            Direction.LEFT  -> newX += 1
            Direction.RIGHT -> newX -= 1
        }
        val newTail = Point(newX, newY)
        val newBody = localState.body + newTail
        localState.body = newBody
    }

    private fun spawnApple() {
        val newApple = gameGrid.spawnApple(localState.body)
        localState.apple = newApple
    }

    private suspend fun tick() {
        Log.i(TAG, "startGame: Ticking...")
        _gameState.update { state ->
            state.copy(direction = localState.direction, body = localState.body, apple = localState.apple)
        }
        // checkWin()
        delay(250L)
    }
}