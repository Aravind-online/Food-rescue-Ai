package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodRescueDao {

    // --- Users ---
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserById(id: Int): Flow<User?>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserByIdSync(id: Int): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    // --- Food Items ---
    @Query("SELECT * FROM food_items WHERE status = 'AVAILABLE' ORDER BY createdAt DESC")
    fun getAvailableFoodItems(): Flow<List<FoodItem>>

    @Query("SELECT * FROM food_items WHERE restaurantId = :restaurantId ORDER BY createdAt DESC")
    fun getFoodItemsByRestaurant(restaurantId: Int): Flow<List<FoodItem>>

    @Query("SELECT * FROM food_items WHERE id = :id")
    fun getFoodItemById(id: Int): Flow<FoodItem?>

    @Query("SELECT * FROM food_items WHERE id = :id")
    suspend fun getFoodItemByIdSync(id: Int): FoodItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodItem(item: FoodItem): Long

    @Update
    suspend fun updateFoodItem(item: FoodItem)

    @Delete
    suspend fun deleteFoodItem(item: FoodItem)

    // --- Reservations ---
    @Query("SELECT * FROM reservations WHERE userId = :userId ORDER BY reservedAt DESC")
    fun getReservationsForUser(userId: Int): Flow<List<Reservation>>

    @Query("SELECT * FROM reservations WHERE restaurantId = :restaurantId ORDER BY reservedAt DESC")
    fun getReservationsForRestaurant(restaurantId: Int): Flow<List<Reservation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReservation(reservation: Reservation): Long

    @Update
    suspend fun updateReservation(reservation: Reservation)

    // --- Donation Alerts (NGO Flow) ---
    @Query("SELECT * FROM donation_alerts WHERE status = 'ALERTED' ORDER BY timestamp DESC")
    fun getActiveDonationAlerts(): Flow<List<DonationAlert>>

    @Query("SELECT * FROM donation_alerts WHERE ngoId = :ngoId ORDER BY timestamp DESC")
    fun getDonationAlertsForNgo(ngoId: Int): Flow<List<DonationAlert>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonationAlert(alert: DonationAlert): Long

    @Update
    suspend fun updateDonationAlert(alert: DonationAlert)
}
