package com.example.weatherapp

import com.google.gson.annotations.SerializedName

data class Day(
    @SerializedName("maxtemp_c") val maxtempC: Double,
    @SerializedName("mintemp_c") val mintempC: Double
)