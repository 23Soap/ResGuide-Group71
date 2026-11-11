package com.example.resguidegroup71.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "restaurants")
data class Restaurant(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val city: String,
    val postalCode: String,
    val address: String,
    val phone: String,
    val comment: String,
    val tags: String,
    val rating: Int, // 1 to 5
    val latitude: Double? = null,
    val longitude: Double? = null
) : Parcelable
