package com.hoanhph29102.Assignment_Kotlin.address

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hoanhph29102.Assignment_Kotlin.ProgressDialog
import com.hoanhph29102.Assignment_Kotlin.activity.HeaderWithBack
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShippingAddressScreen(navController: NavController) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val defaultUsername = currentUser?.displayName ?: ""
    val coroutineScope = rememberCoroutineScope()
    val viewModel : ShippingAddressViewModel = viewModel()

    val isLoading by viewModel.isLoading.observeAsState(false)

    LaunchedEffect(Unit){
        viewModel.loadAddresses()
    }

    Scaffold(
        topBar = {
            HeaderWithBack(modifier = Modifier, text = "Address", navController = navController, onBackClick = {
                navController.popBackStack()
            })
        },
        content = {paddingValues ->
            Box(modifier = Modifier.padding(paddingValues),
                ) {
                if (viewModel.addressList.isEmpty()) {
                    Text(text = "Please add a new address...", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(viewModel.addressList) { address ->
                            CardShippingAddress(address = address, defaultUsername, onDefaultChange = { isChecked ->
                                coroutineScope.launch {
                                    viewModel.updateDefaultAddress(address,isChecked)
                                }
                            },
                                onClickDetail = {
                                    navController.navigate("addressDetail/${address.idAddress}")
                                })
                        }
                    }
                }
                if (isLoading){
                    ProgressDialog()
                }
            }
            
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("addShippingAddress")
            },
                shape = CircleShape,
                containerColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add address")
            }
        }
    )
}


@Composable
fun CardShippingAddress(address: Address, nameUser: String, onDefaultChange: (Boolean) -> Unit,onClickDetail: () -> Unit) {
    var isDefault by remember { mutableStateOf(address.isDefault) }
    Column(modifier = Modifier,
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(12.dp)
                .clickable {
                    onClickDetail()
                }
            ,
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text(
                        text = nameUser,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Icon(Icons.Default.Edit, contentDescription = "")
                }
                Divider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = address.address,
                    modifier = Modifier.padding(12.dp),
                    fontSize = 15.sp,
                    color = Color.Gray
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
            ) {
            Checkbox(checked = address.isDefault, onCheckedChange = {isChecked ->
                isDefault = isChecked
                onDefaultChange(isChecked)

            }
            )
            Text(text = "Use as the shipping address", style = MaterialTheme.typography.titleMedium)
        }
    }
}

suspend fun updateDefaultAddress(address: Address, isDefault: Boolean) {
    val firestore = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    if (userId != null) {
        try {
            // Cập nhật trường isDefault của địa chỉ trong Firestore
            firestore.collection("user").document(userId).collection("address")
                .document(address.idAddress)
                .update("isDefault", isDefault)
                .await()
        } catch (e: Exception) {
            Log.e("TAG", "updateDefaultAddress: $e", )
        }
    }
}
//@Preview(showBackground = true)
//@Composable
//fun PreviewShippingTest() {
//    CardShippingAddress()
//}

//@Preview(showSystemUi = true, showBackground = true)
//@Composable
//fun PreviewShippingAddress() {
//    ShippingAddressScreen()
//}