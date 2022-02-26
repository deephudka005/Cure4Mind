package com.cureya.cure4mind.relaxation.viewHolder

import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.cureya.cure4mind.R
import com.cureya.cure4mind.databinding.CardMusicGridBinding
import com.cureya.cure4mind.model.Content
import com.cureya.cure4mind.relaxation.ui.MusicPlayListFragment

class MusicPlayListViewHolder(
    private val binding: CardMusicGridBinding,
    private val currentContentTitle: String,
    private val listener: MusicPlayListFragment
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Content, position: Int) {
        binding.musicThumbnail.load(item.thumbnailUrl)
        binding.musicTitle.text = item.title

        binding.musicDelete.setOnClickListener {
            listener.removeMusic(position)
            it.setBackgroundColor(getColor(it.context, R.color.red))
        }

        if (currentContentTitle == item.title) {
            binding.musicPlay.setImageResource(R.drawable.ic_play)
        }
    }
}