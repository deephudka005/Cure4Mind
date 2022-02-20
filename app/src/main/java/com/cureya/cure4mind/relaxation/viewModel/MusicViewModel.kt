package com.cureya.cure4mind.relaxation.viewModel

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.cureya.cure4mind.R
import com.cureya.cure4mind.model.Content
import com.cureya.cure4mind.register.SignUpFragment.Companion.USER_LIST
import com.cureya.cure4mind.relaxation.ui.MusicVideoFragment
import com.cureya.cure4mind.relaxation.viewHolder.MusicViewHolder
import com.cureya.cure4mind.util.database
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.lang.IllegalStateException
import java.util.*
import kotlin.IllegalArgumentException

@SuppressLint("StaticFieldLeak")
class MusicViewModel(
    private var pos: Int,
    private val seekbar: SeekBar,
    private val musicTimeTotal: TextView,
    private val musicTimeCount: TextView,
    private val musicFavourite: ImageView,
    private val musicPlay: ImageView,
    private val progressBar: ProgressBar
): ViewModel() {

    private val auth = Firebase.auth
    private lateinit var mediaPlayer: MediaPlayer
    private val contentList = mutableListOf<Content>()

    fun createContentList() {
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

    /* fun createFavouriteMusicList() {
        database.child(USER_LIST).child(CHILD_FAVOURITE_MUSIC).get()
            .addOnSuccessListener { snapshot ->
                snapshot.children.forEach {
                    val item = it.getValue(Content::class.java)!!
                    Log.w("MusicViewModel", "item: $item")
                    favouriteContentList.add(item)
                }
                val content = favouriteContentList[favPos]
                playMusic(content.contentUrl!!)
            }
    } */

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
        setButtonColor()
        setSeekBar()
        updateTimer()
        musicPlay.setImageResource(R.drawable.asset_relaxation_music_playing)
        progressBar.visibility = View.GONE
        musicTimeTotal.text = formatTime(mediaPlayer.duration.toLong())
    }

    fun handleMusicState() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            musicPlay.setImageResource(R.drawable.ic_play)
        } else {
            mediaPlayer.start()
            musicPlay.setImageResource(R.drawable.asset_relaxation_music_playing)
        }
    }

    fun playNextMusic() {
        progressBar.visibility = View.VISIBLE
        // to loop back once we are at the edge of the list
        pos = (pos + 1) % contentList.size

        val content = contentList[pos]

        if (mediaPlayer.isPlaying) mediaPlayer.stop()
        mediaPlayer.release()

        playMusic(content.contentUrl!!)
    }

    fun playPreviousMusic() {
        progressBar.visibility = View.VISIBLE
        // to loop through the list
        if (pos - 1 < 0) {
            pos = contentList.size - 1
        } else { --pos }

        val content = contentList[pos]

        mediaPlayer.stop()
        mediaPlayer.release()

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
                    } else {
                        this@apply.removeValue()
                    }
                    setButtonColor()
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("MusicViewModel", "Error occurred in favourite music list", error.toException())
                }
            })
        }
    }

    fun getCurrentContent() = contentList[pos]

    fun setSeekbarUserProgress(progress: Int) = mediaPlayer.seekTo(progress)

    fun releaseMediaPlayer() = mediaPlayer.release()

    fun setButtonColor() {
        val content = contentList[pos]
        val userUid = auth.currentUser?.uid.toString()

        database.child(USER_LIST).child(userUid).child(CHILD_FAVOURITE_MUSIC).child(content.title!!).apply {
            addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value == null) {
                        ImageViewCompat.setImageTintList(musicFavourite, ColorStateList.valueOf(
                            ContextCompat.getColor(musicFavourite.context, R.color.gray_700))
                        )
                    } else {
                        ImageViewCompat.setImageTintList(musicFavourite, ColorStateList.valueOf(
                            ContextCompat.getColor(musicFavourite.context, R.color.red))
                        )
                    }
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

    private fun setSeekBar() {
        musicTimeCount.text = formatTime(mediaPlayer.duration.toLong())

        seekbar.max = mediaPlayer.duration

        Timer().scheduleAtFixedRate(object: TimerTask() {
            override fun run() {
                try {
                    seekbar.progress = mediaPlayer.currentPosition
                } catch (e: IllegalStateException) {}
            }
        }, 0, 900)
    }

    @SuppressLint("NewApi")
    private fun updateTimer() {
        Timer().scheduleAtFixedRate(object: TimerTask() {
            override fun run() {
                try {
                    val currentTime = formatTime(mediaPlayer.currentPosition.toLong())
                    musicTimeCount.text = currentTime
                } catch (e: Exception) {}
            }
        }, 0, 1000)
    }

    @SuppressLint("NewApi")
    private fun formatTime(mls: Long): String {
        var time = ""
        val minutes = (mls/1000) / 60
        val seconds = (mls/1000) % 60
        time = "$time$minutes:"
        if (seconds < 10) { time+= '0' }
        return "$time$seconds"
    }

    companion object {
        const val CHILD_FAVOURITE_MUSIC = "favouriteMusic"
    }
}

class MusicViewModelFactory(
        private var pos: Int,
        private val seekBar: SeekBar,
        private val musicTimeTotal: TextView,
        private val musicTimeCount: TextView,
        private val musicFavourite: ImageView,
        private val musicPlay: ImageView,
        private val progressBar: ProgressBar
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MusicViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MusicViewModel(pos, seekBar, musicTimeTotal, musicTimeCount, musicFavourite, musicPlay, progressBar)
                    as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }
}