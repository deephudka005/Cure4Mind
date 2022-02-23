package com.cureya.cure4mind.relaxation.viewHolder

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.cureya.cure4mind.databinding.CardMusicGridBinding
import com.cureya.cure4mind.model.Content
import com.cureya.cure4mind.register.SignUpFragment
import com.cureya.cure4mind.relaxation.viewModel.MusicViewModel
import com.cureya.cure4mind.util.database
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class MusicPlayListViewHolder(
    private val binding: CardMusicGridBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Content) {
        binding.musicThumbnail.load(item.thumbnailUrl)
        binding.musicTitle.text = item.title

        binding.musicDelete.setOnClickListener {
            val userId = Firebase.auth.currentUser?.uid.toString()

            database.child(SignUpFragment.USER_LIST).child(userId).child(MusicViewModel.CHILD_FAVOURITE_MUSIC).child(item.title!!).apply {
                addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.value != null) {
                            this@apply.removeValue()
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.e("MusicViewHolder", "Error occurred in favourite music list", error.toException())
                    }
                })
            }
        }
    }
}