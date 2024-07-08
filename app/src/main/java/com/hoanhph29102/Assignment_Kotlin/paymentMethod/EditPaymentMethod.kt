package com.hoanhph29102.Assignment_Kotlin.paymentMethod

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hoanhph29102.Assignment_Kotlin.activity.ButtonSplash
import com.hoanhph29102.Assignment_Kotlin.activity.HeaderWithBack
import com.hoanhph29102.Assignment_Kotlin.profile.User
import com.hoanhph29102.assignment_kotlin.R
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPaymentMethod(paymentMethod: PaymentMethod,navController: NavController) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val defaultUserName = currentUser?.displayName ?: ""

    var cardNumberState by remember { mutableStateOf(TextFieldValue(paymentMethod.cardNumber)) }
    var cvvState by remember { mutableStateOf(TextFieldValue(paymentMethod.cvv)) }

    var expirationDateState by remember { mutableStateOf(TextFieldValue(paymentMethod.expirationDate)) }
    val errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val viewModel: PaymentMethodViewModel = viewModel()

    Scaffold(
        topBar = {
            HeaderWithBack(modifier = Modifier, text = "Add Payment", navController = navController, onBackClick = {
                navController.popBackStack()
            })
        },
        bottomBar = {
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp), contentAlignment = Alignment.Center){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ButtonSplash(modifier = Modifier.padding(horizontal = 8.dp).weight(1f), text = "Edit") {
                        val editedPayment = PaymentMethod(
                            idMethod = paymentMethod.idMethod,
                            cardNumber = cardNumberState.text,
                            cvv = cvvState.text,
                            expirationDate = expirationDateState.text
                        )
                        editPayment(paymentMethod = editedPayment, navController = navController)
                    }
                    ButtonSplash(modifier = Modifier.padding(horizontal = 8.dp).weight(1f), text = "Delete") {
                        deletePayment(paymentMethod, navController)
                    }
                }
            }
        }
    ) {paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)){
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                CardPaymentMethodDemo()
                Spacer(modifier = Modifier.height(40.dp))
                EditPayment(
                    defaultName = defaultUserName,
                    cardNumberState = cardNumberState,
                    cvvState = cvvState,
                    expiryState = expirationDateState,
                    onCardNumberChange = {cardNumberState = it},
                    onCvvChange = {cvvState = it},
                    onExpiryChange = {expirationDateState = it},
                    errorMessage = errorMessage
                )
            }
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPayment(
    defaultName: String,
    cardNumberState: TextFieldValue,
    cvvState: TextFieldValue,
    expiryState: TextFieldValue,
    onCardNumberChange: (TextFieldValue) -> Unit,
    onCvvChange: (TextFieldValue) -> Unit,
    onExpiryChange: (TextFieldValue) -> Unit,
    errorMessage: String?
) {
    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        OutlinedTextField(
            value = TextFieldValue(defaultName),
            onValueChange = {},
            modifier = Modifier
                .height(66.dp)
                .width(335.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                containerColor = Color.LightGray
            ),
            readOnly = true
        )

        OutlinedTextField(
            value = cardNumberState,
            onValueChange = onCardNumberChange,
            modifier = Modifier
                .height(66.dp)
                .width(335.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = Color.LightGray,
            ),
            label = {
                Text(text = "Card Number", style = MaterialTheme.typography.titleMedium)
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = cvvState,
                onValueChange = onCvvChange,
                modifier = Modifier
                    .height(66.dp)
                    .width(150.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    containerColor = Color.LightGray

                ),
                placeholder = {
                    Text(text = "CVV",style = MaterialTheme.typography.titleMedium)
                }
            )
            Spacer(modifier = Modifier.width(30.dp))
            OutlinedTextField(
                value = expiryState,
                onValueChange = onExpiryChange,
                modifier = Modifier
                    .height(66.dp)
                    .width(150.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = Color.LightGray,
                ),
                placeholder = {
                    Text(text = "Expiry",style = MaterialTheme.typography.titleMedium)
                }
            )
        }
    }
}

fun editPayment(paymentMethod: PaymentMethod, navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid

    if (userId != null) {
        firestore.collection("user").document(userId).get().addOnSuccessListener { document ->
            if (document.exists()) {
                val user = document.toObject(User::class.java)
                if (user != null) {
                    val updatedPayment = user.paymentMethods.map {
                        if (it.idMethod == paymentMethod.idMethod) paymentMethod else it
                    }
                    user.paymentMethods = updatedPayment
                    firestore.collection("user").document(userId).set(user)
                        .addOnSuccessListener {
                            //navController.navigate("paymentMethod")
                            navController.popBackStack()
                        }
                        .addOnFailureListener { e ->
                            Log.e("Edit Payment", "editPayment: $e")
                        }
                } else {
                    Log.e("Edit Payment", "editPayment: cannot convert document to User object")
                }
            } else {
                Log.e("Edit Payment", "editPayment: document does not exist")
            }
        }
    }
}

fun deletePayment(paymentMethod: PaymentMethod, navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid

    if (userId != null) {
        firestore.collection("user").document(userId).get().addOnSuccessListener { document ->
            if (document.exists()) {
                val user = document.toObject(User::class.java)
                if (user != null) {
                    val updatedPayment = user.paymentMethods.filter { it.idMethod != paymentMethod.idMethod }
                    user.paymentMethods = updatedPayment
                    firestore.collection("user").document(userId).set(user)
                        .addOnSuccessListener {
                            //navController.navigate("paymentMethod")
                            navController.popBackStack()
                        }
                        .addOnFailureListener { e ->
                            Log.e("Delete Payment", "deletePayment: $e")
                        }
                } else {
                    Log.e("Delete Payment", "deletePayment: cannot convert document to User object")
                }
            } else {
                Log.e("Delete Payment", "deletePayment: document does not exist")
            }
        }
    }
}