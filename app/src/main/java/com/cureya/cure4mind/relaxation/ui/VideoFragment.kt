package com.cureya.cure4mind.relaxation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.cureya.cure4mind.R
import com.cureya.cure4mind.databinding.FragmentRelaxationVideoBinding
import com.cureya.cure4mind.relaxation.viewModel.VideoViewModel
import com.cureya.cure4mind.relaxation.viewModel.VideoViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView


class VideoFragment : Fragment() {

    private lateinit var binding: FragmentRelaxationVideoBinding
    private val navArgument: VideoFragmentArgs by navArgs()

    private lateinit var viewModel: VideoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRelaxationVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, VideoViewModelFactory(
            binding.youtubePlayerView, navArgument.videoUrl))[VideoViewModel::class.java]

        viewLifecycleOwner.lifecycle.addObserver(binding.youtubePlayerView)

        Toast.makeText(context, "Please wait! This may take a while", Toast.LENGTH_LONG).show()
    }

    override fun onStart() {
        val bottomView = (activity as AppCompatActivity)
            .findViewById<BottomNavigationView>(R.id.nav_view)
        bottomView.visibility = View.GONE

        super.onStart()
    }

    override fun onStop() {
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val bottomView = (activity as AppCompatActivity)
            .findViewById<BottomNavigationView>(R.id.nav_view)
        bottomView.visibility = View.VISIBLE

        super.onStop()
    }
}