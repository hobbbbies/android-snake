package com.example.snake

enum class Direction { UP, DOWN, LEFT, RIGHT }

data class GameState(var highscore: Int, var snakeLen: Int, var direction: Direction, var x: Int, var y: Int)


