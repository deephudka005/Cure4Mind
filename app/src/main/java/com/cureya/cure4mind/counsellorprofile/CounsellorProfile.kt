package com.cureya.cure4mind.counsellorprofile

import com.cureya.cure4mind.util.defaultProfilePic
import java.util.*

data class Profile(
    var userId:String = "",
    val photoUrl: String = defaultProfilePic,
    val email: String = "Not provided",
    val gender: String = "Not provided",
    val qualification: String = "",
    val occupation:String = "",
    val experience:String = "",
    val joinedGroups: Int = 0,
    val joinedCureya: Date = Date(),
    val about: String = ""
)