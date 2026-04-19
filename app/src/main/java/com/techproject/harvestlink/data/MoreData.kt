package com.techproject.harvestlink.data

import com.techproject.harvestlink.model.FarmerListing
import com.techproject.harvestlink.model.FarmerOrderRequest
import com.techproject.harvestlink.model.ListingStatus
import com.techproject.harvestlink.model.Produce
import com.techproject.harvestlink.model.User

object MoreData {

    // --- Farmers ---
    val farmerJohn = User.Farmer(
        id = "user1",
        name = "Mukisa Samuel",
        email = "sam.mukisa@farm.com",
        phoneNumber = "+254712345678",
        profileImageRes = null, // R.drawable.farmer_john
        isOnline = true,      // R.drawable.ic_farmer_avatar
        location = "Limuru, Kiambu",
        rating = 4.8,
        salesCompleted = 156,
        farmName = "Green Valley Farm",
        productCategories = listOf("Vegetables", "Herbs")
    )

    val farmerAlice = User.Farmer(
        id = "user2",
        name = "Kirabo Eria",
        email = "eria.kirabo@email.com",
        phoneNumber = "+254723987654",
        profileImageRes = null,
        isOnline = false,
        location = "Karatina, Nyeri",
        rating = 4.9,
        salesCompleted = 312,
        farmName = "Sunrise Organics",
        productCategories = listOf("Fruits", "Vegetables", "Nuts")
    )

    val farmerSamuel = User.Farmer(
        id = "user3",
        name = "Khalayi Patience",
        email = "patience@lakeagri.com",
        phoneNumber = "+254711223344",
        profileImageRes = null,
        isOnline = true,      // R.drawable.ic_default_avatar
        location = "Kisumu",
        rating = 4.6,
        salesCompleted = 89,
        farmName = "Lake Basin Greens",
        productCategories = listOf("Grains", "Legumes")
    )

    val farmers = listOf(farmerJohn, farmerAlice, farmerSamuel)

    // --- Buyers ---
    val buyerJane = User.Buyer(
        id = "user4",
        name = "Tendo Divine",
        email = "divine.tendo@farmfresh.co.ke",
        phoneNumber = "+254700112233",
        profileImageRes = null, // R.drawable.buyer_jane
        isOnline = true,      // R.drawable.ic_buyer_avatar
        deliveryAddress = "Westlands, Nairobi",
        preferredPaymentMethod = "M-Pesa",
        orderHistoryCount = 12
    )

    val buyerDavid = User.Buyer(
        id = "user5",
        name = "Mark Mike",
        email = "mike.mark@business.com",
        phoneNumber = "+254733445566",
        profileImageRes = null,
        isOnline = false,     // R.drawable.ic_buyer_avatar2
        deliveryAddress = "Thika Road, Nairobi",
        preferredPaymentMethod = "Bank Transfer",
        orderHistoryCount = 3
    )

    val buyers = listOf(buyerJane, buyerDavid)

    // --- Produce Listings ---
    val produceList = listOf(
        Produce(
            id = "p1",
            name = "Organic Tomatoes",
            price = 120.0,
            unit = "kg",
            availableQuantity = 50,
            rating = 4.7,
            description = "Fresh, vine-ripened organic tomatoes. Perfect for salads and cooking.",
            harvestDate = "2026-04-10",
            farmer = farmerJohn,
            imageRes = 501, // R.drawable.produce_tomatoes
            category = "Vegetables"
        ),
        Produce(
            id = "p2",
            name = "Sweet Bananas",
            price = 85.0,
            unit = "bunch",
            availableQuantity = 30,
            rating = 4.9,
            description = "Naturally ripened sweet bananas from Karatina.",
            harvestDate = "2026-04-05",
            farmer = farmerAlice,
            imageRes = 502, // R.drawable.produce_bananas
            category = "Fruits"
        ),
        Produce(
            id = "p3",
            name = "Fresh Kale (Sukuma Wiki)",
            price = 60.0,
            unit = "bundle",
            availableQuantity = 100,
            rating = 4.5,
            description = "Tender, organic kale. Harvested daily.",
            harvestDate = "2026-04-15",
            farmer = farmerJohn,
            imageRes = 503, // R.drawable.produce_kale
            category = "Vegetables"
        ),
        Produce(
            id = "p4",
            name = "Macadamia Nuts",
            price = 450.0,
            unit = "kg",
            availableQuantity = 15,
            rating = 4.8,
            description = "Premium quality, raw macadamia nuts from Nyeri.",
            harvestDate = "2026-03-20",
            farmer = farmerAlice,
            imageRes = 504, // R.drawable.produce_macadamia
            category = "Nuts"
        ),
        Produce(
            id = "p5",
            name = "Green Beans",
            price = 90.0,
            unit = "kg",
            availableQuantity = 40,
            rating = 4.3,
            description = "Crisp, pesticide-free green beans. Great for stir-fry.",
            harvestDate = "2026-04-12",
            farmer = farmerSamuel,
            imageRes = null,
            category = "Legumes"
        ),
        Produce(
            id = "p6",
            name = "Yellow Maize",
            price = 75.0,
            unit = "2kg pack",
            availableQuantity = 200,
            rating = 4.2,
            description = "Dry yellow maize, ideal for ugali or animal feed.",
            harvestDate = "2026-02-28",
            farmer = farmerSamuel,
            imageRes = 505, // R.drawable.produce_maize
            category = "Grains"
        )
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

    // Helper function to get produce by category
    fun getProduceByCategory(category: String): List<Produce> {
        return produceList.filter { it.category.equals(category, ignoreCase = true) }
    }

    // Helper to get farmer by ID
    fun getFarmerById(id: String): User.Farmer? {
        return farmers.find { it.id == id }
    }
}