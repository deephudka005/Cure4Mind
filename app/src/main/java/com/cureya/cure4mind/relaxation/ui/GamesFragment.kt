package com.cureya.cure4mind.relaxation.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cureya.cure4mind.databinding.FragmentRelaxationGamesBinding
import com.cureya.cure4mind.relaxation.game.bouncingBall.MainScreenActivity
import com.cureya.cure4mind.relaxation.game.ticTacToe.view.GameActivity

class GamesFragment : Fragment() {

    private lateinit var binding: FragmentRelaxationGamesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRelaxationGamesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tictactoeCard.setOnClickListener {
            val intent = Intent(requireActivity(), GameActivity::class.java)
            startActivity(intent)
        }
        binding.bouncingBallCard.setOnClickListener {
            val intent = Intent(requireActivity(), MainScreenActivity::class.java)
            startActivity(intent)
        }
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}