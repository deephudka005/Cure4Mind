package com.cureya.cure4mind.util

import android.view.View
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter

@BindingAdapter("setVisibility")
fun setProgressVisibility(view: ProgressBar, loadingStatus: Boolean) {
    if (loadingStatus) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.GONE
    }
}