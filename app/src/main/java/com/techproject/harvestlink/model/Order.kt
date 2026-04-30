package com.techproject.harvestlink.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Order(
    val id: Int = 0,
    val orderDate: Long = 0L,
    val orderStatus: OrderStatus = OrderStatus.pending,
    val deliveryAddress: String = "",
    val buyerId: String = "",
    val farmerId: String = "",
    val currency: String = "UGX",
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val totalAmount: Double = 0.0,
    val buyerName: String = "",
    val buyerEmail: String = "",
    val farmerName: String = "",
    val farmerEmail: String = "",
)

@Serializable
data class OrderItem(
    val product: Produce = Produce(),
    val quantity: Int = 0,

)

@Serializable
data class OrderInsert(
    @SerialName("order_date") val orderDate: Long,
    @SerialName("order_status") val orderStatus: String,
    @SerialName("delivery_address") val deliveryAddress: String,
    @SerialName("user_id") val userId: String,
    @SerialName("farmer_id") val farmerId: String
)

@Serializable
data class OrderItemInsert(
    @SerialName("order_id") val orderId: Int,
    @SerialName("produce_id") val produceId: String,
    @SerialName("quantity") val quantity: Int
)

@Serializable
data class OrderIdResponse(val id: Int)

@Serializable
data class OrderDetails(
    @SerialName("order_id") val orderId: Int = 0,
    @SerialName("status") val status: OrderStatus = OrderStatus.pending,
    @SerialName("ordered_at") val orderedAt: Long = 0,
    @SerialName("delivery_address") val deliveryAddress: String = "",
    @SerialName("currency") val currency: String = "UGX",
    @SerialName("subtotal") val subtotal: Double = 0.0,
    @SerialName("delivery_fee") val deliveryFee: Double = 0.0,
    @SerialName("total_amount") val totalAmount: Double = 0.0,
    @SerialName("buyer_name") val buyerName: String = "",
    @SerialName("buyer_id") val buyerId: String = "",
    @SerialName("buyer_email") val buyerEmail: String = "",
    @SerialName("farmer_name") val farmerName: String = "",
    @SerialName("farmer_id") val farmerId: String = "",
    @SerialName("farmer_email") val farmerEmail: String = "",
    @SerialName("item_id") val itemId: Int = 0,
    @SerialName("quantity") val quantity: Double = 0.0,
    @SerialName("produce_name") val produceName: String? = null,
    @SerialName("produce_category") val produceCategory: String? = null,
    @SerialName("produce_unit") val produceUnit: String? = null,
    @SerialName("produce_price") val producePrice: Double? = null
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
    val id: String? = null,
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
enum class OrderStatus(val text: String) {
    delivered(text = "Delivered"),
    in_transit(text = "In Transit"),
    pending(text = "Pending"),
    cancelled(text = "Cancelled")
}

@Serializable
enum class ListingStatus { ACTIVE, PENDING, SOLD }
