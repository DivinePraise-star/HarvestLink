package com.techproject.harvestlink.data

import com.techproject.harvestlink.model.Buyer
import com.techproject.harvestlink.model.Farmer
import com.techproject.harvestlink.model.FarmerListing
import com.techproject.harvestlink.model.FarmerOrderRequest
import com.techproject.harvestlink.model.Produce
import com.techproject.harvestlink.model.OrderDetails
import io.github.jan.supabase.postgrest.from

object MoreData {

    suspend fun fetchFarmers(): List<Farmer> {
        return SupabaseService.client.from("farmers_list").select().decodeList<Farmer>()
    }

    suspend fun fetchBuyers(): List<Buyer> {
        return SupabaseService.client.from("buyers_list").select().decodeList<Buyer>()
    }

    suspend fun updateBuyer(buyer: Buyer) {
        try {
            SupabaseService.client.from("buyers").update(buyer) {
                filter {
                    eq("id", buyer.id)
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    suspend fun deleteBuyer(id: String) {
        try {
            SupabaseService.client.from("users").delete {
                filter {
                    eq("id", id)
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    suspend fun fetchProduce(): List<Produce> {
        return SupabaseService.client.from("produce").select().decodeList<Produce>()
    }

    suspend fun fetchFarmerListings(): List<FarmerListing> {
        return SupabaseService.client.from("farmer_listings").select().decodeList<FarmerListing>()
    }

    suspend fun fetchFarmerOrderRequests(): List<FarmerOrderRequest> {
        return SupabaseService.client.from("farmer_order_requests").select().decodeList<FarmerOrderRequest>()
    }

    suspend fun fetchBuyerOrders(userId: String): List<OrderDetails> {
        return SupabaseService.client.from("order_details").select {
            filter {
                eq("buyer_id", userId)
            }
        }.decodeList<OrderDetails>()
    }

    suspend fun createOrderRequest(request: FarmerOrderRequest) {
        SupabaseService.client.from("farmer_order_requests").insert(request)
    }
}