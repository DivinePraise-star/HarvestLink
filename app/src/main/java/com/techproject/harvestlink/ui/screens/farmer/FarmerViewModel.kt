package com.techproject.harvestlink.ui.screens.farmer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techproject.harvestlink.data.MoreData
import com.techproject.harvestlink.data.SupabaseService
import com.techproject.harvestlink.model.FarmerListing
import com.techproject.harvestlink.model.FarmerOrderRequest
import io.github.jan.supabase.auth.auth
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
    val farmerName: String = "",
    val farmerLocation: String = ""
)

class FarmerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(FarmerUiState())
    val uiState: StateFlow<FarmerUiState> = _uiState.asStateFlow()

    init {
        refreshDashboard()
    }

    fun refreshDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Get the logged-in user's ID from Supabase Auth
                val currentUser = SupabaseService.client.auth.currentUserOrNull()
                val farmerId = currentUser?.id

                // Fetch data filtered to this farmer only
                //val listings = MoreData.fetchFarmerListings(farmerId)
                //val requests = MoreData.fetchFarmerOrderRequests(farmerId)

                // Fetch this farmer's profile for their name and location
                val farmerProfile = if (farmerId != null) {
                    MoreData.fetchFarmerById(farmerId)
                } else null

                _uiState.update {
                    it.copy(
                        listings = emptyList(),
                        orderRequests = emptyList(),
                        farmerName = farmerProfile?.name ?: "Farmer",
                        farmerLocation = farmerProfile?.location ?: "",
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
}