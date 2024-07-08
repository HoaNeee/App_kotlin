package com.hoanhph29102.Assignment_Kotlin.notify

import com.hoanhph29102.Assignment_Kotlin.api.Constants
import com.hoanhph29102.Assignment_Kotlin.favorite.Favorite
import com.hoanhph29102.Assignment_Kotlin.favorite.FavoriteService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface NotifyService {

    @POST("/notify/{userId}")
    suspend fun sendNotification(
        @Path("userId") userId: String,
        @Body notification: NotificationRequest
    )

    @GET("/notify/{userId}")
    suspend fun getNotify(@Path("userId") userId: String): List<Notification>

    @GET("/notify/notifyDetail/{notifyId}")
    suspend fun getNotifyDetail(@Path("notifyId") notifyId: String): Notification

    companion object {
        private var retrofitService: NotifyService? = null
        fun getInstance(): NotifyService {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(Constants.DOMAIN)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(NotifyService::class.java)
            }
            return retrofitService!!
        }
    }
}