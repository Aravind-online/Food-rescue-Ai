package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class FoodRescueRepository(private val dao: FoodRescueDao) {

    // --- Users ---
    suspend fun getUserByEmail(email: String): User? = dao.getUserByEmail(email)
    
    fun getUserById(id: Int): Flow<User?> = dao.getUserById(id)
    
    suspend fun getUserByIdSync(id: Int): User? = dao.getUserByIdSync(id)
    
    suspend fun registerUser(user: User): Int {
        val existing = dao.getUserByEmail(user.email)
        return if (existing != null) {
            existing.id
        } else {
            dao.insertUser(user).toInt()
        }
    }

    suspend fun updateUser(user: User) = dao.updateUser(user)

    // --- Food Items ---
    fun getAvailableFoodItems(): Flow<List<FoodItem>> = dao.getAvailableFoodItems()
    
    fun getFoodItemsByRestaurant(restaurantId: Int): Flow<List<FoodItem>> = 
        dao.getFoodItemsByRestaurant(restaurantId)

    fun getFoodItemById(id: Int): Flow<FoodItem?> = dao.getFoodItemById(id)
    
    suspend fun getFoodItemByIdSync(id: Int): FoodItem? = dao.getFoodItemByIdSync(id)

    suspend fun addFoodItem(item: FoodItem): Int {
        val itemId = dao.insertFoodItem(item).toInt()
        
        // If it's a free donation or marked as donation priority, also post an NGO/Donation alert automatically!
        if (item.isDonation) {
            dao.insertDonationAlert(
                DonationAlert(
                    foodItemId = itemId,
                    restaurantId = item.restaurantId,
                    restaurantName = item.restaurantName,
                    itemName = item.name,
                    quantity = item.quantity,
                    pickupLocation = item.pickupLocation,
                    status = "ALERTED"
                )
            )
        }
        return itemId
    }

    suspend fun updateFoodItem(item: FoodItem) = dao.updateFoodItem(item)
    
    suspend fun deleteFoodItem(item: FoodItem) = dao.deleteFoodItem(item)

    // --- Reservations ---
    fun getReservationsForUser(userId: Int): Flow<List<Reservation>> = 
        dao.getReservationsForUser(userId)
        
    fun getReservationsForRestaurant(restaurantId: Int): Flow<List<Reservation>> = 
        dao.getReservationsForRestaurant(restaurantId)

    suspend fun reserveFoodItem(itemId: Int, userId: Int, userName: String): Boolean {
        val item = dao.getFoodItemByIdSync(itemId) ?: return false
        if (item.status != "AVAILABLE" || item.quantity <= 0) return false
        
        // Decrement quantity or mark reserved
        val newQuantity = item.quantity - 1
        val newStatus = if (newQuantity == 0) "RESERVED" else "AVAILABLE"
        dao.updateFoodItem(item.copy(quantity = newQuantity, status = newStatus))

        // Create reservation
        dao.insertReservation(
            Reservation(
                foodItemId = item.id,
                userId = userId,
                buyerName = userName,
                restaurantId = item.restaurantId,
                restaurantName = item.restaurantName,
                itemName = item.name,
                price = item.discountedPrice,
                isDonation = item.isDonation,
                qrCodeContent = "RESCU_QR_${item.id}_${System.currentTimeMillis() % 100000}",
                status = "RESERVED",
                pickupLocation = item.pickupLocation
            )
        )
        
        // Reward points (e.g. 50 points per rescue item!)
        val user = dao.getUserByIdSync(userId)
        if (user != null) {
            val updatedPoints = user.rewardPoints + 50
            val updatedSustScore = user.sustainabilityScore + 10
            dao.updateUser(user.copy(rewardPoints = updatedPoints, sustainabilityScore = updatedSustScore))
        }

        return true
    }

    suspend fun completePickup(reservationId: Int, buyerUserId: Int, restaurantUserId: Int): Boolean {
        // Collect food and mark complete
        // In physical app, restaurant scans buyer's QR.
        // We'll simulate this inside the app flow.
        return true
    }

    // --- Donation Alerts (NGO) ---
    fun getActiveDonationAlerts(): Flow<List<DonationAlert>> = dao.getActiveDonationAlerts()
    
    fun getDonationAlertsForNgo(ngoId: Int): Flow<List<DonationAlert>> = 
        dao.getDonationAlertsForNgo(ngoId)

    suspend fun claimDonation(alertId: Int, ngoUserId: Int, ngoName: String): Boolean {
        // Find is there any DonationAlert matching
        // In dynamic app, we look up list of alerts
        return true // handled dynamically by viewmodel updating Room
    }

    // --- Prepopulate Mock Data ---
    suspend fun prepopulateIfNeeded() {
        // Let's see if a default admin/restaurant exists
        val existingItems = dao.getAvailableFoodItems().first()
        if (existingItems.isEmpty()) {
            // Seed a default restaurant user and NGO user to allow logging in instantly:
            val restaurantUser = User(
                id = 11,
                email = "resto@foodrescue.com",
                name = "Royal Biryani House",
                role = "RESTAURANT",
                phoneNumber = "+91 98765 43210",
                sustainabilityScore = 140
            )
            val ngoUser = User(
                id = 22,
                email = "hope@ngo.org",
                name = "Asha Food Relief NGO",
                role = "NGO",
                phoneNumber = "+91 99887 76655",
                sustainabilityScore = 180
            )
            val customerUser = User(
                id = 33,
                email = "demo@rescue.com",
                name = "Aravind Kumar",
                role = "CUSTOMER",
                phoneNumber = "+91 91234 56789",
                sustainabilityScore = 110,
                rewardPoints = 120
            )
            dao.insertUser(restaurantUser)
            dao.insertUser(ngoUser)
            dao.insertUser(customerUser)

            // Seed default food items
            val items = listOf(
                FoodItem(
                    id = 1,
                    restaurantId = 11,
                    restaurantName = "Royal Biryani House",
                    name = "Hyderabadi Veg Biryani (Large Handi)",
                    description = "Aromatic basmati rice layered with fresh vegetables, saffron, and traditional Indian spices. Fresh surplus cooked at lunch.",
                    originalPrice = 350.00,
                    discountedPrice = 120.00,
                    isDonation = false,
                    quantity = 3,
                    status = "AVAILABLE",
                    pickupLocation = "Phase 2, Gachibowli, Hyderabad, Telangana",
                    expiryTime = System.currentTimeMillis() + 18000000, // 5 hours
                    freshnessScore = 95,
                    predictedSafeHours = 10,
                    freshnessReport = "Cooked with filtered water and premium basmati. Steam sealed under pressure. Clean, spicy fragrance.",
                    compostAdvice = "If leftover, store in refrigerator immediately. Spiced rice can be recycled as natural garden nitrogen-builder after rinsing out excessive masala."
                ),
                FoodItem(
                    id = 2,
                    restaurantId = 11,
                    restaurantName = "Royal Biryani House",
                    name = "Crispy Punjabi Samosa Plate (Pack of 4)",
                    description = "Hand-folded flaky pastry shells stuffed with spiced mashed potatoes and green peas. Includes green mint chutney.",
                    originalPrice = 120.00,
                    discountedPrice = 40.00,
                    isDonation = false,
                    quantity = 4,
                    status = "AVAILABLE",
                    pickupLocation = "Phase 2, Gachibowli, Hyderabad, Telangana",
                    expiryTime = System.currentTimeMillis() + 28000000, // ~8 hours
                    freshnessScore = 88,
                    predictedSafeHours = 12,
                    freshnessReport = "Fried this noon. Retained crisp golden shell with zero sogginess. Protected from humidity.",
                    compostAdvice = "Excellent dry organic matter. Crush shell crusts to enrich compost pile carbon matrix."
                ),
                FoodItem(
                    id = 3,
                    restaurantId = 11,
                    restaurantName = "Royal Biryani House",
                    name = "Paneer Butter Masala & Roti (Family Pack)",
                    description = "Soft paneer cubes simmered in butter gravy cooked with fresh tomatoes and cashew nuts, with 5 freshly baked butter rotis.",
                    originalPrice = 450.00,
                    discountedPrice = 0.0,
                    isDonation = true,
                    quantity = 5,
                    status = "AVAILABLE",
                    pickupLocation = "Phase 2, Gachibowli, Hyderabad, Telangana",
                    expiryTime = System.currentTimeMillis() + 48000000, // ~13 hours
                    freshnessScore = 96,
                    predictedSafeHours = 24,
                    freshnessReport = "Freshly prepared, packed in hygienic microwaveable containers. Retains perfect taste integrity.",
                    compostAdvice = "Highly nutritious gravy base. If unconsumed, dilute with soil and use to enhance soil microbiome activity."
                )
            )

            for (it in items) {
                dao.insertFoodItem(it)
                if (it.isDonation) {
                    dao.insertDonationAlert(
                        DonationAlert(
                            foodItemId = it.id,
                            restaurantId = it.restaurantId,
                            restaurantName = it.restaurantName,
                            itemName = it.name,
                            quantity = it.quantity,
                            pickupLocation = it.pickupLocation,
                            status = "ALERTED"
                        )
                    )
                }
            }
        }
    }
}
