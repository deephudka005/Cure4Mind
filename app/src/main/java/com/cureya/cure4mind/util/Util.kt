package com.cureya.cure4mind.util

import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

const val DATABASE_URL = "https://cure4mind-d687f-default-rtdb.asia-southeast1.firebasedatabase.app/"
const val STORAGE_BUCKET = "gs://cure4mind-d687f.appspot.com"

val database =
    FirebaseDatabase.getInstance("https://cure4mind-d687f-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

fun Date.toDateString(): String {
    val formatter = SimpleDateFormat("hh:mm, dd MMM", Locale.getDefault());
    return formatter.format(this)
}

const val defaultProfilePic = "https://firebasestorage.googleapis.com/v0/b/cure4mind-d687f.appspot.com/o/static%2Fdefault_profile_pic.png?alt=media&token=4d761fb9-d2c4-42ea-8a7d-a12d5e130f3e"
