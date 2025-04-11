# MapsActivity Technical Documentation

[切换到中文版](MapsActivity_Documentation_CN.md)

## Table of Contents
1. [Overview](#overview)
2. [Core Components](#core-components)
3. [Main Functionality](#main-functionality)
4. [Implementation Details](#implementation-details)
5. [Location Services](#location-services)
6. [Route Simulation](#route-simulation)
7. [Error Handling](#error-handling)
8. [Logging System](#logging-system)
9. [Performance Optimization](#performance-optimization)
10. [User Interface](#user-interface)

## Overview
MapsActivity is an Android activity based on Google Maps SDK, providing real-time location tracking, path drawing, and location simulation features. It uses ViewBinding for view binding and FusedLocationProviderClient for location service management.

## Core Components

### Class Properties
```kotlin
private val pathPoints = mutableListOf<LatLng>()  // Store path points
private var polyline: Polyline? = null            // Path line object
private lateinit var mMap: GoogleMap             // Map object
private lateinit var binding: ActivityMapsBinding // View binding
private lateinit var fusedLocationClient: FusedLocationProviderClient // Location service client
private lateinit var locationCallback: LocationCallback // Location callback
private var currentLocationMarker: Marker? = null // Current location marker
private var isTrackingEnabled = true             // Tracking status
```

## Main Functionality

### Initialization Flow
```mermaid
flowchart TD
    A[onCreate] --> B[Initialize View Binding]
    B --> C[Initialize Location Client]
    C --> D[Get Map Fragment]
    D --> E[Set Button Listeners]
    E --> F[Wait for Map Ready]
    F --> G[onMapReady]
    G --> H{Check Permissions}
    H -->|Granted| I[Initialize Location Service]
    H -->|Denied| J[Request Permissions]
```

### Location Service Initialization
```mermaid
flowchart TD
    A[setupLocationServices] --> B{Check Permissions}
    B -->|Failed| C[Notify User]
    B -->|Success| D{Check GPS}
    D -->|Off| E[Prompt to Enable GPS]
    D -->|On| F[Configure Map UI]
    F --> G[Set Location Request]
    G --> H[Register Location Callback]
    H --> I[Request Location Updates]
```

## Implementation Details

### Location Tracking
```kotlin
val locationRequest = LocationRequest.Builder(
    Priority.PRIORITY_HIGH_ACCURACY, 2000L
)
    .setMinUpdateDistanceMeters(1f)
    .setGranularity(Granularity.GRANULARITY_FINE)
    .setWaitForAccurateLocation(true)
    .build()
```
- Update Interval: 2 seconds
- Minimum Distance: 1 meter
- Accuracy: High
- Wait for Accurate Location

### Path Drawing
```kotlin
private fun updatePolyline() {
    polyline?.remove()
    if (pathPoints.size > 1) {
        polyline = mMap.addPolyline(
            PolylineOptions()
                .addAll(pathPoints)
                .color(android.graphics.Color.BLUE)
                .width(8f)
        )
    }
}
```

### Location Marker Update
```kotlin
private fun updateLocationMarker(latLng: LatLng) {
    if (currentLocationMarker == null) {
        currentLocationMarker = mMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title("Current Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        )
    } else {
        currentLocationMarker?.position = latLng
    }
}
```

## Location Services

### Multi-location Strategy
```mermaid
sequenceDiagram
    participant A as Activity
    participant L as LocationClient
    participant G as GPS
    participant N as Network
    A->>L: Request Location Update
    L->>G: Try GPS Location
    alt GPS Success
        G->>L: Return Location
        L->>A: Update UI
    else GPS Failure
        L->>N: Try Network Location
        N->>L: Return Location
        L->>A: Update UI
    end
```

### Location Update Process
1. Try to get last known location
2. If failed, start high-accuracy GPS location
3. If GPS location fails, fallback to network location
4. Continue updating location information

## Route Simulation

### Simulation Points
```kotlin
val simulatedPoints = listOf(
    LatLng(39.916345, 116.397155), // Forbidden City
    LatLng(39.908722, 116.397499), // Tiananmen Square
    LatLng(39.900269, 116.397872), // Qianmen
    LatLng(39.893, 116.397)        // Near Temple of Heaven
)
```

### Simulation Animation Flow
```mermaid
flowchart TD
    A[Start Simulation] --> B[Clear Old Data]
    B --> C[Set Simulation Points]
    C --> D[Create Handler]
    D --> E[Update Location Periodically]
    E --> F[Update Path]
    F --> G[Update Marker]
    G --> H[Move Camera]
    H --> E
```

## Error Handling

### Permission Handling
```kotlin
private fun hasLocationPermissions(): Boolean {
    val fine = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
    val coarse = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
    return fine == PackageManager.PERMISSION_GRANTED && 
           coarse == PackageManager.PERMISSION_GRANTED
}
```

### Exception Handling
- SecurityException: Permission-related exceptions
- Location service not enabled
- Location acquisition failure
- Network location failure

## Logging System

### Log Tags
Use "MAP" tag for key information:
- Initialization status
- Permission status
- Location updates
- Error messages

### Log Levels
- D: Debug information
- E: Error information
- I: Important status changes

## Performance Optimization

### Location Update Optimization
- Minimum update distance limit
- Smart power management
- Location accuracy balance

### Memory Management
- Timely cleanup of location update listeners
- Appropriate path point storage strategy

## User Interface

### Map Controls
- Location button
- Zoom controls
- Compass
- Manual refresh button
- Route simulation button

### Interaction Feedback
- Toast message notifications
- Location update animations
- Path drawing animations 