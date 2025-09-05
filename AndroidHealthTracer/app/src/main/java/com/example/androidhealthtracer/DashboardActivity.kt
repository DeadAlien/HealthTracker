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
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            Text(text = "Welcome, $email", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            if (profile != null) {
                Text(text = "Your Profile", style = MaterialTheme.typography.titleMedium)
                profile!!.forEach { (key, value) ->
                    Text(text = "$key: ${if (key == "fitness_goals" && value is String) value.replace(",", ", ") else value}")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            val context = LocalContext.current
            Row {
                Button(onClick = {
                    // Navigate to EditProfileActivity
                    val intent = Intent(context, EditProfileActivity::class.java)
                    intent.putExtra("email", email)
                    context.startActivity(intent)
                }, modifier = Modifier.padding(end = 8.dp)) {
                    Text("Edit Profile")
                }
                Button(onClick = {
                    // Logout: clear activity stack and return to login
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                }) {
                    Text("Logout")
                }
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
                DropdownMenuBox(options = listOf("daily", "weekly", "on-demand"), selected = period, onSelect = { period = it })
            }
            Row {
                Text("Workout Location:", modifier = Modifier.align(Alignment.CenterVertically))
                Spacer(modifier = Modifier.width(8.dp))
                DropdownMenuBox(options = listOf("home", "gym"), selected = location, onSelect = { location = it })
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

// EditProfileActivity for navigation (top-level)
class EditProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val email = intent.getStringExtra("email") ?: ""
        setContent {
            AndroidHealthTracerTheme {
                EditProfileScreen(email = email, onBack = { finish() })
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(email: String, onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var activityLevel by remember { mutableStateOf("") }
    var diet by remember { mutableStateOf("") }
    var fitnessGoals by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    // Optionally: Fetch current profile data here and prefill fields

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Age") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = gender, onValueChange = { gender = it }, label = { Text("Gender") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = activityLevel, onValueChange = { activityLevel = it }, label = { Text("Activity Level") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = diet, onValueChange = { diet = it }, label = { Text("Diet") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = fitnessGoals, onValueChange = { fitnessGoals = it }, label = { Text("Fitness Goals") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    loading = true
                    // TODO: Implement save profile logic (call backend API)
                    message = "Profile updated! (not yet saved to backend)"
                    loading = false
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading
            ) {
                Text(if (loading) "Saving..." else "Save")
            }
            if (message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(message, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
@Composable
fun DropdownMenuBox(options: List<String>, selected: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.width(120.dp).clickable { expanded = true },
            label = { Text("Select") },
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = {
                    onSelect(option)
                    expanded = false
                })
            }
        }
    }
}
