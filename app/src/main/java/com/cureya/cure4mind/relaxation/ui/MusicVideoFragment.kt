package com.cureya.cure4mind.relaxation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.cureya.cure4mind.R
import com.cureya.cure4mind.databinding.CardMusicAndVideoBinding
import com.cureya.cure4mind.databinding.FragmentRelaxationMusicVideoBinding
import com.cureya.cure4mind.model.Content
import com.cureya.cure4mind.register.SignUpFragment
import com.cureya.cure4mind.register.SignUpFragment.Companion.USER_LIST
import com.cureya.cure4mind.relaxation.ui.RelaxationFragment.Companion.CONTENT_TYPE_VIDEO
import com.cureya.cure4mind.relaxation.viewHolder.MusicViewHolder
import com.cureya.cure4mind.relaxation.viewHolder.VideoViewHolder
import com.cureya.cure4mind.relaxation.viewModel.MusicVideoViewModel
import com.cureya.cure4mind.relaxation.viewModel.MusicVideoViewModelFactory
import com.cureya.cure4mind.relaxation.viewModel.MusicViewModel
import com.cureya.cure4mind.relaxation.viewModel.MusicViewModel.Companion.CHILD_FAVOURITE_MUSIC
import com.cureya.cure4mind.relaxation.viewModel.MusicViewModelFactory
import com.cureya.cure4mind.util.database
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MusicVideoFragment : Fragment() {

    private lateinit var videoAdapter: FirebaseRecyclerAdapter<Content, VideoViewHolder>
    private lateinit var musicAdapter: FirebaseRecyclerAdapter<Content, MusicViewHolder>
    private lateinit var favouriteMusicAdapter: FirebaseRecyclerAdapter<Content, MusicViewHolder>
    private lateinit var binding: FragmentRelaxationMusicVideoBinding

    private val navArgument: MusicVideoFragmentArgs by navArgs()

    private val musicVideoViewModel: MusicVideoViewModel by viewModels {
        MusicVideoViewModelFactory(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRelaxationMusicVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val contentType = navArgument.contentType

        if (contentType == CONTENT_TYPE_VIDEO) {
            showVideoList()
        } else {
            showMusicList()
        }
    }

    private fun showVideoList() {
        videoAdapter = musicVideoViewModel.getVideoListAdapter()

        binding.label.text = getString(R.string.video)
        binding.heading.text = getString(R.string.we_recommend_you_favorite_video)

        binding.contentRecyclerView.adapter = videoAdapter
        binding.contentRecyclerView.itemAnimator = null

        binding.progressBar.visibility = View.GONE
    }

    private fun showMusicList() {

        binding.label.text = getString(R.string.music)
        binding.heading.text = getString(R.string.we_recommend_you_favourite_music)

        musicAdapter = musicVideoViewModel.getMusicListAdapter()
        favouriteMusicAdapter = musicVideoViewModel.getFavouriteMusicListAdapter()

        binding.contentRecyclerView.adapter = musicAdapter

        binding.chipAll.setOnClickListener {
            binding.contentRecyclerView.adapter = musicAdapter
        }

        binding.chipFavourite.setOnClickListener {
            binding.contentRecyclerView.adapter = favouriteMusicAdapter
        }

        binding.contentRecyclerView.itemAnimator = null

        binding.progressBar.visibility = View.GONE
    }

    override fun onStart() {
        super.onStart()
        try {
            videoAdapter.startListening()
        } catch (e: UninitializedPropertyAccessException) { }
        try {
            musicAdapter.startListening()
            favouriteMusicAdapter.startListening()
        } catch (e: UninitializedPropertyAccessException) { }
    }

    override fun onStop() {
        super.onStop()
        try {
            videoAdapter.stopListening()
        } catch (e: UninitializedPropertyAccessException) { }
        try {
            musicAdapter.stopListening()
            favouriteMusicAdapter.stopListening()
        } catch (e: UninitializedPropertyAccessException) { }
    }

    fun goToVideoFragment(videoUrl: String) {
        try {
            findNavController().navigate(
                MusicVideoFragmentDirections.actionMusicVideoFragmentToVideoFragment(
                    videoUrl
                ))
        } catch (e: Exception) {}
    }

    fun goToMusicFragment(position: Int, imgUrl: String, title: String) {
        try {
            findNavController().navigate(
                MusicVideoFragmentDirections.actionMusicVideoFragmentToMusicFragment(
                    position,
                    imgUrl,
                    title
                )
            )
        } catch (e: Exception) {}
    }

    companion object {
        private const val TAG = "MusicVideoFragment"
        const val VIDEO_LIST = "videos"
        const val MUSIC_LIST = "music"
    }
}