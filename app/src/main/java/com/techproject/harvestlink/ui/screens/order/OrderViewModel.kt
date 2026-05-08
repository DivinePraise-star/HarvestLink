package com.techproject.harvestlink.ui.screens.order

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.techproject.harvestlink.HarvestLinkApplication
import com.techproject.harvestlink.data.MoreData
import com.techproject.harvestlink.data.SessionManager
import com.techproject.harvestlink.model.Order
import com.techproject.harvestlink.model.OrderItem
import com.techproject.harvestlink.model.OrderStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrderViewModel(
    val sessionManager: SessionManager
): ViewModel() {


    private val _orderUiState = MutableStateFlow(OrderUiState())
    val orderUiState: StateFlow<OrderUiState> = _orderUiState

    init {
        loadOrders()
    }

    fun toggleOrderDetails() {
        val showOrderDetailsValue = orderUiState.value.showOrderDetails
        _orderUiState.update {
            it.copy(
                showOrderDetails = !showOrderDetailsValue
            )
        }
    }

    fun loadOrders() {
        viewModelScope.launch {
            _orderUiState.update { it.copy(isLoading = true) }
            try {
                val session = sessionManager.sessionFlow.filterNotNull().first()
                val userId = session.userId

                val data = MoreData.fetchBuyerOrders(userId)
                val filteredList = data.groupBy {
                    Order(
                        id = it.orderId,
                        orderDate = it.orderedAt,
                        orderStatus = it.status,
                        deliveryAddress = it.deliveryAddress ?: "Unavailable",
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
            } finally {
                _orderUiState.update { it.copy(isLoading = false) }
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

    companion object{
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as HarvestLinkApplication
                OrderViewModel(app.sessionManager)
            }
        }
    }
}
