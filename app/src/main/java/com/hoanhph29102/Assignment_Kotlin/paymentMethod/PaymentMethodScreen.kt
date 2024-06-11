package com.hoanhph29102.Assignment_Kotlin.paymentMethod

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.hoanhph29102.Assignment_Kotlin.ProgressDialog
import com.hoanhph29102.Assignment_Kotlin.activity.HeaderWithBack
import com.hoanhph29102.assignment_kotlin.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodScreen(navController: NavController) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val defaultUserName = currentUser?.displayName ?: ""
    val coroutineScope = rememberCoroutineScope()
    val viewModel: PaymentMethodViewModel = viewModel()

    val isLoading by viewModel.isLoading.observeAsState(false)

    LaunchedEffect(Unit){
        viewModel.loadPaymentMethods()
    }
    Scaffold(
        topBar = {
                 HeaderWithBack(modifier = Modifier, text = "Payment", navController = navController, onBackClick = {
                     navController.popBackStack()
                 })
        },
        bottomBar = {},
        floatingActionButton = {
            FloatingActionButton(onClick = {
                                           navController.navigate("addPaymentMethod")
            },
                shape = CircleShape,
                containerColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add paymentMethod")
            }
        }
    ) {paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)){
            if (viewModel.paymentMethodList.isEmpty()) {
                Text(text = "Please add a new payment method...", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(viewModel.paymentMethodList) { method ->
                            CardPaymentMethod(method = method, nameUser = defaultUserName, onDefaultChange = { isChecked ->
                                coroutineScope.launch {
                                    viewModel.updateDefaultPaymentMethod(method, isChecked)
                                }

                            },

                                onClickDetail = {
                                    navController.navigate("paymentDetail/${method.idMethod}")
                                }
                            )
                        }
                    }
                }
            if (isLoading){
                ProgressDialog()
            }
            }
        }
    }

@Composable
fun CardPaymentMethod(method: PaymentMethod, nameUser: String, onDefaultChange: (Boolean) -> Unit, onClickDetail: () -> Unit) {
    var isDefault by remember { mutableStateOf(method.isDefault) }
    Column(modifier = Modifier) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(12.dp)
                .clickable {
                           onClickDetail()
                }
            ,
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xEB1A1A1A)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .clip(shape = RectangleShape)
                        .background(Color.LightGray)
                        .width(31.dp)
                        .height(24.dp)
                    ,
                    ) {
                    Image(painterResource(id = R.drawable.mastercard2),
                        contentDescription = "",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,

                    )
//                    AsyncImage(
//                        model = R.drawable.ic_launcher_background,
//                        contentDescription = "img product",
//                        modifier = Modifier.fillMaxSize(),
//                        contentScale = ContentScale.Crop// Đảm bảo rằng hình ảnh lấp đầy Box
//                    )
                }
                Text(text = method.cardNumber, style = MaterialTheme.typography.headlineMedium, color = Color.White
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Card Holder Name", style = MaterialTheme.typography.titleSmall,color = Color(
                            0xEBC9C9C9
                        )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = nameUser,style = MaterialTheme.typography.titleSmall,color = Color.White)
                    }
                    Column {
                        Text(text = "Expiry Date", style = MaterialTheme.typography.titleSmall,color = Color(0xEBC9C9C9))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = method.expirationDate,style = MaterialTheme.typography.titleSmall,color = Color.White)
                    }
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
            ) {
            Checkbox(checked = method.isDefault, onCheckedChange = {isChecked ->
                isDefault = isChecked
                onDefaultChange(isChecked)
            }
            )
            Text(text = "Use as the payment method", style = MaterialTheme.typography.titleMedium)
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewPaymentTest() {
//Assignment_KotlinTheme {
//    CardPaymentMethod()
//}
//}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun PreviewPaymentScreen() {
//    Assignment_KotlinTheme {
//        PaymentMethodScreen()
//    }
//}