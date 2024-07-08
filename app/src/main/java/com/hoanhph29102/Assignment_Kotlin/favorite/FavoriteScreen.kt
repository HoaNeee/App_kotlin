package com.hoanhph29102.Assignment_Kotlin.favorite

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.hoanhph29102.Assignment_Kotlin.notify.Notification
import com.hoanhph29102.Assignment_Kotlin.notify.NotificationViewModel
import com.hoanhph29102.Assignment_Kotlin.notify.NotificationViewModelFactory

import com.hoanhph29102.Assignment_Kotlin.notify.NotifyService

import com.hoanhph29102.Assignment_Kotlin.order.checkout.DialogConfirm
//import com.hoanhph29102.Assignment_Kotlin.product.getFakeFavoriteProducts
import com.hoanhph29102.Assignment_Kotlin.product.navigateToProductDetail
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(navController: NavController) {
    //val items = getFakeFavoriteProducts(5)

    val favoriteService = FavoriteService.getInstance()
    val favoriteViewModel: FavoriteViewModel = viewModel(
        factory = FavoriteViewModelFactory(favoriteService)
    )
    val favoriteProducts by favoriteViewModel.favoriteProducts.collectAsState(emptyList())

    val isLoading by favoriteViewModel.isLoading.observeAsState(false)

    val notifyService = NotifyService.getInstance()
    val notifyViewModel: NotificationViewModel = viewModel(
        factory = NotificationViewModelFactory(notifyService)
    )
    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarMes by notifyViewModel.snackbarMessage.observeAsState()

    val notifications by notifyViewModel.notifications.observeAsState(emptyList())

    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    var showDialog by remember { mutableStateOf(false) }
    var currentFavoriteToDel by remember {
        mutableStateOf<Favorite?>(null)
    }


    val user = FirebaseAuth.getInstance().currentUser
    val userId = user?.uid

    LaunchedEffect(userId) {
        userId?.let {
            favoriteViewModel.fetchFavoriteProducts(it)
        }
    }

   Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
   ) {paddingValues ->
       Box(modifier = Modifier
           .padding(paddingValues)
           .fillMaxSize()){
           Box(modifier = Modifier
               .fillMaxSize()
               .padding(12.dp)){
               if (favoriteProducts.isNotEmpty()) {
                   LazyColumn(verticalArrangement = Arrangement.spacedBy(7.dp)) {

                       items(favoriteProducts) { favorite ->
                           ItemFavorite(
                               favorite = favorite,
                               navController = navController,
                               onDeleteItem = {
                                   showDialog = true
                                   currentFavoriteToDel = favorite

                               }
                           )
                       }
                   }
               }else{
                   Text(text = "Not favorite")
               }
               if (isLoading){
                   ProgressDialog()
               }
           }
       }

   }

    DialogConfirm(showDialog = showDialog,
        title = "Thông báo",
        text = "Bạn có chắc muốn xóa mục này không?",
        onDismiss = {
            showDialog = false
            currentFavoriteToDel = null
        },
        onConfirm = {
            currentFavoriteToDel?.let {favorite ->
                favoriteViewModel.deleteFavItem(favorite.userId,favorite._id)
                //notifyViewModel.sendNotification(favorite.userId,"Bạn vừa xóa 1 mục", "Bạn vừa xóa mục ${favorite.nameProduct} khỏi danh sách yêu thích")
                //snackbarMessage = "Mục yêu thích đã được xóa"
                notifyViewModel.showSnackbar("Mục yêu thích đã được xóa")
            }

        }

    )
//    snackBarMes?.let {
//        LaunchedEffect(snackBarMes) {
//            snackbarHostState.showSnackbar(it)
//            notifyViewModel.clearSnackbarMessage()
//        }
//    }

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
fun ItemFavorite(
    favorite: Favorite,
    navController: NavController,
    onDeleteItem: () -> Unit
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .clickable {
            navigateToProductDetail(favorite.productId, navController)
            //Log.e("TAG", "ItemFavorite: ${favorite.productId}", )
        }){
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(7.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray)
                        .width(100.dp)
                        .height(100.dp),

                    ) {
                    AsyncImage(
                        model = favorite.imageProduct,
                        contentDescription = "img product",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Column(modifier = Modifier) {
                    Text(text = favorite.nameProduct, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "$ ${favorite.priceProduct}", fontSize = 19.sp, fontWeight = FontWeight.SemiBold)
                }
            }


            Column(modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.Bottom),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(Icons.Default.Close,
                    contentDescription = "remove item favorite",
                    modifier = Modifier.clickable {
                        onDeleteItem()
                    }
                    )
            }

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
fun CustomSnackbar(
    snackbarHostState: SnackbarHostState,
    message: String,
    actionLabel: String? = null,
    duration: SnackbarDuration = SnackbarDuration.Short,
    onDismiss: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    LaunchedEffect(message) {
        scope.launch {
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                duration = duration
            )
            if (result == SnackbarResult.ActionPerformed) {
                onDismiss()
            }
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun PreviewTestFavorite() {
//    ItemFavorite(Modifier.padding(12.dp))
//}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun FavoritePreview() {
//  FavoriteScreen()
//}