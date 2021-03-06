package com.cureya.cure4mind.community.models

import android.os.Parcelable
import com.cureya.cure4mind.util.defaultProfilePic
import kotlinx.parcelize.Parcelize
import java.util.*


@Parcelize
data class Post(
    val postId: String = "",
    val caption: String = "",
    val photoUrl: String? = null,
    val commentCount: Int = 0,
    val createdAt: Date = Date(),
    val likes: List<String> = listOf(),
    val saved: List<String> = listOf(),
    val shares: Int = 0,
    val userId: String = "",
    val profilePhoto: String = defaultProfilePic,
    val userName: String = "",
    val tags: List<TAG> = listOf()
) : Parcelable

@Parcelize
enum class TAG : Parcelable {
    STRESS, ANXIETY, PARANOIA, PSYCHOSIS, DEPRESSION,
}