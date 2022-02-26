package com.cureya.cure4mind.relaxation.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.cureya.cure4mind.R
import com.cureya.cure4mind.databinding.FragmentRelaxationMusicBinding
import com.cureya.cure4mind.relaxation.viewModel.MusicViewModel
import com.cureya.cure4mind.relaxation.viewModel.MusicViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.concurrent.Executors
import java.util.concurrent.Future

class MusicFragment : Fragment() {

    private lateinit var binding: FragmentRelaxationMusicBinding

    private val navArgument: MusicFragmentArgs by navArgs()
    private var position = 0

    private val musicViewModel: MusicViewModel by activityViewModels {
        MusicViewModelFactory(position)
    }

    private val  handler = Handler()
    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var runnable: Runnable
    private lateinit var runningTaskFuture: Future<*>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRelaxationMusicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        position = navArgument.itemPosition

        binding.musicImage.load(navArgument.initialMusicThumbnail)
        binding.musicHeading.text = navArgument.initialMusicTitle

        binding.musicPlay.setOnClickListener {
            musicViewModel.handleMusicState()
        }

        binding.musicNext.setOnClickListener {
            musicViewModel.playNextMusic()
            // setMusicTitleAndImg()
        }

        binding.musicPrevious.setOnClickListener {
            musicViewModel.playPreviousMusic()
            // setMusicTitleAndImg()
        }

        binding.musicFavourite.setOnClickListener {
            musicViewModel.addToUserMusicListAndSetColor()
        }

        // controlling music when user slides seekbar
        binding.seekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) { musicViewModel.setCurrentMusicTimeToUserProgress(progress) }
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        binding.musicList.setOnClickListener {
            findNavController().navigate(MusicFragmentDirections
                .actionMusicFragmentToMusicPlayListFragment(musicViewModel.getCurrentPosition()))
        }

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        observeViews()
    }

    private fun observeViews() {

        musicViewModel.isMediaPlayerPlaying.observe(viewLifecycleOwner) {
            if (it == true) {
                val content = musicViewModel.getCurrentContent()

                binding.musicTimeTotal.text = musicViewModel.formatTime(
                    musicViewModel.getMediaPlayerDuration()!!)

                binding.musicPlay.setImageResource(R.drawable.asset_relaxation_music_playing)

                binding.musicImage.load(content.thumbnailUrl!!)
                binding.musicHeading.text = content.title!!

                updateSeekBarAndTime()
            } else {
                binding.musicPlay.setImageResource(R.drawable.ic_play)
            }
        }
        musicViewModel.isFavouriteMusic.observe(viewLifecycleOwner) {
            if (it == true) {
                ImageViewCompat.setImageTintList(binding.musicFavourite, ColorStateList.valueOf(
                    ContextCompat.getColor(binding.musicFavourite.context, R.color.red)))
            } else {
                ImageViewCompat.setImageTintList(binding.musicFavourite, ColorStateList.valueOf(
                    ContextCompat.getColor(binding.musicFavourite.context, R.color.content_background)))
            }
        }
    }

    private fun updateSeekBarAndTime() {
        binding.seekbar.max = musicViewModel.getMediaPlayerDuration()!!.toInt()

        runnable = Runnable {

            val currentPosition = musicViewModel.getMediaPlayerCurrentPosition()!!
            val currentTime = musicViewModel.formatTime(currentPosition)
            binding.musicTimeCount.text = currentTime
            binding.seekbar.progress = currentPosition.toInt()
            handler.postDelayed(runnable, 1000)
        }
        runningTaskFuture = executor.submit(runnable)
    }

    /* private fun setMusicTitleAndImg() {
        val content = musicViewModel.getCurrentContent()
        binding.musicImage.load(content.thumbnailUrl!!)
        binding.musicHeading.text = content.title!!
    } */

    override fun onStart() {
        val bottomView = (activity as AppCompatActivity)
            .findViewById<BottomNavigationView>(R.id.nav_view)
        bottomView.visibility = View.GONE

        musicViewModel.createAndPlayContentList()

        super.onStart()
    }

    override fun onStop() {
        val bottomView = (activity as AppCompatActivity)
            .findViewById<BottomNavigationView>(R.id.nav_view)
        bottomView.visibility = View.VISIBLE

        runningTaskFuture.cancel(true)
        handler.removeCallbacks(runnable)

        musicViewModel.releaseMediaPlayer()

        super.onStop()
    }
}