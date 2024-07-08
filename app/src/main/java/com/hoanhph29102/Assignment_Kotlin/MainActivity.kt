package com.hoanhph29102.Assignment_Kotlin

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hoanhph29102.Assignment_Kotlin.activity.HeaderHome1
import com.hoanhph29102.Assignment_Kotlin.activity.HomeScreen
import com.hoanhph29102.Assignment_Kotlin.activity.LoginScreen
import com.hoanhph29102.Assignment_Kotlin.activity.RegisterScreen
import com.hoanhph29102.Assignment_Kotlin.activity.SplashScreen
import com.hoanhph29102.Assignment_Kotlin.address.AddShippingAddressScreen
import com.hoanhph29102.Assignment_Kotlin.address.Address
import com.hoanhph29102.Assignment_Kotlin.address.EditAddressScreen
import com.hoanhph29102.Assignment_Kotlin.address.ShippingAddressScreen
import com.hoanhph29102.Assignment_Kotlin.cart.CartScreen
import com.hoanhph29102.Assignment_Kotlin.order.order.OrderScreen
import com.hoanhph29102.Assignment_Kotlin.product.ProductDetailScreen
import com.hoanhph29102.Assignment_Kotlin.profile.ProfileScreen
import com.hoanhph29102.Assignment_Kotlin.favorite.FavoriteScreen
import com.hoanhph29102.Assignment_Kotlin.notify.NotifyScreen
import com.hoanhph29102.Assignment_Kotlin.notify.createNotificationChannel
import com.hoanhph29102.Assignment_Kotlin.order.checkout.CheckoutScreen
import com.hoanhph29102.Assignment_Kotlin.order.order.OrderDetailScreen
import com.hoanhph29102.Assignment_Kotlin.paymentMethod.AddPaymentScreen
import com.hoanhph29102.Assignment_Kotlin.paymentMethod.EditPaymentMethod
import com.hoanhph29102.Assignment_Kotlin.paymentMethod.PaymentMethod
import com.hoanhph29102.Assignment_Kotlin.paymentMethod.PaymentMethodScreen
import com.hoanhph29102.Assignment_Kotlin.profile.User
import com.hoanhph29102.Assignment_Kotlin.ui.theme.Assignment_KotlinTheme
import com.hoanhph29102.assignment_kotlin.R



import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Quyền đã được cấp
            } else {
                // Quyền bị từ chối
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Assignment_KotlinTheme{
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0xD7FFFFFF))
                ){
//                    val sampleProduct = Product(
//                        name = "Minimal Stand",
//                        price = 50.0,
//                        rating = 4.5,
//                        reviewCount = 50,
//                        description = "Minimal Stand is made of natural wood. The design that is very simple and minimal. This is truly one of the best furnitures in any family for now. With 3 different colors, you can easily select the best match for your home.",
//                        imageUrl = "https://example.com/minimal_stand.jpg"
//                    )
                    MainApp()
                }

            }

        }
        createNotificationChannel(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current
    val titleTopAppBar = when (currentRoute) {
        BottomNavItem.Home.route -> "Home"
        BottomNavItem.Favorite.route -> "Favorite"
        BottomNavItem.Notify.route -> "Notify"
        BottomNavItem.Profile.route -> "Profile"
        "otherScreen" -> "Other Screen"
        else -> "My App"
    }


    Scaffold(
        topBar = {
            if (shouldShowTopBar(currentRoute)) {
                TopBar(title = titleTopAppBar, navController)
            }
        },
        bottomBar = {
            if (shouldShowBottomBar(currentRoute)) {
                BottomNavigationBar(navController)
            }
        }
    )
    {
        paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            NavigationGraph(navController, context)
        }
    }
}

sealed class BottomNavItem(val title: String, val icon: Int, val route: String) {
    object Home : BottomNavItem("Home", R.drawable.home, "home")
    object Favorite : BottomNavItem("Favorite", R.drawable.bookmark, "favorite")
    object Notify : BottomNavItem("Notify", R.drawable.notify, "notify")
    object Profile : BottomNavItem("Profile", R.drawable.user, "profile")
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Favorite,
        BottomNavItem.Notify,
        BottomNavItem.Profile
    )
//    NavigationBar(
//        modifier = Modifier.height(60.dp)
//    ) {
//        val coroutineScope = rememberCoroutineScope()
//        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
//        items.forEach { item ->
//            NavigationBarItem(
//                icon = {
//                    Icon(painterResource(id = item.icon), contentDescription = item.title, modifier = Modifier.size(35.dp))
//                },
//                selected = currentRoute == item.route,
//                onClick = {
//                    coroutineScope.launch {
//                        navController.navigate(item.route) {
//                            popUpTo(navController.graph.startDestinationId) { saveState = true }
//                            restoreState = true
//                            launchSingleTop = true
//                        }
//                    }
//                },
//                colors = NavigationBarItemDefaults.colors(
//                    selectedIconColor = Color.Black,
//                    unselectedIconColor = Color.Gray,
//                    indicatorColor = Color.Transparent
//                ),
//                alwaysShowLabel = false,
//
//
//            )
//        }
//    }
    val coroutineScope = rememberCoroutineScope()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    NavigationBar(
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth(),


    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            items.forEach { item ->
                val selected = currentRoute == item.route
                CustomNavigationBarItem(
                    icon = {
                        Icon(
                            painterResource(id = item.icon),
                            contentDescription = item.title,
                            tint = if (selected) Color.Black else Color.Gray,
                            modifier = Modifier.size(35.dp)
                        )
                    },
                    label = {
                    },
                    selected = selected,
                    onClick = {
                        coroutineScope.launch {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                restoreState = true
                                launchSingleTop = true
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()

                    )
            }
        }

    }

}

@Composable
fun CustomNavigationBarItem(
    icon: @Composable () -> Unit,
    label: @Composable () -> Unit,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onClick)
            ,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {
        icon()
        label()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String, navController: NavHostController) {
    HeaderHome1(modifier = Modifier.padding(1.dp), text = title, navController)
}

fun shouldShowTopBar(currentRoute: String?): Boolean {
    return when (currentRoute) {
        BottomNavItem.Home.route,
        BottomNavItem.Favorite.route,
        BottomNavItem.Notify.route,
//        BottomNavItem.Profile.route,
        "otherScreen" -> true
        else -> false
    }
}

fun shouldShowBottomBar(currentRoute: String?): Boolean {
    return when (currentRoute) {
        BottomNavItem.Home.route,
        BottomNavItem.Favorite.route,
        BottomNavItem.Notify.route,
        BottomNavItem.Profile.route -> true
        else -> false
    }
}

@Composable
fun NavigationGraph(navController: NavHostController, context: Context) {
    NavHost(navController, startDestination = "splash") {
        composable("splash"){ SplashScreen(navController = navController) }
        composable("login"){ LoginScreen(navController, context) }
        composable(BottomNavItem.Home.route) { HomeScreen(navController) }
        composable(BottomNavItem.Favorite.route) { FavoriteScreen(navController) }
        composable(BottomNavItem.Notify.route) { NotifyScreen(navController) }
        composable(BottomNavItem.Profile.route) { ProfileScreen(navController) }
        composable(
            "productDetail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType  })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            productId?.let {
                ProductDetailScreen(productId = it, navController = navController)
            }
        }
        composable("cart"){ CartScreen(navController = navController)}
        composable("register"){ RegisterScreen(navController) }
        composable("shippingAddress"){ ShippingAddressScreen(navController) }
        composable("addShippingAddress"){ AddShippingAddressScreen(navController = navController)}
        composable("addressDetail/{addressId}"){ navBackStackEntry ->
            val addressId = navBackStackEntry.arguments?.getString("addressId")
            val address = runBlocking {
                getAddressById(addressId)
            }
            if (address != null){
                EditAddressScreen(address = address, navController = navController)
            } else{
                Text(text = "Address not found")
            }
        }
        composable("paymentMethod"){ PaymentMethodScreen(navController = navController)}
        composable("paymentDetail/{paymentId}"){navBackStackEntry ->
            val paymentId = navBackStackEntry.arguments?.getString("paymentId")
            val payment = runBlocking {
                getPaymentById(paymentId)
            }
            if (payment != null){
                EditPaymentMethod(paymentMethod = payment, navController = navController)
            }else{
                Text(text = "Payment not found")
            }
        }
        composable("addPaymentMethod"){ AddPaymentScreen(navController) }
        composable("submitSuccess"){ OrderSuccessScreen(navController = navController) }
        composable(
            "checkout_screen/{totalMoney}",
            arguments = listOf(navArgument("totalMoney") { type = NavType.StringType })
        ) { backStackEntry ->
            val totalMoneyString = backStackEntry.arguments?.getString("totalMoney") ?: "0.0"
            val totalMoney = totalMoneyString.toDoubleOrNull() ?: 0.0
            CheckoutScreen(navController = navController, totalMoney = totalMoney)
        }

        composable("orderDetail/{orderId}",
                arguments = listOf(navArgument("orderId"){type = NavType.StringType})
            ){navBackStackEntry ->
                val orderId = navBackStackEntry.arguments?.getString("orderId")
                orderId?.let {
                    OrderDetailScreen(orderId = it, navController = navController)
                }

        }
        composable("order?fromSuccesOrder={fromSuccesOrder}",
            arguments = listOf(navArgument("fromSuccesOrder"){
                type = NavType.BoolType
                defaultValue = false
            })
        ){ backStackEntry ->
            val fromSuccesOrder = backStackEntry.arguments?.getBoolean("fromSuccesOrder") ?: false
            OrderScreen(navController = navController,fromSuccesOrder)}


    }
}


@Composable
fun ProgressDialog() {
    Dialog(onDismissRequest = {}) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.wrapContentWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .wrapContentSize()
                    .background(Color.White),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "Loading...")
            }
        }
    }
}

private suspend fun getAddressById(addressId: String?): Address? {
    if (addressId.isNullOrEmpty()) return null

    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid ?: return null

    val firestore = FirebaseFirestore.getInstance()
    val document = firestore.collection("user").document(userId).get().await()

    if (document.exists()) {
        val user = document.toObject(User::class.java)
        return user?.addresses?.find { it.idAddress == addressId }
    }

    return null
}
private suspend fun getPaymentById(paymentId: String?): PaymentMethod? {
    if (paymentId.isNullOrEmpty()) return null

    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid ?: return null

    val firestore = FirebaseFirestore.getInstance()
    val document = firestore.collection("user").document(userId).get().await()

    if (document.exists()) {
        val user = document.toObject(User::class.java)
        return user?.paymentMethods?.find { it.idMethod == paymentId }
    }

    return null
}
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun GreetingPreview() {
//    Assignment_KotlinTheme{
//        ProgressDialog()
//    }
//}