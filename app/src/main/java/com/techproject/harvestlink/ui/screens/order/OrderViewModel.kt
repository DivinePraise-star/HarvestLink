package com.techproject.harvestlink.ui.screens.order

import androidx.lifecycle.ViewModel
import com.techproject.harvestlink.data.MoreData
import com.techproject.harvestlink.model.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class OrderViewModel: ViewModel() {
    private val _orderUiState = MutableStateFlow(OrderUiState())
    val orderUiState: StateFlow<OrderUiState> = _orderUiState

    init {
        loadOrders("buyer-001")
    }

    fun toggleOrderDetails() {
        val showOrderDetailsValue = orderUiState.value.showOrderDetails
        _orderUiState.update {
            it.copy(
                showOrderDetails = !showOrderDetailsValue
            )
        }
    }

    fun loadOrders(currentUserId: String) {
        viewModelScope.launch {
            try {
                val data = MoreData.fetchOrders()
                val filteredList = data.filter { it.userId == currentUserId }.sortedBy { it.orderDate }
                _orderUiState.update {
                    it.copy(
                        orders = filteredList
                    )
                }
            } catch (_: Exception) {
                // Handle error state if needed
            }
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