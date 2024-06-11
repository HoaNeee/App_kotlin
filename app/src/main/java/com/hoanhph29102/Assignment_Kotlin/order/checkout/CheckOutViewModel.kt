package com.hoanhph29102.Assignment_Kotlin.order.checkout

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.hoanhph29102.Assignment_Kotlin.profile.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CheckOutViewModel : ViewModel(){
    private val _user = MutableStateFlow<User?>(null)
    val user : StateFlow<User?> get() = _user

    private val _totalMoney = MutableStateFlow(0.0)
    val totalMoney : StateFlow<Double> get() = _totalMoney

    private val _isLoading = MutableLiveData(false)
    val isLoading : LiveData<Boolean> = _isLoading
    init {
        fetchUserDetail()
    }

    fun fetchUserDetail(){
        _isLoading.value = true
        val userId = FirebaseAuth.getInstance().currentUser?.uid?: return
        val db = FirebaseFirestore.getInstance()
        db.collection("user").document(userId).get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                _user.value = user
                _isLoading.value = false
            }
            .addOnFailureListener{e ->
                Log.e("TAG", "fetchUserDetail: $e", )
                _isLoading.value = true
            }
    }

    fun setTotalMoney (totalMoney: Double){
        _totalMoney.value = totalMoney
    }
}