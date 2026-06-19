package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FoodRescueAppContent(
    viewModel: FoodRescueViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    var isSplashActive by remember { mutableStateOf(true) }

    if (isSplashActive) {
        SplashScreen(
            onDismissSplash = {
                isSplashActive = false
            }
        )
    } else {
        if (currentUser == null) {
            AuthScreen(
                viewModel = viewModel,
                modifier = modifier
            )
        } else {
            MainDashboardScreen(
                viewModel = viewModel,
                modifier = modifier
            )
        }
    }
}

// ==========================================
// 1. SPLASH SCREEN
// ==========================================
@Composable
fun SplashScreen(
    onDismissSplash: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2E7D32), // Forest Green
                        Color(0xFF1B5E20)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Decorative background patterns
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = 350.dp.toPx(),
                center = Offset(size.width, 0f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.02f),
                radius = 200.dp.toPx(),
                center = Offset(0f, size.height * 0.8f)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Drawn Organic App Logo (Apple / Leaf with Bowl shape)
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .background(Color.White.copy(alpha = 0.9f), CircleShape)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Draw a cute organic tomato-green bowl & fork/leaf
                    drawArc(
                        color = Color(0xFFF57C00), // Vibrant Carrot Orange
                        startAngle = 0f,
                        sweepAngle = 180f,
                        useCenter = true,
                        size = Size(size.width, size.height * 0.8f),
                        topLeft = Offset(0f, size.height * 0.15f)
                    )
                    // Draw lush green leaf at top
                    drawOval(
                        color = Color(0xFF2E7D32),
                        size = Size(size.width * 0.35f, size.height * 0.35f),
                        topLeft = Offset(size.width * 0.3f, 0f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Food Rescue AI",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Save Food. Feed People. Reduce Waste.",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onDismissSplash,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF57C00), // Carotene Orange
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 14.dp),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .testTag("splash_cta_button")
            ) {
                Text(
                    text = "Get Started",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ==========================================
// 2. AUTHENTICATION & REGISTRATION SCREEN
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: FoodRescueViewModel,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("demo@rescue.com") }
    var selectedRole by remember { mutableStateOf("CUSTOMER") } // "CUSTOMER", "RESTAURANT", "NGO"
    val isAuthenticating by viewModel.isAuthenticating.collectAsState()
    val authError by viewModel.authError.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Aesthetic Top Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                        )
                    ),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Zero Waste. Full Hearts.",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Connect with surplus meals or declare food relief",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Authentication Card
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.9f)
                .padding(top = 100.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Sign In / Sign Up",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Role selection tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val roles = listOf("CUSTOMER" to "Consumer", "RESTAURANT" to "Kitchen", "NGO" to "Charity")
                    roles.forEach { (roleKey, display) ->
                        val isSelected = selectedRole == roleKey
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                .clickable { selectedRole = roleKey }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = display,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_email_field"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tip: Enter 'demo@rescue.com' to login instantly as a normal citizen, 'resto@foodrescue.com' as Golden Harvest Bistro, or 'hope@ngo.org' for the NGO hub.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )

                if (authError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = authError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.login(email.trim(), selectedRole) },
                    enabled = !isAuthenticating,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("auth_login_button")
                ) {
                    if (isAuthenticating) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = "Authenticate securely",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. MAIN DASHBOARD FRAME
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboardScreen(
    viewModel: FoodRescueViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    var selectedBottomTab by remember { mutableIntStateOf(0) }

    val userRole = currentUser?.role ?: "CUSTOMER"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "Food Rescue AI",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                },
                actions = {
                    Box(modifier = Modifier.padding(end = 12.dp)) {
                        IconButton(onClick = { viewModel.postNotification("Sustainability tip: Sharing leftovers immediately preserves micro-integrity and halves composting times!") }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Alerts")
                        }
                        if (notifications.isNotEmpty()) {
                            Badge(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-2).dp, y = (2).dp)
                            ) {
                                Text(notifications.size.toString())
                            }
                        }
                    }

                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Log Out")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                // Tab 0: Main Screen, Tab 1: Action (Orders/Postings/NGO requests), Tab 2: AI Center, Tab 3: Profile
                NavigationBarItem(
                    selected = selectedBottomTab == 0,
                    onClick = { selectedBottomTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Feed") }
                )
                NavigationBarItem(
                    selected = selectedBottomTab == 1,
                    onClick = { selectedBottomTab = 1 },
                    icon = { 
                        Icon(
                            if (userRole == "RESTAURANT") Icons.Default.Analytics else Icons.Default.ShoppingCart, 
                            contentDescription = null
                        ) 
                    },
                    label = { Text(if (userRole == "RESTAURANT") "Impact" else "Rescues") }
                )
                NavigationBarItem(
                    selected = selectedBottomTab == 2,
                    onClick = { selectedBottomTab = 2 },
                    icon = { Icon(Icons.Default.CameraAlt, contentDescription = null) },
                    label = { Text("AI Studio") }
                )
                NavigationBarItem(
                    selected = selectedBottomTab == 3,
                    onClick = { selectedBottomTab = 3 },
                    icon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                    label = { Text("Profile") }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Notification toast banner inside the app for seamless interactions
            AnimatedVisibility(
                visible = notifications.isNotEmpty(),
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                if (notifications.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.tertiary)
                            .clickable { viewModel.dismissNotification(0) }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Bolt,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = notifications.first(),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Dynamic Tab Selector Based on active Bottom Tab
            Box(modifier = Modifier.weight(1f)) {
                when (selectedBottomTab) {
                    0 -> FeedTab(viewModel = viewModel)
                    1 -> ActionTab(viewModel = viewModel)
                    2 -> AiStudioTab(viewModel = viewModel)
                    3 -> ProfileTab(viewModel = viewModel)
                }
            }
        }
    }
}

// ==========================================
// 4. TAB 0: FEED TAB (HOMEPAGE)
// ==========================================
@Composable
fun FeedTab(viewModel: FoodRescueViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val availableFoods by viewModel.availableFoods.collectAsState()
    val activeDonationAlerts by viewModel.activeDonationAlerts.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var emergencyFilter by remember { mutableStateOf(false) }

    val role = currentUser?.role ?: "CUSTOMER"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header Banner
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hello, ${currentUser?.name ?: "Rescue Companion"}!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = when (role) {
                                "RESTAURANT" -> "Manage your surplus uploads or run preparation predictive metrics."
                                "NGO" -> "Claim surplus donation alerts quickly for emergency shelter food drops."
                                else -> "Pick up local discount boxes or support free charity donations!"
                            },
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (currentUser?.sustainabilityScore ?: 100).toString(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // --- ROLE SPECIFIC PANELS FOR TABS ---
        if (role == "RESTAURANT") {
            item {
                Text(
                    text = "Manage Active Surplus listings",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            val restaurantItems = availableFoods.filter { it.restaurantId == (currentUser?.id ?: 0) }
            if (restaurantItems.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Zero active items uploaded.",
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                "Navigate to 'AI Studio' or the '+' below to upload details.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
            } else {
                items(restaurantItems) { food ->
                    RestaurantFoodItemCard(food = food, viewModel = viewModel)
                }
            }

        } else if (role == "NGO") {
            // NGO CLAIM FEED
            item {
                Text(
                    text = "Available Donation Alerts (NGO Hub)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            val unclaimedAlerts = activeDonationAlerts.filter { it.status == "ALERTED" }
            if (unclaimedAlerts.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Eco, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No new donation alerts active.",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Local kitchens are fully balanced today! Check back soon for surplus notifications.",
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(unclaimedAlerts) { alert ->
                    NgoDonationAlertCard(alert = alert, viewModel = viewModel)
                }
            }

        } else {
            // CUSTOMER DISCOVERY FEED
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Search bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search nearby food...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Categories Horizontal
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val categories = listOf("All", "Pastries", "Stew & Grains", "Veg Boxes", "Free Donations")
                        categories.forEach { cat ->
                            val isSelected = selectedCategory == cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable { selectedCategory = cat }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = cat,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Nearby Rescue Deals",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Within 2 km",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Filter items based on criteria
            val filteredFoods = availableFoods.filter { food ->
                val matchesSearch = food.name.lowercase().contains(searchQuery.lowercase()) ||
                        food.restaurantName.lowercase().contains(searchQuery.lowercase())
                val matchesCategory = when (selectedCategory) {
                    "All" -> true
                    "Pastries" -> food.name.lowercase().contains("pastry") || food.name.lowercase().contains("croissant")
                    "Stew & Grains" -> food.name.lowercase().contains("stew") || food.name.lowercase().contains("soup") || food.name.lowercase().contains("rice")
                    "Veg Boxes" -> food.name.lowercase().contains("veg") || food.name.lowercase().contains("salad")
                    "Free Donations" -> food.isDonation
                    else -> true
                }
                matchesSearch && matchesCategory
            }

            if (filteredFoods.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No nearby surplus meals found matching criteria.",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            } else {
                items(filteredFoods) { food ->
                    CustomerFoodItemCard(food = food, viewModel = viewModel)
                }
            }
        }
    }
}

// ==========================================
// 5. CUSTOMER & RESTAURANT CARDS
// ==========================================
@Composable
fun CustomerFoodItemCard(food: FoodItem, viewModel: FoodRescueViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var isReserving by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Top Section custom organic visual illustration
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                if (food.isDonation) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                // Background leaf illustrations drawn elegantly
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawOval(
                        color = Color.White.copy(alpha = 0.1f),
                        size = Size(140.dp.toPx(), 40.dp.toPx()),
                        topLeft = Offset(size.width * 0.5f, size.height * 0.2f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (food.isDonation) "FREE DONATION" else "SURPLUS DEAL",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = food.name,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Freshness circular indicator
                    if (food.freshnessScore > 0) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${food.freshnessScore}%",
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                            Text(
                                text = "Fresh AI",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Info details Section
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = food.restaurantName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = food.description,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Location inline
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = food.pickupLocation,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Direct Google Map Navigation button to simplify finding food
                val context = LocalContext.current
                OutlinedButton(
                    onClick = {
                        try {
                            val intent = android.content.Intent(
                                android.content.Intent.ACTION_VIEW,
                                android.net.Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + android.net.Uri.encode(food.pickupLocation))
                            )
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // fallback web search
                            try {
                                val webIntent = android.content.Intent(
                                    android.content.Intent.ACTION_VIEW,
                                    android.net.Uri.parse("https://www.google.com/maps/search/?api=1&query=" + android.net.Uri.encode(food.pickupLocation))
                                )
                                context.startActivity(webIntent)
                            } catch (err: Exception) {}
                        }
                    },
                    modifier = Modifier.height(32.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = "Map Directions", modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Google Map Directions", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Prices
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (food.isDonation) {
                            Text(
                                text = "Sponsored Free Rescue",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        } else {
                            Text(
                                text = "₹${String.format(Locale.US, "%.0f", food.discountedPrice)}",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Was ₹${String.format(Locale.US, "%.0f", food.originalPrice)}",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                fontSize = 12.sp,
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                            )
                        }
                    }

                    // Remaining Quantity
                    Box(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${food.quantity} servings left",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }

    // Interactive details sheet dialog
    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = food.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "Prepared and listed by ${food.restaurantName}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = food.description,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // AI Freshness diagnosis report details if available!
                    if (food.freshnessScore > 0) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Eco,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "AI Freshness Diagnostic Score: ${food.freshnessScore}%",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Estimated Safe Consumption window: ${food.predictedSafeHours} hours under climate control.",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = food.freshnessReport,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                                if (food.compostAdvice.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Divider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Soil/Compost Strategy: ${food.compostAdvice}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Location detail with maps navigation action button
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Pickup: ${food.pickupLocation}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        val context = LocalContext.current
                        Button(
                            onClick = {
                                try {
                                    val intent = android.content.Intent(
                                        android.content.Intent.ACTION_VIEW,
                                        android.net.Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + android.net.Uri.encode(food.pickupLocation))
                                    )
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    try {
                                        val webIntent = android.content.Intent(
                                            android.content.Intent.ACTION_VIEW,
                                            android.net.Uri.parse("https://www.google.com/maps/search/?api=1&query=" + android.net.Uri.encode(food.pickupLocation))
                                        )
                                        context.startActivity(webIntent)
                                    } catch (err: Exception) {}
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(36.dp)
                        ) {
                            Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open Map Directions", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showDialog = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Back")
                        }

                        Button(
                            onClick = {
                                isReserving = true
                                viewModel.reserveFood(food.id) { success ->
                                    isReserving = false
                                    showDialog = false
                                }
                            },
                            enabled = !isReserving && food.quantity > 0,
                            modifier = Modifier
                                .weight(1.5f)
                                .testTag("reserve_food_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isReserving) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                            } else {
                                Text(if (food.isDonation) "Claim Free Rescue" else "Reserve Meal Box")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RestaurantFoodItemCard(food: FoodItem, viewModel: FoodRescueViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = food.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Box(
                    modifier = Modifier
                        .background(
                            if (food.quantity > 0) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            else MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (food.quantity > 0) "Active: ${food.quantity} Left" else "Rescued / Out",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (food.quantity > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = food.description,
                fontSize = 12.sp,
                maxLines = 2,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            if (food.freshnessScore > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Eco, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "AI Freshness Index: ${food.freshnessScore}% (${food.predictedSafeHours}h safe timer)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun NgoDonationAlertCard(alert: DonationAlert, viewModel: FoodRescueViewModel) {
    var isClaiming by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "ALERTA REDISTRIBUTION",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = alert.itemName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "Offered by ${alert.restaurantName}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val context = LocalContext.current
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            try {
                                val intent = android.content.Intent(
                                    android.content.Intent.ACTION_VIEW,
                                    android.net.Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + android.net.Uri.encode(alert.pickupLocation))
                                )
                                context.startActivity(intent)
                            } catch (e: Exception) {}
                        }
                ) {
                    Text(
                        text = "Quantity: ${alert.quantity} servings",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Navigation,
                            contentDescription = "Navigate",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Pickup: ${alert.pickupLocation}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Button(
                    onClick = {
                        isClaiming = true
                        viewModel.claimDonation(alert.id) {
                            isClaiming = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("claim_donation_button_${alert.id}")
                ) {
                    if (isClaiming) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                    } else {
                        Text("Claim Rescue")
                    }
                }
            }
        }
    }
}

// ==========================================
// 6. TAB 1: ACTION TAB (DASHBOARDS & RESERVATIONS)
// ==========================================
@Composable
fun ActionTab(viewModel: FoodRescueViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val myReservations by viewModel.myReservations.collectAsState()
    val myClaimedDonations by viewModel.myClaimedDonations.collectAsState()
    val restaurantReservations by viewModel.restaurantReservations.collectAsState()

    val role = currentUser?.role ?: "CUSTOMER"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = when (role) {
                "RESTAURANT" -> "Restaurant Impact and Pickups"
                "NGO" -> "Redistribution Tracking Hub"
                else -> "My Saved & Rescued Food"
            },
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (role == "RESTAURANT") {
                // RESTAURANT DISPLAY GRAPH & ARRIVING CUSTOMERS
                item {
                    Text(
                        text = "Food Waste Analytics (Rescued lbs / Week)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                // Custom Analytical Graph drawn gracefully on Canvas
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                // Draw Graph Grid Lines
                                val gridColor = Color.Black.copy(alpha = 0.05f)
                                drawLine(Color.Black, Offset(0f, size.height), Offset(size.width, size.height), strokeWidth = 2f)
                                drawLine(Color.Black, Offset(0f, 0f), Offset(0f, size.height), strokeWidth = 2f)

                                for (i in 1..3) {
                                    val y = size.height * (i / 4f)
                                    drawLine(gridColor, Offset(0f, y), Offset(size.width, y))
                                }

                                // Points representations of rescued meals volume: Mon=20, Tue=45, Wed=15, Thu=70, Fri=120 lbs saved!
                                val rawPoints = listOf(20f, 50f, 30f, 85f, 130f)
                                val maxVal = 140f
                                val spacing = size.width / (rawPoints.size - 1)

                                val offsets = rawPoints.mapIndexed { index, value ->
                                    val x = index * spacing
                                    val y = size.height - (size.height * (value / maxVal))
                                    Offset(x, y)
                                }

                                // Draw line connecting points
                                for (i in 0 until offsets.size - 1) {
                                    drawLine(
                                        color = Color(0xFF2E7D32),
                                        start = offsets[i],
                                        end = offsets[i + 1],
                                        strokeWidth = 6f
                                    )
                                }

                                // Draw circular indicator points
                                offsets.forEachIndexed { i, pt ->
                                    drawCircle(
                                        color = Color(0xFFF57C00),
                                        radius = 6.dp.toPx(),
                                        center = pt
                                    )
                                }
                            }
                            // Quick labels overlay
                            Text(
                                text = "Weekly Carbon Saved: 142 lbs CO2-eq",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32),
                                modifier = Modifier.align(Alignment.TopEnd)
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Customer Reservations and QR Codes to Scan",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                val activeReservations = restaurantReservations.filter { it.status == "RESERVED" }
                if (activeReservations.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No customers are scheduled for pickups right now.", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                } else {
                    items(activeReservations) { res ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = res.itemName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = "Reserved by ${res.buyerName}", fontSize = 12.sp)
                                    Text(text = "Code: ${res.qrCodeContent}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { viewModel.completeRestaurantPickup(res.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Verify QR")
                                }
                            }
                        }
                    }
                }

            } else if (role == "NGO") {
                // NGO CLAIM TRACKING
                val activeClaims = myClaimedDonations.filter { it.status != "COMPLETED" }
                if (activeClaims.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No active collection runs. Claim a donation alert on the home tab to start!",
                                textAlign = TextAlign.Center,
                                color = Color.Gray
                            )
                        }
                    }
                } else {
                    items(activeClaims) { claim ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = claim.itemName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f),
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "DISPATCHED",
                                            color = MaterialTheme.colorScheme.tertiary,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Ready at: ${claim.restaurantName}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                val context = LocalContext.current
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable {
                                        try {
                                            val intent = android.content.Intent(
                                                android.content.Intent.ACTION_VIEW,
                                                android.net.Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + android.net.Uri.encode(claim.pickupLocation))
                                            )
                                            context.startActivity(intent)
                                        } catch (e: Exception) {}
                                    }
                                ) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Address: ${claim.pickupLocation}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.tertiary,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            try {
                                                val intent = android.content.Intent(
                                                    android.content.Intent.ACTION_VIEW,
                                                    android.net.Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + android.net.Uri.encode(claim.pickupLocation))
                                                )
                                                context.startActivity(intent)
                                            } catch (e: Exception) {}
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).height(40.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                    ) {
                                        Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Directions", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }

                                    Button(
                                        onClick = { viewModel.completeNgoCollection(claim.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1.3f).height(40.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                    ) {
                                        Text("Collected & Safe", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

            } else {
                // CUSTOMER COMPANION VIEW
                if (myReservations.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.Gray)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Your basket is empty",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Pick up discounted meals in the 'Feed' tab to populate this list.",
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(myReservations) { res ->
                        var showQrDialog by remember { mutableStateOf(false) }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = res.itemName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (res.status == "RESERVED") MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                                else Color.Gray.copy(alpha = 0.1f),
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = res.status,
                                            color = if (res.status == "RESERVED") MaterialTheme.colorScheme.primary else Color.Gray,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Text(
                                    text = "From: ${res.restaurantName}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                val context = LocalContext.current
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable {
                                        try {
                                            val intent = android.content.Intent(
                                                android.content.Intent.ACTION_VIEW,
                                                android.net.Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + android.net.Uri.encode(res.pickupLocation))
                                            )
                                            context.startActivity(intent)
                                        } catch (e: Exception) {}
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Navigation,
                                        contentDescription = "Navigate",
                                        tint = MaterialTheme.colorScheme.tertiary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "Address: ${res.pickupLocation}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.tertiary,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (res.isDonation) "Sponsored Item" else "Total: ₹${String.format(Locale.US, "%.0f", res.price)}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )

                                    if (res.status == "RESERVED") {
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            OutlinedButton(
                                                onClick = {
                                                    try {
                                                        val intent = android.content.Intent(
                                                            android.content.Intent.ACTION_VIEW,
                                                            android.net.Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + android.net.Uri.encode(res.pickupLocation))
                                                        )
                                                        context.startActivity(intent)
                                                    } catch (e: Exception) {}
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp),
                                                modifier = Modifier.height(36.dp)
                                            ) {
                                                Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Directions", fontSize = 11.sp)
                                            }

                                            Button(
                                                onClick = { showQrDialog = true },
                                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier
                                                    .testTag("show_qr_button_${res.id}")
                                                    .height(36.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp)
                                            ) {
                                                Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Show QR", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Verifiable QR Code dialog mockup
                        if (showQrDialog) {
                            Dialog(onDismissRequest = { showQrDialog = false }) {
                                Card(
                                    modifier = Modifier.width(280.dp),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Pickup Verification QR",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        
                                        // Draw beautiful verification QR illustration box
                                        Box(
                                            modifier = Modifier
                                                .size(160.dp)
                                                .background(Color.White, RoundedCornerShape(12.dp))
                                                .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Canvas(modifier = Modifier.fillMaxSize()) {
                                                // Cute nested rectangles drawing a simulated QR pixel pattern
                                                val stroke = Stroke(width = 6f)
                                                drawRect(Color.Black, Offset(0f, 0f), Size(30.dp.toPx(), 30.dp.toPx()), style = stroke)
                                                drawRect(Color.Black, Offset(size.width - 30.dp.toPx(), 0f), Size(30.dp.toPx(), 30.dp.toPx()), style = stroke)
                                                drawRect(Color.Black, Offset(0f, size.height - 30.dp.toPx()), Size(30.dp.toPx(), 30.dp.toPx()), style = stroke)
                                                
                                                // Draw random QR code dots
                                                drawRect(Color.Black, Offset(size.width / 2 - 10, size.height / 2 - 10), Size(20f, 20f))
                                                drawRect(Color.Black, Offset(size.width / 2 + 15, size.height / 2 + 15), Size(15f, 15f))
                                                drawRect(Color.Black, Offset(size.width / 3, size.height / 4), Size(15f, 15f))
                                                drawRect(Color.Black, Offset(size.width * 0.7f, size.height * 0.7f), Size(25f, 25f))
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Text(
                                            text = res.qrCodeContent,
                                            fontSize = 11.sp,
                                            color = Color.Gray,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Present this to the restaurant staff when picking up the food box.",
                                            fontSize = 10.sp,
                                            textAlign = TextAlign.Center,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        Button(
                                            onClick = { showQrDialog = false },
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Close")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 7. TAB 2: AI STUDIO TAB (FRESHNESS & WASTE TOOLS)
// ==========================================
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AiStudioTab(viewModel: FoodRescueViewModel) {
    var selectedAiTool by remember { mutableIntStateOf(0) } // 0 = Freshness tool, 1 = Prep forecasting tool

    // Form states
    var foodName by remember { mutableStateOf("Fresh Berry Pie") }
    var description by remember { mutableStateOf("Fruit bakery left at warm room temperature for around 6 hours.") }
    
    // Prep variables
    var weekdayValue by remember { mutableStateOf("15") }
    var weekendValue by remember { mutableStateOf("45") }
    var activeStockValue by remember { mutableStateOf("10") }

    val isAnalyzingFreshness by viewModel.isAnalyzingFreshness.collectAsState()
    val freshnessResult by viewModel.freshnessAnalysisResult.collectAsState()

    val isPredictingWaste by viewModel.isPredictingWaste.collectAsState()
    val wasteResult by viewModel.wastePredictionResult.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Aesthetic Title
        Text(
            text = "AI Studio Tools",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        // Subselector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selectedAiTool == 0) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { selectedAiTool = 0 }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Freshness Index",
                    color = if (selectedAiTool == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selectedAiTool == 1) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { selectedAiTool = 1 }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Waste Predictor",
                    color = if (selectedAiTool == 1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (selectedAiTool == 0) {
            // TOOL 0: FRESHNESS ESTIMATION
            Text(
                text = "AI Freshness Detection Engine",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Take a photo or describe the surplus ingredients to calculate micro-bacterial safety ratios.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    OutlinedTextField(
                        value = foodName,
                        onValueChange = { foodName = it },
                        label = { Text("What is the Food / Beverage?") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Quality descriptors & preparation time") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Mock camera triggers representation
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                RoundedCornerShape(12.dp)
                            )
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            .clickable { viewModel.postNotification("Camera sensor triggered. High-resolution texture capture simulated!") }
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Aesthetic Texture Image Captured (Simulated)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Button(
                        onClick = { viewModel.runAiFreshnessDetection(foodName, description, null) },
                        enabled = !isAnalyzingFreshness && foodName.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("run_freshness_analysis")
                    ) {
                        if (isAnalyzingFreshness) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                        } else {
                            Text("Compute Diagnostic Insights")
                        }
                    }
                }
            }

            // Results showcase
            AnimatedVisibility(
                visible = freshnessResult != null,
                enter = fadeIn() + expandVertically()
            ) {
                if (freshnessResult != null) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.04f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Diagnostic Report",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF2E7D32), CircleShape)
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "${freshnessResult?.freshnessScore}% Fresh",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Divider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))

                            Text(
                                text = "Prediction: Safe to consume for the next ${freshnessResult?.predictedSafeHours} hours under typical ambient conditions.",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary
                            )

                            Text(
                                text = freshnessResult?.freshnessReport ?: "",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "Eco-Composting Guide: ${freshnessResult?.compostAdvice}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Quick Button to Add directly with this AI diagnostic attached!
                            if (viewModel.currentUser.collectAsState().value?.role == "RESTAURANT") {
                                var formOriginalPrice by remember { mutableStateOf("150") }
                                var formDiscountedPrice by remember { mutableStateOf("50") }
                                var formQuantity by remember { mutableStateOf("3") }
                                var formAddress by remember { mutableStateOf("Phase 2, Gachibowli, Hyderabad, Telangana") }
                                var donateForFree by remember { mutableStateOf(false) }

                                Divider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                Text("Publish Details (INR):", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = formOriginalPrice,
                                        onValueChange = { formOriginalPrice = it },
                                        label = { Text("Original Price (₹)", fontSize = 11.sp) },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = formDiscountedPrice,
                                        onValueChange = { formDiscountedPrice = it },
                                        label = { Text("Offer Price (₹)", fontSize = 11.sp) },
                                        singleLine = true,
                                        enabled = !donateForFree,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { donateForFree = !donateForFree }
                                ) {
                                    Checkbox(checked = donateForFree, onCheckedChange = { donateForFree = it })
                                    Text("Donate this food for free", fontSize = 12.sp)
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = formQuantity,
                                        onValueChange = { formQuantity = it },
                                        label = { Text("Servings", fontSize = 11.sp) },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = formAddress,
                                        onValueChange = { formAddress = it },
                                        label = { Text("Pickup Location", fontSize = 11.sp) },
                                        singleLine = true,
                                        modifier = Modifier.weight(2f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        val orig = formOriginalPrice.toDoubleOrNull() ?: 150.0
                                        val disc = if (donateForFree) 0.0 else (formDiscountedPrice.toDoubleOrNull() ?: 50.0)
                                        val qty = formQuantity.toIntOrNull() ?: 3

                                        viewModel.addFoodItem(
                                            name = foodName,
                                            description = description,
                                            originalPrice = orig,
                                            discountedPrice = disc,
                                            quantity = qty,
                                            isDonation = donateForFree,
                                            pickupLocation = formAddress,
                                            expiryHours = freshnessResult?.predictedSafeHours ?: 12,
                                            freshnessScore = freshnessResult?.freshnessScore ?: 80,
                                            predictedHours = freshnessResult?.predictedSafeHours ?: 12,
                                            reportText = freshnessResult?.freshnessReport ?: "",
                                            compostText = freshnessResult?.compostAdvice ?: ""
                                        ) { success ->
                                            if (success) {
                                                viewModel.clearFreshnessAnalysisResult()
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                                    modifier = Modifier.fillMaxWidth().testTag("upload_to_shop_button")
                                ) {
                                    Text("Upload to Shop with AI Seal Attached")
                                }
                            }
                        }
                    }
                }
            }

        } else {
            // TOOL 1: WASTE PREDICTOR
            Text(
                text = "Kitchen Preparation Rate AI",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Inputs food transaction volume to predict leftovers and receive preparation schedules.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    OutlinedTextField(
                        value = foodName,
                        onValueChange = { foodName = it },
                        label = { Text("What is the food dish name?") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = weekdayValue,
                            onValueChange = { weekdayValue = it },
                            label = { Text("Weekday sales") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = weekendValue,
                            onValueChange = { weekendValue = it },
                            label = { Text("Weekend sales") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    OutlinedTextField(
                        value = activeStockValue,
                        onValueChange = { activeStockValue = it },
                        label = { Text("Active unsold stock currently in kitchen") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            val weekday = weekdayValue.toIntOrNull() ?: 15
                            val weekend = weekendValue.toIntOrNull() ?: 45
                            val stock = activeStockValue.toIntOrNull() ?: 10
                            viewModel.runFoodWastePrediction(foodName, weekday, weekend, stock)
                        },
                        enabled = !isPredictingWaste && foodName.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isPredictingWaste) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                        } else {
                            Text("Compute Kitchen Optimization Plan")
                        }
                    }
                }
            }

            // Waste results showcase
            AnimatedVisibility(
                visible = wasteResult != null,
                enter = fadeIn() + expandVertically()
            ) {
                if (wasteResult != null) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.04f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Optimization Forecast",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary
                            )

                            Divider(color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(text = "Optimal Daily Prep", fontSize = 11.sp, color = Color.Gray)
                                    Text(text = "${wasteResult?.recommendedQty} Packages", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                                Column {
                                    Text(text = "Predicted Surplus", fontSize = 11.sp, color = Color.Gray)
                                    Text(text = "${wasteResult?.predictedSurplus} Packages", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                                }
                            }

                            Text(
                                text = "Waste Analysis Reasoning:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Text(
                                text = wasteResult?.reasoning ?: "",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.08f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "Automated Pricing Markdown Strategy: ${wasteResult?.discountStrategy}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 8. TAB 3: PROFILE TAB (XP, EMERGENCY RELIEF, SETTINGS)
// ==========================================
@Composable
fun ProfileTab(viewModel: FoodRescueViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    var isEmergencyModeActive by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Aesthetic Title
        Text(
            text = "Companion Profile",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        // User Avatar card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.tertiary
                                )
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentUser?.name?.take(2)?.uppercase() ?: "US",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = currentUser?.name ?: "Rescue Companion",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Text(
                    text = currentUser?.email ?: "user@foodrescue.com",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Sustainability Score",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "${currentUser?.sustainabilityScore ?: 100} XP",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Reward Points",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "${currentUser?.rewardPoints ?: 0} pts",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
        }

        // --- EMERGENCY FOOD RELIEF MODE ---
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isEmergencyModeActive) Color(0xFFD32F2F).copy(alpha = 0.08f)
                else MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.dp, if (isEmergencyModeActive) Color(0xFFD32F2F) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Emergency Food Relief Mode",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (isEmergencyModeActive) Color(0xFFD32F2F) else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Instantly flag critical surplus stock and broadcast coordinates to emergency networks.",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                    Switch(
                        checked = isEmergencyModeActive,
                        onCheckedChange = {
                            isEmergencyModeActive = it
                            if (it) {
                                viewModel.postNotification("Emergency Relief coordinates active. Distress alerts broadcast to all nearby soup kitchens & rescue vans!")
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFFD32F2F),
                            checkedTrackColor = Color(0xFFD32F2F).copy(alpha = 0.3f)
                        )
                    )
                }

                if (isEmergencyModeActive) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFD32F2F)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "CRITICAL CO-ORDINATION SYSTEM ENABLED",
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Distress signals broadcasted. Our nearby automated vans are notified to pick up leftover food packs directly from listed locations and deliver to municipal community shelter coordinates.",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }
        }

        // Settings Block
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(text = "App Preferences", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Push Food Discovery Notifications", fontSize = 12.sp)
                    Checkbox(checked = true, onCheckedChange = {})
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Share Anonymized Food Waste Statistics", fontSize = 12.sp)
                    Checkbox(checked = true, onCheckedChange = {})
                }

                Button(
                    onClick = { viewModel.postNotification("User preferences saved locally!") },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Apply preferences")
                }
            }
        }
    }
}
