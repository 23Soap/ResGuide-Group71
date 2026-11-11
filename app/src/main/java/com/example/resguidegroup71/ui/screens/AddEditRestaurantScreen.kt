package com.example.resguidegroup71.ui.screens

import android.location.Geocoder
import android.widget.Toast
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.resguidegroup71.data.Restaurant
import com.example.resguidegroup71.viewmodel.RestaurantViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditRestaurantScreen(
    restaurantId: Int?,
    onNavigateBack: () -> Unit,
    viewModel: RestaurantViewModel = viewModel()
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var rating by remember { mutableFloatStateOf(3f) }

    if (restaurantId != null) {
        val restaurant by viewModel.getRestaurantById(restaurantId).collectAsState(initial = null)
        LaunchedEffect(restaurant) {
            restaurant?.let {
                name = it.name
                city = it.city
                postalCode = it.postalCode
                address = it.address
                phone = it.phone
                comment = it.comment
                tags = it.tags
                rating = it.rating.toFloat()
            }
        }
    }

    Scaffold(
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
            })
        },
        topBar = {
            TopAppBar(
                title = { Text(if (restaurantId == null) "Add Restaurant" else "Edit Restaurant") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Restaurant Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("City") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = postalCode, onValueChange = { postalCode = it }, label = { Text("Postal Code") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = tags, onValueChange = { tags = it }, label = { Text("Tags (e.g. Vegan, Italian)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = comment, onValueChange = { comment = it }, label = { Text("Comment") }, modifier = Modifier.fillMaxWidth(), minLines = 3)

            Spacer(modifier = Modifier.height(8.dp))
            Text("Rating: ${rating.toInt()} / 5")
            Slider(
                value = rating,
                onValueChange = { rating = it },
                valueRange = 1f..5f,
                steps = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (name.isBlank() || city.isBlank() || postalCode.isBlank() || 
                        address.isBlank() || phone.isBlank() || tags.isBlank() || comment.isBlank()) {
                        Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    } else {
                        coroutineScope.launch {
                            var lat: Double? = null
                            var lon: Double? = null
                            
                            // Convert the entered address to Coordinates using Geocoder in the background
                            val fullAddress = "$address, $city, $postalCode"
                            withContext(Dispatchers.IO) {
                                try {
                                    val geocoder = Geocoder(context, Locale.getDefault())
                                    @Suppress("DEPRECATION")
                                    val addresses = geocoder.getFromLocationName(fullAddress, 1)
                                    if (!addresses.isNullOrEmpty()) {
                                        lat = addresses[0].latitude
                                        lon = addresses[0].longitude
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }

                            val restaurant = Restaurant(
                                id = restaurantId ?: 0,
                                name = name,
                                city = city,
                                postalCode = postalCode,
                                address = address,
                                phone = phone,
                                comment = comment,
                                tags = tags,
                                rating = rating.toInt(),
                                latitude = lat,
                                longitude = lon
                            )
                            
                            if (restaurantId == null) {
                                viewModel.insert(restaurant)
                            } else {
                                viewModel.update(restaurant)
                            }
                            
                            // Let the user know if coordinates could not be resolved, but save anyway
                            if (lat == null || lon == null) {
                                Toast.makeText(context, "Saved, but exact location on map couldn't be found.", Toast.LENGTH_LONG).show()
                            }
                            onNavigateBack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Restaurant")
            }
        }
    }
}
