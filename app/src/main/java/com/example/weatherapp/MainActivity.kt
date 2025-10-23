package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call

import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor


class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        Log.d("DEBUG", "MainActivity started")
        fetchWeatherData("Jaipur") //El nombre de la city XD
        SearchCity()


    }

    private fun SearchCity() {
        val searchview=binding.searchView
        searchview.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })


    }


    private fun fetchWeatherData(city_name: String) {
        Log.d("DEBUG", "fetchWeatherData called for $city_name")

        val logging = HttpLoggingInterceptor { message ->
            Log.d("API_LOG", message)
        }
        logging.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiInterface::class.java)

        val call = retrofit.getWeatherData(
            city_name,
            "2553a908ba48d63dd91ba765c83cacad",
            "metric"
        )

        call.enqueue(object : Callback<WeatherApp> {
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    Log.d("API_RESPONSE", "✅ Data OK: Temp=${data.main.temp}, City=$city_name")

                    // -------------------------------
                    // ACTUALIZAR UI AQUÍ 👇
                    // -------------------------------
                    val temperature = data.main.temp.toString()
                    val humidity = data.main.humidity.toString()
                    val windSpeed = data.wind.speed
                    val sunRise = data.sys.sunrise.toLong()
                    val sunSet = data.sys.sunset.toLong()
                    val seaLevel = data.main.pressure
                    val condition = data.weather.firstOrNull()?.main ?: "Unknown"
                    val maxTemp = data.main.temp_max
                    val minTemp = data.main.temp_min

                    binding.temp.text = "$temperature °C"
                    binding.weather.text = condition
                    binding.maxTemp.text = "Max: $maxTemp °C"
                    binding.minTemp.text = "Min: $minTemp °C"
                    binding.humidity.text = "$humidity %"
                    binding.windSpeed.text = "$windSpeed m/s"
                    binding.sunrise.text = time(sunRise)
                    binding.sunset.text = time(sunSet)
                    binding.sea.text = "$seaLevel hpa"
                    binding.condition.text = condition
                    binding.cityName.text = city_name
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.date.text = date()

                    changeIMagesAccordingToWeatherCondition(condition)
                    // -------------------------------
                } else {
                    val errBody = response.errorBody()?.string()
                    Log.e("API_ERROR", "❌ Code=${response.code()}, Body=$errBody")
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                Log.e("API_FAILURE", "🚨 Error=${t.message}")
            }
        })
    }
    private fun changeIMagesAccordingToWeatherCondition(conditions:String) {
        when(conditions){

            "Clear","Sunny","Clear Sky"->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Haze","Partly Clouds","Mist","Clouds","Foggy","OverCast"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Light Rain","Rain","Thunderstorm","Drizzle","Moderate Rain","Showers","Heavy Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow","Snow","Moderate Snow","Heavy Snow","Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)}
        }
        binding.lottieAnimationView.playAnimation()
    }

    fun dayName(timeStamp:Long): String{
        val sdf=SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
    private fun date():String {
        val sdf=SimpleDateFormat("dd mm yyyy", Locale.getDefault())
        return sdf.format((Date()))

    }
    private fun time(timeStamp:Long):String {
        val sdf=SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timeStamp*1000)))

    }


}



//{"coord":{"lon":75.8167,"lat":26.9167},"weather":[{"id":721,"main":"Haze","description":"haze","icon":"50n"}],"base":"stations","main":{"temp":297.77,"feels_like":297.59,"temp_min":297.77,"temp_max":297.77,"pressure":1010,"humidity":50,"sea_level":1010,"grnd_level":961},"visibility":3000,"wind":{"speed":0,"deg":0},"clouds":{"all":20},"dt":1729367501,"sys":{"type":1,"id":9170,"country":"IN","sunrise":1729385937,"sunset":1729427023},"timezone":19800,"id":1269515,"name":"Jaipur","cod":200}
//



//https://api.openweathermap.org/data/2.5/weather?q=jaipur&appid=2553a908ba48d63dd91ba765c83cacad
//{"coord":{"lon":75.8167,"lat":26.9167},"weather":[{"id":721,"main":"Haze","description":"haze","icon":"50n"}],"base":"stations","main":{"temp":298.77,"feels_like":298.54,"temp_min":298.77,"temp_max":298.77,"pressure":1012,"humidity":44,"sea_level":1012,"grnd_level":963},"visibility":3000,"wind":{"speed":1.54,"deg":0},"clouds":{"all":40},"dt":1729531438,"sys":{"type":1,"id":9170,"country":"IN","sunrise":1729472373,"sunset":1729513369},"timezone":19800,"id":1269515,"name":"Jaipur","cod":200}
