package com.hoanhph29102.Assignment_Kotlin.address

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import com.hoanhph29102.Assignment_Kotlin.profile.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ShippingAddressViewModel : ViewModel() {
    var addressList by mutableStateOf<List<Address>>(emptyList())
        private set


    private val _isLoading = MutableLiveData(false)
    val isLoading : LiveData<Boolean> = _isLoading
    init {
        loadAddresses()
    }

    fun loadAddresses() {
        viewModelScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                try {
                    _isLoading.value = true
                    val document = FirebaseFirestore.getInstance().collection("user").document(userId).get().await()
                    if (document.exists()) {
                        val user = document.toObject(User::class.java)
                        addressList = user?.addresses ?: emptyList()
                    }
                } catch (e: Exception) {
                    Log.e("viewModelAddress", "loadAddresses: $e")

                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    suspend fun updateDefaultAddress(selectedAddress: Address, isDefault: Boolean) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            try {
                _isLoading.value = true
                val firestore = FirebaseFirestore.getInstance()
                val userDocument = firestore.collection("user").document(userId).get().await()
                if (userDocument.exists()) {
                    val user = userDocument.toObject(User::class.java)
                    user?.addresses?.forEach { address ->
                        if (address.idAddress == selectedAddress.idAddress) {
                            address.isDefault = isDefault
                        } else {
                            address.isDefault = false
                        }
                    }
                    firestore.collection("user").document(userId).set(user!!).await()
                    addressList = user.addresses
                }
            } catch (e: Exception) {
                Log.e("viewModel update address default", "updateDefaultAddress: $e")
            } finally {
                _isLoading.value = false
            }
        }
    }
}