package com.techproject.harvestlink

data class Farmer(
    val id: String,
    val name: String,
    val location: String,
    val rating: Double,
    val salesCompleted: Int,
    val profileImageRes: Int? = null,
    val isOnline: Boolean = false
)

data class Produce(
    val id: String,
    val name: String,
    val price: Double,
    val unit: String,
    val availableQuantity: Int,
    val rating: Double,
    val description: String,
    val harvestDate: String,
    val farmer: Farmer,
    val imageRes: Int? = null,
    val category: String
)

enum class OrderStatus {
    PENDING, PROCESSING, DELIVERED, CANCELLED
}

data class Order(
    val id: String,
    val produce: Produce,
    val quantity: Int,
    val totalPrice: Double,
    val status: OrderStatus,
    val orderDate: String
)

data class Message(
    val id: String,
    val senderName: String,
    val lastMessage: String,
    val timestamp: String,
    val unreadCount: Int = 0
)

val sampleFarmers = listOf(
    Farmer("1", "Ritah Patience", "Kasese, Uganda", 4.8, 127, isOnline = true),
    Farmer("2", "Samuel Mukisa", "Masaka, Kampala", 4.9, 93, isOnline = true)
)

val sampleProduce = listOf(
    Produce(
        "1", "Organic Tomatoes", 40.0, "kg", 50, 4.8,
        "Fresh organic tomatoes, harvested this morning. Rich in nutrients and perfect for cooking or salads.",
        "March 7, 2026", sampleFarmers[0], category = "Vegetables"
    ),
    Produce(
        "2", "Fresh Carrots", 30.0, "kg", 100, 4.7,
        "Sweet and crunchy carrots, perfect for snacks or cooking.",
        "March 6, 2026", sampleFarmers[1], category = "Vegetables"
    )
)

val sampleOrders = listOf(
    Order("ORD001", sampleProduce[0], 5, 200.0, OrderStatus.DELIVERED, "Oct 12, 2023"),
    Order("ORD002", sampleProduce[1], 10, 300.0, OrderStatus.PROCESSING, "Oct 15, 2023"),
    Order("ORD003", sampleProduce[0], 2, 80.0, OrderStatus.PENDING, "Oct 18, 2023")
)

val sampleMessages = listOf(
    Message("1", "Ritah Patience", "Is the delivery time okay for you?", "10:30 AM", 1),
    Message("2", "Samuel Mukisa", "I have added more carrots to the stock.", "Yesterday", 0)
)

// ── Farmer-side models ────────────────────────────────────────────────────────

enum class ListingStatus { ACTIVE, PENDING, SOLD }

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

val sampleFarmerListings = listOf(
    FarmerListing("L1", "Maize", 200, 1200.0, ListingStatus.ACTIVE),
    FarmerListing("L2", "Tomatoes", 50, 2500.0, ListingStatus.ACTIVE),
    FarmerListing("L3", "Beans", 100, 3000.0, ListingStatus.PENDING),
    FarmerListing("L4", "Cassava", 0, 800.0, ListingStatus.SOLD, quantitySold = 300)
)

val sampleFarmerOrderRequests = listOf(
    FarmerOrderRequest(
        id = "R1",
        buyerName = "Nakato Fresh Foods",
        buyerLocation = "Nakasero Market, Kampala",
        produceName = "Maize",
        quantity = 80,
        offeredPricePerKg = 1100,
        buyerNote = "Can arrange pickup from your location. Prefer weekend delivery.",
        requestDate = "14 Apr 2026",
        isResponded = false
    ),
    FarmerOrderRequest(
        id = "R2",
        buyerName = "Ssemakula Traders",
        buyerLocation = "Owino Market, Kampala",
        produceName = "Tomatoes",
        quantity = 30,
        offeredPricePerKg = 2300,
        buyerNote = "Need delivery by Friday morning.",
        requestDate = "13 Apr 2026",
        isResponded = false
    )
)