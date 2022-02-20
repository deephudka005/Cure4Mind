package com.cureya.cure4mind.chat.notification

import com.cureya.cure4mind.community.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NotificationApiInstance {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val api by lazy {
        retrofit.create(NotificationApi::class.java)
    }
}