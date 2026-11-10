package com.journey.replica.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await
import java.util.Locale

data class LocationResult(val lat: Double, val lng: Double, val address: String?)

object LocationUtils {
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(context: Context): LocationResult? {
        return try {
            val client = LocationServices.getFusedLocationProviderClient(context)
            val cts = CancellationTokenSource()
            val loc: Location? = client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cts.token).await()
                ?: client.lastLocation.await()
            loc?.let {
                val addr = reverseGeocode(context, it.latitude, it.longitude)
                LocationResult(it.latitude, it.longitude, addr)
            }
        } catch (_: Exception) { null }
    }

    fun reverseGeocode(context: Context, lat: Double, lng: Double): String? {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            @Suppress("DEPRECATION")
            val list = geocoder.getFromLocation(lat, lng, 1)
            val a = list?.firstOrNull()
            listOfNotNull(a?.featureName, a?.locality, a?.adminArea, a?.countryName)
                .distinct().joinToString(", ").ifBlank { null }
        } catch (_: Exception) { null }
    }
}
