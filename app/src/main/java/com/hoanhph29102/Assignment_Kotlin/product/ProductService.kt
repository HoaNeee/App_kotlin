package com.hoanhph29102.Assignment_Kotlin.product

import com.hoanhph29102.Assignment_Kotlin.api.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductService {
    @GET("/")  // Thay "products" bằng endpoint API thực tế của bạn
    suspend fun getProducts(): List<Product>

    @GET("productDetail/{productId}")
    suspend fun getProductDetails(@Path("productId") productId: String): Product

    @GET("/category")
    suspend fun getCategories(): List<Category>

    @GET("/filter-products/{categoryId}")
    suspend fun getProductByCategory(@Path("categoryId") categoryId: String): List<Product>

    companion object {
        private var retrofitService: ProductService? = null
        fun getInstance(): ProductService {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(Constants.DOMAIN)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(ProductService::class.java)
            }
            return retrofitService!!
        }
    }
}