package com.cureya.cure4mind.relaxation.viewModel

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cureya.cure4mind.model.Content
import com.cureya.cure4mind.register.SignUpFragment.Companion.USER_LIST
import com.cureya.cure4mind.relaxation.ui.MusicVideoFragment.Companion.VIDEO_LIST
import com.cureya.cure4mind.util.database
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@SuppressLint("StaticFieldLeak")
class VideoViewModel(
    private val youtubePlayer: YouTubePlayerView,
    private val videoUrl: String
) : ViewModel() {

    private val _videoLoadingStatus = MutableLiveData<Boolean>()
    val videoLoadingStatus: LiveData<Boolean> get() = _videoLoadingStatus

    init {
        playVideo()
        // retrieveRestVideos()
        _videoLoadingStatus.value = true
    }

    private fun playVideo() {
        try {
            val videoId = videoUrl.split('=')[1]
            youtubePlayer.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.loadVideo(videoId, 0f)
                    _videoLoadingStatus.value = false
                }
            })
        } catch (e: Exception) {
            Log.e("VideoViewModel", "unable to load youtube video", e)
        }
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
    }*/
}

class VideoViewModelFactory(
    private val youtubePlayer: YouTubePlayerView,
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