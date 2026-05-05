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
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

object MoreData {

    suspend fun signUp(email: String, password: String, name: String, role: String, number: String, location: String): UserInfo? {
        return SupabaseService.client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
            data = buildJsonObject {
                put("name", name)
                put("role", role)
                put("number", number)
                put("location", location)
            }
        }
    }

    suspend fun signIn(email: String, password: String){
        SupabaseService.client.auth.signInWith(Email) {
                this.email = email
                this.password = password
        }
    }

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
    suspend fun addFarmerListing(listing: FarmerListing) {
        SupabaseService.client.from("farmer_listings").insert(listing)
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
        return if (farmerId != null) {
            SupabaseService.client.from("farmer_listings")
                .select { filter { eq("farmer_id", farmerId) } }
                .decodeList()
        } else {
            SupabaseService.client.from("farmer_listings").select().decodeList()
        }
    }

    suspend fun fetchFarmerOrderRequests(farmerId: String? = null): List<FarmerOrderRequest> {
        return if (farmerId != null) {
            SupabaseService.client.from("farmer_order_requests")
                .select { filter { eq("farmer_id", farmerId) } }
                .decodeList()
        } else {
            SupabaseService.client.from("farmer_order_requests").select().decodeList()
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
    suspend fun fetchFarmerById(farmerId: String): Farmer? {
        return SupabaseService.client.from("farmers_list")
            .select { filter { eq("id", farmerId) } }
            .decodeList<Farmer>()
            .firstOrNull()
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
