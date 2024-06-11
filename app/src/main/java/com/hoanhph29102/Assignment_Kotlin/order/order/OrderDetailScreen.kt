package com.hoanhph29102.Assignment_Kotlin.order.order

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.hoanhph29102.Assignment_Kotlin.activity.HeaderWithBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(orderId: String, navController: NavController) {

    val orderViewModel : OrderViewModel = viewModel(
        factory = OrderViewModelFactory(OrderService.getInstance())
    )
    val orders by orderViewModel.orderDetail.collectAsState()

    orderViewModel.getDetailOrder(orderId)
    Scaffold(
        topBar = {
            HeaderWithBack(modifier = Modifier, text = "Order Detail", navController = navController, onBackClick = {
                navController.popBackStack()
            })
        }
    ) {paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)){
            orders?.let { order ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "Order no${order.OrderID}")
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Date: ",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Gray
                                )
                                Text(text = order.date, style = MaterialTheme.typography.titleMedium)
                            }
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Name : ",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Gray
                                )
                                Text(text = order.nameUser, style = MaterialTheme.typography.titleMedium)
                            }
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Address: ",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Gray
                                )
                                Text(text = order.addressUser, style = MaterialTheme.typography.titleMedium)
                            }
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Number Card: ",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Gray
                                )
                                Text(text = order.paymentUser, style = MaterialTheme.typography.titleMedium)
                            }
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Quantity: ",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "${order.totalQuantity}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Products: ",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Gray
                                )
                                val productsInfo =
                                    order.items.joinToString { "${it.nameProduct} - ${it.quantity} \n" }

                                Text(
                                    text = productsInfo,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(start = 12.dp)
                                )
                            }
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Status: ",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "Delivering",
                                    style = MaterialTheme.typography.titleMedium.copy(color = Color.Green)
                                )
                            }

                        }
                    }
                }
            }
        }

    }
}

//@Preview(showSystemUi = true, showBackground = true)
//@Composable
//fun PreviewOrderDetail() {
//    OrderDetailScreen()
//}