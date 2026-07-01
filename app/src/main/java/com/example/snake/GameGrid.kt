package com.example.snake

import android.util.Log

private const val TAG = "GameGrid"
private const val TILE = '-'
private const val WALL = '#'
private const val APPLE ='@'
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
    }

    private fun spawnApple() {
        val gridSize = game_grid.size
        val range = (gridSize-2) * (gridSize-2)
        val appleCoords = (1..range).random()
        val i = appleCoords / gridSize
        val j = appleCoords % gridSize
        Log.i(TAG, "spawnApple: $i, $j ")
        game_grid[i][j] = APPLE
    }

    fun checkCollision(x: Int, y: Int): Boolean {
        if (x >= GRID_SIZE || y >= GRID_SIZE || x < 0 || y < 0) return false
        return game_grid[x][y] != TILE
    }
}