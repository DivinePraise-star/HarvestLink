package com.techproject.harvestlink.ui.screens.order

import com.techproject.harvestlink.model.Order

data class OrderUiState(
    val orders: List<Order> = emptyList(),
    val showOrderDetails: Boolean = false,
    val selectedOrder: Order? = null,
)
