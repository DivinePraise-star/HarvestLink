package com.techproject.harvestlink.data

import com.techproject.harvestlink.model.Buyer
import com.techproject.harvestlink.model.Farmer
import com.techproject.harvestlink.model.FarmerListing
import com.techproject.harvestlink.model.FarmerOrderRequest
import com.techproject.harvestlink.model.Produce
import com.techproject.harvestlink.model.OrderDetails
import com.techproject.harvestlink.model.Order
import com.techproject.harvestlink.model.OrderIdResponse
import com.techproject.harvestlink.model.OrderItem
import com.techproject.harvestlink.model.OrderInsert
import com.techproject.harvestlink.model.OrderItemInsert
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
        return SupabaseService.client.from("produce_details").select().decodeList<Produce>()
    }

    suspend fun fetchFarmerProduce(farmerId: String): List<Produce>{
        return SupabaseService.client.from("produce_details")
            .select {
                filter {
                    eq("farmer_id", farmerId)
                }
            }.decodeList<Produce>()
    }

    suspend fun fetchFarmerListings(farmerId: String? = null): List<FarmerListing> {
        return if (farmerId == null) {
            SupabaseService.client.from("farmer_listings").select().decodeList<FarmerListing>()
        } else {
            SupabaseService.client.from("farmer_listings").select {
                filter { eq("farmer_id", farmerId) }
            }.decodeList<FarmerListing>()
        }
    }

    suspend fun fetchFarmerOrderRequests(farmerId: String? = null): List<FarmerOrderRequest> {
        return if (farmerId == null) {
            SupabaseService.client.from("farmer_order_requests").select().decodeList<FarmerOrderRequest>()
        } else {
            SupabaseService.client.from("farmer_order_requests").select {
                filter { eq("farmer_id", farmerId) }
            }.decodeList<FarmerOrderRequest>()
        }
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

    suspend fun placeOrder(order: Order, orderItems: List<OrderItem>): Long {
        val orderInsert = OrderInsert(
            orderDate = System.currentTimeMillis(),
            orderStatus = order.orderStatus.name.uppercase(),
            deliveryAddress = order.deliveryAddress,
            userId = order.buyerId,
            farmerId = order.farmerId,
        )

        val response = SupabaseService.client.from("orders").insert(orderInsert) {
            select()
        }.decodeSingle<OrderIdResponse>()

        val orderId = response.id

        val itemsToInsert = orderItems.map {
            OrderItemInsert(
                orderId = orderId,
                produceId = it.product.id,
                quantity = it.quantity
            )
        }

        SupabaseService.client.from("order_items").insert(itemsToInsert)

        return orderId.toLong()
    }
}
