package com.example.resguidegroup71.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.resguidegroup71.data.Restaurant
import com.example.resguidegroup71.data.RestaurantDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RestaurantViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = RestaurantDatabase.getDatabase(application).restaurantDao()

    val allRestaurants: StateFlow<List<Restaurant>> = dao.getAllRestaurants()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(restaurant: Restaurant) = viewModelScope.launch(Dispatchers.IO) {
        dao.insertRestaurant(restaurant)
    }

    fun update(restaurant: Restaurant) = viewModelScope.launch(Dispatchers.IO) {
        dao.updateRestaurant(restaurant)
    }

    fun delete(restaurant: Restaurant) = viewModelScope.launch(Dispatchers.IO) {
        dao.deleteRestaurant(restaurant)
    }

    fun getRestaurantById(id: Int): Flow<Restaurant?> {
        return dao.getRestaurantById(id)
    }
}
