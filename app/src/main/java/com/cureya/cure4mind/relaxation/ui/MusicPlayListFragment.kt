package com.cureya.cure4mind.relaxation.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import coil.load
import com.cureya.cure4mind.R
import com.cureya.cure4mind.databinding.CardMusicGridBinding
import com.cureya.cure4mind.databinding.FragmentRelaxationMusicPlaylistBinding
import com.cureya.cure4mind.model.Content
import com.cureya.cure4mind.register.SignUpFragment
import com.cureya.cure4mind.relaxation.adapter.MusicPlayListAdapter
import com.cureya.cure4mind.relaxation.ui.MusicVideoFragment.Companion.MUSIC_LIST
import com.cureya.cure4mind.relaxation.viewHolder.MusicPlayListViewHolder
import com.cureya.cure4mind.relaxation.viewModel.MusicViewModel
import com.cureya.cure4mind.relaxation.viewModel.MusicViewModelFactory
import com.cureya.cure4mind.util.database
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MusicPlayListFragment : Fragment() {

    private val auth = Firebase.auth
    private lateinit var musicList:  FirebaseRecyclerOptions<Content>
    private lateinit var binding: FragmentRelaxationMusicPlaylistBinding
    private lateinit var musicAdapter: FirebaseRecyclerAdapter<Content, MusicPlayListViewHolder>

    private val musicViewModel: MusicViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRelaxationMusicPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val musicRef = database.child(MUSIC_LIST)
        val currentContentTitle = musicViewModel.getCurrentContent().title!!

        musicList = FirebaseRecyclerOptions.Builder<Content>()
            .setQuery(musicRef, Content::class.java)
            .build()

        musicAdapter = object : FirebaseRecyclerAdapter<Content, MusicPlayListViewHolder>(musicList) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicPlayListViewHolder {
                val layoutView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.card_music_grid, parent, false)
                return MusicPlayListViewHolder(
                    CardMusicGridBinding.bind(layoutView),
                    currentContentTitle,
                    this@MusicPlayListFragment
                )
            }
            override fun onBindViewHolder(holder: MusicPlayListViewHolder, position: Int, model: Content) {
                holder.bind(model, position)
            }
        }

        binding.contentRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = musicAdapter
            itemAnimator = null
        }
    }

    fun removeMusic(pos: Int) {
        musicViewModel.removeMusicAtPosition(pos)
    }

    override fun onStart() {
        musicAdapter.startListening()
        super.onStart()
    }

    override fun onStop() {
        musicAdapter.stopListening()
        super.onStop()
    }
}