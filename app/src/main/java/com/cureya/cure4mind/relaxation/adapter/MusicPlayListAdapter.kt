package com.cureya.cure4mind.relaxation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.cureya.cure4mind.databinding.CardMusicGridBinding
import com.cureya.cure4mind.model.Content
import com.cureya.cure4mind.relaxation.ui.MusicPlayListFragment

class MusicPlayListAdapter(
    private val listener: MusicPlayListFragment,
    private val playingMusicPosition: Int
) : ListAdapter<Content, MusicPlayListAdapter.MusicPlayListViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicPlayListViewHolder {
        return MusicPlayListViewHolder(
            CardMusicGridBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false),
            listener
        )
    }

    override fun onBindViewHolder(holder: MusicPlayListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, position)
    }

    override fun submitList(list: MutableList<Content>?) {
        super.submitList(list)
    }

    inner class MusicPlayListViewHolder(
        private val binding: CardMusicGridBinding,
        private val listener: MusicPlayListFragment
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Content, position: Int) {
            binding.musicThumbnail.load(item.thumbnailUrl)
            binding.musicTitle.text = item.title

            if (playingMusicPosition == position) {
                binding.musicPlay.visibility = View.VISIBLE
            } else binding.musicPlay.visibility = View.GONE

            binding.musicDelete.setOnClickListener {
                listener.removeMusic(position)
            }
        }
    }

    companion object {

        private val DiffCallback = object : DiffUtil.ItemCallback<Content>() {
            override fun areItemsTheSame(oldItem: Content, newItem: Content): Boolean {
                return (oldItem === newItem)
            }
            override fun areContentsTheSame(oldItem: Content, newItem: Content): Boolean {
                return (oldItem.title == newItem.title)
            }
        }
    }
}