package com.techproject.harvestlink.model

import kotlinx.serialization.Serializable

sealed class User {
    abstract val id: String
    abstract val name: String
    abstract val email: String
    abstract val phoneNumber: String
    abstract val isOnline: Boolean

    @Serializable
    data class Buyer(
        override val id: String = "",
        override val name: String = "",
        override val email: String = "",
        override val phoneNumber: String = "",
        override val isOnline: Boolean = false,
        val deliveryAddress: String? = null,
        val preferredPaymentMethod: String? = null,
        val orderHistoryCount: Int = 0
    ) : User()

    @Serializable
    data class Farmer(
        override val id: String = "",
        override val name: String = "",
        override val email: String = "",
        override val phoneNumber: String = "",
        override val isOnline: Boolean = false,
        val location: String = "",
        val rating: Double = 0.0,
        val salesCompleted: Int = 0,
        val farmName: String? = null,
    ) : User()
}

@Serializable
data class Produce(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val unit: String = "",
    val availableQuantity: Int = 0,
    val rating: Double = 0.0,
    val description: String = "",
    val harvestDate: String = "",
    val farmerId: String = "",
    val imageUrl: String? = null,
    val category: String = ""
)
