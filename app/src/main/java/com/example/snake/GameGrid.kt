package com.example.snake

import android.util.Log

private const val TAG = "GameGrid"
private const val TILE = '-'
private const val WALL = '#'
private const val APPLE ='@'
private const val PLAYER = 'o'
class GameGrid(gridSize: Int) {
    private val game_grid = Array(gridSize) { Array(gridSize) {'-'} }

    init {
        generateGrid()
        spawnApple()
        printGrid()
    }

    // mostly for debugging
    private fun printGrid() {
        val sb = StringBuilder("\n")
        for (row in game_grid) {
            sb.append(row.joinToString(" ")).append("\n")
        }
        Log.i(TAG, sb.toString())
    }

    private fun generateGrid() {
        val gridSize = game_grid.size
        for (i in 0..< gridSize) {
            for (j in 0 ..< gridSize) {
                if (i == 0 || i == gridSize-1 || j == 0 || j == gridSize-1) {
                    game_grid[i][j] = WALL
                } else {
                    game_grid[i][j] = TILE
                }
            }
        }
        game_grid[1][1] = PLAYER
    }

    private fun spawnApple() {
        val gridSize = game_grid.size
        val x = (1..gridSize-1).random()
        val y = (1..gridSize-1).random()
        Log.i(TAG, "spawnApple: $x, $y ")
        if (game_grid[x][y] == PLAYER) return spawnApple()
        game_grid[y][x] = APPLE
    }

    fun checkCollision(x: Int, y: Int): Boolean {
        if (x >= GRID_SIZE || y >= GRID_SIZE || x < 0 || y < 0) {
            Log.i(TAG, "checkCollision: returning here")
            return false
        }
        Log.i(TAG, "checkCollision: ${game_grid[x][y] != TILE}. Game_grid = ${game_grid[x][y]}")
        return game_grid[x][y] != TILE
    }

    fun movePlayer(x: Int, y: Int, direction: Direction): Boolean {
        var nextX = x
        var nextY = y
        when (direction) {
            Direction.UP -> nextY--
            Direction.DOWN -> nextY++
            Direction.LEFT -> nextX--
            Direction.RIGHT -> nextX++
        }
        if (checkCollision(nextX, nextY)) {
            return false
        }
        // check for apple
        game_grid[nextY][nextX] = PLAYER

        return true
    }
}