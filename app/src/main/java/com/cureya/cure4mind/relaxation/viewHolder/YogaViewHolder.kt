package com.cureya.cure4mind.relaxation.viewHolder

import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cureya.cure4mind.databinding.CardYogaBinding
import com.cureya.cure4mind.model.Yoga
import com.cureya.cure4mind.relaxation.ui.YogaFragment

class YogaViewHolder(private val binding: CardYogaBinding,
                     private val listener: YogaFragment,
                     private val progressBar: ProgressBar
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Yoga) {
        progressBar.visibility = View.GONE

        binding.yogaCardTitle.text = item.title
        Glide.with(binding.yogaCardImage.context).load(item.imgUrl).into(binding.yogaCardImage)

        binding.yogaCard.setOnClickListener {
            listener.goToYogaDetailsFragment(item.title!!)
        }
    }
}