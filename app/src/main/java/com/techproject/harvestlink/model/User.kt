package com.techproject.harvestlink.model

import androidx.room.Entity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User (
    val id: String,
    val name: String,
    val email: String,
    @SerialName("phone_number")
    val phoneNumber: String,
    @SerialName("is_online")
    val isOnline: Boolean
)

@Serializable
data class Buyer(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    @SerialName("phone_number")
    val phoneNumber: String = "",
    @SerialName("is_online")
    val isOnline: Boolean = false,
    @SerialName("delivery_address")
    val deliveryAddress: String? = null,
    @SerialName("preferred_payment_method")
    val preferredPaymentMethod: String? = null,
    @SerialName("order_history_count")
    val orderHistoryCount: Int = 0
)

@Serializable
data class Farmer(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    @SerialName("phone_number")
    val phoneNumber: String = "",
    @SerialName("is_online")
    val isOnline: Boolean = false,
    val location: String = "",
    val rating: Double = 0.0,
    @SerialName("sales_completed")
    val salesCompleted: Int = 0,
    @SerialName("farm_name")
    val farmName: String? = null,
)

@Serializable
data class Produce(
    val id: String = "",
    val name: String = "",
    @SerialName("farmer_name") val farmerName: String = "",
    val price: Double = 0.0,
    val unit: String = "",
    @SerialName("available_quantity") val availableQuantity: Double = 0.0,
    val rating: Double = 0.0,
    val description: String = "",
    @SerialName("harvest_date") val harvestDate: String? = null,
    @SerialName("farmer_id") val farmerId: String = "",
    @SerialName("image_url")val imageUrl: String? = null,
    val category: String = ""
)
