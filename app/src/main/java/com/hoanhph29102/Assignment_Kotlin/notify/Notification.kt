package com.hoanhph29102.Assignment_Kotlin.notify

import java.util.Date

data class Notification(
    val _id: String,
    val title: String,
    val description: String,
    val userId: String,
    val createAt: Date
){
    val createAtTime: Long
        get() = createAt.time
}

data class NotificationRequest(
    val title: String,
    val description: String
)