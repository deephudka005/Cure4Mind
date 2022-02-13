package com.cureya.cure4mind.model

import com.cureya.cure4mind.community.utils.defaultProfilePic
import java.util.*

data class User(
    val name: String?,
    val email: String?,
    val photoUrl: String = defaultProfilePic,
    val password: String?,
    val gender: String?,
    val joinedCureya : Date = Date()
)