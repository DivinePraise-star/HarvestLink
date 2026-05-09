package com.techproject.harvestlink.ui.screens.farmer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techproject.harvestlink.data.MoreData
import com.techproject.harvestlink.data.SupabaseService
import com.techproject.harvestlink.model.FarmerOrderRequest
import com.techproject.harvestlink.model.Produce
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FarmerUiState(
    val listings: List<Produce> = emptyList(),
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
                val currentUser = SupabaseService.client.auth.currentUserOrNull()
                val farmerId = currentUser?.id

                val listings = MoreData.fetchFarmerListings(farmerId)
                
                // Fetch requests safely. If the table is broken, we at least show listings.
                val requests = try {
                    MoreData.fetchFarmerOrderRequests(farmerId)
                } catch (e: Exception) {
                    emptyList()
                }

                val farmerProfile = if (farmerId != null) {
                    MoreData.fetchFarmerById(farmerId)
                } else null

                _uiState.update {
                    it.copy(
                        listings = listings,
                        orderRequests = requests,
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