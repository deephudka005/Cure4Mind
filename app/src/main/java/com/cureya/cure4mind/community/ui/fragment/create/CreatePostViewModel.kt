package com.cureya.cure4mind.community.ui.fragment.create

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cureya.cure4mind.community.models.Post
import com.cureya.cure4mind.community.models.TAG
import com.cureya.cure4mind.community.models.User
import com.cureya.cure4mind.util.STORAGE_BUCKET
import com.cureya.cure4mind.util.database
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class CreatePostViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val storageRef = Firebase.storage(STORAGE_BUCKET).reference

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> get() = _error

    private val _tag = MutableLiveData<TAG?>()
    val tag: LiveData<TAG?> get() = _tag

    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> get() = _currentUser

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            try {
                val user =
                    database.child("users").child(auth.uid!!).get().await()
                        .getValue(User::class.java)!!
                user.userId = auth.uid!!
                _currentUser.value = user
            } catch (e: Exception) {
                Log.e(TAG, "loadUser:", e)
            }
        }
    }


    fun setTag(tag: TAG) {
        _tag.value = tag
    }

    fun createPost(imageUri: Uri?, caption: String, tags: List<TAG>) {
        if(imageUri==null && caption.isBlank()) {
            _error.value = "Please select an image or write a post!"
            return
        }
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                var url : String? = null
                val postId = "${auth.uid!!}${System.currentTimeMillis()}"
                imageUri?.let {
                    val imageRef =
                        storageRef.child("community/post/${auth.uid!!}/IMG000${System.currentTimeMillis()}")
                    imageRef.putFile(imageUri).await()
                    url = imageRef.downloadUrl.await().toString()
                }
                val post = Post(
                    postId = postId,
                    caption = caption,
                    userId = auth.uid!!,
                    likes = listOf(),
                    commentCount = 0,
                    shares = 0,
                    createdAt = Date(),
                    userName = _currentUser.value!!.name,
                    photoUrl = url,
                    profilePhoto = _currentUser.value!!.photoUrl,
                    tags = tags
                )
                database.child("community").child("posts")
                    .child(postId)
                    .setValue(post).await()
            } catch (e: Exception) {
                _error.value = e.message
                Log.e(TAG, "createPost: ", e)
            }
            _isLoading.value = false
        }
    }

    companion object {
        private const val TAG = "CREATE_POST_VIEW_MODEL"
    }
}