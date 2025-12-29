package com.example.storeapp.models

enum class ProductCategory(val displayName: String) {
    ELECTRONICS("Electronics"),
    FOOD("Food"),
    CLOTHING("Clothing"),
    HOME_APPLIANCES("Home Appliances"),
    OTHERS("Others");

    companion object {
        fun fromString(value: String): ProductCategory {
            return values().find { it.name == value || it.displayName == value } ?: OTHERS
        }
    }
}
