package com.cureya.cure4mind.chat.data.models

data class Notification(
    val title: String = "",
    val message : String = ""
)

data class PushNotification(
    val data:Notification,
    val to : String
)