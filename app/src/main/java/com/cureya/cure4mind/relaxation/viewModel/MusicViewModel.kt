package com.cureya.cure4mind.relaxation.viewModel

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cureya.cure4mind.model.Content
import com.cureya.cure4mind.register.SignUpFragment.Companion.USER_LIST
import com.cureya.cure4mind.relaxation.ui.MusicVideoFragment
import com.cureya.cure4mind.util.database
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.IllegalArgumentException

class MusicViewModel(private var pos: Int): ViewModel() {

    private val auth = Firebase.auth
    private var mediaPlayer: MediaPlayer? = null

    private var contentList = mutableListOf<Content>()

    private val _isMediaPlayerPlaying = MutableLiveData<Boolean?>()
    val isMediaPlayerPlaying get() = _isMediaPlayerPlaying

    private val _isFavouriteMusic = MutableLiveData<Boolean>()
    val isFavouriteMusic get() = _isFavouriteMusic


    fun createAndPlayContentList() {
        database.child(MusicVideoFragment.MUSIC_LIST).get()
            .addOnSuccessListener { snapshot ->
                snapshot.children.forEach {
                    val item = it.getValue(Content::class.java)!!
                    contentList.add(item)
                }
                val content = contentList[pos]
                playMusic(content.contentUrl!!)
            }
    }

    fun handleMusicState() {
        if (_isMediaPlayerPlaying.value == true) {
            mediaPlayer?.pause()
            _isMediaPlayerPlaying.value = false
        } else {
            mediaPlayer?.start()
            _isMediaPlayerPlaying.value = true
        }
    }

    fun playNextMusic() {
        // to loop back once we are at the edge of the list
        pos = (pos + 1) % contentList.size

        val content = contentList[pos]

        mediaPlayer?.stop()
        mediaPlayer?.release()
        _isMediaPlayerPlaying.value = false

        playMusic(content.contentUrl!!)
    }

    fun playPreviousMusic() {
        // to loop through the list
        if (pos - 1 < 0) {
            pos = contentList.size - 1
        } else { --pos }

        val content = contentList[pos]

        mediaPlayer?.stop()
        mediaPlayer?.release()
        _isMediaPlayerPlaying.value = false

        playMusic(content.contentUrl!!)
    }

    fun addToUserMusicListAndSetColor() {
        val content = contentList[pos]
        val userUid = auth.currentUser?.uid.toString()

        database.child(USER_LIST).child(userUid).child(CHILD_FAVOURITE_MUSIC).child(content.title!!).apply {
            addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value == null) {
                        this@apply.setValue(content)
                        _isFavouriteMusic.value = true
                    } else {
                        this@apply.removeValue()
                        _isFavouriteMusic.value = false
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("MusicViewModel", "Error occurred in favourite music list", error.toException())
                }
            })
        }
    }

    @SuppressLint("NewApi")
    fun formatTime(mls: Long): String {
        var time = ""
        val minutes = (mls/1000) / 60
        val seconds = (mls/1000) % 60
        time = "$time$minutes:"
        if (seconds < 10) { time+= '0' }
        return "$time$seconds"
    }

    fun getCurrentPosition() = pos

    fun getCurrentContent() = contentList[pos]

    fun getMediaPlayerDuration() = mediaPlayer?.duration?.toLong()

    fun getMediaPlayerCurrentPosition() = mediaPlayer?.currentPosition?.toLong()

    fun setCurrentMusicTimeToUserProgress(progress: Int) = mediaPlayer?.seekTo(progress)

    fun removeMusicAtPosition(pos: Int) {
        contentList[pos].title = "null"
    }

    fun releaseMediaPlayer() {
        _isMediaPlayerPlaying.value = false
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun playMusic(url: String) {
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(url)
            prepare() // might take long! (for buffering, etc)
            start()
        }
        _isMediaPlayerPlaying.value = mediaPlayer?.isPlaying
        isItFavouriteMusic()
    }

    private fun isItFavouriteMusic() {
        val content = contentList[pos]
        val userUid = auth.currentUser?.uid.toString()

        database.child(USER_LIST).child(userUid).child(CHILD_FAVOURITE_MUSIC).child(content.title!!).apply {
            addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    _isFavouriteMusic.value = (snapshot.value != null)
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e(
                        "MusicViewModel",
                        "Error occurred getting favorite music",
                        error.toException()
                    )
                }
            })
        }
    }

    companion object {
        const val CHILD_FAVOURITE_MUSIC = "favouriteMusic"
    }
}

class MusicViewModelFactory(private var pos: Int) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MusicViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MusicViewModel(pos)
                    as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }
}
