package com.techproject.harvestlink.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Order(
    val id: Int = 0,
    @SerialName("order_date") val orderDate: Long = 0L,
    @SerialName("order_status") val orderStatus: OrderStatus = OrderStatus.PENDING,
    @SerialName("delivery_address") val deliveryAddress: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("farmer_id") val farmerId: String = "",
    val items: List<OrderItem> = emptyList()
)

@Serializable
data class FarmerListing(
    val id: String = "",
    @SerialName("produce_name") val produceName: String = "",
    @SerialName("quantity_available") val quantityAvailable: Int = 0,
    @SerialName("price_per_unit") val pricePerUnit: Double = 0.0,
    val status: ListingStatus = ListingStatus.ACTIVE,
    @SerialName("quantity_sold") val quantitySold: Int = 0
)

@Serializable
data class FarmerOrderRequest(
    val id: String = "",
    @SerialName("buyer_name") val buyerName: String = "",
    @SerialName("buyer_location") val buyerLocation: String = "",
    @SerialName("produce_name") val produceName: String = "",
    val quantity: Int = 0,
    @SerialName("offered_price_per_kg") val offeredPricePerKg: Int = 0,
    @SerialName("buyer_note") val buyerNote: String = "",
    @SerialName("request_date") val requestDate: String = "",
    @SerialName("is_responded") val isResponded: Boolean = false
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
