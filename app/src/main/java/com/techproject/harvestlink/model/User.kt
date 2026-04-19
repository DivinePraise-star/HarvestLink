package com.techproject.harvestlink.model

import com.techproject.harvestlink.R

sealed class User {
    abstract val id: String
    abstract val name: String
    abstract val email: String
    abstract val phoneNumber: String
    abstract val profileImageRes: Int?
    abstract val isOnline: Boolean


    data class Buyer(
        override val id: String,
        override val name: String,
        override val email: String,
        override val phoneNumber: String,
        override val profileImageRes: Int? = null,
        override val isOnline: Boolean = false,

        // Buyer-specific fields
        val deliveryAddress: String? = null,
        val preferredPaymentMethod: String? = null,
        val orderHistoryCount: Int = 0
    ) : User()

    data class Farmer(
        override val id: String,
        override val name: String,
        override val email: String,
        override val phoneNumber: String,
        override val profileImageRes: Int? = null,
        override val isOnline: Boolean = false,

        // Seller-specific fields
        val location: String,
        val rating: Double,
        val salesCompleted: Int,
        val farmName: String? = null,
        val productCategories: List<String> = emptyList()
    ) : User()
}

data class Produce(
    val id: String,
    val name: String,
    val price: Double,
    val unit: String,
    val availableQuantity: Int,
    val rating: Double,
    val description: String,
    val harvestDate: String,
    val farmer: User.Farmer,
    val imageRes: Int? = null,
    val category: String
)