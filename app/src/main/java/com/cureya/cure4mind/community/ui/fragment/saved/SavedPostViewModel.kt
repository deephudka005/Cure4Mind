package com.cureya.cure4mind.community.ui.fragment.saved

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cureya.cure4mind.community.models.Post
import com.cureya.cure4mind.util.database
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.GenericTypeIndicator
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SavedPostViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val _posts = MutableLiveData<List<Post>>(listOf())
    val posts: LiveData<List<Post>> get() = _posts

    init {
        loadPosts()
    }

    private fun loadPosts() {
        viewModelScope.launch {
            try {
                val postIds = database.child("community").child("saved").child(auth.uid!!)
                    .get().await().children.map { it.getValue(String::class.java)!! }
                val p = postIds.mapNotNull { getPostById(it) }
                _posts.value = p
            } catch (e: FirebaseException) {
                Log.e(TAG, "likePost: ", e)
            }
        }
    }


    fun unSavePost(postId: String) {
        viewModelScope.launch {
            try {
                database.child("community").child("saved").child(auth.uid!!).child(postId)
                    .removeValue().await()
                val t: GenericTypeIndicator<MutableList<String>> =
                    object : GenericTypeIndicator<MutableList<String>>() {}
                val saved = database.child("community").child("posts").child(postId).child("saved")
                    .get().await().getValue(t)
                    ?: mutableListOf()
                saved.remove(auth.uid!!)
                database.child("community").child("posts").child(postId).child("saved")
                    .setValue(saved)
                loadPosts()
            } catch (e: Exception) {
                Log.e(TAG, "savePost: ", e)
            }
        }

    }

    private suspend fun getPostById(postId: String) = database.child("community").child("posts")
        .child(postId).get().await().getValue(Post::class.java)

    companion object {
        private const val TAG = "SAVED_VIEW_MODEL"
    }
}