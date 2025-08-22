package com.example.androidhealthtracer
import androidx.compose.ui.Alignment

import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import com.example.androidhealthtracer.network.RetrofitClient
import com.example.androidhealthtracer.network.RegisterResponse
import com.example.androidhealthtracer.ui.theme.AndroidHealthTracerTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidHealthTracerTheme {
                RegisterScreen { params ->
                    register(params)
                }
            }
        }
    }

    private fun register(params: Map<String, String>) {
        RetrofitClient.instance.register(params).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@RegisterActivity, "Registration successful!", Toast.LENGTH_SHORT).show()
                    finish() // Go back to login
                } else {
                    Toast.makeText(this@RegisterActivity, "Registration failed: ${response.body()?.message}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

@Composable
fun RegisterScreen(onRegister: (Map<String, String>) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var genderExpanded by remember { mutableStateOf(false) }
    var gender by remember { mutableStateOf("") }
    val genderOptions = listOf("M" to "Male", "F" to "Female", "Other" to "Other")
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var goalWeight by remember { mutableStateOf("") }
    var activityExpanded by remember { mutableStateOf(false) }
    var activity by remember { mutableStateOf("") }
    val activityOptions = listOf("Sedentary", "Lightly Active", "Moderately Active", "Very Active", "Super Active")
    var dietExpanded by remember { mutableStateOf(false) }
    var diet by remember { mutableStateOf("") }
    val dietOptions = listOf(
        "High Protein", "Vegetarian", "Vegan", "Balanced/Mixed", "Low Carb / Keto",
        "High Carb", "Paleo", "Gluten-Free", "No preference"
    )
    val fitnessGoalsOptions = listOf(
        "Fat Loss", "Muscle Gain", "Weight Maintenance", "Flexibility & Mobility",
        "Cardiovascular Health", "Mental Well-being", "Strength & Performance",
        "Endurance / Stamina", "Medical Rehabilitation"
    )
    var fitnessGoalsSelected by remember { mutableStateOf(setOf<String>()) }
    var fitnessGoals by remember { mutableStateOf("") }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .padding(bottom = 32.dp), // Add bottom padding for button visibility
                verticalArrangement = Arrangement.Top // Start from top for better scroll
            ) {
                Text(text = "Register", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = mobile, onValueChange = { mobile = it }, label = { Text("Mobile") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Age") }, modifier = Modifier.fillMaxWidth())
                Box {
                    OutlinedTextField(
                        value = genderOptions.find { it.first == gender }?.second ?: "Select Gender",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Gender") },
                        modifier = Modifier.fillMaxWidth().clickable { genderExpanded = true },
                        trailingIcon = {
                            IconButton(onClick = { genderExpanded = !genderExpanded }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = genderExpanded,
                        onDismissRequest = { genderExpanded = false }
                    ) {
                        genderOptions.forEach { (value, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    gender = value
                                    genderExpanded = false
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(value = height, onValueChange = { height = it }, label = { Text("Height (cm)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Weight (kg)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = goalWeight, onValueChange = { goalWeight = it }, label = { Text("Goal Weight (kg)") }, modifier = Modifier.fillMaxWidth())
                Box {
                    OutlinedTextField(
                        value = activity,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Activity Level") },
                        modifier = Modifier.fillMaxWidth().clickable { activityExpanded = true },
                        trailingIcon = {
                            IconButton(onClick = { activityExpanded = !activityExpanded }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = activityExpanded,
                        onDismissRequest = { activityExpanded = false }
                    ) {
                        activityOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    activity = option
                                    activityExpanded = false
                                }
                            )
                        }
                    }
                }
                Box {
                    OutlinedTextField(
                        value = if (diet.isNotEmpty()) diet else "Select Diet",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Diet") },
                        modifier = Modifier.fillMaxWidth().clickable { dietExpanded = true },
                        trailingIcon = {
                            IconButton(onClick = { dietExpanded = !dietExpanded }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = dietExpanded,
                        onDismissRequest = { dietExpanded = false }
                    ) {
                        dietOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    diet = option
                                    dietExpanded = false
                                }
                            )
                        }
                    }
                }
                Text(text = "Fitness Goals", style = MaterialTheme.typography.bodyLarge)
                Column {
                    fitnessGoalsOptions.forEach { goal ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable {
                                    fitnessGoalsSelected = if (fitnessGoalsSelected.contains(goal)) {
                                        fitnessGoalsSelected - goal
                                    } else {
                                        fitnessGoalsSelected + goal
                                    }
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = fitnessGoalsSelected.contains(goal),
                                onCheckedChange = {
                                    fitnessGoalsSelected = if (fitnessGoalsSelected.contains(goal)) {
                                        fitnessGoalsSelected - goal
                                    } else {
                                        fitnessGoalsSelected + goal
                                    }
                                }
                            )
                            Text(goal, Modifier.padding(start = 8.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = {
                    val params = mapOf(
                        "email" to email,
                        "password" to password,
                        "mobile" to mobile,
                        "age" to age,
                        "gender" to gender,
                        "height" to height,
                        "weight" to weight,
                        "goal_weight" to goalWeight,
                        "activity" to activity,
                        "diet" to diet,
                        "fitness_goals" to fitnessGoalsSelected.joinToString(",")
                    )
                    onRegister(params)
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Register")
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
