package com.example.androidhealthtracer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androidhealthtracer.network.ApiService
import com.example.androidhealthtracer.network.LoginResponse
import com.example.androidhealthtracer.network.RetrofitClient
import com.example.androidhealthtracer.ui.theme.AndroidHealthTracerTheme
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidHealthTracerTheme {
                ForgotPasswordScreen()
            }
        }
    }
}

@Composable
fun ForgotPasswordScreen() {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var step by remember { mutableStateOf(1) } // 1 for email, 2 for OTP/password

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (step == 1) {
                Text("Forgot Password", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        // In a real app, you'd call an API to send an OTP.
                        // For now, we'll just proceed to the next step.
                        if (email.isNotBlank()) {
                            step = 2
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Please enter your email.")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Send OTP")
                }
            } else {
                Text("Reset Password", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = otp,
                    onValueChange = { otp = it },
                    label = { Text("OTP") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        coroutineScope.launch {
                            if (otp == "123456") {
                                val params = mapOf(
                                    "email" to email,
                                    "otp" to otp,
                                    "password" to newPassword
                                )
                                RetrofitClient.instance.resetPassword(params).enqueue(object : Callback<LoginResponse> {
                                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                                        if (response.isSuccessful && response.body()?.success == true) {
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Password reset successfully!")
                                            }
                                            // finish() or navigate to login
                                        } else {
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Failed to reset password: ${response.body()?.message}")
                                            }
                                        }
                                    }

                                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Error: ${t.message}")
                                        }
                                    }
                                })
                            } else {
                                snackbarHostState.showSnackbar("Invalid OTP.")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reset Password")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
    AndroidHealthTracerTheme {
        ForgotPasswordScreen()
    }
}
