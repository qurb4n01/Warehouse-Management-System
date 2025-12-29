package com.example.storeapp.models

data class Product(
    val id: Int,
    val name: String,
    val productCount: Int,
    val price: Double,
    val qrCode: String,
    val userId: Int
)
