package com.techproject.harvestlink.data

import com.techproject.harvestlink.data.SupabaseService.client
import com.techproject.harvestlink.model.Buyer
import com.techproject.harvestlink.model.Farmer
import io.github.jan.supabase.postgrest.postgrest
import com.techproject.harvestlink.model.FarmerListing
import com.techproject.harvestlink.model.FarmerOrderRequest
import com.techproject.harvestlink.model.Produce
import com.techproject.harvestlink.model.Order
import com.techproject.harvestlink.model.OrderDetails
import com.techproject.harvestlink.model.OrderItem
import io.github.jan.supabase.postgrest.rpc
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.addJsonObject

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
        return SupabaseClient.client.postgrest["produce_details"].select().decodeList<Produce>()
    }

    suspend fun fetchFarmerProduce(farmerId: String): List<Produce>{
        return SupabaseClient.client.postgrest.from("produce_details")
            .select {
                filter {
                    eq("farmer_id", farmerId)
                }
            }.decodeList<Produce>()
    }

    suspend fun fetchFarmerListings(): List<FarmerListing> {
        return SupabaseClient.client.postgrest["farmer_listings"].select().decodeList<FarmerListing>()
    }

    suspend fun fetchFarmerOrderRequests(): List<FarmerOrderRequest> {
        return SupabaseClient.client.postgrest["farmer_order_requests"].select().decodeList<FarmerOrderRequest>()
    }

    suspend fun fetchBuyerOrders(userId: String): List<OrderDetails> {
        return SupabaseClient.client.postgrest["order_details"].select{
            filter { eq("buyer_id", userId) }
        }.decodeList<OrderDetails>()
    }

    suspend fun placeOrder(order: Order,orderItems: List<OrderItem>): Long{
        val payload = buildJsonObject {
            put("p_buyer_id", order.buyerId)
            put("p_farmer_id", order.farmerId)
            put("p_currency", order.currency)
            put("p_subtotal", order.subtotal)
            put("p_delivery_fee", order.deliveryFee)
            put("p_total_amount", order.totalAmount)
            putJsonArray("p_items") {
                orderItems.forEach { item ->
                    addJsonObject {
                        put("produce_id", item.product.id)
                        put("quantity", item.quantity.toDouble())
                    }
                }
            }
        }
        val result = client.postgrest.rpc("create_order",payload)

        return result.decodeAs<Long>()
    }
}