package com.example.snake

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import kotlin.math.log

private const val TAG = "SnakeView"
class SnakeView(context: Context, attrs: AttributeSet) : View(context, attrs), GestureDetector.OnGestureListener {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }

    private val squareSize = 50
    private val squarePaint = Paint().apply {
        color = Color.BLACK
    }

    private val applePaint = Paint().apply {
        color = Color.RED
    }

    private var state: GameState? = null
    private val gestureDetector = GestureDetector(context, this)
    var onDirectionChanged: ((Direction) -> Unit)? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val currentState = state ?: return

        val cellWidth = width.toFloat() / (GRID_SIZE - 2)
        val cellHeight = height.toFloat() / (GRID_SIZE - 2)

        canvas.drawColor(Color.WHITE)
        Log.i(TAG, "onDraw: SNAKEBODY: ${currentState.body}")
        for (segment in currentState.body) {
            Log.i(TAG, "onDraw: Drawing segment at ${segment.x}, ${segment.y}")
            drawSegment(canvas, segment.x.toFloat(), segment.y.toFloat(), cellWidth, cellHeight)
        }
        Log.i(TAG, "onDraw: Apple Coords: ${currentState.apple.x.toFloat()}, ${currentState.apple.y.toFloat()}")
        drawApple(canvas, currentState.apple.x.toFloat(), currentState.apple.y.toFloat(), cellWidth, cellHeight)
    }

    fun drawSegment(canvas: Canvas, x: Float, y: Float, cellWidth: Float, cellHeight: Float) {
        val actualX = (x - 1) * cellWidth
        val actualY = (y - 1) * cellHeight
        canvas.drawRect(
            actualX,
            actualY,
            actualX + cellWidth,
            actualY + cellHeight,
            squarePaint
        )
    }

    fun drawApple(canvas: Canvas, x: Float, y: Float, cellWidth: Float, cellHeight: Float) {
        val actualX = (x - 1) * cellWidth
        val actualY = (y - 1) * cellHeight
        canvas.drawRect(
            actualX,
            actualY,
            actualX + cellWidth,
            actualY + cellHeight,
            applePaint
        )
    }

    fun render(gameState: GameState) {
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
        Log.i(TAG, "onFling: dx: $dx, dy: $dy")
        val direction: Direction = if (abs(dx) > abs(dy)) {
            if (dx > 0) {
                Direction.RIGHT
            } else {
                Direction.LEFT
            }
        } else {
            if (dy > 0) {
                Direction.DOWN
            } else {
                Direction.UP
            }
        }
        onDirectionChanged?.invoke(direction)
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

