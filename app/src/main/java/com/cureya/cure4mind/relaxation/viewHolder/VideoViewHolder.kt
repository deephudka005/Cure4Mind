package com.cureya.cure4mind.relaxation.viewHolder

import androidx.recyclerview.widget.RecyclerView
import coil.imageLoader
import coil.request.ImageRequest
import com.cureya.cure4mind.databinding.CardMusicAndVideoBinding
import com.cureya.cure4mind.model.Content
import com.cureya.cure4mind.relaxation.ui.MusicVideoFragment

class VideoViewHolder(private val binding: CardMusicAndVideoBinding,
                      private val listener: MusicVideoFragment
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Content) {
        binding.contentTitle.text = item.title
        binding.contentTime.text = item.duration

        // loading image as a layout background
        val view = binding.contentBackgroundContainer
        val request = ImageRequest.Builder(view.context)
            .data(item.thumbnailUrl)
            .target(onSuccess = { result -> view.background = result })
            .build()
        view.context.imageLoader.enqueue(request)

        binding.cardPlayer.setOnClickListener {
            listener.goToVideoFragment(item.contentUrl!!)
        }
    }
}