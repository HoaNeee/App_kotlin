package com.hoanhph29102.Assignment_Kotlin.cart


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.hoanhph29102.Assignment_Kotlin.ProgressDialog
import com.hoanhph29102.Assignment_Kotlin.activity.ButtonSplash
import com.hoanhph29102.Assignment_Kotlin.activity.HeaderWithBack
import com.hoanhph29102.Assignment_Kotlin.product.Product
import com.hoanhph29102.Assignment_Kotlin.product.QuantityProduct
import com.hoanhph29102.Assignment_Kotlin.product.navigateToProductDetail

//import com.hoanhph29102.Assignment_Kotlin.product.getFakeFavoriteProducts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController) {
    //val items = getFakeFavoriteProducts(5)

    val cartService = CartService.getInstance()
    val cartViewModel : CartViewModel = viewModel(
        factory = CartViewModelFactory(cartService)
    )
    val cartProducts by cartViewModel.cartProducts.collectAsState()
    val totalCartPrice by cartViewModel.totalCartPrice.observeAsState(0.0)
    val user = FirebaseAuth.getInstance().currentUser
    val userId = user?.uid

    val isLoading by cartViewModel.isLoading.observeAsState(false)

    LaunchedEffect(cartProducts) {
        userId?.let { cartViewModel.getTotalMoney(it) }
    }

    LaunchedEffect(userId){
        userId?.let {
            cartViewModel.fetchCart(it)
            cartViewModel.getTotalMoney(it)
        }
    }

    Scaffold(
        topBar = {
                 HeaderWithBack(modifier = Modifier, text = "Cart", navController = navController, onBackClick = {
                    navController.popBackStack()
                 })
        },
        content = {paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                if (cartProducts.isNullOrEmpty() || cartProducts.size <= 0){
                    Text(text = "The shopping cart is currently empty!", modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.titleMedium)
                }
                LazyColumn(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    items(cartProducts){cart ->
                        ItemCart(cart = cart,
                            onQuantityUpdate = {newQuantity ->
                                cartViewModel.fetchCart(cart.userId)
                                cartViewModel.updateCartQuantity(cart._id, newQuantity)

                        },
                            navController = navController,
                        )
                    }
                }
                if (isLoading){
                    ProgressDialog()
                }
            }
        },
        bottomBar = {
            Column(modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
                ){

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Total: ", color = Color.Gray)

                    Text(text = "$ $totalCartPrice")
                }
                ButtonSplash(modifier = Modifier
                    .width(335.dp)
                    .height(60.dp)
                    .padding(8.dp),
                    text = "Check out",
                    onclick = {
                        navController.navigate("checkout_screen/${totalCartPrice}")
                    }
                    )
            }
        }
    )
}

@Composable
fun ItemCart(
    cart: Cart,
    onQuantityUpdate: (newQuantity: Int) -> Unit ,
    navController: NavController,
) {
    val quantityState = remember {
        mutableStateOf(cart.quantity)
    }
    val totalCartItemState = remember {
        mutableStateOf(cart.totalCartItem)
    }
    val cartService = CartService.getInstance()
    val cartViewModel : CartViewModel = viewModel(
        factory = CartViewModelFactory(cartService)
    )

    LaunchedEffect(quantityState.value) {
        totalCartItemState.value = cart.priceProduct * quantityState.value
        cartViewModel.getTotalMoney(cart.userId)
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .clickable {
            navigateToProductDetail(cart.productId, navController)
        }
    ){
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(7.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
//                AsyncImage(model = cart.imageProduct,
//                    contentDescription = "cart image",
//                    modifier = Modifier
//                        .align(Alignment.CenterVertically)
//                        .padding(bottom = 12.dp)
//                        .clip(
//                            RoundedCornerShape(12.dp)
//                        )
//                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray)
                        .width(70.dp)
                        .height(90.dp)
                        .padding(bottom = 12.dp)
                    ,

                    ) {
                    AsyncImage(
                        model = cart.imageProduct,
                        contentDescription = "img product",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Column(modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    Column {
                        Text(text = cart.nameProduct, style = MaterialTheme.typography.titleMedium.copy(color = Color.Gray))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "$ ${totalCartItemState.value}", style = MaterialTheme.typography.titleMedium)
                    }

                    QuantityCart(modifier = Modifier,
                        quantity = quantityState.value,
                        onMinus = {
                            if (quantityState.value > 1) {
                                quantityState.value -= 1
                                totalCartItemState.value -= cart.priceProduct
                                onQuantityUpdate(quantityState.value)
                            }
                        },
                        onPlus = {
                            quantityState.value += 1
                            onQuantityUpdate(quantityState.value)
                            totalCartItemState.value += cart.priceProduct
                        })
                }
            }

            Icon(Icons.Default.Close,
                contentDescription = "",
                modifier = Modifier.clickable {
                    cartViewModel.deleteCartItem(cart._id,cart.userId)
                }
                )

        }
        Divider(
            color = Color.LightGray,
            thickness = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun QuantityCart(
    modifier: Modifier,
    quantity: Int,
    onMinus: () -> Unit,
    onPlus: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Button(
            onClick = onMinus,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0x9ED8D8D8),
                contentColor = Color.Gray
            ),
            shape = RoundedCornerShape(5.dp),
            contentPadding = PaddingValues(1.dp),
            modifier = Modifier.size(30.dp)
        ) {
            Text("-", fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = "$quantity", fontSize = 16.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Button(
            onClick = onPlus,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0x9ED8D8D8),
                contentColor = Color.Gray
            ),
            shape = RoundedCornerShape(5.dp),
            contentPadding = PaddingValues(1.dp),
            modifier = Modifier.size(30.dp)
        ) {
            Text("+", fontSize = 20.sp)
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewCartTest() {
//    ItemCart()
//}

//@Preview
//@Composable
//fun PreviewCart() {
//    CartScreen()
//}