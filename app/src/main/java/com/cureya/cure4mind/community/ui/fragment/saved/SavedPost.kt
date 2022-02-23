package com.cureya.cure4mind.community.ui.fragment.saved

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cureya.cure4mind.databinding.SavedPostFragmentBinding

class SavedPost : Fragment() {

    private lateinit var viewModel: SavedPostViewModel
    private lateinit var adapter: SavedPostRecyclerAdapter

    private var _binding: SavedPostFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SavedPostFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMembers()
        initUi()
        observeData()
    }

    private fun initMembers() {
        viewModel = ViewModelProvider(this)[SavedPostViewModel::class.java]
        adapter = SavedPostRecyclerAdapter({ }, {})
    }

    private fun initUi() {
        binding.apply {
            savedPostRecycler.adapter = adapter
            savedPostRecycler.layoutManager = LinearLayoutManager(requireContext())
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun observeData() {
        viewModel.posts.observe(viewLifecycleOwner) {
            adapter.loadPosts(it)
        }
    }

    companion object {
        private const val TAG = "SAVED_POST_FRAGMENT"
    }

}