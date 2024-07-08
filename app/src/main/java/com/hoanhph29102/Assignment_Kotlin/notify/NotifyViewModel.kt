package com.hoanhph29102.Assignment_Kotlin.notify

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationViewModel(private val notifyService: NotifyService) : ViewModel() {
    private val _notifications = MutableLiveData<List<Notification>>(emptyList())
    val notifications: LiveData<List<Notification>> = _notifications

    private val _snackbarMessage = MutableLiveData<String?>(null)
    val snackbarMessage: LiveData<String?> = _snackbarMessage

    private val _notifyDetail = MutableLiveData<Notification?>(null)
    val notifyDetail: LiveData<Notification?> = _notifyDetail

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    init {

    }
    fun fetchNotifications(userId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val notificationsList = withContext(Dispatchers.IO) {
                    notifyService.getNotify(userId)
                }
                _notifications.value = notificationsList
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Error fetching notifications: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Hàm gửi thông báo
    fun sendNotification(userId: String, title: String, description: String) {
        viewModelScope.launch {
            try {
                val notificationRequest = NotificationRequest(title, description)
                notifyService.sendNotification(userId, notificationRequest)
                // Xử lý khi gửi thông báo thành công (nếu cần thiết)
                fetchNotifications(userId)
            } catch (e: Exception) {
                // Xử lý khi gặp lỗi trong quá trình gửi thông báo
                e.printStackTrace()
            }finally {
                _isLoading.value = false
            }
        }
    }
    fun getNotifyDetail(notifyId: String){
        viewModelScope.launch {
            try {
                val notify = withContext(Dispatchers.IO){
                    notifyService.getNotifyDetail(notifyId)
                }
                _notifyDetail.value = notify
            } catch (e: Exception) {
                Log.e("NotifyViewModel", "Error fetching notify detail details: ${e.message}", e)
            }
        }
    }
    fun showSnackbar(message: String) {
        _snackbarMessage.value = message
    }

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }
}

class NotificationViewModelFactory(private val notifyService: NotifyService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotificationViewModel(notifyService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}