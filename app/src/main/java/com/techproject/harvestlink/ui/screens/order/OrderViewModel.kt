package com.techproject.harvestlink.ui.screens.order

import androidx.lifecycle.ViewModel
import com.techproject.harvestlink.data.LocalOrderData
import com.techproject.harvestlink.model.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class OrderViewModel: ViewModel() {

    private val _orderUiState = MutableStateFlow(OrderUiState())
    val orderUiState: StateFlow<OrderUiState> = _orderUiState


    init {
        loadOrders("user5")
    }


    fun toggleOrderDetails() {
        val showOrderDetailsValue = orderUiState.value.showOrderDetails
        _orderUiState.update {
            it.copy(
                showOrderDetails = !showOrderDetailsValue
            )
        }
    }

    fun loadOrders(currentUserId: String){
        val data = LocalOrderData.sampleOrderData

        val filteredList = data.filter { orders -> orders.userId == currentUserId }.sortedBy { it.orderDate }

        _orderUiState.update {
            it.copy(
                orders = filteredList
            )
        }
    }

    fun showOrderDetails(selectedOrder: Order) {
        val showOrderDetailsValue = orderUiState.value.showOrderDetails
        _orderUiState.update {
            it.copy(
                showOrderDetails = !showOrderDetailsValue,
                selectedOrder = selectedOrder
            )
        }
    }
}