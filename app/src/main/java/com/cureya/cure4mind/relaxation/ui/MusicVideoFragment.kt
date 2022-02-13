package com.cureya.cure4mind.relaxation.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.cureya.cure4mind.R
import com.cureya.cure4mind.databinding.CardMusicAndVideoBinding
import com.cureya.cure4mind.databinding.FragmentRelaxationMusicVideoBinding
import com.cureya.cure4mind.model.Content
import com.cureya.cure4mind.relaxation.ui.RelaxationFragment.Companion.CONTENT_TYPE_VIDEO
import com.cureya.cure4mind.relaxation.viewHolder.MusicViewHolder
import com.cureya.cure4mind.relaxation.viewHolder.VideoViewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MusicVideoFragment : Fragment() {

    private lateinit var videoAdapter: FirebaseRecyclerAdapter<Content, VideoViewHolder>
    private lateinit var musicAdapter: FirebaseRecyclerAdapter<Content, MusicViewHolder>
    private lateinit var binding: FragmentRelaxationMusicVideoBinding
    private lateinit var db: FirebaseDatabase

    private val navArgument: MusicVideoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.w("MusicVideoFragment", "Back to musicVideo fragment onCreate()")
        binding = FragmentRelaxationMusicVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.w("MusicVideoFragment", "Back to musicVideo fragment onViewCreated()")

        val contentType = navArgument.contentType

        db = Firebase.database

        if (contentType == CONTENT_TYPE_VIDEO) {
            showVideoList()
        } else {
            Log.w("MusicVideoFragment", "showMusicList() called")
            showMusicList()
        }
    }

    private fun showVideoList() {
        val videoRef = db.reference.child(VIDEO_LIST)

        val videoList = FirebaseRecyclerOptions.Builder<Content>()
            .setQuery(videoRef, Content::class.java)
            .build()

        binding.label.text = getString(R.string.video)
        binding.heading.text = getString(R.string.we_recommend_you_favorite_video)

        videoAdapter = object : FirebaseRecyclerAdapter<Content, VideoViewHolder>(videoList) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
                val layoutView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.card_music_and_video, parent, false)
                return VideoViewHolder(
                    CardMusicAndVideoBinding.bind(layoutView),
                    this@MusicVideoFragment,
                    binding.progressBar
                )
            }
            override fun onBindViewHolder(holder: VideoViewHolder, position: Int, model: Content) {
                holder.bind(model)
            }
        }
        Log.w(TAG, "adapter list count: ${videoAdapter.itemCount}")
        binding.contentRecyclerView.adapter = videoAdapter
        binding.contentRecyclerView.itemAnimator = null
    }

    private fun showMusicList() {
        val musicRef = db.reference.child(MUSIC_LIST)
        Log.w(TAG, "inside showMusicList")

        val musicList = FirebaseRecyclerOptions.Builder<Content>()
            .setQuery(musicRef, Content::class.java)
            .build()

        Log.w(TAG, "musicList: ${musicList.snapshots}")

        binding.label.text = getString(R.string.music)
        binding.heading.text = getString(R.string.we_recommend_you_favourite_music)

        musicAdapter = object : FirebaseRecyclerAdapter<Content, MusicViewHolder>(musicList) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
                val layoutView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.card_music_and_video, parent, false)
                return MusicViewHolder(
                    CardMusicAndVideoBinding.bind(layoutView),
                    this@MusicVideoFragment,
                    binding.progressBar
                )
            }
            override fun onBindViewHolder(holder: MusicViewHolder, position: Int, model: Content) {
                holder.bind(model, position)
            }
        }
        Log.w(TAG, "adapter list count: ${musicAdapter.itemCount}")
        binding.contentRecyclerView.adapter = musicAdapter
        binding.contentRecyclerView.itemAnimator = null
    }

    override fun onStart() {
        super.onStart()
        Log.w("MusicVideoFragment", "Back to musicVideo fragment onStart()")
        try {
            videoAdapter.startListening()
            Log.w(TAG, "Video adapter called")
        } catch (e: UninitializedPropertyAccessException) { }
        try {
            musicAdapter.startListening()
            Log.w(TAG, "Music adapter called")
        } catch (e: UninitializedPropertyAccessException) { }
    }

    override fun onStop() {
        super.onStop()
        try {
            videoAdapter.stopListening()
        } catch (e: UninitializedPropertyAccessException) { }
        try {
            musicAdapter.stopListening()
        } catch (e: UninitializedPropertyAccessException) { }
    }

    fun goToVideoFragment(videoUrl: String) = findNavController().navigate(
        MusicVideoFragmentDirections.actionMusicVideoFragmentToVideoFragment(
            videoUrl
        )
    )

    fun goToMusicFragment(position: Int) = findNavController().navigate(
        MusicVideoFragmentDirections.actionMusicVideoFragmentToMusicFragment(position)
    )

    companion object {
        private const val TAG = "MusicVideoFragment"
        const val VIDEO_LIST = "videos"
        const val MUSIC_LIST = "music"
    }
}