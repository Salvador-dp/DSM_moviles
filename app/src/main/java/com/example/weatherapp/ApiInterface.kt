// ApiInterface.kt
package com.example.weatherapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("forecast.json")
    fun getWeatherData(
        @Query("key") apiKey: String,
        @Query("q") city: String,
        @Query("lang") lang: String, // 👈 ¡NUEVO PARÁMETRO DE IDIOMA!
        @Query("days") days: Int = 1,
        @Query("aqi") aqi: String = "no"
    ): Call<WeatherAppResponse>
}