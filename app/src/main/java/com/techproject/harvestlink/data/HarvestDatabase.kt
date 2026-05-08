//package com.techproject.harvestlink.data
//
//import android.content.Context
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//import com.techproject.harvestlink.model.Message
//
//@Database(entities = [Message::class], version = 1, exportSchema = true)
//abstract class HarvestDatabase: RoomDatabase() {
//    abstract fun getDao(): HarvestDao
//    companion object{
//        @Volatile
//        private var Instance: HarvestDatabase? = null
//
//        fun getDatabase(context: Context): HarvestDatabase {
//            return Instance ?: synchronized(this){
//                Room.databaseBuilder(context, HarvestDatabase::class.java, "chat_database")
//                    .fallbackToDestructiveMigration(false)
//                    .build()
//                    .also { Instance = it }
//            }
//        }
//    }
//}
//
////DatabaseModels