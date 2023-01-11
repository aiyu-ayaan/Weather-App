package com.plcoding.weatherapp.domain.weather

import androidx.annotation.Keep
import java.time.LocalDateTime

@Keep
data class WeatherInfo(
    val weatherDataPerData: Map<Int, List<WeatherData>>,
    val currentWeatherDat: WeatherData?
)

@Keep
data class WeatherData(
    val time: LocalDateTime,
    val temperatureCelsius: Double,
    val pressure: Double,
    val windSpeed: Double,
    val humidity: Double,
    val weatherType: WeatherType
)