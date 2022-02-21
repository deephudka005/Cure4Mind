package com.cureya.cure4mind.relaxation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cureya.cure4mind.R
import com.cureya.cure4mind.databinding.CardMusicGridBinding
import com.cureya.cure4mind.databinding.FragmentRelaxationMusicPlaylistBinding
import com.cureya.cure4mind.model.Content
import com.cureya.cure4mind.register.SignUpFragment
import com.cureya.cure4mind.relaxation.viewHolder.MusicPlayListViewHolder
import com.cureya.cure4mind.relaxation.viewModel.MusicViewModel
import com.cureya.cure4mind.util.database
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MusicPlayListFragment : Fragment() {

    private val auth = Firebase.auth
    private lateinit var binding: FragmentRelaxationMusicPlaylistBinding
    private lateinit var favouriteMusicAdapter: FirebaseRecyclerAdapter<Content, MusicPlayListViewHolder>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRelaxationMusicPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userUid = auth.currentUser?.uid.toString()

        val favouriteMusicRef = database.child(SignUpFragment.USER_LIST).child(userUid).child(
            MusicViewModel.CHILD_FAVOURITE_MUSIC
        )

        val favouriteList = FirebaseRecyclerOptions.Builder<Content>()
            .setQuery(favouriteMusicRef, Content::class.java)
            .build()

        favouriteMusicAdapter = object : FirebaseRecyclerAdapter<Content, MusicPlayListViewHolder>(favouriteList) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicPlayListViewHolder {
                val layoutView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.card_music_grid, parent, false)
                return MusicPlayListViewHolder(
                    CardMusicGridBinding.bind(layoutView)
                )
            }
            override fun onBindViewHolder(holder: MusicPlayListViewHolder, position: Int, model: Content) {
                holder.bind(model)
            }
        }

        binding.contentRecyclerView.adapter = favouriteMusicAdapter
        binding.contentRecyclerView.itemAnimator = null
    }

    override fun onStart() {
        favouriteMusicAdapter.startListening()
        super.onStart()
    }

    override fun onStop() {
        favouriteMusicAdapter.stopListening()
        super.onStop()
    }
}