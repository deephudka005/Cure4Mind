package com.cureya.cure4mind.relaxation.ui

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.cureya.cure4mind.R
import com.cureya.cure4mind.databinding.CardMusicBinding
import com.cureya.cure4mind.databinding.FragmentRelaxationMusicBinding
import com.cureya.cure4mind.model.Content
import com.cureya.cure4mind.relaxation.ui.MusicVideoFragment.Companion.MUSIC_LIST
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.lang.IllegalStateException
import java.time.Duration
import java.util.*
import kotlin.math.abs

class MusicFragment : Fragment() {

    // private lateinit var adapter: FirebaseRecyclerAdapter<Content, CardMusicViewHolder>
    private lateinit var binding: FragmentRelaxationMusicBinding
    private lateinit var dbRef: DatabaseReference

    private lateinit var mediaPlayer: MediaPlayer
    private val contentList = mutableListOf<Content>()
    private var position = 0

    private val navArgument: MusicFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRelaxationMusicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbRef = FirebaseDatabase.getInstance("https://cure4mind-d687f-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

        position = navArgument.itemPosition

        createContentList()

        binding.musicPlay.setOnClickListener { handleMusicState() }

        binding.musicNext.setOnClickListener { playNextMusic() }

        binding.musicPrevious.setOnClickListener { playPreviousMusic() }

        // seekbar music control
        binding.musicSeekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2) { mediaPlayer.seekTo(p1) }
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        binding.backButton.setOnClickListener { findNavController().navigateUp() }
    }

    private fun createContentList() {
        dbRef.child(MUSIC_LIST).get()
            .addOnSuccessListener { snapshot ->
                snapshot.children.forEach {
                    val item = it.getValue(Content::class.java)!!
                    contentList.add(item)
                }
                val content = contentList[position]
                playMusicWithUrl(content.contentUrl!!, content.title!!, content.thumbnailUrl!!)
            }
    }

    private fun playMusicWithUrl(url: String, title: String, imgUrl: String) {
        binding.musicHeading.text = title
        binding.musicImage.load(imgUrl)

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
        binding.progressBar.visibility = View.GONE
        setSeekBar()
        updateTimer()
    }

    private fun setSeekBar() {
        binding.musicTimeTotal.text = formatTime(mediaPlayer.duration.toLong())

        binding.musicSeekbar.max = mediaPlayer.duration

        Timer().scheduleAtFixedRate(object: TimerTask() {
            override fun run() {
                try {
                    binding.musicSeekbar.progress = mediaPlayer.currentPosition
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
                    binding.musicTimeCount.text = currentTime
                } catch (e: Exception) {}
            }
        }, 0, 1000)
    }

    private fun playNextMusic() {
        mediaPlayer.stop()
        mediaPlayer.release()

        binding.progressBar.visibility = View.VISIBLE

        // to loop back once we are at the edge of the list
        position = (position + 1) % contentList.size

        val content = contentList[position]
        playMusicWithUrl(content.contentUrl!!, content.title!!, content.thumbnailUrl!!)
    }

    private fun playPreviousMusic() {
        mediaPlayer.stop()
        mediaPlayer.release()

        binding.progressBar.visibility = View.VISIBLE

        // to loop through the list
        if (position - 1 < 0) {
            position = contentList.size - 1
        } else { --position }

        val content = contentList[position]
        playMusicWithUrl(content.contentUrl!!, content.title!!, content.thumbnailUrl!!)
    }

    private fun handleMusicState() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            binding.musicPlay.setImageResource(R.drawable.ic_play)
        } else {
            mediaPlayer.start()
            binding.musicPlay.setImageResource(R.drawable.asset_relaxation_music_playing)
        }
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

    override fun onPause() {
        super.onPause()
        mediaPlayer.release()
    }
}