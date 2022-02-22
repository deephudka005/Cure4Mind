package com.cureya.cure4mind.relaxation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.bumptech.glide.Glide
import com.cureya.cure4mind.R
import com.cureya.cure4mind.databinding.FragmentRelaxationMusicBinding
import com.cureya.cure4mind.relaxation.viewModel.MusicViewModel
import com.cureya.cure4mind.relaxation.viewModel.MusicViewModel.Companion.CHILD_FAVOURITE_MUSIC
import com.cureya.cure4mind.relaxation.viewModel.MusicViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.concurrent.TimeUnit
import kotlin.time.DurationUnit

class MusicFragment : Fragment() {

    private lateinit var binding: FragmentRelaxationMusicBinding

    private val navArgument: MusicFragmentArgs by navArgs()
    private var position = 0

    private val musicViewModel: MusicViewModel by viewModels {
        MusicViewModelFactory(
            position,
            binding.seekbar,
            binding.musicTimeTotal,
            binding.musicTimeCount,
            binding.musicFavourite,
            binding.musicPlay,
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

        binding.musicImage.load(navArgument.initialMusicThumbnail)
        binding.musicHeading.text = navArgument.initialMusicTitle

        musicViewModel.createContentList()

        binding.musicPlay.setOnClickListener { musicViewModel.handleMusicState() }

        binding.musicNext.setOnClickListener {
            musicViewModel.playNextMusic()
            setMusicTitleAndImg()
        }

        binding.musicPrevious.setOnClickListener {
            musicViewModel.playPreviousMusic()
            setMusicTitleAndImg()
        }

        binding.musicFavourite.setOnClickListener {
            musicViewModel.addToUserMusicListAndSetColor()
        }

        // controlling music when user slides seekbar
        binding.seekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2) { musicViewModel.setSeekbarUserProgress(p1) }
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        binding.musicList.setOnClickListener {
            findNavController().navigate(R.id.action_musicFragment_to_musicPlayListFragment)
        }

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setMusicTitleAndImg() {
        val content = musicViewModel.getCurrentContent()
        Glide.with(this).load(content.thumbnailUrl!!).into(binding.musicImage)
        binding.musicHeading.text = content.title!!
    }

    override fun onStart() {
        val bottomView = (activity as AppCompatActivity)
            .findViewById<BottomNavigationView>(R.id.nav_view)
        bottomView.visibility = View.GONE
        super.onStart()
    }

    override fun onPause() {
        musicViewModel.stopTimers()
        super.onPause()
    }

    override fun onStop() {
        val bottomView = (activity as AppCompatActivity)
            .findViewById<BottomNavigationView>(R.id.nav_view)
        bottomView.visibility = View.VISIBLE
        musicViewModel.releaseMediaPlayer()
        super.onStop()
    }
}