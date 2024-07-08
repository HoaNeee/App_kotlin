package com.hoanhph29102.Assignment_Kotlin.activity

import android.net.Uri
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.hoanhph29102.Assignment_Kotlin.MainActivity
import com.hoanhph29102.Assignment_Kotlin.order.checkout.DialogConfirm
import com.hoanhph29102.Assignment_Kotlin.product.Category
import com.hoanhph29102.Assignment_Kotlin.product.ListMenuChip
import com.hoanhph29102.Assignment_Kotlin.product.ListProduct
import com.hoanhph29102.Assignment_Kotlin.product.Product
import com.hoanhph29102.Assignment_Kotlin.product.ProductScreen
import com.hoanhph29102.assignment_kotlin.R
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController){
//    Column {
//        HeaderHome(Modifier.padding(1.dp))
//    }
    
//    val sampleProduct = Product(
//        name = "Minimal Stand",
//        price = 50.0,
//        stars = 4.5,
//        reviews = 50,
//        description = "Minimal Stand is made of natural wood. The design that is very simple and minimal. This is truly one of the best furnitures in any family for now. With 3 different colors, you can easily select the best match for your home.",
//        image = "https://atinproduction.com/wp-content/uploads/2021/07/AWP01220-scaled-1280x1920.jpg"
//    )

    var showDialog by remember {
        mutableStateOf(false)
    }

    BackHandler(onBack = {
        showDialog = true
    })

    Scaffold(
        content = {
            paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                Column {
                    ProductScreen(navController = navController)
                }
            }
        }
    )
    val context = LocalContext.current
    DialogConfirm(
        showDialog = showDialog,
        title = "Thông Báo",
        text = "Bạn có chắc muốn thoát không?",
        onDismiss = { showDialog = false},
        onConfirm = {
            //navController.navigate("splash")
            (context as? MainActivity)?.finish()
        }
    )
}


@Composable
fun HeaderHome(modifier: Modifier){
    Box(modifier = modifier
        .fillMaxWidth()
        .height(70.dp),
        ) {
        Icon(Icons.Default.Search, contentDescription = "", modifier = Modifier
            .size(40.dp)
            .padding(start = 10.dp)
            .align(alignment = Alignment.CenterStart))
        Column(modifier = Modifier.align(alignment = Alignment.Center)
            ) {
            Text(text = "Make Home",
                fontSize = 19.sp,
                fontFamily = FontFamily.SansSerif,
                color = Color.Gray,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally))
            Text(text = "Beautiful", fontSize = 26.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold)
        }
        Icon(Icons.Default.ShoppingCart, contentDescription = "", modifier = Modifier
            .size(40.dp)
            .padding(end = 10.dp)
            .align(alignment = Alignment.CenterEnd))
    }
}
@Composable
fun HeaderHome1(modifier: Modifier,text : String, navController: NavController){
    Box(modifier = modifier
        .fillMaxWidth()
        .height(50.dp),
    ) {
        Icon(Icons.Default.Search,
            contentDescription = "",
            modifier = Modifier
                .size(40.dp)
                .padding(start = 10.dp)
                .align(alignment = Alignment.CenterStart)
                .clickable {

                }

        )
        Column(modifier = Modifier.align(alignment = Alignment.Center)
        ) {
            Text(text = text,
                fontSize = 26.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold)
        }
        Icon(Icons.Default.ShoppingCart, contentDescription = "", modifier = Modifier
            .size(40.dp)
            .padding(end = 10.dp)
            .align(alignment = Alignment.CenterEnd)
            .clickable {
                navController.navigate("cart")
            }
        )
    }
}

@Composable
fun HeaderWithBack(modifier: Modifier,text : String, navController: NavController, onBackClick: () -> Unit) {
    Box(modifier = modifier
        .fillMaxWidth()
        .height(50.dp),
    ) {
        Icon(Icons.Default.ArrowBack,
            contentDescription = "",
            modifier = Modifier
                .size(40.dp)
                .padding(start = 10.dp)
                .align(alignment = Alignment.CenterStart)
                .clickable {
                    onBackClick()
                }

        )
        Column(modifier = Modifier.align(alignment = Alignment.Center)
        ) {
            Text(text = text,
                fontSize = 26.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold)
        }
    }
}



//@Preview(showBackground = true)
//@Composable
//fun PreviewHomeTest() {
//
//
//}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewHomeScreen(){
    //HomeScreen()
}