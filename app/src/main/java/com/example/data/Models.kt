package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String,
    val name: String,
    val role: String, // "CUSTOMER", "RESTAURANT", "NGO"
    val phoneNumber: String = "",
    val sustainabilityScore: Int = 100, // Score based on rescue activity
    val rewardPoints: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "food_items")
data class FoodItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val restaurantId: Int,
    val restaurantName: String,
    val name: String,
    val description: String,
    val imageUrl: String = "", // Can store a locally selected image or base64
    val originalPrice: Double,
    val discountedPrice: Double,
    val isDonation: Boolean = false,
    val quantity: Int = 1,
    val status: String = "AVAILABLE", // "AVAILABLE", "RESERVED", "COLLECTED"
    val pickupLocation: String = "123 Green Avenue",
    val expiryTime: Long = System.currentTimeMillis() + 86400000, // default 24 hours
    
    // AI analysis cache variables
    val freshnessScore: Int = -1, // -1 means not analyzed
    val predictedSafeHours: Int = -1,
    val freshnessReport: String = "",
    val compostAdvice: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reservations")
data class Reservation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val foodItemId: Int,
    val userId: Int,
    val buyerName: String,
    val restaurantId: Int,
    val restaurantName: String,
    val itemName: String,
    val price: Double,
    val isDonation: Boolean = false,
    val qrCodeContent: String, // verification code for pickups
    val status: String = "RESERVED", // "RESERVED", "COLLECTED"
    val pickupLocation: String = "Phase 2, Gachibowli, Hyderabad, Telangana",
    val reservedAt: Long = System.currentTimeMillis(),
    val collectedAt: Long = 0
)

@Entity(tableName = "donation_alerts")
data class DonationAlert(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val foodItemId: Int,
    val restaurantId: Int,
    val restaurantName: String,
    val itemName: String,
    val quantity: Int,
    val pickupLocation: String,
    val status: String = "ALERTED", // "ALERTED", "CLAIMED", "COMPLETED"
    val ngoId: Int? = null,
    val ngoName: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
