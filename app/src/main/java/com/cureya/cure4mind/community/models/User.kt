package com.cureya.cure4mind.community.models

import android.os.Parcelable
import com.cureya.cure4mind.util.defaultProfilePic
import kotlinx.parcelize.Parcelize


@Parcelize
class User(
    var userId: String = "",
    val photoUrl: String = defaultProfilePic,
    val name : String = "null",
    val email : String = "null"
) : Parcelable