package com.techproject.harvestlink.data.chats

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.techproject.harvestlink.model.Message

@Database(entities = [Message::class], version = 1, exportSchema = true)
abstract class ChatDatabase: RoomDatabase() {
    abstract fun getDao(): MessageDao

    companion object{
        @Volatile
        private var Instance: ChatDatabase? = null

        fun getDatabase(context: Context): ChatDatabase{
            return Instance ?: synchronized(this){
                Room.databaseBuilder(context, ChatDatabase::class.java, "chat_database")
                    .fallbackToDestructiveMigration(false)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}