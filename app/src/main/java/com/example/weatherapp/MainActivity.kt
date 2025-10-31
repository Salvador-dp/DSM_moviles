package com.example.weatherapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

// 👇 IMPORTACIONES DE CLASES DE DATOS
import com.example.weatherapp.WeatherAppResponse
import com.example.weatherapp.ForecastDay
import com.example.weatherapp.Astro
import com.example.weatherapp.Day
// 👆 FIN DE IMPORTACIONES DE CLASES DE DATOS


class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    // ⚠️ Importante: Reemplaza con tu clave de WeatherAPI.com
    private val API_KEY = "aee3225c8fbf48d69ea34830252910"

    // ⚠️ URL Base para WeatherAPI.com con HTTPS
    private val BASE_URL = "https://api.weatherapi.com/v1/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        Log.d("DEBUG", "MainActivity started")

        // Carga el clima de la ciudad por defecto al iniciar
        fetchWeatherData("San Salvador")
        SearchCity()
    }

    private fun SearchCity() {
        val searchView = binding.searchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    // Llama a la API con el nombre de la ciudad ingresada
                    fetchWeatherData(query.trim())

                    // Oculta el teclado y quita el foco del SearchView
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }


    private fun fetchWeatherData(city_name: String) {
        // 1. Validar conexión a Internet antes de la llamada a la API
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "🚨 Sin conexión a Internet. Por favor, revisa tu red.", Toast.LENGTH_LONG).show()
            Log.e("NETWORK_ERROR", "No hay conexión a Internet. Cancelando solicitud.")
            return // Detiene la función si no hay red
        }

        Log.d("DEBUG", "Buscando clima para: $city_name")

        val formattedCity = city_name.trim().replace(" ", "%20")

        val logging = HttpLoggingInterceptor { message ->
            Log.d("API_LOG", message)
        }
        logging.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiInterface::class.java)

        // Llamada a la API con los nuevos parámetros (API Key y idioma español)
        val call = retrofit.getWeatherData(
            API_KEY,
            formattedCity,
            "es"
        )

        call.enqueue(object : Callback<WeatherAppResponse> {
            override fun onResponse(call: Call<WeatherAppResponse>, response: Response<WeatherAppResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    Log.d("API_RESPONSE", "✅ Ciudad: $city_name | Temp: ${data.current.tempC} °C")

                    // 🌟 Extracción de datos
                    val temperature = data.current.tempC.toString()
                    val humidity = data.current.humidity.toString()
                    val windSpeed = data.current.windKph
                    val seaLevel = data.current.pressureMb
                    val condition = data.current.condition.text

                    val maxTemp = data.forecast.forecastday.firstOrNull()?.day?.maxtempC ?: data.current.tempC
                    val minTemp = data.forecast.forecastday.firstOrNull()?.day?.mintempC ?: data.current.tempC

                    val sunRise = data.forecast.forecastday.firstOrNull()?.astro?.sunrise ?: "--"
                    val sunSet = data.forecast.forecastday.firstOrNull()?.astro?.sunset ?: "--"

                    binding.temp.text = "$temperature °C"
                    binding.weather.text = condition
                    binding.maxTemp.text = "Max: $maxTemp °C"
                    binding.minTemp.text = "Min: $minTemp °C"
                    binding.humidity.text = "$humidity %"
                    binding.windSpeed.text = "$windSpeed kph"
                    binding.sunrise.text = sunRise
                    binding.sunset.text = sunSet
                    binding.sea.text = "$seaLevel mb"
                    binding.condition.text = condition
                    binding.cityName.text = data.location.name
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.date.text = date()

                    changeIMAGESAccordingToWeatherCondition(condition)
                } else {
                    // 2. Manejo de error: Ciudad no encontrada (código 400, por ejemplo)
                    Log.e("API_ERROR", "❌ Código: ${response.code()} - ${response.message()}")
                    binding.cityName.text = "Ciudad no encontrada 😢"
                    binding.temp.text = "--"
                    Toast.makeText(this@MainActivity, "😢 Ciudad no encontrada. Verifica la ortografía.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<WeatherAppResponse>, t: Throwable) {
                // 3. Manejo de error: Fallo de conexión o servidor
                Log.e("API_FAILURE", "🚨 Error=${t.message}")
                Toast.makeText(this@MainActivity, "🚨 Error de conexión. Revisa tu red o el servidor.", Toast.LENGTH_LONG).show()
            }
        })
    }

    // Función para verificar la conexión a Internet
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }


    private fun changeIMAGESAccordingToWeatherCondition(conditions:String) {
        // Usamos toLowerCase() por si las mayúsculas/minúsculas varían
        when(conditions.lowercase(Locale.ROOT)){

            // ☀️ DÍAS SOLEADOS y DESPEJADOS (Solo si está totalmente limpio)
            "despejado", "soleado" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

            // ☁️ DÍAS PARCIALMENTE NUBLADOS Y GRISES (Condiciones intermedias)
            "parcialmente nublado", "nublado", "cubierto", "niebla", "neblina", "cielo cubierto" -> {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }


            // 🌧️ LLUVIA Y CHUBASCOS
            "lluvia ligera", "lluvia moderada", "lluvia intensa", "fuertes lluvias", // 👈 ¡Añadida "fuertes lluvias"!
            "posibilidad de lluvia en zonas", "llovizna ligera",
            "chubascos de lluvia moderada o fuerte" -> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }

            // ⚡️ TORMENTAS
            "posibilidad de tormentas", "lluvia ligera con truenos",
            "lluvia moderada o intensa con truenos" -> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }

            // ❄️ NIEVE
            "nieve ligera", "nieve moderada", "nieve fuerte", "ventisca",
            "aguanieve ligera", "aguanieve moderada o fuerte" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }

            // ❓ POR DEFECTO
            else -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
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
