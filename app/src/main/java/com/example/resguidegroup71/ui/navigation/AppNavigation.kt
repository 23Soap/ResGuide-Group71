package com.example.resguidegroup71.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.resguidegroup71.ui.screens.AboutScreen
import com.example.resguidegroup71.ui.screens.AddEditRestaurantScreen
import com.example.resguidegroup71.ui.screens.MainMenuScreen
import com.example.resguidegroup71.ui.screens.MapScreen
import com.example.resguidegroup71.ui.screens.RestaurantDetailsScreen
import com.example.resguidegroup71.ui.screens.SplashScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(onSplashFinished = {
                navController.navigate("main_menu") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }
        composable("main_menu") {
            MainMenuScreen(
                onNavigateToAddEdit = {
                    navController.navigate("add_edit")
                },
                onNavigateToDetails = { id ->
                    navController.navigate("details/$id")
                },
                onNavigateToMap = {
                    navController.navigate("map")
                },
                onNavigateToAbout = {
                    navController.navigate("about")
                }
            )
        }
        composable("add_edit?id={id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
            AddEditRestaurantScreen(
                restaurantId = id,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("details/{id}") { backStackEntry ->
            val idStr = backStackEntry.arguments?.getString("id")
            val id = idStr?.toIntOrNull()
            if (id != null) {
                RestaurantDetailsScreen(
                    restaurantId = id,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToEdit = { editId ->
                        navController.navigate("add_edit?id=$editId")
                    }
                )
            }
        }
        composable("map") {
            MapScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("about") {
            AboutScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
