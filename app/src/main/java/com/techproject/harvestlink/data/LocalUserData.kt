package com.techproject.harvestlink.data

import com.techproject.harvestlink.model.User

object LocalUserData {
    suspend fun fetchUsers(): List<User> {
        // Fetch both farmers and buyers from Supabase
        val farmers = MoreData.fetchFarmers()
        val buyers = MoreData.fetchBuyers()
        return farmers + buyers
    }
}