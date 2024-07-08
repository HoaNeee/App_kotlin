package com.hoanhph29102.Assignment_Kotlin.product

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductViewModel : ViewModel() {
    private val productService = ProductService.getInstance()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _productDetails = MutableStateFlow<Product?>(null)
    val productDetails: StateFlow<Product?> = _productDetails

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _selectedCategoryIndex = MutableStateFlow(-1)
    val selectedCategoryIndex: StateFlow<Int> = _selectedCategoryIndex

    private val _filteredProducts = MutableStateFlow<List<Product>>(emptyList())
    val filteredProducts: StateFlow<List<Product>> = _filteredProducts

    private val _isLoading = MutableLiveData(false)
    val isLoading : LiveData<Boolean> = _isLoading

    init {
        fetchProducts()
        fetchCategories()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            try {
                val productList = withContext(Dispatchers.IO) {
                    productService.getProducts()
                }
                _products.value = productList
                _filteredProducts.value = productList
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error fetching products: ${e.message}", e)
            }
        }
    }

    private fun fetchCategories(){
        viewModelScope.launch {
            try {
                val categoryList = withContext(Dispatchers.IO) {
                    productService.getCategories()
                }
                _categories.value = categoryList
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error fetching categories: ${e.message}", e)
            }
        }
    }

    fun getProductDetails(productId: String) {
        viewModelScope.launch {
            try {
                val product = withContext(Dispatchers.IO) {
                    productService.getProductDetails(productId)
                }
                _productDetails.value = product
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error fetching product details: ${e.message}", e)
            }
        }
    }

    fun selectCategory(index: Int) {
        //_selectedCategoryIndex.value = index

        viewModelScope.launch {
            try {
                _isLoading.value = true
                if (_selectedCategoryIndex.value == index){
                    _selectedCategoryIndex.value = -1
                    _filteredProducts.value = _products.value
                }
                else {
                    _selectedCategoryIndex.value = index
                    if (index >= 0 && index < _categories.value.size) {
                        val selectedCategoryId = _categories.value[index]._id
                        val filteredProductList = withContext(Dispatchers.IO) {
                            productService.getProductByCategory(selectedCategoryId)
                        }
                        _filteredProducts.value = filteredProductList
                    } else {
                        _filteredProducts.value = _products.value
                    }
                }
            }catch (e: Exception){
                Log.e("TAG", "selectCategory error: $e", )
            }finally {
                _isLoading.value = false
            }
        }
    }

    fun clearSelectedCategory() {
        _selectedCategoryIndex.value = -1
        _filteredProducts.value = _products.value
    }

}

