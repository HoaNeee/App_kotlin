package com.hoanhph29102.Assignment_Kotlin.notify

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.hoanhph29102.Assignment_Kotlin.activity.ButtonSplash
import com.hoanhph29102.assignment_kotlin.R
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@Composable
fun NotifyScreen(navController: NavController) {
    val notifyService = NotifyService.getInstance()

    val notificationViewModel: NotificationViewModel = viewModel(
        factory = NotificationViewModelFactory(notifyService)
    )

    val notifyDetails by notificationViewModel.notifyDetail.observeAsState(null)


    val notificationsState = notificationViewModel.notifications.observeAsState(emptyList())
    val notifications = notificationsState.value

    val user = FirebaseAuth.getInstance().currentUser
    val userId = user?.uid

    var showDialog by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(userId) {
        userId?.let {
            notificationViewModel.fetchNotifications(it)
        }
    }

    //Log.e("Notify", "NotifyScreen: ${notificationViewModel.notifications.value}", )
    Box(modifier = Modifier
        .fillMaxSize(),
        contentAlignment = Alignment.Center
        ){
        if (notifications.isNotEmpty()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp)
            ) {
                items(notifications) { notification ->
                    ItemNotify(notification = notification, onClickItem = {
                        showDialog = true
                        notificationViewModel.getNotifyDetail(notification._id)
                    })

                }
            }
        }else {
            Text(text = "Not notification")
        }
        if (showDialog){
            DialogDetailNotify(
                onConfirmation = { showDialog = false },
                dialogTitle = notifyDetails?.title ?: "Not found",
                dialogMessage = notifyDetails?.description ?: "Not found"
            )
        }

    }
}

@Composable
fun ItemNotify(
    notification: Notification,
    onClickItem: (String) -> Unit
) {
    val timeAgo = getTimeAgo(notification.createAt.time)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable {
                onClickItem(notification._id)
            }
        ,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ){
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),

            ){
            Row(modifier = Modifier.fillMaxSize(),

                ) {
                Image(
                    painter = painterResource(id = R.drawable.notify2),
                    contentDescription = "",
                    modifier = Modifier
                        .height(50.dp)
                        .width(50.dp)
                        .align(Alignment.CenterVertically)
                )
                Column(modifier = Modifier.padding(top = 12.dp, start = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                    Text(text = notification.title, style = MaterialTheme.typography.titleMedium)
                    Text(text = notification.description,
                        style = MaterialTheme.typography.titleSmall.copy(color = Color.Gray),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(text = timeAgo, style = MaterialTheme.typography.titleSmall.copy(color = Color.Gray, fontSize = 12.sp))
                }
            }
        }
    }

}

fun getTimeAgo(time: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - time

    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> "just now"
        diff < TimeUnit.MINUTES.toMillis(60) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)} minutes ago"
        diff < TimeUnit.HOURS.toMillis(24) -> "${TimeUnit.MILLISECONDS.toHours(diff)} hours ago"
        else -> "${TimeUnit.MILLISECONDS.toDays(diff)} days ago"
    }
}

@Composable
fun DialogDetailNotify(
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogMessage: String
) {
    Dialog(onDismissRequest = onConfirmation,) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
            modifier = Modifier
                .width(300.dp)
                .padding(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(dialogTitle, style =
                MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(20.dp))
                Text(dialogMessage, style =
                MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(20.dp))
                ButtonSplash(modifier = Modifier
                    .width(80.dp)
                    .height(40.dp)
                    .align(Alignment.End), text = "OK", onclick = onConfirmation)
            }
        }
    }
}




//@Preview
//@Composable
//fun previewTest() {
//    ItemNotify()
//}
