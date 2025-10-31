package com.example.weatherapp

import com.google.gson.annotations.SerializedName

data class WeatherAppResponse(
    val location: Location,
    val current: Current,
    val forecast: Forecast
)