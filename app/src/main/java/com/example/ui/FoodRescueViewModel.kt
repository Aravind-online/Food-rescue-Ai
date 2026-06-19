package com.example.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.api.GeminiService
import com.example.api.FreshnessAnalysis
import com.example.api.WastePrediction
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FoodRescueViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "FoodRescueViewModel"
    private val database = FoodRescueDatabase.getDatabase(application)
    private val repository = FoodRescueRepository(database.dao())

    // --- State Holders ---
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isAuthenticating = MutableStateFlow(false)
    val isAuthenticating: StateFlow<Boolean> = _isAuthenticating.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _authSuccessEvent = MutableSharedFlow<Boolean>()
    val authSuccessEvent = _authSuccessEvent.asSharedFlow()

    // --- AI Feature State ---
    private val _isAnalyzingFreshness = MutableStateFlow(false)
    val isAnalyzingFreshness: StateFlow<Boolean> = _isAnalyzingFreshness.asStateFlow()

    private val _freshnessAnalysisResult = MutableStateFlow<FreshnessAnalysis?>(null)
    val freshnessAnalysisResult: StateFlow<FreshnessAnalysis?> = _freshnessAnalysisResult.asStateFlow()

    private val _isPredictingWaste = MutableStateFlow(false)
    val isPredictingWaste: StateFlow<Boolean> = _isPredictingWaste.asStateFlow()

    private val _wastePredictionResult = MutableStateFlow<WastePrediction?>(null)
    val wastePredictionResult: StateFlow<WastePrediction?> = _wastePredictionResult.asStateFlow()

    // --- Local simulated active app notifications and alerts ---
    private val _notifications = MutableStateFlow<List<String>>(emptyList())
    val notifications: StateFlow<List<String>> = _notifications.asStateFlow()

    // --- Reactive DB Queries mapped dynamically by role ---
    val availableFoods: StateFlow<List<FoodItem>> = repository.getAvailableFoodItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myFoodItems: StateFlow<List<FoodItem>> = _currentUser
        .filterNotNull()
        .flatMapLatest { user -> repository.getFoodItemsByRestaurant(user.id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myReservations: StateFlow<List<Reservation>> = _currentUser
        .filterNotNull()
        .flatMapLatest { user -> repository.getReservationsForUser(user.id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeDonationAlerts: StateFlow<List<DonationAlert>> = repository.getActiveDonationAlerts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myClaimedDonations: StateFlow<List<DonationAlert>> = _currentUser
        .filterNotNull()
        .flatMapLatest { user -> repository.getDonationAlertsForNgo(user.id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val restaurantReservations: StateFlow<List<Reservation>> = _currentUser
        .filterNotNull()
        .flatMapLatest { user -> repository.getReservationsForRestaurant(user.id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            try {
                // Ensure initial seed data is loaded
                repository.prepopulateIfNeeded()
                
                // Automatically log in a default customer for demonstration convenience
                val demoUser = repository.getUserByEmail("demo@rescue.com")
                _currentUser.value = demoUser
            } catch (e: Exception) {
                Log.e(TAG, "Failed seeding database: ${e.localizedMessage}")
            }
        }
    }

    // --- Authentication ---
    fun login(email: String, role: String) {
        viewModelScope.launch {
            _isAuthenticating.value = true
            _authError.value = null
            try {
                if (email.isBlank()) {
                    _authError.value = "Email cannot be empty"
                    _isAuthenticating.value = false
                    return@launch
                }
                
                // Get or create dynamic user
                val existing = repository.getUserByEmail(email)
                if (existing != null) {
                    if (existing.role != role) {
                        _authError.value = "Role mismatch! User registered as ${existing.role}."
                    } else {
                        _currentUser.value = existing
                        _authSuccessEvent.emit(true)
                    }
                } else {
                    // Automatically register them to make prototoyping extremely seamless
                    val cleanName = email.substringBefore("@").replaceFirstChar { it.uppercase() }
                    val newUser = User(
                        email = email,
                        name = "$cleanName (${role.lowercase().replaceFirstChar { it.uppercase() }})",
                        role = role
                    )
                    val id = repository.registerUser(newUser)
                    val created = repository.getUserByIdSync(id)
                    _currentUser.value = created
                    _authSuccessEvent.emit(true)
                }
            } catch (e: Exception) {
                _authError.value = "Auth failed: ${e.localizedMessage}"
            } finally {
                _isAuthenticating.value = false
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _freshnessAnalysisResult.value = null
        _wastePredictionResult.value = null
    }

    // --- Customer Action: Reserve Food Item ---
    fun reserveFood(itemId: Int, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = _currentUser.value
            if (user == null) {
                onComplete(false)
                return@launch
            }
            try {
                val success = repository.reserveFoodItem(itemId, user.id, user.name)
                if (success) {
                    postNotification("New Reservation Confirmed! You saved high-quality food from landfill. 50 XP & 50 points added!")
                    // Request user update to refresh scores
                    val updatedUser = repository.getUserByIdSync(user.id)
                    _currentUser.value = updatedUser
                }
                onComplete(success)
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }

    // --- Restaurant Actions: Upload listings ---
    fun addFoodItem(
        name: String,
        description: String,
        originalPrice: Double,
        discountedPrice: Double,
        quantity: Int,
        isDonation: Boolean,
        pickupLocation: String,
        expiryHours: Int,
        freshnessScore: Int = -1,
        predictedHours: Int = -1,
        reportText: String = "",
        compostText: String = "",
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val rest = _currentUser.value
            if (rest == null || rest.role != "RESTAURANT") {
                onComplete(false)
                return@launch
            }
            try {
                val item = FoodItem(
                    restaurantId = rest.id,
                    restaurantName = rest.name,
                    name = name,
                    description = description,
                    originalPrice = originalPrice,
                    discountedPrice = if (isDonation) 0.0 else discountedPrice,
                    quantity = quantity,
                    isDonation = isDonation,
                    pickupLocation = pickupLocation.ifBlank { "Suite 100, 452 Downtown Blvd" },
                    expiryTime = System.currentTimeMillis() + (expiryHours * 3600000),
                    status = "AVAILABLE",
                    freshnessScore = freshnessScore,
                    predictedSafeHours = predictedHours,
                    freshnessReport = reportText,
                    compostAdvice = compostText
                )
                repository.addFoodItem(item)
                
                if (isDonation) {
                    postNotification("Donation Alert dispatched to nearby NGOs: Hope Shelter notified!")
                } else {
                    postNotification("Fresh surplus markdown uploaded! Discount notifications broadcasted to nearby students.")
                }
                onComplete(true)
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }

    // --- NGO Actions: Claim Donations ---
    fun claimDonation(alertId: Int, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = _currentUser.value
            if (user == null || user.role != "NGO") {
                onComplete(false)
                return@launch
            }
            try {
                // Find and claim donation alert
                val dbAlert = database.dao().getActiveDonationAlerts().first().find { it.id == alertId }
                if (dbAlert != null) {
                    val updatedAlert = dbAlert.copy(
                        status = "CLAIMED",
                        ngoId = user.id,
                        ngoName = user.name
                    )
                    database.dao().updateDonationAlert(updatedAlert)

                    // Mark food item as reserved/claimed as well
                    val foodItem = database.dao().getFoodItemByIdSync(dbAlert.foodItemId)
                    if (foodItem != null) {
                        database.dao().updateFoodItem(foodItem.copy(status = "RESERVED", quantity = 0))
                    }

                    // Increment NGO sustainability statistics
                    val updatedUser = user.copy(sustainabilityScore = user.sustainabilityScore + 15)
                    repository.updateUser(updatedUser)
                    _currentUser.value = updatedUser

                    postNotification("Rescue Claim Confirmed! Collection team dispatched to ${dbAlert.restaurantName}.")
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }

    fun completeNgoCollection(alertId: Int) {
        viewModelScope.launch {
            try {
                // Find alert in DB
                val alerts = database.dao().getDonationAlertsForNgo(_currentUser.value?.id ?: 0).first()
                val targetAlert = alerts.find { it.id == alertId }
                if (targetAlert != null) {
                    database.dao().updateDonationAlert(targetAlert.copy(status = "COMPLETED"))
                    postNotification("Food collected safely. High-quality redistribution completed!")
                }
            } catch (e: Exception) {
                Log.e(TAG, "NGO collection completion failed: ${e.localizedMessage}")
            }
        }
    }

    fun completeRestaurantPickup(resId: Int) {
        viewModelScope.launch {
            try {
                val list = repository.getReservationsForRestaurant(_currentUser.value?.id ?: 0).first()
                val target = list.find { it.id == resId }
                if (target != null) {
                    database.dao().updateReservation(target.copy(status = "COLLECTED", collectedAt = System.currentTimeMillis()))
                    // Mark food item collected (already set status reserved, let's keep it complete)
                    val food = database.dao().getFoodItemByIdSync(target.foodItemId)
                    if (food != null) {
                        database.dao().updateFoodItem(food.copy(status = "COLLECTED"))
                    }
                    postNotification("QR verification successful! Rescue complete.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Completed pickup update failed")
            }
        }
    }

    // --- AI Trigger: Image Freshness Analysis ---
    fun runAiFreshnessDetection(foodName: String, description: String, base64Image: String?) {
        viewModelScope.launch {
            _isAnalyzingFreshness.value = true
            _freshnessAnalysisResult.value = null
            try {
                val result = GeminiService.analyzeFreshness(foodName, description, base64Image)
                _freshnessAnalysisResult.value = result
            } catch (e: Exception) {
                Log.e(TAG, "Freshness analysis crashed: ${e.localizedMessage}")
            } finally {
                _isAnalyzingFreshness.value = false
            }
        }
    }

    // --- AI Trigger: Food Waste Quantity Predictive Tool ---
    fun runFoodWastePrediction(foodName: String, weekdaySales: Int, weekendSales: Int, activeStock: Int) {
        viewModelScope.launch {
            _isPredictingWaste.value = true
            _wastePredictionResult.value = null
            try {
                val result = GeminiService.predictPreparationWaste(foodName, weekdaySales, weekendSales, activeStock)
                _wastePredictionResult.value = result
            } catch (e: Exception) {
                Log.e(TAG, "Waste prediction crashed: ${e.localizedMessage}")
            } finally {
                _isPredictingWaste.value = false
            }
        }
    }

    fun clearFreshnessAnalysisResult() {
        _freshnessAnalysisResult.value = null
    }

    // --- Helpers / Notifications ---
    fun postNotification(text: String) {
        val current = _notifications.value.toMutableList()
        current.add(0, text)
        _notifications.value = current
    }

    fun dismissNotification(index: Int) {
        val current = _notifications.value.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _notifications.value = current
        }
    }
}
