package com.example.resguidegroup71.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.resguidegroup71.data.Restaurant
import com.example.resguidegroup71.viewmodel.RestaurantViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(
    onNavigateToAddEdit: () -> Unit,
    onNavigateToDetails: (Int) -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToAbout: () -> Unit,
    viewModel: RestaurantViewModel = viewModel()
) {
    var showMenu by remember { mutableStateOf(false) }
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    
    val restaurantList by viewModel.allRestaurants.collectAsState()

    // Filter list based on Name or Tags
    val filteredList = if (searchQuery.isBlank()) {
        restaurantList
    } else {
        restaurantList.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.tags.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            if (isSearchActive) {
                TopAppBar(
                    title = {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search by name or tag...") },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            isSearchActive = false
                            searchQuery = ""
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Close Search")
                        }
                    },
                    actions = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear text")
                            }
                        }
                    }
                )
            } else {
                TopAppBar(
                    title = { Text("ResGuide") },
                    navigationIcon = {
                        IconButton(onClick = { showMenu = !showMenu }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("About Us") },
                                onClick = {
                                    showMenu = false
                                    onNavigateToAbout()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Info, contentDescription = "About Us")
                                }
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                        IconButton(onClick = onNavigateToMap) {
                            Icon(Icons.Default.LocationOn, contentDescription = "Map")
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (!isSearchActive) {
                FloatingActionButton(onClick = { onNavigateToAddEdit() }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Restaurant")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (filteredList.isEmpty()) {
                Text(
                    text = if (searchQuery.isNotEmpty()) "No results found." else "No restaurants found. Tap + to add one.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredList) { restaurant ->
                        RestaurantItem(
                            restaurant = restaurant,
                            onClick = { onNavigateToDetails(restaurant.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RestaurantItem(
    restaurant: Restaurant,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = restaurant.name, style = MaterialTheme.typography.titleLarge)
                Text(text = "${restaurant.city} - ${restaurant.rating}/5 Stars")
                Text(text = restaurant.tags, style = MaterialTheme.typography.bodySmall)
            }
            
            // "Get directions" button on the far right
            Button(
                onClick = {
                    // Start Google Maps (or any map app) intent
                    val uri = Uri.parse("google.navigation:q=${restaurant.latitude},${restaurant.longitude}")
                    val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    
                    // If Google maps is not installed, fallback to general geo intent
                    if (mapIntent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(mapIntent)
                    } else {
                        val genericUri = Uri.parse("geo:${restaurant.latitude},${restaurant.longitude}?q=${restaurant.latitude},${restaurant.longitude}(${restaurant.name})")
                        val genericIntent = Intent(Intent.ACTION_VIEW, genericUri)
                        context.startActivity(genericIntent)
                    }
                },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(text = "Get directions", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
