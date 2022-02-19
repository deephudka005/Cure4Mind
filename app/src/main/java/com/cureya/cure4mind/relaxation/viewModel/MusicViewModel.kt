package com.cureya.cure4mind.relaxation.viewModel

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.cureya.cure4mind.R
import com.cureya.cure4mind.model.Content
import com.cureya.cure4mind.relaxation.ui.MusicVideoFragment
import com.cureya.cure4mind.relaxation.viewHolder.MusicViewHolder
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.lang.IllegalStateException
import java.util.*
import kotlin.IllegalArgumentException

@SuppressLint("StaticFieldLeak")
class MusicViewModel(
    private var pos: Int,
    private val seekbar: SeekBar,
    private val musicTimeTotal: TextView,
    private val musicTimeCount: TextView,
    private val musicPlay: ImageView,
    private val progressBar: ProgressBar
): ViewModel() {

    private lateinit var mediaPlayer: MediaPlayer
    private val contentList = mutableListOf<Content>()

    private var dbRef: DatabaseReference = FirebaseDatabase
        .getInstance("https://cure4mind-d687f-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

    fun createContentList() {
        dbRef.child(MusicVideoFragment.MUSIC_LIST).get()
            .addOnSuccessListener { snapshot ->
                snapshot.children.forEach {
                    val item = it.getValue(Content::class.java)!!
                    contentList.add(item)
                }
                val content = contentList[pos]
                playMusic(content.contentUrl!!)
            }
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

    fun getCurrentContent() = contentList[pos]

    fun setSeekbarUserProgress(progress: Int) = mediaPlayer.seekTo(progress)

    fun releaseMediaPlayer() = mediaPlayer.release()

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
}

class MusicViewModelFactory(
        private var pos: Int,
        private val seekBar: SeekBar,
        private val musicTimeTotal: TextView,
        private val musicTimeCount: TextView,
        private val musicPlay: ImageView,
        private val progressBar: ProgressBar
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MusicViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MusicViewModel(pos, seekBar, musicTimeTotal, musicTimeCount, musicPlay, progressBar)
                    as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }
}