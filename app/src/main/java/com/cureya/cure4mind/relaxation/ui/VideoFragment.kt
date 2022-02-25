package com.cureya.cure4mind.relaxation.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.navArgs
import com.cureya.cure4mind.R
import com.cureya.cure4mind.databinding.FragmentRelaxationVideoBinding
import com.cureya.cure4mind.util.API_KEY
import com.cureya.cure4mind.util.shortToast
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer

// Some work is still left, will finish them by today
// specially the unresolved bugs; youtubePlayerApi itself is buggy
// they haven't moved their api to androidX yet, but the
// following code still runs
class VideoFragment : Fragment() {

    private lateinit var binding: FragmentRelaxationVideoBinding
    private val navArgument: VideoFragmentArgs by navArgs()

    private val _videoLoadingStatus = MutableLiveData<Boolean>()
    val videoLoadingStatus: LiveData<Boolean> get() = _videoLoadingStatus

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        _videoLoadingStatus.value = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRelaxationVideoBinding.inflate(inflater, container, false)

        initializeYoutubeFragment()

        context?.shortToast("Please wait! This may take a while")

        return binding.root
    }

    private fun initializeYoutubeFragment() {
        val videoUrl = navArgument.videoUrl

        val youtubePlayerFragment = YouTubePlayerSupportFragment()
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.youtube_fragment, youtubePlayerFragment)
        transaction.commit()

        youtubePlayerFragment.initialize(
            API_KEY,
            object: YouTubePlayer.OnInitializedListener {

                override fun onInitializationSuccess(
                    provider: YouTubePlayer.Provider?,
                    ytPlayer: YouTubePlayer?,
                    p2: Boolean
                ) {
                    val videoId = videoUrl.split('=')[1]
                    ytPlayer?.loadVideo(videoId)
                    ytPlayer?.play()
                    _videoLoadingStatus.value = true
                }
                override fun onInitializationFailure(
                    provider: YouTubePlayer.Provider?,
                    p1: YouTubeInitializationResult?
                ) {
                    Log.e("VideoViewModel", "Video Player Failed")
                }
            }
        )
    }

    override fun onStart() {
        val bottomView = (activity as AppCompatActivity)
            .findViewById<BottomNavigationView>(R.id.nav_view)
        bottomView.visibility = View.GONE

        super.onStart()
    }

    override fun onStop() {
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val bottomView = (activity as AppCompatActivity)
            .findViewById<BottomNavigationView>(R.id.nav_view)
        bottomView.visibility = View.VISIBLE

        super.onStop()
    }
}