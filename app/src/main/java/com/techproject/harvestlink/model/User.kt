package com.techproject.harvestlink.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

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
        @SerialName("phone_number") override val phoneNumber: String = "",
        @SerialName("is_online") override val isOnline: Boolean = false,
        @SerialName("delivery_address") val deliveryAddress: String? = null,
        @SerialName("preferred_payment_method") val preferredPaymentMethod: String? = null,
        @SerialName("order_history_count") val orderHistoryCount: Int = 0
    ) : User()

    @Serializable
    data class Farmer(
        override val id: String = "",
        override val name: String = "",
        override val email: String = "",
        @SerialName("phone_number") override val phoneNumber: String = "",
        @SerialName("is_online") override val isOnline: Boolean = false,
        val location: String = "",
        val rating: Double = 0.0,
        @SerialName("sales_completed") val salesCompleted: Int = 0,
        @SerialName("farm_name") val farmName: String? = null,
    ) : User()
}

@Serializable
data class Produce(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val unit: String = "",
    @SerialName("available_quantity") val availableQuantity: Int = 0,
    val rating: Double = 0.0,
    val description: String = "",
    @SerialName("harvest_date") val harvestDate: String = "",
    @SerialName("farmer_id") val farmerId: String = "",
    val imageUrl: String? = null,
    val category: String = ""
)
