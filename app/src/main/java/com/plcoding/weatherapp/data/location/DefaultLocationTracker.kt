package com.plcoding.weatherapp.data.location

import android.app.Application
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.plcoding.weatherapp.domain.location.LocationTracker
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class DefaultLocationTracker @Inject constructor(
    private val locationClient: FusedLocationProviderClient,
    private val application: Application
) : LocationTracker {
    override suspend fun getLocation(): Location? {
        val hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(
            application,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        val hasAccessCourseLocationPermission = ContextCompat.checkSelfPermission(
            application,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        val locationManager =
            application.getSystemService(android.content.Context.LOCATION_SERVICE) as android.location.LocationManager
        val isGpsEnabled =
            locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER) ||
                    locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
        if (!hasAccessCourseLocationPermission || !hasAccessFineLocationPermission || !isGpsEnabled) {
            return null
        }
        return suspendCancellableCoroutine { count ->
            locationClient.lastLocation.apply {
                if (isComplete) {
                    if (isSuccessful)
                        count.resume(result)
                    else
                        count.resume(null)
                    return@suspendCancellableCoroutine
                }
                addOnSuccessListener {
                    count.resume(it)
                }
                addOnFailureListener {
                    count.resume(null)
                }
                addOnCanceledListener {
                    count.cancel()
                }
            }
        }
    }
}
