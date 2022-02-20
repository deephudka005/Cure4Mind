package com.cureya.cure4mind.relaxation.viewModel

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cureya.cure4mind.R
import com.cureya.cure4mind.databinding.CardMusicAndVideoBinding
import com.cureya.cure4mind.model.Content
import com.cureya.cure4mind.register.SignUpFragment
import com.cureya.cure4mind.register.SignUpFragment.Companion.USER_LIST
import com.cureya.cure4mind.relaxation.ui.MusicVideoFragment
import com.cureya.cure4mind.relaxation.viewHolder.MusicViewHolder
import com.cureya.cure4mind.relaxation.viewHolder.VideoViewHolder
import com.cureya.cure4mind.relaxation.viewModel.MusicViewModel.Companion.CHILD_FAVOURITE_MUSIC
import com.cureya.cure4mind.util.database
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*

@SuppressLint("StaticFieldLeak")
class MusicVideoViewModel(
        private val musicVideoFragment: MusicVideoFragment,
        private val progressBar: ProgressBar
) : ViewModel() {

    private val auth = Firebase.auth

    fun getVideoListAdapter() : FirebaseRecyclerAdapter<Content, VideoViewHolder> {
        val videoRef = database.child(MusicVideoFragment.VIDEO_LIST)

        val videoList = FirebaseRecyclerOptions.Builder<Content>()
            .setQuery(videoRef, Content::class.java)
            .build()

        val videoAdapter = object : FirebaseRecyclerAdapter<Content, VideoViewHolder>(videoList) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
                val layoutView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.card_music_and_video, parent, false)
                return VideoViewHolder(
                    CardMusicAndVideoBinding.bind(layoutView),
                    musicVideoFragment,
                    progressBar
                )
            }
            override fun onBindViewHolder(holder: VideoViewHolder, position: Int, model: Content) {
                holder.bind(model)
            }
        }
        return videoAdapter
    }

    fun getMusicListAdapter() : FirebaseRecyclerAdapter<Content, MusicViewHolder> {

        val musicRef = database.child(MusicVideoFragment.MUSIC_LIST)

        val musicList = FirebaseRecyclerOptions.Builder<Content>()
            .setQuery(musicRef, Content::class.java)
            .build()

        val musicAdapter = object : FirebaseRecyclerAdapter<Content, MusicViewHolder>(musicList) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
                val layoutView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.card_music_and_video, parent, false)
                return MusicViewHolder(
                    CardMusicAndVideoBinding.bind(layoutView),
                    musicVideoFragment,
                    progressBar
                )
            }
            override fun onBindViewHolder(holder: MusicViewHolder, position: Int, model: Content) {
                holder.bind(model, position)
            }
        }
        return musicAdapter
    }

    fun getFavouriteMusicListAdapter() : FirebaseRecyclerAdapter<Content, MusicViewHolder> {
        val userUid = auth.currentUser?.uid.toString()

        val favouriteMusicRef = database.child(USER_LIST).child(userUid).child(
            CHILD_FAVOURITE_MUSIC
        )

        val favouriteList = FirebaseRecyclerOptions.Builder<Content>()
            .setQuery(favouriteMusicRef, Content::class.java)
            .build()

        val favouriteMusicAdapter = object : FirebaseRecyclerAdapter<Content, MusicViewHolder>(favouriteList) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
                val layoutView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.card_music_and_video, parent, false)
                return MusicViewHolder(
                    CardMusicAndVideoBinding.bind(layoutView),
                    musicVideoFragment,
                    progressBar
                )
            }
            override fun onBindViewHolder(holder: MusicViewHolder, position: Int, model: Content) {
                holder.bind(model, position)
            }
        }
        return favouriteMusicAdapter
    }
}

class MusicVideoViewModelFactory(
    private val musicVideoFragment: MusicVideoFragment,
    private val progressBar: ProgressBar
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MusicVideoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MusicVideoViewModel(musicVideoFragment, progressBar)
                    as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }
}