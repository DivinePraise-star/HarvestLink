package com.techproject.harvestlink.data

import com.techproject.harvestlink.model.FarmerListing
import com.techproject.harvestlink.model.FarmerOrderRequest
import com.techproject.harvestlink.model.Produce
import com.techproject.harvestlink.model.User
import com.techproject.harvestlink.model.Order
import io.github.jan.supabase.postgrest.postgrest

object MoreData {

    suspend fun fetchFarmers(): List<User.Farmer> {
        return SupabaseClient.client.postgrest["farmers"].select().decodeList<User.Farmer>()
    }

    suspend fun fetchBuyers(): List<User.Buyer> {
        return SupabaseClient.client.postgrest["buyers"].select().decodeList<User.Buyer>()
    }

    suspend fun fetchBuyerById(id: String): User.Buyer? {
        return SupabaseClient.client.postgrest["buyers"].select {
            eq("id", id)
        }.decodeSingleOrNull<User.Buyer>()
    }

    suspend fun updateBuyer(buyer: User.Buyer) {
        SupabaseClient.client.postgrest["buyers"].update(buyer) {
            eq("id", buyer.id)
        }
    }

    suspend fun deleteBuyer(id: String) {
        SupabaseClient.client.postgrest["buyers"].delete {
            eq("id", id)
        }
    }

    suspend fun fetchProduce(): List<Produce> {
        return SupabaseClient.client.postgrest["produce"].select().decodeList<Produce>()
    }

    suspend fun fetchFarmerListings(): List<FarmerListing> {
        return SupabaseClient.client.postgrest["farmer_listings"].select().decodeList<FarmerListing>()
    }

    suspend fun fetchFarmerOrderRequests(): List<FarmerOrderRequest> {
        return SupabaseClient.client.postgrest["farmer_order_requests"].select().decodeList<FarmerOrderRequest>()
    }

    suspend fun fetchOrders(): List<Order> {
        return SupabaseClient.client.postgrest["orders"].select().decodeList<Order>()
    }
}
