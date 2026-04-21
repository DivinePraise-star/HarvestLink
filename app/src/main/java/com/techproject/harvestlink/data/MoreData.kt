package com.techproject.harvestlink.data

import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import com.techproject.harvestlink.model.FarmerListing
import com.techproject.harvestlink.model.FarmerOrderRequest
import com.techproject.harvestlink.model.ListingStatus
import com.techproject.harvestlink.model.Produce
import com.techproject.harvestlink.model.User
import com.techproject.harvestlink.model.Order

object MoreData {

    // Data will be fetched from Supabase

    // TODO: Implement fetching farmers, buyers, produce, listings, and order requests from Supabase

    // Example stubs for later implementation:
    suspend fun fetchFarmers(): List<User.Farmer> {
        val result = SupabaseService.client.postgrest.from("farmers").select().decodeList<User.Farmer>()
        return result
    }

    suspend fun fetchBuyers(): List<User.Buyer> {
        val result = SupabaseService.client.postgrest.from("buyers").select().decodeList<User.Buyer>()
        return result
    }

    suspend fun fetchProduce(): List<Produce> {
        val result = SupabaseService.client.postgrest.from("produce").select().decodeList<Produce>()
        return result
    }

    suspend fun fetchFarmerListings(): List<FarmerListing> {
        val result = SupabaseService.client.postgrest.from("farmer_listings").select().decodeList<FarmerListing>()
        return result
    }

    suspend fun fetchFarmerOrderRequests(): List<FarmerOrderRequest> {
        val result = SupabaseService.client.postgrest.from("farmer_order_requests").select().decodeList<FarmerOrderRequest>()
        return result
    }

    suspend fun fetchOrders(): List<Order> {
        val result = SupabaseService.client.postgrest.from("orders").select().decodeList<Order>()
        return result
    }
}