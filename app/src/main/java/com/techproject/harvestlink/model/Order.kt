package com.techproject.harvestlink.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: Int = 0,
    @SerialName(value = "order_date")
    val orderDate: Long = 0L,
    @SerialName(value = "order_status")
    val orderStatus: OrderStatus = OrderStatus.PENDING,
    @SerialName(value = "delivery_address")
    val deliveryAddress: String = "",
    @SerialName(value = "user_id")
    val userId: String = "",
    @SerialName(value = "farmer_id")
    val farmerId: String = "",
    val items: List<OrderItem> = emptyList()
)

@Serializable
data class FarmerListing(
    val id: String = "",
    val produceName: String = "",
    val quantityAvailable: Int = 0,
    val pricePerUnit: Double = 0.0,
    val status: ListingStatus = ListingStatus.ACTIVE,
    val quantitySold: Int = 0
)

@Serializable
data class FarmerOrderRequest(
    val id: String = "",
    val buyerName: String = "",
    val buyerLocation: String = "",
    val produceName: String = "",
    val quantity: Int = 0,
    val offeredPricePerKg: Int = 0,
    val buyerNote: String = "",
    val requestDate: String = "",
    val isResponded: Boolean = false
)

@Serializable
data class OrderItem(
    val product: Produce = Produce(),
    val quantity: Int = 0,
)

@Serializable
enum class OrderStatus(val text: String) {
    DELIVERED(text = "Delivered"),
    IN_TRANSIT(text = "In Transit"),
    PENDING(text = "Pending"),
    CANCELLED(text = "Cancelled")
}

@Serializable
enum class ListingStatus { ACTIVE, PENDING, SOLD }
