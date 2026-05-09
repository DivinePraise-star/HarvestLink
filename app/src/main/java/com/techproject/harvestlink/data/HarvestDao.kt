package com.techproject.harvestlink.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface HarvestDao {
	// Farmers
	@Query("SELECT * FROM farmer_cache")
	suspend fun getFarmers(): List<FarmerEntity>

	@Query("SELECT * FROM farmer_cache WHERE id = :farmerId LIMIT 1")
	suspend fun getFarmerById(farmerId: String): FarmerEntity?

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun upsertFarmers(items: List<FarmerEntity>)

	@Query("DELETE FROM farmer_cache")
	suspend fun clearFarmers()

	@Transaction
	suspend fun replaceFarmers(items: List<FarmerEntity>) {
		clearFarmers()
		upsertFarmers(items)
	}

	// Buyers
	@Query("SELECT * FROM buyer_cache")
	suspend fun getBuyers(): List<BuyerEntity>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun upsertBuyers(items: List<BuyerEntity>)

	@Query("DELETE FROM buyer_cache")
	suspend fun clearBuyers()

	@Transaction
	suspend fun replaceBuyers(items: List<BuyerEntity>) {
		clearBuyers()
		upsertBuyers(items)
	}

	// Produce
	@Query("SELECT * FROM produce_cache")
	suspend fun getProduce(): List<ProduceEntity>

	@Query("SELECT * FROM produce_cache WHERE farmer_id = :farmerId")
	suspend fun getProduceByFarmer(farmerId: String): List<ProduceEntity>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun upsertProduce(items: List<ProduceEntity>)

	@Query("DELETE FROM produce_cache")
	suspend fun clearProduce()

	@Query("DELETE FROM produce_cache WHERE farmer_id = :farmerId")
	suspend fun clearProduceForFarmer(farmerId: String)

	@Transaction
	suspend fun replaceProduce(items: List<ProduceEntity>) {
		clearProduce()
		upsertProduce(items)
	}

	@Transaction
	suspend fun replaceProduceForFarmer(farmerId: String, items: List<ProduceEntity>) {
		clearProduceForFarmer(farmerId)
		upsertProduce(items)
	}

	// Order details
	@Query("SELECT * FROM order_details_cache WHERE buyer_id = :buyerId ORDER BY order_id, item_id")
	suspend fun getOrderDetailsForBuyer(buyerId: String): List<OrderDetailsEntity>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun upsertOrderDetails(items: List<OrderDetailsEntity>)

	@Query("DELETE FROM order_details_cache WHERE buyer_id = :buyerId")
	suspend fun clearOrderDetailsForBuyer(buyerId: String)

	@Transaction
	suspend fun replaceOrderDetailsForBuyer(buyerId: String, items: List<OrderDetailsEntity>) {
		clearOrderDetailsForBuyer(buyerId)
		upsertOrderDetails(items)
	}
}