package com.hoanhph29102.Assignment_Kotlin.activity

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.hoanhph29102.Assignment_Kotlin.profile.User
import com.hoanhph29102.assignment_kotlin.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController){
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    var nameState by remember { mutableStateOf(TextFieldValue("")) }
    var emailState by remember { mutableStateOf(TextFieldValue("")) }
    var passwordState by remember { mutableStateOf(TextFieldValue("")) }
    var confirmPasswordState by remember { mutableStateOf(TextFieldValue("")) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var showDialog by remember {
        mutableStateOf(false)
    }

    if (showDialog && errorMessage != null) {
        DialogNotify(onConfirmation = {showDialog = false }, dialogTitle = "Thông báo", dialogMessage = errorMessage!!)
    }
    

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 8.dp)

    ) {
        HeaderRegis(Modifier.weight(0.5f))
        Column(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(12.dp)
            .shadow(5.dp)
            .background(color = Color.White)
            .weight(4.5f)
            ,

            ) {
            BodyRegis(
                navController,
                nameState,
                emailState,
                passwordState,
                confirmPasswordState,
                onNameChange = {nameState = it},
                onEmailChange = {emailState = it},
                onPasswordChange = {passwordState = it},
                onConfirmPasswordChange = {confirmPasswordState = it},
                onSignUpClick = {
                    if (passwordState.text == confirmPasswordState.text){
                        auth.createUserWithEmailAndPassword(emailState.text, passwordState.text)
                            .addOnCompleteListener{task ->
                                if (task.isSuccessful){
                                    val firebaseUser = auth.currentUser
                                    val uid = firebaseUser?.uid

                                    val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(nameState.text).build()
                                    firebaseUser?.updateProfile(profileUpdates)?.addOnCompleteListener{ profileUpdateTask ->
                                        if (profileUpdateTask.isSuccessful){
                                            val user = User(
                                                uid = uid ?: "",
                                                name = nameState.text,
                                                email = emailState.text
                                            )
                                            db.collection("user").document(uid!!).set(user).addOnSuccessListener {
                                                navController.navigate("login")
                                            }
                                                .addOnFailureListener{ e ->
                                                    errorMessage = "Đã có lỗi xảy ra, vui lòng thử lại sau ít phút! $e"
                                                    showDialog = true
                                                }
                                        }
                                    }
                                   
                                }
                                else{
                                    val exception = task.exception
                                    errorMessage = when (exception) {
                                        is FirebaseAuthUserCollisionException -> "Email đã tồn tại"
                                        is  FirebaseAuthWeakPasswordException -> "Mật khẩu quá yếu, vui lòng chọn mật khẩu mạnh hơn"
                                        is FirebaseAuthInvalidCredentialsException -> "Email không hợp lệ"
                                        else -> "Đã có lỗi xảy ra, vui lòng thử lại sau ít phút!"
                                    }
                                    showDialog = true
                                }
                            }
                    }
                    else{
                        errorMessage = "Mật khẩu xác nhận không đúng, vui lòng thử lại"
                        showDialog = true
                    }
                },
                errorMessage = errorMessage
            )

        }


    }
}
@Composable
fun HeaderRegis(modifier: Modifier){
    Box(modifier = modifier.fillMaxWidth()) {
        Divider(
            color = Color.Gray,
            thickness = 2.dp,
            modifier = Modifier
                .padding(horizontal = 30.dp)
                .align(Alignment.Center)
        )

        Image(
            painter = painterResource(id = R.drawable.img_login),
            contentDescription = null,
            modifier = Modifier
                .size(70.dp)
                .align(Alignment.Center)
                .clip(RoundedCornerShape(8.dp))
                .background(color = Color.White)
                .padding(horizontal = 10.dp)
        )
    }

    Text(
        text = "WELCOME",
        fontSize = 24.sp,
        color = Color.Black,
        modifier = Modifier.padding(start = 25.dp, top = 10.dp, bottom = 20.dp),
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Serif
    )
}

@Composable
fun BodyRegis(
    navController: NavController,
    nameState: TextFieldValue,
    emailState: TextFieldValue,
    passwordState: TextFieldValue,
    confirmPasswordState: TextFieldValue,
    onNameChange: (TextFieldValue) -> Unit,
    onEmailChange: (TextFieldValue) -> Unit,
    onPasswordChange: (TextFieldValue) -> Unit,
    onConfirmPasswordChange: (TextFieldValue) -> Unit,
    onSignUpClick: () -> Unit,
    errorMessage: String?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 25.dp),
        verticalArrangement = Arrangement.Top,

        ) {

        TextFieldItem(value = nameState,
            onValueChange = onNameChange,
            label = { Text(text = "Name",style = MaterialTheme.typography.titleMedium, color = Color.Gray) },
            visualTransformation = VisualTransformation.None)

        TextFieldItem(value = emailState,
            onValueChange = onEmailChange,
            label = { Text(text = "Email",style = MaterialTheme.typography.titleMedium, color = Color.Gray) },
            visualTransformation = VisualTransformation.None)
        
        TextFieldItem(value = passwordState,
            onValueChange = onPasswordChange,
            label = { Text(text = "Password",style = MaterialTheme.typography.titleMedium, color = Color.Gray) },
            visualTransformation = PasswordVisualTransformation())

        TextFieldItem(value = confirmPasswordState,
            onValueChange = onConfirmPasswordChange,
            label = { Text(text = "Confirm Password",style = MaterialTheme.typography.titleMedium, color = Color.Gray)},
            visualTransformation = PasswordVisualTransformation())

//        if (errorMessage != null) {
//            Text(
//                text = errorMessage,
//                color = Color.Red,
//                modifier = Modifier.padding(top = 10.dp)
//            )
//        }

        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick =onSignUpClick,
                modifier = Modifier
                    .width(285.dp)
                    .height(50.dp)

                ,
                shape = RoundedCornerShape(7.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                ),

                ) {
                Text(
                    text = "SIGN UP",
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
            Row(modifier = Modifier.padding(top = 7.dp)) {
                Text(text = "Already have account? ",
                    color = Color.Gray,
                    fontSize = 14.sp,
                )
                Text(text = " SIGN IN",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable {
                        navController.navigate("login")
                    }
                )
            }
        }
    }
}

@Composable
fun ErrorDialog(onDismiss: () -> Unit, message: String) {
    AlertDialog(onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(text = "OK")
            }
        },
        text = { Text(text = message)})
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldItem(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    label: @Composable (() -> Unit),
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation
){
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier.padding(vertical = 10.dp),
        textStyle = TextStyle(fontSize = 20.sp),
        singleLine = true,
        visualTransformation = visualTransformation,
        )
}


//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun TextFieldPreview(){
//    LoginScreen()
//}
//@Preview(showSystemUi = true, showBackground = true)
//@Composable
//fun RegisterPreview(){
//    RegisterScreen()
//}