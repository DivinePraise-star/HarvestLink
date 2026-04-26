package com.techproject.harvestlink.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techproject.harvestlink.data.MoreData
import com.techproject.harvestlink.model.Buyer
import com.techproject.harvestlink.model.Farmer
import com.techproject.harvestlink.model.Produce
import com.techproject.harvestlink.model.User
import com.techproject.harvestlink.model.OrderStatus
import kotlinx.coroutines.launch

class HarvestViewModel : ViewModel() {
    var filterUiState by mutableStateOf(FilterUiState())
        private set

    var homeUiState by mutableStateOf(HomeUiState())
        private set

    var buyerProfile by mutableStateOf<Buyer>(Buyer())
        private set

    // Data for Buyer Home
    var farmersList by mutableStateOf<List<Farmer>>(emptyList())
        private set
    var activeOrdersCount by mutableStateOf(0)
        private set

    // Produce loading state
    var produceList by mutableStateOf<List<Produce>>(emptyList())
        private set
    var produceLoading by mutableStateOf(true)
        private set
    var produceError by mutableStateOf<String?>(null)
        private set

    // Expose distinct categories from produceList
    val categories: List<String>
        get() = (listOf("All") + produceList.map { it.category }.distinct()).filter { it.isNotBlank() }

    // Filtered list state
    var filteredList by mutableStateOf<List<Produce>>(emptyList())
        private set

    init {
        loadProduce()
    }

    fun setRole(isFarmer: Boolean) {
        homeUiState = homeUiState.copy(isFarmer = isFarmer)
        if (!isFarmer) {
            loadBuyerData()
        }
    }

    private fun loadBuyerData() {
        viewModelScope.launch {
            try {
                val buyers = MoreData.fetchBuyers()
                if (buyers.isNotEmpty()) {
                    buyerProfile = buyers[0]
                    val allOrders = MoreData.fetchOrders()
                    activeOrdersCount = allOrders.count { 
                        it.userId == buyerProfile?.id && 
                        it.orderStatus != OrderStatus.DELIVERED && 
                        it.orderStatus != OrderStatus.CANCELLED 
                    }
                }
                farmersList = MoreData.fetchFarmers()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun loadProduce() {
        produceLoading = true
        produceError = null
        viewModelScope.launch {
            try {
                val loaded = MoreData.fetchProduce()
                produceList = loaded
                filteredList = loaded
                // Set default currentProduce if not set
                if (loaded.isNotEmpty()) {
                    homeUiState = homeUiState.copy(currentProduce = loaded[0])
                }
            } catch (e: Exception) {
                produceError = e.localizedMessage ?: "Unknown error"
            } finally {
                produceLoading = false
            }
        }
    }

    fun updateProfile(buyer: Buyer) {
        viewModelScope.launch {
            try {
                MoreData.updateBuyer(buyer)
                buyerProfile = buyer
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteAccount(onDeleted: () -> Unit) {
        val id = buyerProfile?.id ?: return
        viewModelScope.launch {
            try {
                MoreData.deleteBuyer(id)
                buyerProfile = Buyer()
                onDeleted()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateCurrentProduce(produce: Produce) {
        homeUiState = homeUiState.copy(currentProduce = produce)
    }

    /**
     * Filter Logic
     */
    fun updatePriceRange(range: ClosedFloatingPointRange<Float>) {
        filterUiState = filterUiState.copy(priceRange = range)
        applyFilters()
    }

    fun toggleCategory(category: String) {
        val current = filterUiState.categories.toMutableList()

        if (current.contains(category)) {
            current.remove(category)
        } else {
            current.add(category)
        }

        filterUiState = filterUiState.copy(categories = current)
        applyFilters()
    }

    private fun applyFilters() {
        val filter = filterUiState
        filteredList = produceList.filter { item ->
            val matchesCategory =
                filter.categories.isEmpty() ||
                        filter.categories.contains(item.category)
            val matchesPrice =
                item.price.toFloat() in filter.priceRange
            matchesCategory && matchesPrice
        }
    }

    fun toggleBeginner(){
        homeUiState = homeUiState.copy(beginner = !homeUiState.beginner)
    }

    fun updateAuthState(state: AuthState) {
        homeUiState = homeUiState.copy(authState = state)
    }

    fun logout() {
        homeUiState = homeUiState.copy(
            beginner = true,
            authState = AuthState.SIGN_IN
        )
        buyerProfile = Buyer()
    }

    fun toggleFarmer(){
        homeUiState = homeUiState.copy(isFarmer = !homeUiState.isFarmer)
    }

    fun toggleNavBar() {
        homeUiState = homeUiState.copy(showNavBar = !homeUiState.showNavBar)
    }
}


data class FilterUiState(
    val categories: List<String> = emptyList(),
    val priceRange: ClosedFloatingPointRange<Float> = 0f..10000f,
)

data class HomeUiState(
    val currentProduce: Produce? = null,
    val beginner: Boolean = true,
    val isFarmer:Boolean = false,
    val showNavBar: Boolean = true,
    val authState: AuthState = AuthState.SPLASH
)

enum class AuthState {
    SPLASH,
    ONBOARDING,
    WELCOME,
    SIGN_IN,
    SIGN_UP,
    FORGOT_PASSWORD,
    AUTHENTICATED
}
