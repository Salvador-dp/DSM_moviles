package com.example.weatherapp

import com.google.gson.annotations.SerializedName

data class Current(
    // Temperatura en Celsius
    @SerializedName("temp_c") val tempC: Double,

    // Velocidad del viento en kilómetros por hora (kph)
    @SerializedName("wind_kph") val windKph: Double,

    val humidity: Int,

    // Presión en milibares (mb), se usa para "Sea Level"
    @SerializedName("pressure_mb") val pressureMb: Double,

    val condition: Condition
)