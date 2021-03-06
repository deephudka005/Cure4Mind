package com.cureya.cure4mind.community.ui.fragment.dashboard

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.cureya.cure4mind.R
import com.cureya.cure4mind.community.models.Post
import com.cureya.cure4mind.community.models.TAG
import com.cureya.cure4mind.util.toDateString
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView

class PostRecyclerAdapter(
    val likePost: (String) -> Unit,
    val unlikePost: (String) -> Unit,
    val share: (Post) -> Unit,
    val onPostClick: (Post) -> Unit,
    val showMenu: (View, Post) -> Unit,
    val onProfileClick: (userId: String) -> Unit,
    val savePost:(postId:String) -> Unit,
    val unSavePost : (postId:String) -> Unit
) : RecyclerView.Adapter<PostRecyclerAdapter.PostViewHolder>() {

    private val posts: MutableList<Post> = mutableListOf()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val originalPosts = mutableListOf<Post>()

    fun updateData(posts: List<Post>) {
        this.posts.clear()
        this.posts.addAll(posts)
        originalPosts.clear()
        originalPosts.addAll(posts)
        notifyDataSetChanged()
    }

    fun filterPosts(tag: TAG?) {
        this.posts.clear()
        if (tag != null) {
            Log.d(TAG, "filterPosts: $tag")
            this.posts.addAll(originalPosts.filter { it.tags[0] == tag })
        } else {
            Log.d(TAG, "filterPosts: $tag")
            this.posts.addAll(originalPosts)
        }
    }


    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profilePhoto: CircleImageView = itemView.findViewById(R.id.post_user_image)
        val userName: TextView = itemView.findViewById(R.id.post_card_username)
        val postTime: TextView = itemView.findViewById(R.id.post_card_time)
        val profession: TextView = itemView.findViewById(R.id.post_card_userprofession)
        val caption: TextView = itemView.findViewById(R.id.post_card_caption)
        val postImage: ImageView = itemView.findViewById(R.id.post_card_photo)
        val like: ImageView = itemView.findViewById(R.id.post_like)
        val likeCount: TextView = itemView.findViewById(R.id.post_card_like_count)
        val save: ImageView = itemView.findViewById(R.id.post_save)
        val saveCount: TextView = itemView.findViewById(R.id.post_card_love_count)
        val comment: FrameLayout = itemView.findViewById(R.id.post_card_comment_frame)
        val menu: ImageView = itemView.findViewById(R.id.post_card_menu_button)
        val commentCount: TextView = itemView.findViewById(R.id.post_card_comment_count)
        val share: ImageView = itemView.findViewById(R.id.post_share)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return (PostViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.community_post_card, parent, false)
        ))
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        val isLiked = post.likes.contains(auth.uid!!)
        val isSaved = post.saved.contains(auth.uid!!)

        holder.apply {
            profilePhoto.load(post.profilePhoto)
            userName.text = post.userName
            postImage.load(post.photoUrl)
            caption.text = post.caption
            likeCount.text = post.likes.size.toString()
            commentCount.text = post.commentCount.toString()
            saveCount.text = post.saved.size.toString()
            postTime.text = post.createdAt.toDateString()
            profession.text = post.tags[0].name
            if (!isLiked)
                like.setColorFilter(Color.argb(255, 64, 64, 64))
             else
                like.setColorFilter(Color.argb(255, 255, 0, 0))

            if (!isSaved)
                save.setColorFilter(Color.argb(255, 64, 64, 64))
            else
                save.setColorFilter(Color.argb(255, 255, 0, 0))

            comment.setOnClickListener { onPostClick(post) }
            postImage.setOnClickListener { onPostClick(post) }
            share.setOnClickListener { share(post) }
            menu.setOnClickListener { showMenu(it, post) }
            like.setOnClickListener { if (isLiked) unlikePost(post.postId) else likePost(post.postId) }
            save.setOnClickListener { if (isSaved) unSavePost(post.postId) else savePost(post.postId) }
            profilePhoto.setOnClickListener { onProfileClick(post.userId) }
        }
    }

    override fun getItemCount(): Int = this.posts.size

    companion object {
        private const val TAG = "POST_RECYCLER_ADAPTER"
    }
}