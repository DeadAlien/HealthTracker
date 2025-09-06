
package com.example.androidhealthtracer.network

import retrofit2.Call
import retrofit2.http.*

data class RoutineResponse(val success: Boolean, val routine: List<Map<String, Any>>?, val message: String?)
data class LoginResponse(val success: Boolean, val user: Map<String, Any>?, val message: String?)
data class RegisterResponse(val success: Boolean, val message: String?)
data class LogResponse(val success: Boolean, val message: String?)
data class DashboardResponse(val success: Boolean, val profile: Map<String, Any>?, val message: String?)

interface ApiService {
    @GET("api/routine")
    fun getRoutine(
        @Query("email") email: String,
        @Query("period") period: String = "weekly",
        @Query("location") location: String = "home"
    ): Call<RoutineResponse>

    @FormUrlEncoded
    @POST("api/login")
    fun login(@Field("email") email: String, @Field("password") password: String): Call<LoginResponse>

    @FormUrlEncoded
    @POST("api/register")
    fun register(@FieldMap params: Map<String, String>): Call<RegisterResponse>

    @GET("api/dashboard")
    fun getDashboard(@Query("email") email: String): Call<DashboardResponse>

    @FormUrlEncoded
    @POST("edit_log")
    fun editLog(@FieldMap params: Map<String, String>): Call<LogResponse>

    @GET("delete_log")
    fun deleteLog(@Query("date") date: String, @Query("email") email: String): Call<LogResponse>
    @FormUrlEncoded
    @POST("api/profile")
    fun updateProfile(@Query("email") email: String, @FieldMap params: Map<String, String>): Call<DashboardResponse>

    @POST("api/reset_password")
    fun resetPassword(@Body params: Map<String, String>): Call<LoginResponse>
}
