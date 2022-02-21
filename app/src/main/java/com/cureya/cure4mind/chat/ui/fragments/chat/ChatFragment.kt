package com.cureya.cure4mind.chat.ui.fragments.chat

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.cureya.cure4mind.chat.data.models.User
import com.cureya.cure4mind.chat.ui.adapters.ChatRecyclerAdapter
import com.cureya.cure4mind.databinding.FragmentOneOnOneChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ChatFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var dbReference: DatabaseReference
    private lateinit var user: User
    private lateinit var getContent: ActivityResultLauncher<String>
    private lateinit var chatRecyclerAdapter: ChatRecyclerAdapter

    private val chatViewModel: ChatViewModel by viewModels()

    private var _binding: FragmentOneOnOneChatBinding? = null
    private val binding get() = _binding!!
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOneOnOneChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        initMembers(view)
        chatViewModel.loadChats(user.userId!!)
        setupListeners(view)
        setupRecycler()
        observeData()
        scrollToBottom()
    }

    private fun initMembers(view: View) {
        dbReference =
            FirebaseDatabase.getInstance("https://cureyadraft-default-rtdb.asia-southeast1.firebasedatabase.app").reference
        auth = FirebaseAuth.getInstance()
        user = ChatFragmentArgs.fromBundle(requireArguments()).user
        chatRecyclerAdapter = ChatRecyclerAdapter(auth.uid!!)
        binding.apply {
            userName.text = user.name
            profile.load(user.photoUrl)

        }
        getContent =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                val contentResolver = requireActivity().contentResolver
                imageUri = null
                if (uri != null) {
                    contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                        cursor.moveToFirst()
                        val size = cursor.getLong(sizeIndex)
                        if (size < 2000000) {
                            imageUri = uri
                            previewImage(uri)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "File size exceeded 2MB",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
    }

    private fun setupRecycler() {
        binding.apply {
            chatRecycler.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            chatRecycler.adapter = chatRecyclerAdapter
        }
    }

    private fun setupListeners(view: View) {
        binding.apply {
            backButton.setOnClickListener {
                findNavController().popBackStack()
            }
            chatbar.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    sendButton.visibility = View.VISIBLE
                } else {
                    sendButton.visibility = View.GONE
                }
            }
            sendButton.setOnClickListener { sendMessage() }
            image.setOnClickListener {
                getContent.launch("image/*")
            }
            discardImage.setOnClickListener { discardImage() }
        }
    }

    private fun previewImage(uri: Uri) {
        binding.apply {
            imagePreview.load(uri)
            imagePreviewContainer.visibility = View.VISIBLE
        }
    }

    private fun discardImage() {
        binding.apply {
            imageUri = null
            imagePreviewContainer.visibility = View.GONE
        }
    }

    private fun sendMessage() {
            val text = binding.chatbar.text.toString()
            if (text.isEmpty() || text.isBlank()) {
                Toast.makeText(requireContext(), "Please provide a message", Toast.LENGTH_SHORT)
                    .show()
            } else {
                prepareSend()
                chatViewModel.sendMessage(user.userId!!,text, imageUri) {
                    binding.apply {
                        imagePreviewContainer.visibility=View.GONE
                        imageSendingProgress.visibility = View.GONE
                        sendButton.isEnabled = true
                    }
                }
            }
            binding.chatbar.text.clear()
            scrollToBottom()
    }

    private fun scrollToBottom() =
        binding.chatRecycler.scrollToPosition(chatRecyclerAdapter.itemCount - 1)

    private fun observeData() {
        chatViewModel.getChats().observe(viewLifecycleOwner) {
            chatRecyclerAdapter.updateData(it.messages)
        }
    }



    private fun prepareSend() {
        binding.apply {
            imageSendingProgress.visibility = View.VISIBLE
            sendButton.isEnabled = false
        }
    }

    companion object {
        private const val TAG = "CHAT_FRAGMENT"
    }
}