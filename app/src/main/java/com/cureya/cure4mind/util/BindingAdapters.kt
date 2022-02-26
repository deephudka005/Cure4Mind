package com.cureya.cure4mind.util

import android.view.View
import androidx.databinding.BindingAdapter

// to be used in layouts
@BindingAdapter("setVisibility")
fun setProgressVisibility(view: View, loadingStatus: Boolean) {
    if (loadingStatus) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.GONE
    }
}

@BindingAdapter("progressVisibility")
fun setProgressWithMedia(view: View, isPlaying: Boolean?) {
    if (isPlaying == true) {
        view.visibility = View.GONE
    } else {
        view.visibility = View.INVISIBLE
    }
}