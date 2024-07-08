package com.hoanhph29102.Assignment_Kotlin.product

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.hoanhph29102.Assignment_Kotlin.cart.CartService
import com.hoanhph29102.Assignment_Kotlin.cart.CartViewModel
import com.hoanhph29102.Assignment_Kotlin.cart.CartViewModelFactory
import com.hoanhph29102.Assignment_Kotlin.favorite.CustomSnackbar
import com.hoanhph29102.Assignment_Kotlin.favorite.FavoriteService
import com.hoanhph29102.Assignment_Kotlin.favorite.FavoriteViewModel
import com.hoanhph29102.Assignment_Kotlin.favorite.FavoriteViewModelFactory
import com.hoanhph29102.Assignment_Kotlin.notify.Notification
import com.hoanhph29102.Assignment_Kotlin.notify.NotificationViewModel
import com.hoanhph29102.Assignment_Kotlin.notify.NotificationViewModelFactory
import com.hoanhph29102.Assignment_Kotlin.notify.NotifyService

@Composable
fun ProductDetailScreen(productId: String, navController: NavController) {
    val productViewModel: ProductViewModel = viewModel()
    val product by productViewModel.productDetails.collectAsState()

    productViewModel.getProductDetails(productId)

    val productQuantityState = remember { mutableStateOf(1) }

    val userId = FirebaseAuth.getInstance().currentUser?.uid

    //favorite
    val favoriteService = FavoriteService.getInstance()
    val favoriteViewModel: FavoriteViewModel = viewModel(
        factory = FavoriteViewModelFactory(favoriteService)
    )
    val favoriteProducts by favoriteViewModel.favoriteProducts.collectAsState()

    //cart
    val cartService = CartService.getInstance()
    val cartViewModel : CartViewModel = viewModel(
        factory = CartViewModelFactory(cartService)
    )
    val cartProducts by cartViewModel.cartProducts.collectAsState()

    //snackbar
    val notifyService = NotifyService.getInstance()
    val notifyViewModel: NotificationViewModel = viewModel(
        factory = NotificationViewModelFactory(notifyService)
    )
    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarMes by notifyViewModel.snackbarMessage.observeAsState()


    LaunchedEffect(userId) {
        userId?.let { favoriteViewModel.fetchFavoriteProducts(it) }
    }
    LaunchedEffect(userId) {
        userId?.let { cartViewModel.fetchCart(it) }
    }
    //val isFavorite = favoriteProducts.any { it.productId == productId }
    val isFavorite = favoriteProducts.any {it.userId == userId && it.productId == productId && it.isFavorite == true }
    // Fetch favorites when the screen is composed


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 70.dp) // Space for footer
        ) {
            product?.let { product ->
                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxWidth()
                ) {
                    HeaderProductDetail(product, navController)
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    BodyProductDetail(product,
                        quantity = productQuantityState.value,
                        onMinus = {
                                  if (productQuantityState.value > 1){
                                      productQuantityState.value -= 1
                                  }
                        },
                        onPlus = {productQuantityState.value += 1}
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
            FooterProductDetail(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                productId = productId,
                userId = userId?:"",
                isFavorite = isFavorite,
                quantity = productQuantityState.value,
                onAddToCartClick = {notifyViewModel.showSnackbar("Đã thêm vào giỏ hàng!")}
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

        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
        }

}

@Composable
fun HeaderProductDetail(product: Product,navController: NavController){
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(start = 30.dp)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 12.dp)

        ) {
            // Product Image
            AsyncImage(
                model = product.image,
                contentDescription = "Product Image",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape = RoundedCornerShape(bottomStart = 100.dp)),
                contentScale = ContentScale.FillBounds,
            )


        }
        Button(onClick = {

        },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(13.dp),
            contentPadding = PaddingValues(12.dp),
            modifier = Modifier
                .padding(top = 10.dp)
                .size(33.dp)
            ) {
            Icon(Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.clickable {
                    navController.popBackStack()
                }
                )
        }

        // Color Picker
        Button(onClick = {

        },
            modifier = Modifier.align(Alignment.CenterStart),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,

            ),
            elevation = ButtonDefaults.buttonElevation(12.dp),
            ) {
            Column(
                //modifier = Modifier.padding(vertical = 22.dp)
            ) {
                listOf(Color.Gray, Color.Yellow, Color.Blue).forEach { color ->
                    Box(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .size(26.dp)
                            .background(color, shape = CircleShape)
                            .clickable { /* Handle color selection */ }
                    )
                }
            }
        }
    }
}

@Composable
fun BodyProductDetail(
    product: Product,
    quantity: Int,
    onMinus: () -> Unit,
    onPlus: () -> Unit) {
    //Log.e("TAG", "BodyProductDetail: ${product.categoryId}", )
    Spacer(modifier = Modifier.height(15.dp))
    Column(modifier = Modifier.fillMaxWidth()){
        Text(text = product.name,
            fontSize = 25.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.SansSerif
            )
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Text(text = "$ ${product.price}", fontSize = 28.sp, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.weight(1f))
            QuantityProduct(modifier = Modifier,
                quantity = quantity,
                onMinus = onMinus,
                onPlus = onPlus
            )

        }
        Spacer(modifier = Modifier.height(8.dp))
        RateProduct(stars = product.stars.toString(),
            reviewCount = product.reviews.toString())
        Spacer(modifier = Modifier.height(13.dp))
        Text(text = product.description,
            lineHeight = 24.sp,
            color = Color.Gray,
            fontSize = 16.sp
            )
    }
}

@Composable
fun FooterProductDetail(
    modifier: Modifier,
    userId: String?,
    productId: String,
    isFavorite: Boolean,
    quantity: Int,
    onAddToCartClick: () -> Unit
    ) {

    val favoriteService = FavoriteService.getInstance()

    val favoriteViewModel: FavoriteViewModel = viewModel(
        factory = FavoriteViewModelFactory(favoriteService)
    )
    val cartService = CartService.getInstance()
    val cartViewModel : CartViewModel = viewModel(
        factory = CartViewModelFactory(cartService)
    )
    //val cartProducts by cartViewModel.cartProducts.collectAsState()
    //val favoriteProducts by favoriteViewModel.favoriteProducts.collectAsState()

    Row(modifier = modifier
        .fillMaxWidth()
        .height(50.dp)) {
        Button(onClick = {
            if (isFavorite) {
                userId?.let { favoriteViewModel.removeFromFavorites(it, productId) }
            } else {
                userId?.let { favoriteViewModel.addToFavorites(it, productId) }
            }
        },
            modifier = Modifier
                .fillMaxHeight()
                .size(60.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Gray,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(5.dp),
            contentPadding = PaddingValues(10.dp)
                ) {
            if (isFavorite) Icon(Icons.Default.Favorite, contentDescription = "add to favorite") else Icon(Icons.Default.FavoriteBorder, contentDescription = "add to favorite")
        }
        Spacer(modifier = Modifier.width(20.dp))
        Button(
            onClick = {
                      userId?.let {
                            cartViewModel.addToCart(it,productId, quantity = quantity)

                      }
                onAddToCartClick()
            },
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .padding(horizontal = 20.dp)
            ,
            shape = RoundedCornerShape(7.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            ),
            elevation = ButtonDefaults.buttonElevation(12.dp)
            ) {
            Text(
                text = "Add to cart",
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun QuantityProduct(
    modifier: Modifier,
    quantity: Int,
    onMinus: () -> Unit,
    onPlus: () -> Unit
) {

    Row(verticalAlignment = Alignment.CenterVertically){
        Button(onClick = onMinus,
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
        Button(onClick = onPlus,
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

@Composable
fun RateProduct(stars: String, reviewCount: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Star,
            contentDescription = "",
            tint = Color.Yellow,
            modifier = Modifier.size(30.dp)
            )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = stars, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = "($reviewCount review)", color = Color.Gray)
    }
}

fun navigateToProductDetail(productId: String,navController: NavController){
    navController.navigate("productDetail/$productId")
}
