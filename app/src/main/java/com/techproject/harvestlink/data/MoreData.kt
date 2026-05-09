package com.techproject.harvestlink.data

import com.techproject.harvestlink.model.Buyer
import com.techproject.harvestlink.model.Farmer
import com.techproject.harvestlink.model.FarmerOrderRequest
import com.techproject.harvestlink.model.FarmerOrderRequestInsert
import com.techproject.harvestlink.model.Order
import com.techproject.harvestlink.model.OrderDetails
import com.techproject.harvestlink.model.OrderIdResponse
import com.techproject.harvestlink.model.OrderInsert
import com.techproject.harvestlink.model.OrderItem
import com.techproject.harvestlink.model.OrderItemInsert
import com.techproject.harvestlink.model.Produce
import com.techproject.harvestlink.model.ProduceInsert
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray

object MoreData {

    private var cacheDao: HarvestDao? = null

    fun initCache(dao: HarvestDao) {
        cacheDao = dao
    }

    // ─── Auth ────────────────────────────────────────────────────────────────

    suspend fun signUp(
        email: String,
        password: String,
        name: String,
        role: String,
        number: String,
        location: String
    ): UserInfo? {
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

    suspend fun signIn(email: String, password: String) {
        SupabaseService.client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    // ─── Farmers ─────────────────────────────────────────────────────────────

    suspend fun fetchFarmers(): List<Farmer> {
        return try {
            val remote = SupabaseService.client.from("farmers_list").select().decodeList<Farmer>()
            cacheDao?.replaceFarmers(remote.map { it.toEntity() })
            remote
        } catch (e: Exception) {
            cacheDao?.getFarmers()?.map { it.toModel() } ?: emptyList()
        }
    }

    suspend fun fetchFarmerById(farmerId: String): Farmer? {
        return try {
            val remote = SupabaseService.client.from("farmers_list")
                .select { filter { eq("id", farmerId) } }
                .decodeList<Farmer>()
                .firstOrNull()
            if (remote != null) {
                cacheDao?.upsertFarmers(listOf(remote.toEntity()))
            }
            remote
        } catch (e: Exception) {
            cacheDao?.getFarmerById(farmerId)?.toModel()
        }
    }

    // ─── Produce / Farmer Listings ───────────────────────────────────────────

    suspend fun fetchProduce(): List<Produce> {
        return try {
            val remote = SupabaseService.client.from("produce_details").select().decodeList<Produce>()
            cacheDao?.replaceProduce(remote.map { it.toEntity() })
            remote
        } catch (e: Exception) {
            cacheDao?.getProduce()?.map { it.toModel() } ?: emptyList()
        }
    }

    suspend fun fetchFarmerProduce(farmerId: String): List<Produce> {
        return try {
            val remote = SupabaseService.client.from("produce_details")
                .select { filter { eq("farmer_id", farmerId) } }
                .decodeList<Produce>()
            cacheDao?.replaceProduceForFarmer(farmerId, remote.map { it.toEntity() })
            remote
        } catch (e: Exception) {
            cacheDao?.getProduceByFarmer(farmerId)?.map { it.toModel() } ?: emptyList()
        }
    }

    // Returns this farmer's own listings from the produce table, filtered by their ID
    suspend fun fetchFarmerListings(farmerId: String? = null): List<Produce> {
        return if (farmerId != null) {
            SupabaseService.client.from("produce")
                .select { filter { eq("farmer_id", farmerId) } }
                .decodeList<Produce>()
        } else {
            SupabaseService.client.from("produce").select().decodeList<Produce>()
        }
    }

    // Uses ProduceInsert to only send columns that exist in the produce table
    // (avoids sending farmerName which is a view-only field not in the base table)
    suspend fun addFarmerListing(produce: Produce) {
        val insert = ProduceInsert(
            farmerId = produce.farmerId,
            name = produce.name,
            category = produce.category,
            unit = produce.unit,
            description = produce.description.ifBlank { "" },
            harvestDate = produce.harvestDate?.ifBlank { null },
            imageUrl = produce.imageUrl,
            price = produce.price,
            availableQuantity = produce.availableQuantity,
            rating = produce.rating
        )
        SupabaseService.client.from("produce").insert(insert)
    }

    suspend fun uploadProduceImage(fileName: String, bytes: ByteArray): String? {
        return try {
            val bucket = SupabaseService.client.storage.from("produce-images")
            bucket.upload(fileName, bytes) {
                upsert = true
            }
            bucket.publicUrl(fileName)
        } catch (e: Exception) {
            null
        }
    }

    // ─── Order Requests ───────────────────────────────────────────────────────

    suspend fun fetchFarmerOrderRequests(farmerId: String? = null): List<FarmerOrderRequest> {
        // Return an empty list for now until the farmer_id column is added to the database table.
        // This avoids showing every farmer all requests.
        return emptyList()
    }

    suspend fun createOrderRequest(request: FarmerOrderRequest) {
        val insert = FarmerOrderRequestInsert(
            buyerName = request.buyerName,
            buyerLocation = request.buyerLocation,
            produceName = request.produceName,
            quantity = request.quantity,
            offeredPricePerKg = request.offeredPricePerKg,
            buyerNote = request.buyerNote,
            requestDate = request.requestDate,
            isResponded = request.isResponded
        )
        SupabaseService.client.from("farmer_order_requests").insert(insert)
    }

    // ─── Buyers ───────────────────────────────────────────────────────────────

    suspend fun fetchBuyers(): List<Buyer> {
        return try {
            val remote = SupabaseService.client.from("buyers_list").select().decodeList<Buyer>()
            cacheDao?.replaceBuyers(remote.map { it.toEntity() })
            remote
        } catch (e: Exception) {
            cacheDao?.getBuyers()?.map { it.toModel() } ?: emptyList()
        }
    }

    suspend fun updateBuyer(buyer: Buyer) {
        try {
            SupabaseService.client.from("buyers").update(buyer) {
                filter { eq("id", buyer.id) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteBuyer(id: String) {
        try {
            SupabaseService.client.from("users").delete {
                filter { eq("id", id) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ─── Orders ───────────────────────────────────────────────────────────────

    suspend fun fetchBuyerOrders(userId: String): List<OrderDetails> {
        return try {
            val remote = SupabaseService.client.from("order_details").select {
                filter { eq("buyer_id", userId) }
            }.decodeList<OrderDetails>()
            cacheDao?.replaceOrderDetailsForBuyer(userId, remote.map { it.toEntity() })
            remote
        } catch (e: Exception) {
            cacheDao?.getOrderDetailsForBuyer(userId)?.map { it.toModel() } ?: emptyList()
        }
    }

    suspend fun placeOrder(order: Order, orderItems: List<OrderItem>): Long {
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
        val result = SupabaseService.client.postgrest.rpc("create_order", payload)
        return result.decodeAs<Long>()
    }
}