package com.techproject.harvestlink.ui.screens.order

import com.techproject.harvestlink.model.Order
import com.techproject.harvestlink.model.OrderDetails

data class OrderUiState(
    val orders:   Map<Order, List<OrderDetails>> = emptyMap(),
    val showOrderDetails: Boolean = false,
    val selectedOrder: Order? = null,
)
