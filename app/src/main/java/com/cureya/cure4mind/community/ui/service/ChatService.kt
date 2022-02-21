package com.cureya.cure4mind.community.ui.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class ChatService : FirebaseMessagingService() {
    companion object{
        private const val TAG = "CHAT_SERVICE"
    }
}