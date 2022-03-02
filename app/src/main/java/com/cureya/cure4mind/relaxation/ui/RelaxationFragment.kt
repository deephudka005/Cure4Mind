package com.cureya.cure4mind.relaxation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.cureya.cure4mind.R
import com.cureya.cure4mind.databinding.FragmentRelaxationBinding

class RelaxationFragment : Fragment() {

    private lateinit var binding: FragmentRelaxationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRelaxationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.yogaFrame.setOnClickListener {
            findNavController().navigate(R.id.action_relaxationFragment_to_yogaFragment)
        }
        binding.gameFrame.setOnClickListener {
            findNavController().navigate(R.id.action_relaxationFragment_to_gamesFragment)
        }
        binding.musicFrame.setOnClickListener {
            findNavController().navigate(
                RelaxationFragmentDirections.actionRelaxationFragmentToMusicVideoFragment(
                    CONTENT_TYPE_MUSIC
                )
            )
        }
        binding.videoFrame.setOnClickListener {
            findNavController().navigate(
                RelaxationFragmentDirections.actionRelaxationFragmentToMusicVideoFragment(
                    CONTENT_TYPE_VIDEO
                )
            )
        }
    }

    companion object {
        const val CONTENT_TYPE_VIDEO = 44
        const val CONTENT_TYPE_MUSIC = 42
    }
}