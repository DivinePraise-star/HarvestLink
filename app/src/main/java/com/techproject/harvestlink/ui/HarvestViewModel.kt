package com.techproject.harvestlink.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techproject.harvestlink.data.MoreData
import com.techproject.harvestlink.model.Produce
import kotlinx.coroutines.launch

class HarvestViewModel : ViewModel() {
    var filterUiState by mutableStateOf(FilterUiState())
        private set

    var homeUiState by mutableStateOf(HomeUiState())
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
        get() = produceList.map { it.category }.distinct().filter { it.isNotBlank() }

    // Filtered list state
    var filteredList by mutableStateOf<List<Produce>>(emptyList())
        private set

    init {
        loadProduce()
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

    fun toggleFarmer(){
        homeUiState = homeUiState.copy(isFarmer = !homeUiState.isFarmer)
    }

    fun toggleNavBar() {
        homeUiState = homeUiState.copy(showNavBar = !homeUiState.showNavBar)
    }
}


data class FilterUiState(
    val categories: List<String> = emptyList(),
    val priceRange: ClosedFloatingPointRange<Float> = 0f..500f,
)

data class HomeUiState(
    val currentProduce: Produce? = null,
    val beginner: Boolean = true,
    val isFarmer:Boolean = false,
    val showNavBar: Boolean = true
)
