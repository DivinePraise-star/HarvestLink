package com.techproject.harvestlink.model

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: Int = 0,
    val orderDate: Long = 0L,
    val orderStatus: OrderStatus = OrderStatus.PENDING,
    val deliveryAddress: String = "",
    val userId: String = "",
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
