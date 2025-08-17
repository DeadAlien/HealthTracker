package com.example.androidhealthtracer


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
                .padding(32.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Welcome to HealthTracker Dashboard!", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))
            when {
                loading -> Text("Loading profile...")
                error != null -> Text("Error: $error", color = MaterialTheme.colorScheme.error)
                profile != null -> {
                    profile!!.forEach { (key, value) ->
                        Text(text = "$key: $value")
                    }
                }
            }
        }
    }
}
