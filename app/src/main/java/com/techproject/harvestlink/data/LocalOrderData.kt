package com.techproject.harvestlink.data

import com.techproject.harvestlink.model.Order
import com.techproject.harvestlink.model.OrderItem
import com.techproject.harvestlink.model.OrderStatus

val produceList = MoreData.produceList
val farmerJohn = MoreData.farmerJohn
val farmerAlice = MoreData.farmerAlice
val farmerSamuel = MoreData.farmerSamuel
val buyerJane = MoreData.buyerJane
val buyerDavid = MoreData.buyerDavid


val orderItem1 = OrderItem(
    product = produceList[0], // Organic Tomatoes
    quantity = 3
)
val orderItem2 = OrderItem(
    product = produceList[2], // Fresh Kale
    quantity = 2
)
val orderItem3 = OrderItem(
    product = produceList[1], // Sweet Bananas
    quantity = 5
)
val orderItem4 = OrderItem(
    product = produceList[4], // Green Beans
    quantity = 1
)
val orderItem5 = OrderItem(
    product = produceList[3], // Macadamia Nuts
    quantity = 2
)
val orderItem6 = OrderItem(
    product = produceList[5], // Yellow Maize
    quantity = 10
)

object LocalOrderData {
    val sampleOrderData  = listOf(
        Order(
            id = 1001,
            orderDate = System.currentTimeMillis() - 86400000 * 2, // 2 days ago
            items = listOf(orderItem1, orderItem2),
            orderStatus = OrderStatus.CANCELLED,
            deliveryAddress = buyerJane.deliveryAddress ?: "Westlands, Nairobi",
            userId = buyerJane.id,
            farmerId = farmerJohn.id
        ),
        Order(
            id = 1002,
            orderDate = System.currentTimeMillis() - 86400000, // 1 day ago
            items = listOf(orderItem3, orderItem5),
            orderStatus = OrderStatus.DELIVERED,
            deliveryAddress = "Kilimani, Nairobi",
            userId = buyerDavid.id,
            farmerId = farmerAlice.id
        ),
        Order(
            id = 1003,
            orderDate = System.currentTimeMillis() - 3600000 * 5, // 5 hours ago
            items = listOf(orderItem4),
            orderStatus = OrderStatus.PENDING,
            deliveryAddress = buyerJane.deliveryAddress ?: "Westlands, Nairobi",
            userId = buyerJane.id,
            farmerId = farmerSamuel.id
        ),
        Order(
            id = 1004,
            orderDate = System.currentTimeMillis() - 86400000 * 5, // 5 days ago
            items = listOf(orderItem6),
            orderStatus = OrderStatus.IN_TRANSIT,
            deliveryAddress = "Thika Road, Nairobi",
            userId = buyerDavid.id,
            farmerId = farmerSamuel.id
        ),
        Order(
            id = 1005,
            orderDate = System.currentTimeMillis() - 3600000 * 2, // 2 hours ago
            items = listOf(orderItem1.copy(quantity = 1), orderItem2.copy(quantity = 4)),
            orderStatus = OrderStatus.DELIVERED,
            deliveryAddress = buyerJane.deliveryAddress ?: "Westlands, Nairobi",
            userId = buyerJane.id,
            farmerId = farmerJohn.id
        )
    )
}
