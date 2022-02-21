package com.cureya.cure4mind.community.ui.fragment.saved

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.cureya.cure4mind.R
import com.cureya.cure4mind.community.models.Post
import com.cureya.cure4mind.util.toDateString

class SavedPostRecyclerAdapter(
    private val onPostClick: (Post) -> Unit
) : RecyclerView.Adapter<SavedPostRecyclerAdapter.SavedPostHolder>() {

    private val posts = mutableListOf<Post>()

    fun loadPosts(posts: List<Post>) {
        this.posts.clear()
        this.posts.addAll(posts)
        notifyDataSetChanged()
    }

    inner class SavedPostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.saved_post_image)
        val caption: TextView = itemView.findViewById(R.id.saved_post_caption)
        val time: TextView = itemView.findViewById(R.id.saved_post_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedPostHolder {
        return SavedPostHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.saved_post_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SavedPostHolder, position: Int) {
        val post = posts[position]
        holder.image.load(post.photoUrl)
        holder.caption.text = post.caption
        holder.time.text = post.createdAt.toDateString()
        holder.itemView.setOnClickListener { onPostClick(post) }
    }

    override fun getItemCount(): Int = posts.size

}