package com.example.androidhealthtracer

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.androidhealthtracer.network.DashboardResponse
import com.example.androidhealthtracer.network.RetrofitClient
import com.example.androidhealthtracer.ui.theme.AndroidHealthTracerTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val email = intent.getStringExtra("email") ?: ""
        setContent {
            AndroidHealthTracerTheme {
                EditProfileScreen(email = email)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(email: String) {
    var mobile by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var goalWeight by remember { mutableStateOf("") }
    var activityLevel by remember { mutableStateOf("") }
    var dietPreference by remember { mutableStateOf("") }
    var fitnessGoals by remember { mutableStateOf<List<String>>(emptyList()) }
    var allergies by remember { mutableStateOf<List<String>>(emptyList()) }
    var dislikes by remember { mutableStateOf<List<String>>(emptyList()) }
    var foodsToAvoid by remember { mutableStateOf<List<String>>(emptyList()) }
    var preferredWorkoutRestDays by remember { mutableStateOf("") }
    var preferredWorkoutTime by remember { mutableStateOf("") }
    var goalWizard by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(email) {
        RetrofitClient.instance.getDashboard(email).enqueue(object : Callback<DashboardResponse> {
            override fun onResponse(call: Call<DashboardResponse>, response: Response<DashboardResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val profile = response.body()?.profile
                    profile?.let {
                        mobile = it["mobile"]?.toString() ?: ""
                        age = it["age"]?.toString() ?: ""
                        gender = it["gender"]?.toString() ?: ""
                        height = it["height"]?.toString() ?: ""
                        weight = it["weight"]?.toString() ?: ""
                        goalWeight = it["goal_weight"]?.toString() ?: ""
                        activityLevel = it["activity"]?.toString() ?: ""
                        dietPreference = it["diet"]?.toString() ?: ""
                        fitnessGoals = (it["fitness_goals"] as? String)?.split(",")?.map { it.trim() } ?: emptyList()
                        allergies = (it["allergies"] as? String)?.split(",")?.map { it.trim() } ?: emptyList()
                        dislikes = (it["dislikes"] as? String)?.split(",")?.map { it.trim() } ?: emptyList()
                        foodsToAvoid = (it["foods_to_avoid"] as? String)?.split(",")?.map { it.trim() } ?: emptyList()
                        preferredWorkoutRestDays = (it["preferred_workout_days"] as? String)?.split(",")?.map { it.trim() }?.joinToString() ?: ""
                        preferredWorkoutTime = it["workout_time"]?.toString() ?: ""
                        goalWizard = it["goal_wizard"]?.toString() ?: ""
                    }
                }
            }

            override fun onFailure(call: Call<DashboardResponse>, t: Throwable) {
                Toast.makeText(context, "Failed to load profile data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text("Edit Profile") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(value = mobile, onValueChange = { mobile = it }, label = { Text("Mobile") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Age") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = gender, onValueChange = { gender = it }, label = { Text("Gender") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = height, onValueChange = { height = it }, label = { Text("Height (cm)") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Weight (kg)") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = goalWeight, onValueChange = { goalWeight = it }, label = { Text("Goal Weight (kg)") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))

            DropdownMenuComponent(
                label = "Activity Level",
                options = listOf(
                    "🛋️ Sedentary (Little or no exercise)",
                    "🤸 Lightly active (Light exercise/sports 1-3 days/week)",
                    "🏃 Moderately active (Moderate exercise/sports 3-5 days/week)",
                    "💪 Very active (Hard exercise/sports 6-7 days a week)",
                    "⚡ Super active (Very hard exercise/physical job & exercise 2x/day)"
                ),
                selectedOption = activityLevel,
                onOptionSelected = { activityLevel = it }
            )
            Spacer(modifier = Modifier.height(16.dp))

            DropdownMenuComponent(
                label = "Diet Preference",
                options = listOf(
                    "🥬 Balanced", "🍗 High Protein", "🥑 Low-Carb", "🥦 Vegetarian", "🥕 Vegan", "🥩 Keto", "🐟 Paleo", "🌾 Gluten-Free", "🥛 Dairy-Free"
                ),
                selectedOption = dietPreference,
                onOptionSelected = { dietPreference = it }
            )
            Spacer(modifier = Modifier.height(16.dp))

            MultiSelectChip(
                label = "Fitness Goals",
                options = listOf(
                    "🔥 Fat Loss", "💪 Muscle Gain", "⚖️ Weight Maintenance", "🧘 Flexibility & Mobility", "❤️ Cardiovascular Health", "🧠 Mental Well-being", "🏋️ Strength & Performance", "🏃 Endurance / Stamina", "🩺 Medical Rehabilitation"
                ),
                selectedOptions = fitnessGoals,
                onSelectionChanged = { fitnessGoals = it }
            )
            Spacer(modifier = Modifier.height(16.dp))

            MultiSelectChip(
                label = "Allergies",
                options = listOf(
                    "None", "Peanuts", "Tree Nuts (almonds, cashews, walnuts, etc.)", "Dairy / Lactose", "Eggs", "Wheat / Gluten", "Soy", "Fish", "Shellfish", "Sesame", "Other"
                ),
                selectedOptions = allergies,
                onSelectionChanged = { allergies = it }
            )
            Spacer(modifier = Modifier.height(16.dp))

            MultiSelectChip(
                label = "Dislikes",
                options = listOf(
                    "None", "Spicy Food", "Bitter Food", "Sweet/Desserts", "Sour Food", "High-Fat Food", "Raw Vegetables", "Cooked Vegetables", "Legumes / Beans", "Seafood", "Meat (beef/pork/chicken)", "Other"
                ),
                selectedOptions = dislikes,
                onSelectionChanged = { dislikes = it }
            )
            Spacer(modifier = Modifier.height(16.dp))

            MultiSelectChip(
                label = "Foods to Avoid",
                options = listOf(
                    "None", "Fast Food / Junk Food", "Fried Food", "Sugary Drinks", "Alcohol", "Red Meat", "Processed Meats (sausages, bacon, etc.)", "High Salt Foods", "High Sugar Foods", "Caffeine", "Other"
                ),
                selectedOptions = foodsToAvoid,
                onSelectionChanged = { foodsToAvoid = it }
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = preferredWorkoutRestDays, onValueChange = { preferredWorkoutRestDays = it }, label = { Text("Preferred Workout/Rest Days") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = preferredWorkoutTime, onValueChange = { preferredWorkoutTime = it }, label = { Text("Preferred Workout Time") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = goalWizard, onValueChange = { goalWizard = it }, label = { Text("Goal Wizard (SMART template)") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                val updatedProfile = mapOf(
                    "mobile" to mobile,
                    "age" to age,
                    "gender" to gender,
                    "height" to height,
                    "weight" to weight,
                    "goal_weight" to goalWeight,
                    "activity" to activityLevel,
                    "diet" to dietPreference,
                    "fitness_goals" to fitnessGoals.joinToString(","),
                    "allergies" to allergies.joinToString(","),
                    "dislikes" to dislikes.joinToString(","),
                    "foods_to_avoid" to foodsToAvoid.joinToString(","),
                    "preferred_workout_days" to preferredWorkoutRestDays,
                    "workout_time" to preferredWorkoutTime,
                    "goal_wizard" to goalWizard
                )

                RetrofitClient.instance.updateProfile(email, updatedProfile).enqueue(object : Callback<DashboardResponse> {
                    override fun onResponse(call: Call<DashboardResponse>, response: Response<DashboardResponse>) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                            (context as? Activity)?.finish()
                        } else {
                            Toast.makeText(context, "Failed to update profile: ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<DashboardResponse>, t: Throwable) {
                        Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Save")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                (context as? Activity)?.finish()
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Back to Dashboard")
            }
        }
    }
}

@Composable
fun DropdownMenuComponent(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = { },
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    modifier = Modifier.clickable { expanded = true }
                )
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MultiSelectChip(
    label: String,
    options: List<String>,
    selectedOptions: List<String>,
    onSelectionChanged: (List<String>) -> Unit
) {
    Column {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                val isSelected = selectedOptions.contains(option)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        val newSelection = if (isSelected) {
                            selectedOptions - option
                        } else {
                            selectedOptions + option
                        }
                        onSelectionChanged(newSelection)
                    },
                    label = { Text(option) }
                )
            }
        }
    }
}
