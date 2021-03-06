package com.cureya.cure4mind.chat.data.models

import android.os.Parcelable
import com.cureya.cure4mind.chat.data.models.Message
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize


/*
* Data class representing a user in chats
* For chat we need the profile photo of user,
* name and the status, if user is a counselor or not
*/

@IgnoreExtraProperties
@Parcelize
open class User(
    var userId: String? = null,
    val name: String? = null,
    val photoUrl: String? = null,
    val isCounselor: Boolean = false,
    var lastMessage : Message? = null
) : Parcelable

