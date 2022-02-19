package com.cureya.cure4mind.relaxation.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.cureya.cure4mind.databinding.FragmentRelaxationMusicBinding
import com.cureya.cure4mind.relaxation.viewModel.MusicViewModel
import com.cureya.cure4mind.relaxation.viewModel.MusicViewModelFactory

class MusicFragment : Fragment() {

    private lateinit var binding: FragmentRelaxationMusicBinding

    private val navArgument: MusicFragmentArgs by navArgs()
    private var position = 0

    private val musicViewModel: MusicViewModel by viewModels {
        MusicViewModelFactory(
            position,
            binding.musicSeekbar,
            binding.musicTimeTotal,
            binding.musicTimeCount,
            binding.musicHeading,
            binding.musicImage,
            binding.progressBar
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRelaxationMusicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        position = navArgument.itemPosition

        musicViewModel.createContentList()

        binding.musicPlay.setOnClickListener { musicViewModel.handleMusicState(it as ImageView) }

        binding.musicNext.setOnClickListener {
            musicViewModel.playNextMusic()
        }

        binding.musicPrevious.setOnClickListener {
            musicViewModel.playPreviousMusic()
        }

        // seekbar music control
        binding.musicSeekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2) { musicViewModel.setSeekbarUserProgress(p1) }
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        binding.backButton.setOnClickListener { findNavController().navigateUp() }
    }

    /* private fun playMusicWithUrl(url: String, title: String, imgUrl: String) {
        musicViewModel.playMusic(url)

        binding.musicHeading.text = title
        binding.musicImage.load(imgUrl)
        binding.progressBar.visibility = View.GONE
        musicViewModel.setSeekBar(
            binding.musicTimeTotal,
            binding.musicSeekbar
        )
        musicViewModel.updateTimer(
            binding.musicTimeCount
        )
    } */

    override fun onStop() {
        musicViewModel.releaseMediaPlayer()
        super.onStop()
    }
}