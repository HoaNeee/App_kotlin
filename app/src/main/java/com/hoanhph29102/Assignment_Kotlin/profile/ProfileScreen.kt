package com.hoanhph29102.Assignment_Kotlin.profile

import android.util.Log
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.hoanhph29102.Assignment_Kotlin.order.checkout.DialogConfirm
import com.hoanhph29102.assignment_kotlin.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid

    val userState = remember {
        mutableStateOf(User())
    }
    BackHandler(onBack = {
        navController.navigate("home")
    })
    var showDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(userId){
        if (userId != null){
            firestore.collection("user").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null){
                        val user = document.toObject(User::class.java)
                        if (user != null){
                            userState.value = user
                        }
                        else{
                            Log.e("Profile", "User null ", )
                        }
                    }
                    else{
                        Log.e("Profile", "document null ", )
                    }
                }
                .addOnFailureListener{e ->
                    Log.e("Profile", ":ProfileScreen $e")
                }
        }
    }
    Scaffold(
        topBar = {
            HeaderProfile(modifier = Modifier, text = "Profile", onLogout = {
                showDialog = true
            })
        }
    ) {paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            ItemMyProfile(user = userState.value)
            Spacer(modifier = Modifier.height(12.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                CardOptionProfile(optionText = "My Orders", onClick = {navController.navigate("order?fromSuccesOrder=false")})
                CardOptionProfile(optionText = "Shipping Address",onClick = {navController.navigate("shippingAddress")})
                CardOptionProfile(optionText = "Payment Method",onClick = {navController.navigate("paymentMethod")})

                CardOptionProfile(optionText = "Setting",onClick = {})
            }
        }

    }
    DialogConfirm(showDialog = showDialog,
        title = "Thông Báo",
        text = "Bạn có chắc muốn đăng xuất?",
        onDismiss = {
                    showDialog = false
                    },
        onConfirm = {
            logout(navController)
        })

}

@Composable
fun ItemMyProfile(user: User) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .background(color = Color.White)
            ,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.LightGray)
                .size(100.dp)
            ) {
            AsyncImage(model = "https://chiemtaimobile.vn/images/companies/1/%E1%BA%A2nh%20Blog/avatar-facebook-dep/Anh-avatar-hoat-hinh-de-thuong-xinh-xan.jpg?1704788263223",
                contentDescription = "img profile",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Column(
            modifier = Modifier.padding(start = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = user.name, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold))
            Text(text = user.email, style = MaterialTheme.typography.titleMedium.copy(color = Color.Gray))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardOptionProfile(optionText : String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
        ,
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        onClick = onClick

    ) {
        Row(modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = optionText,
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                style = MaterialTheme.typography.titleMedium
            )
            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = "Option",
                modifier =
                Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}
@Composable
fun HeaderProfile(modifier: Modifier,text : String, onLogout: () -> Unit){
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
        Icon(painter = painterResource(id = R.drawable.img_logout), contentDescription = "log out", modifier = Modifier
            .size(40.dp)
            .padding(end = 10.dp)
            .align(alignment = Alignment.CenterEnd)
            .clickable {
                onLogout()
            }
        )
    }
}

fun logout(navController: NavController){
    FirebaseAuth.getInstance().signOut()
    navController.navigate("login"){
        popUpTo(navController.graph.startDestinationId){
            inclusive = true
        }
    }
}
//@Preview(showBackground = true)
//@Composable
//fun PreviewProfileTest() {
//    CardOptionProfile(optionText = "My Address")
//}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun PreviewScreen() {
//    ProfileScreen()
//}

