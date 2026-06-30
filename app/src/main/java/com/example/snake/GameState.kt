package com.example.snake

import kotlin.text.get

const val GRID_SIZE = 20

const val MOVE_SPEED = 1

enum class Direction { UP, DOWN, LEFT, RIGHT }

data class GameState(var highscore: Int, var snakeLen: Int, var direction: Direction, var x: Int, var y: Int)
