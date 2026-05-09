package com.techproject.harvestlink.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.techproject.harvestlink.data.chats.ChatDao

@Database(
	entities = [
		BuyerEntity::class,
		FarmerEntity::class,
		ProduceEntity::class,
		OrderDetailsEntity::class,
		UserEntity::class,
		ConversationCacheEntity::class,
		MessageEntity::class
	],
	version = 2,
	exportSchema = true
)
abstract class HarvestDatabase : RoomDatabase() {
	abstract fun harvestDao(): HarvestDao
	abstract fun chatDao(): ChatDao

	companion object {
		@Volatile
		private var Instance: HarvestDatabase? = null

		fun getDatabase(context: Context): HarvestDatabase {
			return Instance ?: synchronized(this) {
				Room.databaseBuilder(
					context.applicationContext,
					HarvestDatabase::class.java,
					"harvestlink_database"
				)
					.fallbackToDestructiveMigration(false)
					.build()
					.also { Instance = it }
			}
		}
	}
}