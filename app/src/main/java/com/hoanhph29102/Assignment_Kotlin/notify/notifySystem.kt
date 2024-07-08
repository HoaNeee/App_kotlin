package com.hoanhph29102.Assignment_Kotlin.notify

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.hoanhph29102.assignment_kotlin.R

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Order Notification"
        val descriptionText = "Notification for order success"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("order_channel", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

fun showOrderNotification(context: Context, orderId: String) {
    val notificationId = 1
    val builder = NotificationCompat.Builder(context, "order_channel")
        .setSmallIcon(R.drawable.notify) // Thay thế bằng icon của bạn
        .setContentTitle("Đơn hàng được tạo thành công")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(false)

    // Kiểm tra quyền
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, builder.build())
            }
        } else {
            // Yêu cầu quyền
            ActivityCompat.requestPermissions(
                (context as Activity),
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }
    } else {
        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }
}




fun checkAndRequestNotificationPermission(activity: Activity): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
            1
        )
        false
    } else {
        true
    }
}