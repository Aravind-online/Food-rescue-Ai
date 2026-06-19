package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        User::class,
        FoodItem::class,
        Reservation::class,
        DonationAlert::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FoodRescueDatabase : RoomDatabase() {
    
    abstract fun dao(): FoodRescueDao

    companion object {
        @Volatile
        private var INSTANCE: FoodRescueDatabase? = null

        fun getDatabase(context: Context): FoodRescueDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FoodRescueDatabase::class.java,
                    "food_rescue_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
