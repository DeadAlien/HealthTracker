package com.example.androidhealthtracer

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.androidhealthtracer.network.DashboardResponse
import com.example.androidhealthtracer.network.RetrofitClient
import com.example.androidhealthtracer.ui.theme.AndroidHealthTracerTheme
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import androidx.compose.foundation.shape.CircleShape

class EditProfileActivity : ComponentActivity() {

    private var selectedImageUri by mutableStateOf<android.net.Uri?>(null)

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: android.net.Uri? ->
        selectedImageUri = uri
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val email = intent.getStringExtra("email") ?: ""
        setContent {
            AndroidHealthTracerTheme {
                EditProfileScreen(
                    email = email,
                    onImagePick = { imagePickerLauncher.launch("image/*") },
                    imageUri = selectedImageUri
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditProfileScreen(email: String, onImagePick: () -> Unit, imageUri: android.net.Uri?) {
    var profile by remember { mutableStateOf<Map<String, Any>?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }
    val context = LocalContext.current

    LaunchedEffect(email) {
        RetrofitClient.instance.getDashboard(email).enqueue(object : Callback<DashboardResponse> {
            override fun onResponse(call: Call<DashboardResponse>, response: Response<DashboardResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val profileData = response.body()?.profile
                    profileData?.let {
                        profile = it
                    }
                } else {
                    error = response.message()
                }
                loading = false
            }

            override fun onFailure(call: Call<DashboardResponse>, t: Throwable) {
                error = t.message
                loading = false
            }
        })
    }

    Scaffold { innerPadding ->
        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = error!!)
            }
        } else if (profile != null) {
            val updatedProfile = remember { mutableStateMapOf<String, Any>().apply { putAll(profile!!) } }

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Edit Profile", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    if (imageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(model = imageUri),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(128.dp)
                                .clip(CircleShape)
                                .clickable { onImagePick() }
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(128.dp)
                                .clip(CircleShape)
                                .clickable { onImagePick() }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Dynamically create fields based on profile data
                updatedProfile.forEach { (key, value) ->
                    var textValue by remember(value) { mutableStateOf(value?.toString() ?: "") }
                    if (key !in listOf("id", "user_id", "email", "password", "profile_picture")) { // Fields to exclude
                        when (key) {
                            "fitness_goals" -> {
                                val goals = remember { mutableStateListOf<String>().apply { addAll(value as? List<String> ?: emptyList()) } }
                                val allGoals = listOf("Weight Loss", "Muscle Gain", "Cardio Fitness", "Flexibility", "Stress Relief")
                                Text("Fitness Goals", style = MaterialTheme.typography.titleMedium)
                                FlowRow {
                                    allGoals.forEach { goal ->
                                        val isSelected = goals.contains(goal)
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = {
                                                if (isSelected) goals.remove(goal) else goals.add(goal)
                                                updatedProfile[key] = goals.toList()
                                            },
                                            label = { Text(goal) },
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                    }
                                }
                            }
                            else -> {
                                OutlinedTextField(
                                    value = textValue,
                                    onValueChange = {
                                        textValue = it
                                        updatedProfile[key] = it
                                    },
                                    label = { Text(key.replaceFirstChar { it.uppercase() }) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Button(
                    onClick = {
                        val multipartBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
                        updatedProfile.forEach { (key, value) ->
                            if (value is List<*>) {
                                value.forEach { item ->
                                    multipartBodyBuilder.addFormDataPart(key, item.toString())
                                }
                            } else {
                                multipartBodyBuilder.addFormDataPart(key, value.toString())
                            }
                        }

                        imageUri?.let { uri ->
                            context.contentResolver.openInputStream(uri)?.let { inputStream ->
                                val file = File(context.cacheDir, "profile_picture.jpg")
                                FileOutputStream(file).use { outputStream ->
                                    inputStream.copyTo(outputStream)
                                }
                                multipartBodyBuilder.addFormDataPart(
                                    "profile_picture",
                                    file.name,
                                    file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                                )
                            }
                        }

                        RetrofitClient.instance.updateProfile(email, multipartBodyBuilder.build()).enqueue(object : Callback<ResponseBody> {
                            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                if (response.isSuccessful) {
                                    Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                                    (context as? Activity)?.finish()
                                } else {
                                    Toast.makeText(context, "Failed to update profile.", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Changes")
                }
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
