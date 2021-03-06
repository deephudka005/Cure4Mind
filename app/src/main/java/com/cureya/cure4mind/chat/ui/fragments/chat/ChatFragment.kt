package com.cureya.cure4mind.chat.ui.fragments.chat

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.cureya.cure4mind.util.shortToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ChatFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var dbReference: DatabaseReference
    private lateinit var user: User
    private lateinit var getImageContent: ActivityResultLauncher<String>
    private lateinit var getFileContent: ActivityResultLauncher<String>
    private lateinit var chatRecyclerAdapter: ChatRecyclerAdapter

    private val chatViewModel: ChatViewModel by viewModels()
    private var _binding: FragmentOneOnOneChatBinding? = null
    private val binding get() = _binding!!
    private var imageUri: Uri? = null
    private var fileUri: Uri? = null
    private var fileName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOneOnOneChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMembers()
        chatViewModel.loadChats(user.userId!!)
        setupListeners()
        setupRecycler()
        observeData()
        scrollToBottom()
    }

    private fun initMembers() {
        dbReference =
            FirebaseDatabase.getInstance("https://cureyadraft-default-rtdb.asia-southeast1.firebasedatabase.app").reference
        auth = FirebaseAuth.getInstance()
        user = ChatFragmentArgs.fromBundle(requireArguments()).user
        chatRecyclerAdapter = ChatRecyclerAdapter(auth.uid!!) {
           Intent().apply {
               setDataAndType(Uri.parse(it.fileUrl), "application/pdf");
               startActivity(this)
           }
        }
        binding.apply {
            userName.text = user.name
            profile.load(user.photoUrl)
        }
        getImageContent = getActivityResultLauncherForFile(2000000) { uri, _ ->
            imageUri = uri
            previewImage(uri)
        }
        getFileContent = getActivityResultLauncherForFile(2000000) { uri, fileName ->
            fileUri = uri
            this.fileName=fileName
            previewFile(fileName)
        }
    }


    private fun setupRecycler() {
        binding.apply {
            chatRecycler.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            chatRecycler.adapter = chatRecyclerAdapter
        }
    }

    private fun setupListeners() {
        binding.apply {
            backButton.setOnClickListener {
                findNavController().popBackStack()
            }
            chatbar.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    sendButton.visibility = View.VISIBLE
                } else {
                    sendButton.visibility = View.GONE
                }
            }
            sendButton.setOnClickListener { sendMessage() }
            image.setOnClickListener {
                imageUri = null
                getImageContent.launch("image/*")
            }
            attach.setOnClickListener {
                fileUri = null
                getFileContent.launch("application/pdf")
            }
            discardImage.setOnClickListener { discardImage() }
            discardFile.setOnClickListener { discardFile() }
        }
    }

    private fun previewImage(uri: Uri) {
        discardFile()
        binding.apply {
            imagePreview.load(uri)
            imagePreviewContainer.visibility = View.VISIBLE
        }
    }

    private fun discardImage() {
        imageUri = null
        binding.imagePreviewContainer.visibility = View.GONE
        binding.imageSendingProgress.visibility = View.GONE
    }

    private fun previewFile(fileName: String) {
        discardImage()
        binding.filePreview.visibility = View.VISIBLE
        binding.fileName.text = fileName
    }

    private fun discardFile() {
        fileUri = null
        binding.filePreview.visibility = View.GONE
    }

    private fun sendMessage() {
        val text = binding.chatbar.text.toString()
        prepareSend()
        chatViewModel.sendMessage(user.userId!!, text, imageUri,fileUri,this.fileName) {
            binding.apply {
                binding.chatbar.text.clear()
                discardFile()
                discardImage()
                scrollToBottom()
                sendButton.isEnabled = true
            }
        }
    }

    private fun scrollToBottom() =
        binding.chatRecycler.scrollToPosition(chatRecyclerAdapter.itemCount)

    private fun observeData() {
        chatViewModel.getChats().observe(viewLifecycleOwner) {
            chatRecyclerAdapter.updateData(it.messages)
            scrollToBottom()
        }
    }


    private fun getActivityResultLauncherForFile(
        maxSize: Long,
        onResult: (Uri, String) -> Unit,
    ) = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        val contentResolver = requireActivity().contentResolver
        if (uri != null) {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                val fileNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                val size = cursor.getLong(sizeIndex)
                val fileName = cursor.getString(fileNameIndex)
                if (size < maxSize) {
                    onResult(uri, fileName)
                } else {
                    requireContext().shortToast("File size exceeded 2MB")
                }
            }
        }
    }

    private fun prepareSend() {
        binding.apply {
            imageSendingProgress.visibility = View.VISIBLE
            sendingFile.visibility = View.VISIBLE
            sendButton.isEnabled = false
        }
    }

    companion object {
        private const val TAG = "CHAT_FRAGMENT"
    }
}