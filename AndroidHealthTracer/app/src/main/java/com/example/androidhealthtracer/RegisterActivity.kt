package com.example.androidhealthtracer

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    var gender by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var goalWeight by remember { mutableStateOf("") }
    var activity by remember { mutableStateOf("") }
    var diet by remember { mutableStateOf("") }
    var fitnessGoals by remember { mutableStateOf("") }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(32.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Register", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = mobile, onValueChange = { mobile = it }, label = { Text("Mobile") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Age") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = gender, onValueChange = { gender = it }, label = { Text("Gender") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = height, onValueChange = { height = it }, label = { Text("Height (cm)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Weight (kg)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = goalWeight, onValueChange = { goalWeight = it }, label = { Text("Goal Weight (kg)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = activity, onValueChange = { activity = it }, label = { Text("Activity Level") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = diet, onValueChange = { diet = it }, label = { Text("Diet") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = fitnessGoals, onValueChange = { fitnessGoals = it }, label = { Text("Fitness Goals (comma separated)") }, modifier = Modifier.fillMaxWidth())
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
                    "fitness_goals" to fitnessGoals
                )
                onRegister(params)
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Register")
            }
        }
    }
}
