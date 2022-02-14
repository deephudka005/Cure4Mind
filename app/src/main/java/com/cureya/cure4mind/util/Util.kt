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

const val defaultProfilePic = "https://firebasestorage.googleapis.com/v0/b/cureyadraft.appspot.com/o/static%2Fdefault_profile_pic.png?alt=media&token=a52249e9-4de2-4e67-8e73-fa71ff26f289"
