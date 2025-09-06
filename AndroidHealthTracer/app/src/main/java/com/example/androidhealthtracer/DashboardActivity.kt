package com.example.androidhealthtracer

import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.platform.LocalContext
import com.example.androidhealthtracer.network.RetrofitClient
import com.example.androidhealthtracer.network.DashboardResponse
import com.example.androidhealthtracer.ui.theme.AndroidHealthTracerTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.graphics.vector.ImageVector

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val email = intent.getStringExtra("email") ?: ""
        setContent {
            AndroidHealthTracerTheme {
                DashboardScreen(email)
            }
        }
    }
}

@Composable
fun DashboardScreen(email: String) {
    var profile by remember { mutableStateOf<Map<String, Any>?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }
    var period by remember { mutableStateOf("daily") }
    var location by remember { mutableStateOf("home") }
    var routine by remember { mutableStateOf<List<Map<String, Any>>?>(null) }
    var routineLoading by remember { mutableStateOf(false) }
    var routineError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(email) {
        RetrofitClient.instance.getDashboard(email).enqueue(object : Callback<DashboardResponse> {
            override fun onResponse(call: Call<DashboardResponse>, response: Response<DashboardResponse>) {
                loading = false
                if (response.isSuccessful && response.body()?.success == true) {
                    profile = response.body()?.profile
                } else {
                    error = response.body()?.message ?: "Failed to load profile."
                }
            }
            override fun onFailure(call: Call<DashboardResponse>, t: Throwable) {
                loading = false
                error = t.message
            }
        })
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            HeaderSection(email = email, profile = profile)
            Column(modifier = Modifier.padding(16.dp)) {
                val currentProfile = profile
                if (currentProfile != null) {
                    ProfileSection(profile = currentProfile, email = email)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Log Daily Activities", style = MaterialTheme.typography.titleMedium)
                // Daily log form fields (simplified)
                var breakfast by remember { mutableStateOf("") }
                var lunch by remember { mutableStateOf("") }
                var snacks by remember { mutableStateOf("") }
                var dinner by remember { mutableStateOf("") }
                var workout by remember { mutableStateOf("") }
                var water by remember { mutableStateOf("") }
                var sleep by remember { mutableStateOf("") }
                var weight by remember { mutableStateOf("") }
                OutlinedTextField(value = breakfast, onValueChange = { breakfast = it }, label = { Text("Breakfast") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = lunch, onValueChange = { lunch = it }, label = { Text("Lunch") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = snacks, onValueChange = { snacks = it }, label = { Text("Snacks") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = dinner, onValueChange = { dinner = it }, label = { Text("Dinner") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = workout, onValueChange = { workout = it }, label = { Text("Workout") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = water, onValueChange = { water = it }, label = { Text("Water Intake (ml)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = sleep, onValueChange = { sleep = it }, label = { Text("Sleep (hours)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Weight (kg)") }, modifier = Modifier.fillMaxWidth())
                Button(onClick = { /* TODO: Save log to backend */ }, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Text("Save Log")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Recent Progress (Last 7 Days)", style = MaterialTheme.typography.titleMedium)
                // TODO: Replace with real log data from backend
                Text("No logs available.")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Generate Personalized Routine", style = MaterialTheme.typography.titleMedium)
                Row {
                    Text("Period:", modifier = Modifier.align(Alignment.CenterVertically))
                    Spacer(modifier = Modifier.width(8.dp))
                    DropdownMenuComponent(options = listOf("daily", "weekly", "on-demand"), selected = period, onSelect = { period = it })
                }
                Row {
                    Text("Workout Location:", modifier = Modifier.align(Alignment.CenterVertically))
                    Spacer(modifier = Modifier.width(8.dp))
                    DropdownMenuComponent(options = listOf("home", "gym"), selected = location, onSelect = { location = it })
                }
                Button(
                    onClick = {
                        routineLoading = true
                        routineError = null
                        routine = null
                        RetrofitClient.instance.getRoutine(email, period, location).enqueue(object : retrofit2.Callback<com.example.androidhealthtracer.network.RoutineResponse> {
                            override fun onResponse(
                                call: Call<com.example.androidhealthtracer.network.RoutineResponse>,
                                response: Response<com.example.androidhealthtracer.network.RoutineResponse>
                            ) {
                                routineLoading = false
                                if (response.isSuccessful && response.body()?.success == true) {
                                    routine = response.body()?.routine
                                } else {
                                    routineError = response.body()?.message ?: "Failed to generate routine."
                                }
                            }
                            override fun onFailure(
                                call: Call<com.example.androidhealthtracer.network.RoutineResponse>,
                                t: Throwable
                            ) {
                                routineLoading = false
                                routineError = t.message
                            }
                        })
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Text(if (routineLoading) "Generating..." else "Generate Routine")
                }
                if (routineError != null) {
                    Text(routineError ?: "", color = MaterialTheme.colorScheme.error)
                }
                if (routine != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Routine:", style = MaterialTheme.typography.titleMedium)
                    for (day in routine!!) {
                        val dayName = day["day"]?.toString() ?: "Day"
                        val meals = day["meals"] as? List<Map<String, Any>>
                        val workout = day["workout"]
                        Column(modifier = Modifier.padding(bottom = 8.dp)) {
                            Text(dayName, style = MaterialTheme.typography.titleSmall)
                            if (meals != null) {
                                Text("Meals:", style = MaterialTheme.typography.bodyMedium)
                                for (meal in meals) {
                                    val mealName = meal["meal"]?.toString() ?: "Meal"
                                    val food = meal["food"]?.toString() ?: ""
                                    Text("- $mealName: $food", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            if (workout != null) {
                                val workoutName = if (workout is Map<*, *>) workout["workout"].toString() else workout.toString()
                                Text("Workout: $workoutName", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileSection(profile: Map<String, Any>, email: String) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Personal Info
            Text("Personal Info", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            ProfileInfoRow(label = "Age", value = profile["age"]?.toString())
            ProfileInfoRow(label = "Gender", value = profile["gender"]?.toString())
            ProfileInfoRow(label = "Height", value = profile["height"]?.toString()?.let { "$it cm" })
            ProfileInfoRow(label = "⚖️ Weight", value = profile["weight"]?.toString()?.let { "$it kg" })

            Spacer(modifier = Modifier.height(16.dp))

            // Health Info
            Text("Health Info", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            ProfileInfoRow(label = "🍎 Diet", value = profile["diet"]?.toString())
            ProfileInfoRow(label = "Allergies", value = formatList(profile["allergies"]))
            ProfileInfoRow(label = "🏋️ Fitness Goals", value = formatList(profile["fitness_goals"]))
            ProfileInfoRow(label = "🚫 Foods to Avoid", value = formatList(profile["foods_to_avoid"]))

            Spacer(modifier = Modifier.height(16.dp))

            // Weight Progress
            val currentWeight = profile["weight"]?.toString()?.toFloatOrNull()
            val goalWeight = profile["goal_weight"]?.toString()?.toFloatOrNull()
            if (currentWeight != null && goalWeight != null && goalWeight > 0) {
                Text("Weight Progress", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                WeightProgressBar(currentWeight = currentWeight, goalWeight = goalWeight)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                GradientButton(
                    text = "Edit Profile",
                    icon = Icons.Default.Edit,
                    onClick = {
                        val intent = Intent(context, EditProfileActivity::class.java)
                        intent.putExtra("email", email)
                        context.startActivity(intent)
                    }
                )
                GradientButton(
                    text = "Logout",
                    icon = Icons.Default.ExitToApp,
                    onClick = {
                        val intent = Intent(context, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun GradientButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                    )
                )
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, color = Color.White)
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String?) {
    if (!value.isNullOrBlank()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Text(text = value)
        }
    }
}

@Composable
fun WeightProgressBar(currentWeight: Float, goalWeight: Float) {
    val progress = (currentWeight / goalWeight).coerceIn(0f, 1f)
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Current: $currentWeight kg / Goal: $goalWeight kg")
    }
}

fun formatList(value: Any?): String? {
    return when (value) {
        is List<*> -> value.joinToString(", ")
        is String -> value
        else -> null
    }
}

@Composable
fun HeaderSection(email: String, profile: Map<String, Any>?) {
    val motivationalQuotes = listOf(
        "Your body achieves what your mind believes 💪",
        "The only bad workout is the one that didn't happen.",
        "Push yourself, because no one else is going to do it for you."
    )
    val quote = remember { motivationalQuotes.random() }
    val profilePictureUrl = profile?.get("profile_picture")?.toString()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF87CEEB), Color(0xFF00008B))
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (profilePictureUrl != null) {
                    Image(
                        painter = rememberAsyncImagePainter(model = "http://10.0.2.2:5000/$profilePictureUrl"),
                        contentDescription = "Profile Avatar",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile Avatar",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.Gray.copy(alpha = 0.5f)),
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = email,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = quote,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun DropdownMenuComponent(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .width(150.dp)
                .clickable { expanded = true },
            label = { Text("Select") },
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
