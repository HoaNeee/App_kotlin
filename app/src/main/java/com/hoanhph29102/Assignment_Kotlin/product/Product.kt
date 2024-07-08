package com.hoanhph29102.Assignment_Kotlin.product


data class Product(
    val _id: String,
    val name: String,
    val price: Double,
    val stars: Double,
    val reviews: Int,
    val description: String,
    val image: String,
    val categoryId: String,
)



//fun getFakeFavoriteProducts(numberOfProducts: Int): List<Product> {
//    val sampleProduct = Product(
//        name = "Minimal Stand",
//        price = 50.0,
//        stars = 4.5,
//        reviews = 50,
//        description = "Minimal Stand is made of natural wood. The design that is very simple and minimal.",
//        image = "https://atinproduction.com/wp-content/uploads/2021/07/AWP01220-scaled-1280x1920.jpg"
//    )
//
//    return List(numberOfProducts) { sampleProduct }
//}