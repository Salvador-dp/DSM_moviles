package com.example.weatherapp

data class Astro(
    // WeatherAPI.com devuelve la hora como String, ej: "06:15 AM"
    val sunrise: String,
    val sunset: String
)