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
