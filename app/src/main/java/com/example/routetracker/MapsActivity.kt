package com.example.routetracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.routetracker.databinding.ActivityMapsBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private val pathPoints = mutableListOf<LatLng>()
    private var polyline: Polyline? = null
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var currentLocationMarker: Marker? = null
    private var isTrackingEnabled = true

    private val locationPermissionRequestCode = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("MAP", "onCreate 初始化")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.btnRefreshLocation.setOnClickListener {
            Log.d("MAP", "🟡 点击了手动刷新位置按钮")
            requestSingleLocation()
        }
        binding.btnSimulateRoute.setOnClickListener {
            Log.d("MAP", "🟢 开始模拟路线动画")

            // 清空旧路径和标记
            pathPoints.clear()
            currentLocationMarker?.remove()
            polyline?.remove()

            // 模拟点（可按需扩展）
            val simulatedPoints = listOf(
                LatLng(39.916345, 116.397155), // 故宫
                LatLng(39.908722, 116.397499), // 天安门
                LatLng(39.900269, 116.397872), // 前门
                LatLng(39.893, 116.397)        // 天坛边
            )

            // 开始模拟移动
            var index = 0
            val handler = android.os.Handler(Looper.getMainLooper())

            val runnable = object : Runnable {
                override fun run() {
                    if (index >= simulatedPoints.size) return

                    val point = simulatedPoints[index]
                    Log.d("MAP", "🚶 模拟移动到：$point")

                    // 更新路径与标记
                    pathPoints.add(point)
                    updatePolyline()
                    updateLocationMarker(point)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 16f))

                    index++
                    handler.postDelayed(this, 1000L) // 每隔 1 秒移动一次
                }
            }

            handler.post(runnable)
        }


    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d("MAP", "✅ 地图准备好了")
        Toast.makeText(this, "地图加载成功", Toast.LENGTH_SHORT).show()
        mMap = googleMap

        if (hasLocationPermissions()) {
            Log.d("MAP", "✅ 权限已授予，开始初始化位置服务")
            setupLocationServices()
        } else {
            Log.d("MAP", "❌ 未获取权限，发起权限请求")
            requestLocationPermissions()
        }

        mMap.setOnCameraMoveStartedListener { reason ->
            if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                isTrackingEnabled = false
                Log.d("MAP", "📌 用户手动操作地图，停止跟踪")
            }
        }
    }

    private fun hasLocationPermissions(): Boolean {
        val fine = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        Log.d("MAP", "权限检查 -> Fine: $fine, Coarse: $coarse")
        return fine == PackageManager.PERMISSION_GRANTED && coarse == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermissions() {
        Log.d("MAP", "请求定位权限")
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            locationPermissionRequestCode
        )
    }

    private fun setupLocationServices() {
        Log.d("MAP", "开始设置定位服务")
        try {
            if (!hasLocationPermissions()) {
                Log.e("MAP", "权限仍未授予，无法设置定位")
                Toast.makeText(this, "请授予位置权限", Toast.LENGTH_LONG).show()
                return
            }

            // 检查位置服务是否开启
            val locationManager = getSystemService(LOCATION_SERVICE) as android.location.LocationManager
            if (!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
                Log.e("MAP", "GPS未开启")
                Toast.makeText(this, "请开启GPS定位服务", Toast.LENGTH_LONG).show()
                return
            }

            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = true
            mMap.uiSettings.isZoomControlsEnabled = true
            mMap.uiSettings.isCompassEnabled = true

            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 2000L
            )
                .setMinUpdateDistanceMeters(1f)
                .setGranularity(Granularity.GRANULARITY_FINE)
                .setWaitForAccurateLocation(true)
                .build()

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    Log.d("MAP", "📍 onLocationResult 被调用")
                    val location = locationResult.lastLocation
                    if (location != null) {
                        val latLng = LatLng(location.latitude, location.longitude)
                        Log.d("MAP", "📍 实时定位：$latLng, 精度: ${location.accuracy}米")

                        updateLocationMarker(latLng)

                        if (isTrackingEnabled) {
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                        }

                        pathPoints.add(latLng)
                        updatePolyline()
                    } else {
                        Log.e("MAP", "⚠️ 位置为 null")
                        Toast.makeText(this@MapsActivity, "无法获取位置信息", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            ).addOnSuccessListener {
                Log.d("MAP", "✅ requestLocationUpdates 注册成功")
                Toast.makeText(this, "位置服务已启动", Toast.LENGTH_SHORT).show()
                requestSingleLocation()
            }.addOnFailureListener {
                Log.e("MAP", "❌ 注册失败：${it.message}", it)
                Toast.makeText(this, "位置服务启动失败: ${it.message}", Toast.LENGTH_LONG).show()
            }

            // 尝试获取最后已知位置
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val latLng = LatLng(location.latitude, location.longitude)
                        Log.d("MAP", "📍 lastLocation 用于立刻跳转：$latLng")
                        updateLocationMarker(latLng)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                    } else {
                        Log.d("MAP", "⚠️ lastLocation 为 null，等待 onLocationResult")
                        // 如果最后位置为空，尝试使用网络定位
                        val networkLocationRequest = LocationRequest.Builder(
                            Priority.PRIORITY_BALANCED_POWER_ACCURACY, 5000L
                        ).build()
                        
                        fusedLocationClient.requestLocationUpdates(
                            networkLocationRequest,
                            object : LocationCallback() {
                                override fun onLocationResult(locationResult: LocationResult) {
                                    val location = locationResult.lastLocation
                                    if (location != null) {
                                        val latLng = LatLng(location.latitude, location.longitude)
                                        Log.d("MAP", "📍 网络定位成功：$latLng")
                                        updateLocationMarker(latLng)
                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                                        fusedLocationClient.removeLocationUpdates(this)
                                    }
                                }
                            },
                            Looper.getMainLooper()
                        )
                    }
                }
                .addOnFailureListener {
                    Log.e("MAP", "❌ 获取最后位置失败：${it.message}", it)
                }

        } catch (e: SecurityException) {
            Log.e("MAP", "❌ 权限异常: ${e.message}")
            Toast.makeText(this, "位置权限异常: ${e.message}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e("MAP", "❌ 其他异常: ${e.message}", e)
            Toast.makeText(this, "初始化位置服务失败: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateLocationMarker(latLng: LatLng) {
        if (currentLocationMarker == null) {
            currentLocationMarker = mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("当前位置")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            )
        } else {
            currentLocationMarker?.position = latLng
        }
    }

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

    private fun requestSingleLocation() {
        if (!hasLocationPermissions()) {
            Log.d("MAP", "❌ 权限未授予，无法获取单次定位")
            requestLocationPermissions()
            return
        }

        isTrackingEnabled = true

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 0L
        ).setMaxUpdates(1).build()

        Log.d("MAP", "📡 请求单次定位")

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    Log.d("MAP", "📍 单次定位成功")
                    fusedLocationClient.removeLocationUpdates(this)
                    val location = result.lastLocation
                    if (location != null) {
                        val latLng = LatLng(location.latitude, location.longitude)
                        updateLocationMarker(latLng)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                        pathPoints.add(latLng)
                        updatePolyline()
                    } else {
                        Log.d("MAP", "⚠️ 单次定位返回 null")
                    }
                }
            },
            Looper.getMainLooper()
        ).addOnSuccessListener {
            Log.d("MAP", "📡 单次定位请求已注册")
        }.addOnFailureListener {
            Log.e("MAP", "❌ 单次定位注册失败：${it.message}", it)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MAP", "✅ 用户授予了权限")
                setupLocationServices()
            } else {
                Log.e("MAP", "❌ 用户拒绝了权限")
                Toast.makeText(this, "需要位置权限才能使用此功能", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::locationCallback.isInitialized) {
            Log.d("MAP", "🔚 onDestroy，移除位置更新")
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}