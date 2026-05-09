package com.techproject.harvestlink.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import com.techproject.harvestlink.data.chats.ChatRepository
import com.techproject.harvestlink.model.Buyer
import com.techproject.harvestlink.model.Farmer
import com.techproject.harvestlink.model.FarmerOrderRequest
import com.techproject.harvestlink.model.Produce
import com.techproject.harvestlink.model.OrderStatus
import com.techproject.harvestlink.model.Notification
import com.techproject.harvestlink.model.NotificationType
import kotlinx.coroutines.launch
import com.techproject.harvestlink.data.SupabaseService.client
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.serialization.json.jsonPrimitive

class HarvestViewModel(
    val chatRepository: ChatRepository,
    val sessionManager: SessionManager,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {
    var filterUiState by mutableStateOf(FilterUiState())
        private set

    var homeUiState by mutableStateOf(HomeUiState())
        private set

    var notifications by mutableStateOf<List<Notification>>(emptyList())
        private set

    var notificationCount by mutableStateOf(0)
        private set

    var buyerProfile by mutableStateOf<Buyer>(Buyer())
        private set

    // Data for Buyer Home
    var farmersList by mutableStateOf<List<Farmer>>(emptyList())
        private set
    // Add this property to track current user
    var currentUserId by mutableStateOf("")

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
        seedSampleNotifications()
        checkExistingSession()
    }

    private fun checkExistingSession() {
        viewModelScope.launch {
            sessionManager.sessionFlow.collect { session ->
                if (session != null && homeUiState.beginner) {
                    try {
                        client.auth.importAuthToken(session.accessToken, session.refreshToken)
                        currentUserId = session.userId  // ← SAVE THIS
                        savedStateHandle["userId"] = session.userId
                        homeUiState = homeUiState.copy(
                            beginner = false,
                            isFarmer = session.role == "farmer"
                        )
                        if (session.role == "buyer") {
                            loadBuyerData()
                        }
                    } catch (_: Exception) {
                        sessionManager.clearSession()
                    }
                }
            }
        }
    }

    private fun seedSampleNotifications() {
        notifications = listOf(
            Notification(
                id = "1",
                title = "Order Confirmed",
                message = "Your order for Organic Tomatoes has been confirmed by Farmer Ritah.",
                type = NotificationType.ORDER
            ),
            Notification(
                id = "2",
                title = "New Message",
                message = "Farmer Eria sent you a message regarding your delivery.",
                type = NotificationType.CHAT
            ),
            Notification(
                id = "3",
                title = "Harvest Alert",
                message = "Fresh Cabbages are now available from nearby farms!",
                type = NotificationType.PROMO
            )
        )
        updateNotificationCountFromList()
    }

    private fun updateNotificationCountFromList() {
        notificationCount = notifications.count { !it.isRead }
    }

    fun markAllNotificationsAsRead() {
        notifications = notifications.map { it.copy(isRead = true) }
        updateNotificationCountFromList()
    }

    fun markNotificationAsRead(id: String) {
        notifications = notifications.map { 
            if (it.id == id) it.copy(isRead = true) else it
        }
        updateNotificationCountFromList()
    }

    fun setRole(isFarmer: Boolean) {
        homeUiState = homeUiState.copy(isFarmer = isFarmer)
        if (!isFarmer) {
            loadBuyerData()
        }
        // Save the current session
        saveCurrentSession(isFarmer)
    }

    fun saveCurrentSession(isFarmer: Boolean) {
        viewModelScope.launch {
            try {
                val status = client.auth.sessionStatus.value
                if (status is SessionStatus.Authenticated) {
                    val session = status.session
                    val metadata = session.user?.userMetadata

                    val role = metadata?.get("role")?.jsonPrimitive?.content

                    val userId = session.user?.id ?: ""
                    currentUserId = userId  // ← SAVE THIS
                    sessionManager.saveSession(
                        accessToken = session.accessToken,
                        refreshToken = session.refreshToken,
                        userId = userId,
                        role = if (isFarmer) "farmer" else "buyer"
                    )
                }
            } catch (_: Exception) { }
        }
    }

    private fun loadBuyerData() {
        viewModelScope.launch {
            try {
                // Fetch the SPECIFIC logged-in buyer, not buyers[0]
                val buyers = MoreData.fetchBuyers()
                buyerProfile = buyers.find { it.id == currentUserId } ?: buyers.firstOrNull() ?: Buyer()

                // Use the ACTUAL user ID for orders
                val allOrders = MoreData.fetchBuyerOrders(currentUserId)
                    .groupBy { it.orderId }
                activeOrdersCount = allOrders.count {
                    it.value.any { order -> order.status != OrderStatus.cancelled }
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
        val id = currentUserId
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

    fun updateCurrentFarmer(farmer: Farmer) {
        homeUiState = homeUiState.copy(currentFarmer = farmer)
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

    fun enterGuestMode() {
        currentUserId = ""
        buyerProfile = Buyer(name = "Guest")
        activeOrdersCount = 0
        homeUiState = homeUiState.copy(
            beginner = false,
            isFarmer = false,
            showNavBar = true,
            authState = AuthState.SIGN_IN
        )
    }

    fun requireAuthentication() {
        homeUiState = homeUiState.copy(
            beginner = true,
            authState = AuthState.SIGN_IN
        )
    }

    fun logout() {
        viewModelScope.launch {
            try {
                // Clear Supabase session
                client.auth.signOut()
            } catch (_: Exception) {
                // Continue even if signOut fails
            }
            // Clear local session
            sessionManager.clearSession()
        }
        homeUiState = homeUiState.copy(
            beginner = true,
            authState = AuthState.SIGN_IN,
            isFarmer = false
        )
        currentUserId = ""
        buyerProfile = Buyer()
    }

    fun toggleFarmer(){
        homeUiState = homeUiState.copy(isFarmer = !homeUiState.isFarmer)
    }

    fun toggleNavBar() {
        homeUiState = homeUiState.copy(showNavBar = !homeUiState.showNavBar)
    }

    fun updateNotificationCount(count: Int) {
        notificationCount = count
    }

    suspend fun getOrCreateConversation(userId: String): String{
        return chatRepository.getOrCreateConversation(buyerProfile.id,userId)
    }

    fun submitOrderRequest(request: FarmerOrderRequest) {
        viewModelScope.launch {
            try {
                MoreData.createOrderRequest(request)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    companion object{
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as HarvestLinkApplication)
                val chatRepo = app.container.repository
                HarvestViewModel(chatRepo, app.sessionManager, this.createSavedStateHandle())
            }
        }
    }
}


data class FilterUiState(
    val categories: List<String> = emptyList(),
    val priceRange: ClosedFloatingPointRange<Float> = 0f..10000f,
)

data class HomeUiState(
    val currentProduce: Produce = Produce(),
    val currentFarmer: Farmer? = null,
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
