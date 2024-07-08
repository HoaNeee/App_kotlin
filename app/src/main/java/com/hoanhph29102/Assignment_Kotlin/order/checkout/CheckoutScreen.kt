package com.hoanhph29102.Assignment_Kotlin.order.checkout

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.hoanhph29102.Assignment_Kotlin.ProgressDialog
import com.hoanhph29102.Assignment_Kotlin.activity.ButtonSplash
import com.hoanhph29102.Assignment_Kotlin.activity.DialogNotify
import com.hoanhph29102.Assignment_Kotlin.activity.HeaderWithBack
import com.hoanhph29102.Assignment_Kotlin.address.Address
import com.hoanhph29102.Assignment_Kotlin.favorite.CustomSnackbar
import com.hoanhph29102.Assignment_Kotlin.notify.Notification
import com.hoanhph29102.Assignment_Kotlin.notify.NotificationViewModel
import com.hoanhph29102.Assignment_Kotlin.notify.NotificationViewModelFactory
import com.hoanhph29102.Assignment_Kotlin.notify.NotifyService
import com.hoanhph29102.Assignment_Kotlin.notify.showOrderNotification
import com.hoanhph29102.Assignment_Kotlin.order.order.OrderService
import com.hoanhph29102.Assignment_Kotlin.order.order.OrderViewModel
import com.hoanhph29102.Assignment_Kotlin.order.order.OrderViewModelFactory
import com.hoanhph29102.Assignment_Kotlin.paymentMethod.PaymentMethod
import com.hoanhph29102.Assignment_Kotlin.profile.User
import com.hoanhph29102.assignment_kotlin.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(navController: NavController, totalMoney: Double) {

    val checkOutViewModel: CheckOutViewModel = viewModel()
    val user by checkOutViewModel.user.collectAsState()

    checkOutViewModel.setTotalMoney(totalMoney)
    val totalAmount by checkOutViewModel.totalMoney.collectAsState()

    val orderViewModel: OrderViewModel = viewModel(factory = OrderViewModelFactory(OrderService.getInstance()))
    val orderId by orderViewModel.orderId.observeAsState()


    val notifyService = NotifyService.getInstance()
    val notifyViewModel: NotificationViewModel = viewModel(
        factory = NotificationViewModelFactory(notifyService)
    )
    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarMes by notifyViewModel.snackbarMessage.observeAsState()

    val notifications by notifyViewModel.notifications.observeAsState(emptyList())

    var showDialog by remember { mutableStateOf(false) }
    var showDialogNotify by remember {
        mutableStateOf(false)
    }

    val isLoadingData by checkOutViewModel.isLoading.observeAsState(false)
    val isLoadingOrder by orderViewModel.isLoading.observeAsState(false)
    val isSuccess by orderViewModel.isSuccess.observeAsState(false)
    //orderViewModel.fetDefaultUser()

    val context = LocalContext.current

    LaunchedEffect(Unit){
        checkOutViewModel.fetchUserDetail()
    }

    Box(modifier = Modifier.background(color = Color.Transparent)){
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            topBar = {
                HeaderWithBack(modifier = Modifier, text = "Check Out", navController = navController, onBackClick = {
                    navController.popBackStack()
                })
            },
            bottomBar = {
                ButtonSplash(modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 12.dp)
                    .height(60.dp), text = "SUBMIT ORDER") {
                    showDialog = true
                }
            }
        ){
            paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)){
                Column(modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceAround
                    ) {
                    user?.let { currentUser ->
                        val defaultAddresses = currentUser.addresses.find { it.isDefault }
                        val defaultPayments = currentUser.paymentMethods.find { it.isDefault }

                        ShippingAddressCheckout(defaultAddress = defaultAddresses, user = user!!, navController = navController)
                        PaymentMethodCheckout(defaultPayment = defaultPayments, navController = navController)
                    }
                    TotalCheckout(totalAmount = totalAmount)

                }
                if (isLoadingData || isLoadingOrder){
                    ProgressDialog()
                }
                if (showDialogNotify){
                    DialogNotify(
                        onConfirmation = { showDialogNotify = false },
                        dialogTitle = "Oops!",
                        dialogMessage = "Bạn chưa chọn địa chỉ giao hàng hoặc phương thức thanh toán kìa!")
                }
                LaunchedEffect(orderId) {
                    orderId?.let { id ->
                        user?.let { currentUser ->
                            notifyViewModel.sendNotification(currentUser.uid, "Đơn hàng được tạo thành công", "Bạn vừa tạo thành công đơn hàng có mã Order No$id")
                            navController.navigate("submitSuccess") {
                                popUpTo("home") { inclusive = false }
                            }
                            showOrderNotification(context,id)
                            orderViewModel.clearCart(currentUser.uid)
                        }
                    }
                }
            }
        }
    }
    DialogConfirm(showDialog = showDialog,
        title = "Thông báo",
        text = "Bạn có chắc muốn xác nhận đơn hàng không?",
        onDismiss = {
                    showDialog = false
        },
        onConfirm = {
            user?.let { currentUser ->
                val defaultAddress = currentUser.addresses.find { it.isDefault }
                val defaultPayment = currentUser.paymentMethods.find { it.isDefault }

                if (defaultAddress != null && defaultPayment != null) {
                    orderViewModel.createOrder(
                        userId = currentUser.uid,
                        name = currentUser.name,
                        address = defaultAddress.address,
                        payment = defaultPayment.cardNumber
                    )

                    //Log.d("CheckoutScreen", "Notifications: ${notificationViewModel.notifications.value}")
//                    navController.navigate("submitSuccess"){
//                        popUpTo("home"){inclusive = false}
//                    }

                } else {
                    showDialogNotify = true
                }

                //orderViewModel.clearCart(currentUser.uid)
                //notifyViewModel.sendNotification(currentUser.uid,"Đơn hàng được tạo thành công", "Bạn vừa tạo thành công đơn hàng có mã ")
            }
        }
        )
    snackBarMes?.let {
        CustomSnackbar(
            snackbarHostState = snackbarHostState,
            message = it,
            actionLabel = "Đóng",
            duration = SnackbarDuration.Short,
            onDismiss = {
                notifyViewModel.clearSnackbarMessage()
            }
        )
    }
}

@Composable
fun ShippingAddressCheckout(defaultAddress: Address?,user: User,navController: NavController) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(6.dp)

    ) {
        TitleItemCheckout(text = "Shopping Address",
            onIconClick = {
            navController.navigate("shippingAddress"){
                launchSingleTop = true
                restoreState = true
            }
        })
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
            ,
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
            ) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(12.dp)
                )
                Divider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = defaultAddress?.address ?:"You have not selected a default address!",
                    modifier = Modifier.padding(12.dp),
                    fontSize = 15.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun PaymentMethodCheckout(defaultPayment: PaymentMethod?, navController: NavController) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(6.dp)
    ) {
        TitleItemCheckout(text = "Payment",
            onIconClick = {
                navController.navigate("paymentMethod")
            })
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
            ,
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Row(modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Image(painterResource(id = R.drawable.mastercard2 ) , contentDescription = "Image method", modifier = Modifier
                    .padding(start = 12.dp)
                    .size(45.dp))
                Text(text = defaultPayment?.cardNumber ?:"You have not selected a default payment method!", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
fun TotalCheckout(totalAmount: Double) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(6.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
            ,
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceAround
                ) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                    Text(text = "Order", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                    Text(text = "$ $totalAmount", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Delivery", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                    Text(text = "$ 5.00", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Total:", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                    Text(text = "$ ${totalAmount + 5.00}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }

            }
        }
    }
}


@Composable
fun TitleItemCheckout(text: String,onIconClick: () -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text,
            style = MaterialTheme.typography.titleMedium,
            color = Color.Gray)
        Icon(Icons.Default.Edit,
            contentDescription = "Edit shipping address",
            modifier = Modifier.clickable {
            onIconClick()
        })
    }
}

@Composable
fun DialogConfirm(
    showDialog: Boolean,
    title: String,
    text: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text(text = title) },
            text = { Text(text = text) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    }
                ) {
                    Text("Xác nhận")
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text("Hủy")
                }
            }
        )
    }
}


//@Preview(showBackground = true)
//@Composable
//fun PreviewCheckoutTest() {
//    TotalCheckout()
//}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun PreviewCheckout() {
//    CheckoutScreen()
//}