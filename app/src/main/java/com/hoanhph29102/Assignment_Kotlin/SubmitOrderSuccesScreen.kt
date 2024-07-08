package com.hoanhph29102.Assignment_Kotlin

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hoanhph29102.Assignment_Kotlin.activity.ButtonSplash
import com.hoanhph29102.assignment_kotlin.R

@Composable
fun OrderSuccessScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize().padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
        Text(text = "SUCCESS!", style = MaterialTheme.typography.displayMedium)
        Image(painterResource(id = R.drawable.imagesuccess),
            contentDescription = "",
            modifier = Modifier.size(200.dp)
        )
        Column {
            Text(
                text = "Your order will be delivered soon.",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray
            )
            Text(
                text = "Thank you for choosing our app!",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray
            )
        }
        ButtonSplash(modifier = Modifier
            .width(315.dp)
            .height(60.dp), text = "Track your orders") {
            navController.navigate("order?fromSuccesOrder=true")
        }
        OutlinedButton(onClick = {
                                 navController.navigate("home"){

                                 }
        },
            modifier = Modifier
                .width(315.dp)
                .height(60.dp),
            shape = RoundedCornerShape(12.dp)

        ) {
            Text(text = "BACK TO HOME", color = Color.Black)
        }
    }
}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun PreviewOrderSuccess() {
//    OrderSuccessScreen()
//}