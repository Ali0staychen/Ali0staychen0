package com.journey.replica.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

data class WeatherInfo(val tempC: Double?, val description: String?, val icon: String?, val place: String?)

object WeatherService {
    private val client = OkHttpClient()

    suspend fun fetch(lat: Double, lon: Double): WeatherInfo? = withContext(Dispatchers.IO) {
        try {
            val url = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current=temperature_2m,weather_code"
            val req = Request.Builder().url(url).build()
            client.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) return@withContext null
                val body = resp.body?.string() ?: return@withContext null
                val json = JSONObject(body)
                val current = json.optJSONObject("current") ?: return@withContext null
                val temp = current.optDouble("temperature_2m")
                val code = current.optInt("weather_code")
                WeatherInfo(
                    tempC = if (temp.isNaN()) null else temp,
                    description = weatherCodeToText(code),
                    icon = code.toString(),
                    place = null
                )
            }
        } catch (_: Exception) { null }
    }

    private fun weatherCodeToText(code: Int): String = when (code) {
        0 -> "Clear sky"
        1, 2, 3 -> "Partly cloudy"
        45, 48 -> "Fog"
        51, 53, 55 -> "Drizzle"
        61, 63, 65 -> "Rain"
        71, 73, 75 -> "Snow"
        80, 81, 82 -> "Rain showers"
        95 -> "Thunderstorm"
        else -> "Cloudy"
    }
}
