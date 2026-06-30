package com.example.snake

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

private const val TAG = "GameFragment"
class GameFragment: Fragment(R.layout.fragment_game) {
    private val gameEngine: GameEngine by activityViewModels()
    private var highscore: Int? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val snakeView = view.findViewById<SnakeView>(R.id.snakeView)
        snakeView.onDirectionChanged = { direction ->
            gameEngine.changeDirection(direction)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Each launch call is a coroutine that sits there waiting for new state
                launch {
                    gameEngine.screen.collect { screen ->
                        navigateState(screen)
                    }
                }
                launch {
                    gameEngine.gameState.collect { gameState ->
                        Log.i(TAG, "onViewCreated: Calling Render")
                        snakeView.render(gameState)
                    }
                }
            }
        }
    }



    fun navigateState(screen: GameEngine.GameScreen) {
        when (screen) {
            GameEngine.GameScreen.MENU -> showMenu()
            GameEngine.GameScreen.STARTED -> startGame()
        }
    }

    fun showMenu() {
        childFragmentManager.beginTransaction()
            .replace(R.id.menuFragmentContainer, MenuFragment())
            .setReorderingAllowed(true)
            .commit()
    }

    fun startGame() {
        val fragment = childFragmentManager.findFragmentById(R.id.menuFragmentContainer) ?: return
        Log.i(TAG, "startGame: Inside of startGame")
        childFragmentManager.beginTransaction()
            .remove(fragment)
            .commit()
    }
}

