package com.example.snake

const val GRID_SIZE = 20

const val MOVE_SPEED = 1

enum class Direction { UP, DOWN, LEFT, RIGHT }

data class Point(val x: Int, val y: Int)

typealias Body = List<Point>

data class GameState(var snakeLen: Int, var direction: Direction, var body: Body, var apple: Point)

