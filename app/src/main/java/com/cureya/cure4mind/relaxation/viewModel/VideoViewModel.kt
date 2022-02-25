package com.cureya.cure4mind.relaxation.viewModel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cureya.cure4mind.util.API_KEY
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerFragment
import com.google.android.youtube.player.YouTubePlayerSupportFragment

@SuppressLint("StaticFieldLeak")
class VideoViewModel(
        private val youtubePlayer: YouTubePlayerFragment,
        private val videoUrl: String
) : ViewModel() {

    /* private val _videoLoadingStatus = MutableLiveData<Boolean>()
    val videoLoadingStatus: LiveData<Boolean> get() = _videoLoadingStatus

    init {
        initializeAndPlayVideo()
        // retrieveRestVideos()
        _videoLoadingStatus.value = true
    }

    private fun initializeAndPlayVideo() {
        youtubePlayer.initialize(
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
                    _videoLoadingStatus.value = false
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

    /* private fun retrieveRestVideos() {

        database.child(VIDEO_LIST).addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value != null) {
                    snapshot.children.forEach {
                        val video = it.getValue(Content::class.java)!!
                        val contentUrl = video.contentUrl!!
                        val videoId = contentUrl.split('=')[1]

                        if (contentUrl != videoUrl) {
                            youtubePlayer.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                                override fun onReady(youTubePlayer: YouTubePlayer) {
                                    youTubePlayer.cueVideo(videoId, 0f)
                                }
                            })
                        }
                    }
                }
            }
        })
    }*/ */
}

class VideoViewModelFactory(
    private val youtubePlayer: YouTubePlayerFragment,
    private val videoUrl: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VideoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VideoViewModel(youtubePlayer, videoUrl) as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }
}