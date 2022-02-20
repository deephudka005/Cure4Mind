package com.cureya.cure4mind.chat.notification

import com.cureya.cure4mind.chat.data.models.PushNotification
import com.cureya.cure4mind.community.Constants
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface NotificationApi {

    @POST("fcm/send")
    suspend fun postNotification(
       @Body data:PushNotification,
       @Header("Authorization") key : String = "key=${Constants.SERVER_KEY}",
       @Header("Content-type") contentType : String = Constants.CONTENT_TYPE
    ) : Response<ResponseBody>
}