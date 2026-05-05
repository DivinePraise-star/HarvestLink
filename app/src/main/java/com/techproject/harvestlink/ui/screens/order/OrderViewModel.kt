package com.techproject.harvestlink.ui.screens.order

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techproject.harvestlink.data.MoreData
import com.techproject.harvestlink.model.Order
import com.techproject.harvestlink.model.OrderItem
import com.techproject.harvestlink.model.OrderStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrderViewModel(
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val userId: String = savedStateHandle.get<String>("userId") ?: ""
    private val _orderUiState = MutableStateFlow(OrderUiState())
    val orderUiState: StateFlow<OrderUiState> = _orderUiState

    init {
        loadOrders(userId)
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
                val data = MoreData.fetchBuyerOrders(currentUserId)
                val filteredList = data.groupBy {
                    Order(
                        id = it.orderId,
                        orderDate = it.orderedAt,
                        orderStatus = it.status,
                        deliveryAddress = it.deliveryAddress,
                        buyerId = it.buyerId,
                        farmerId = it.farmerId,
                        currency = it.currency,
                        subtotal = it.subtotal,
                        deliveryFee = it.deliveryFee,
                        totalAmount = it.totalAmount,
                        buyerName = it.buyerName,
                        buyerEmail = it.buyerEmail,
                        farmerName = it.farmerName,
                        farmerEmail = it.farmerEmail,
                    )
                }
                _orderUiState.update {
                    it.copy(
                        orders = filteredList
                    )
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "Failed to load orders", e)
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

    fun placeOrder(order: Order, orderItems: List<OrderItem>, onSuccess: (Long) -> Unit) {
        viewModelScope.launch {
            try {
                val result = MoreData.placeOrder(order, orderItems)
                onSuccess(result)
            } catch (e: Exception) {
                Log.e("OrderViewModel", "Error placing order", e)
            }
        }
    }
}
