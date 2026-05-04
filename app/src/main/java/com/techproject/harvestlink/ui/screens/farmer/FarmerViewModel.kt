package com.techproject.harvestlink.ui.screens.farmer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techproject.harvestlink.data.MoreData
import com.techproject.harvestlink.model.FarmerListing
import com.techproject.harvestlink.model.FarmerOrderRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FarmerUiState(
    val listings: List<FarmerListing> = emptyList(),
    val orderRequests: List<FarmerOrderRequest> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val farmerName: String = "Ritah Patience" // Default or fetched name
)

class FarmerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(FarmerUiState())
    val uiState: StateFlow<FarmerUiState> = _uiState.asStateFlow()

    init {
        refreshDashboard()
    }

    fun refreshDashboard(farmerId: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // In a real app, we'd pass the logged-in farmer's ID here
                val listings = MoreData.fetchFarmerListings(farmerId)
                val requests = MoreData.fetchFarmerOrderRequests(farmerId)
                
                _uiState.update { 
                    it.copy(
                        listings = listings,
                        orderRequests = requests,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        error = e.localizedMessage ?: "Failed to load dashboard"
                    )
                }
            }
        }
    }
    
    // Additional logic for accepting/rejecting orders can go here
}
