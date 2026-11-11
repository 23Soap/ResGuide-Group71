package com.example.resguidegroup71.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.resguidegroup71.viewmodel.RestaurantViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailsScreen(
    restaurantId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    viewModel: RestaurantViewModel = viewModel()
) {
    val restaurant by viewModel.getRestaurantById(restaurantId).collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Restaurant Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (restaurant != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = restaurant!!.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                
                DetailRow(label = "City:", value = restaurant!!.city)
                DetailRow(label = "Postal Code:", value = restaurant!!.postalCode)
                DetailRow(label = "Address:", value = restaurant!!.address)
                DetailRow(label = "Phone:", value = restaurant!!.phone)
                DetailRow(label = "Tags:", value = restaurant!!.tags)
                DetailRow(label = "Rating:", value = "${restaurant!!.rating} / 5")
                
                Text(text = "Comments:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(text = restaurant!!.comment, style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { onNavigateToEdit(restaurantId) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Edit")
                    }
                    
                    Button(
                        onClick = {
                            viewModel.delete(restaurant!!)
                            onNavigateBack()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Delete")
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        Text(text = value, modifier = Modifier.weight(2f))
    }
}
