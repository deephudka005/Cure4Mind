package com.cureya.cure4mind.chat.ui.fragments.chatList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.cureya.cure4mind.R
import com.cureya.cure4mind.chat.ui.adapters.AllUsersRecyclerAdapter
import com.cureya.cure4mind.util.database
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.tasks.await


class ChatListFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    private lateinit var allUsersRecycler: RecyclerView
    private lateinit var chatUsersRecycler: RecyclerView

    private lateinit var allUsersAdapter: AllUsersRecyclerAdapter

    private val chatListViewModel: ChatListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.chat_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMembers(view)
        setMessageToken()
        setLayoutManager()
        setAdapters()
        observeData()
    }

    private fun initMembers(view: View) {
        val navController = findNavController()
        auth = FirebaseAuth.getInstance()
        allUsersRecycler = view.findViewById(R.id.all_users_recycler)
        chatUsersRecycler = view.findViewById(R.id.chat_users_recycler)
        allUsersAdapter = AllUsersRecyclerAdapter(requireContext(), { user ->
            val direction = ChatListFragmentDirections.actionChatListFragmentToChatFragment(user)
            navController.navigate(direction)
        }) {
            val direction = ChatListFragmentDirections.actionChatListFragmentToPersonalProfile(it)
            navController.navigate(direction)
        }
        view.findViewById<CircleImageView>(R.id.profile).load(auth.currentUser?.photoUrl)
    }

    private fun observeData() {
        chatListViewModel.getAllUsers().observe(viewLifecycleOwner) {
            Log.d(TAG, "observeData: ${it[0]}")
            allUsersAdapter.updateData(it)
        }
    }

    private fun setAdapters() {
        allUsersRecycler.adapter = allUsersAdapter
//        chatUsersRecycler.adapter = chatUsersAdapter
    }

    private fun setLayoutManager() {
        allUsersRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private fun setMessageToken() {
        lifecycleScope.launchWhenCreated {
            val token  = FirebaseMessaging.getInstance().token.await()
            database.child("message_tokens").child(auth.uid!!).setValue(token).await()
            Log.d(TAG, "setMessageToken: $token")
        }
    }

    companion object {
        private const val TAG = "CHAT_LIST_FRAGMENT"
    }

}