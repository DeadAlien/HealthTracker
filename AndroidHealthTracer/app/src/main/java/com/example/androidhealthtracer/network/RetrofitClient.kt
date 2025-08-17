package com.example.androidhealthtracer.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder

object RetrofitClient {
    // Use your Wi-Fi IPv4 address for Flask backend connectivity
    private const val BASE_URL = "http://10.0.2.2:5000/" // Emulator access to host Flask backend

    private val gson = GsonBuilder().setLenient().create()

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}
