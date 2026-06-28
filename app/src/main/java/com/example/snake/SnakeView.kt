package com.example.snake

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

class SnakeView(context: Context) : View(context), GestureDetector.OnGestureListener {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }

    private val squareSize = 50
    private val squarePaint = Paint().apply {
        color = Color.BLACK
    }
    private val moveSpeed = 1
    private var state: GameEngine.GameState? = null
    private val gestureDetector = GestureDetector(context, this)

    var onDirectionChanged: ((Direction) -> Unit)? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var dx = 0
        var dy = 0
        // up and down may be reversed
        when(state?.direction) {
            Direction.UP -> dy = moveSpeed
            Direction.DOWN -> dy = -1*moveSpeed
            Direction.RIGHT -> dx = moveSpeed
            Direction.LEFT -> dx = -1*moveSpeed
            null -> {
                println("No direction set, returning")
                return
            }
        }

        canvas.drawColor(Color.WHITE)
        canvas.drawRect(
            x,
            y,
            x + squareSize,
            y + squareSize,
            squarePaint
        )
    }

    fun render(gameState: GameEngine.GameState) {
        this.state = gameState
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        if (e1 == null || onDirectionChanged == null) return false

        val dx = e2.x - e1.x
        val dy = e2.y - e1.y

        if (dx > 0) {
            onDirectionChanged?.invoke(Direction.RIGHT)
        } else if (dx < 0) {
            onDirectionChanged?.invoke(Direction.LEFT)
        } else if (dy > 0) {
            onDirectionChanged?.invoke(Direction.UP)
        } else if (dy < 0) {
            onDirectionChanged?.invoke(Direction.DOWN)
        } else {
            println("No direction delta!")
            return false
        }

        println("Swipe detected")
        return true
    }

    override fun onDown(p0: MotionEvent): Boolean {
        return true
    }

    override fun onLongPress(p0: MotionEvent) {
        return
    }

    override fun onScroll(p0: MotionEvent?, p1: MotionEvent, p2: Float, p3: Float): Boolean {
        return true
    }

    override fun onShowPress(event: MotionEvent) {
        return
    }

    override fun onSingleTapUp(event: MotionEvent): Boolean {
        return true
    }
}

