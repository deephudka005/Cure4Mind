package com.cureya.cure4mind.chat.ui.fragments.chat

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cureya.cure4mind.chat.data.models.Chat
import com.cureya.cure4mind.chat.data.models.Message
import com.cureya.cure4mind.chat.data.models.Notification
import com.cureya.cure4mind.chat.data.models.PushNotification
import com.cureya.cure4mind.chat.notification.NotificationApiInstance
import com.cureya.cure4mind.util.STORAGE_BUCKET
import com.cureya.cure4mind.util.database
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class ChatViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val chat = MutableLiveData<Chat>()
    private val storageRef = Firebase.storage(STORAGE_BUCKET).reference

    fun getChats(): LiveData<Chat> {
        return chat
    }

    fun loadChats(receiverId: String) {
        viewModelScope.launch {
            FirebaseMessaging.getInstance()
                .subscribeToTopic("notifications${getChatId(receiverId)} ")
            val messages =
                database.child("chats").child(getChatId(receiverId)).child("messages").get()
                    .await().children.map { it.getValue(Message::class.java)!! }
            chat.value = Chat(messages)
            listenForChatValueChanges(receiverId)
        }
    }

    fun sendMessage(
        receiverId: String,
        text: String? = null,
        imageUri: Uri? = null,
        next: () -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                if (text == null && imageUri == null) {
                    throw IllegalArgumentException("Both message text and image uri are null")
                } else {
                    var url: String? = null
                    imageUri?.let {
                        val imageRef =
                            storageRef.child("chats/images/${getChatId(receiverId)}/IMG000${System.currentTimeMillis()}")
                        imageRef.putFile(imageUri).await()
                        url = imageRef.downloadUrl.await().toString()
                    }
                    val message = Message(
                        text = text!!,
                        photoUrl = url,
                        senderId = auth.uid!!,
                        receiverId = receiverId
                    )
                    database.child("chats").child(getChatId(receiverId)).child("messages").push()
                        .setValue(message).await()
                    updateLastMessage(message)
                    val token = database.child("message_tokens").child(receiverId).get().await()
                        .getValue(String::class.java)
                    val senderName =
                        database.child("users").child(receiverId).child("name").get().await()
                            .getValue(
                                String::class.java
                            )
                    token?.let {
                        sendNotification(
                            PushNotification(
                                Notification(title = senderName.toString(), message = text),
                                token
                            )
                        )
                    }
                    next()
                }
            } catch (e: Exception) {

            }
        }
    }

    private fun listenForChatValueChanges(receiverId: String) {
        val chatValueListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {}
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val messages =
                        dataSnapshot.child("messages").children.map { it.getValue(Message::class.java)!! }
                    val userChat = Chat(messages)
                    chat.value = userChat
                } else {
                    chat.value = Chat()
                }
            }
        }
        database.child("chats").child(getChatId(receiverId))
            .addValueEventListener(chatValueListener)
    }


    private suspend fun updateLastMessage(message: Message) {
        database.child("last_message").child(getChatId(message.receiverId!!)).setValue(message)
            .await()
    }


    private fun getChatId(receiverId: String) =
        if (auth.uid!! > receiverId) auth.uid!! + receiverId else receiverId + auth.uid;


    private fun sendNotification(notification: PushNotification) {
        viewModelScope.launch {
            try {
                val response = NotificationApiInstance.api.postNotification(notification)
                Log.d(TAG, "sendNotification: $response")
            } catch (e: Exception) {
                Log.e(TAG, "sendNotification: ", e)
            }
        }
    }

    companion object {
        private const val TAG = "CHAT_FRAGMENT"
    }

}