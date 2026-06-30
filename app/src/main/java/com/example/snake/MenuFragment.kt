package com.example.snake

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

private const val TAG = "MenuFragment"
class MenuFragment : Fragment(R.layout.fragment_menu) {
    private val gameEngine: GameEngine by activityViewModels()
    var button: Button? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button = view.findViewById<Button>(R.id.startButton)

        Log.i(TAG, "onViewCreated: Created")
        button?.setOnClickListener {
            Log.i(TAG, "onViewCreated: Start button clicked")
            gameEngine.startGame()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        button = null
    }
}