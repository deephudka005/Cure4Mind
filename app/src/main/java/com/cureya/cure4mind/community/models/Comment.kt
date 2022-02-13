package com.cureya.cure4mind.community.models

import com.cureya.cure4mind.community.utils.defaultProfilePic
import java.util.*

class Comment(
    val text: String = "",
    val userId: String = "",
    val userName: String = "",
    val photoUrl: String = defaultProfilePic,
    val createdAt: Date = Date()
)