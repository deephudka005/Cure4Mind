package com.cureya.cure4mind.community.ui.fragment.saved

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.cureya.cure4mind.R
import com.cureya.cure4mind.community.models.Post
import com.cureya.cure4mind.util.toDateString

class SavedPostRecyclerAdapter(
    private val onPostClick: (Post) -> Unit,
    private val unSave : () -> Unit
) : RecyclerView.Adapter<SavedPostRecyclerAdapter.SavedPostHolder>() {

    private val posts = mutableListOf<Post>()

    fun loadPosts(posts: List<Post>) {
        this.posts.clear()
        this.posts.addAll(posts)
        notifyDataSetChanged()
    }

    inner class SavedPostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.saved_post_image)
        val time: TextView = itemView.findViewById(R.id.saved_post_time)
        val userName: TextView = itemView.findViewById(R.id.saved_post_user)
        val viewPost: Button = itemView.findViewById(R.id.view_post)
        val unSave: Button = itemView.findViewById(R.id.unsave_post)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedPostHolder {
        return SavedPostHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.saved_post_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SavedPostHolder, position: Int) {
        val post = posts[position]
        holder.apply {
            image.load(post.photoUrl)
            time.text = post.createdAt.toDateString()
            viewPost.setOnClickListener {  onPostClick(post)}
            unSave.setOnClickListener {  }
        }
    }

    override fun getItemCount(): Int = posts.size

}