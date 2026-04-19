package com.techproject.harvestlink.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.techproject.harvestlink.data.MoreData
import com.techproject.harvestlink.model.Produce

class HarvestViewModel : ViewModel() {
    var filterUiState by mutableStateOf(FilterUiState())
        private set

    var homeUiState by mutableStateOf(HomeUiState())
        private set


    fun updateCurrentProduce(produce: Produce) {
        homeUiState = homeUiState.copy(currentProduce = produce)
    }


    /**
     * Filter Logic
     */
    private val originalList = MoreData.produceList

    var filteredList by mutableStateOf(originalList)
        private set

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

        filteredList = originalList.filter { item ->

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
    val currentProduce: Produce = MoreData.produceList[0],
    val beginner: Boolean = true,
    val isFarmer:Boolean = false,
    val showNavBar: Boolean = true
)


