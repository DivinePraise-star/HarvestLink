package com.techproject.harvestlink.data

import com.techproject.harvestlink.model.Buyer
import com.techproject.harvestlink.model.Farmer
import io.github.jan.supabase.postgrest.postgrest
import com.techproject.harvestlink.model.FarmerListing
import com.techproject.harvestlink.model.FarmerOrderRequest
import com.techproject.harvestlink.model.Produce
import com.techproject.harvestlink.model.Order
import io.github.jan.supabase.postgrest.rpc

object MoreData {

    suspend fun fetchFarmers(): List<Farmer> {
        return SupabaseClient.client.postgrest["farmers_list"].select().decodeList<Farmer>()
    }

    suspend fun fetchBuyers(): List<Buyer> {
        return SupabaseClient.client.postgrest["buyers_list"].select().decodeList<Buyer>()
    }

    suspend fun updateBuyer(buyer: Buyer) {
        try {
            SupabaseClient.client.postgrest.rpc("update_buyer", buyer)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    suspend fun deleteBuyer(id: String) {
        try {
            SupabaseClient.client.postgrest.rpc("delete_user", id)
        }catch (e: Exception){
            e.printStackTrace()
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