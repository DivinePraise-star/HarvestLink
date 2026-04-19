package com.techproject.harvestlink.model

data class Order(
    val id: Int,
    val orderDate:Long,
    val items: List<OrderItem>,
    val orderStatus: OrderStatus,
    val deliveryAddress: String,
    val userId: String,
    val farmerId: String
)

data class FarmerListing(
    val id: String,
    val produceName: String,
    val quantityAvailable: Int,
    val pricePerUnit: Double,
    val status: ListingStatus,
    val quantitySold: Int = 0
)

data class FarmerOrderRequest(
    val id: String,
    val buyerName: String,
    val buyerLocation: String,
    val produceName: String,
    val quantity: Int,
    val offeredPricePerKg: Int,
    val buyerNote: String,
    val requestDate: String,
    val isResponded: Boolean = false
)


data class OrderItem(
    val product: Produce,
    val quantity: Int,
)

enum class OrderStatus(val text: String) {
    DELIVERED(text = "Delivered"),
    IN_TRANSIT(text = "In Transit"),
    PENDING(text = "Pending"),
    CANCELLED(text = "Cancelled")
}

enum class ListingStatus { ACTIVE, PENDING, SOLD }