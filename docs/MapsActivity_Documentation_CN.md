# MapsActivity 技术文档

[Switch to English Version](MapsActivity_Documentation_EN.md)

## 目录
1. [概述](#概述)
2. [核心组件](#核心组件)
3. [主要功能](#主要功能)
4. [实现细节](#实现细节)
5. [位置服务](#位置服务)
6. [路线模拟](#路线模拟)
7. [错误处理](#错误处理)
8. [日志系统](#日志系统)
9. [性能优化](#性能优化)
10. [用户界面](#用户界面)

## 概述
MapsActivity 是一个基于 Google Maps SDK 的 Android 活动，提供实时位置追踪、路径绘制和位置模拟功能。该活动使用 ViewBinding 进行视图绑定，使用 FusedLocationProviderClient 进行位置服务管理。

## 核心组件

### 类属性
```kotlin
private val pathPoints = mutableListOf<LatLng>()  // 存储路径点
private var polyline: Polyline? = null            // 路径线对象
private lateinit var mMap: GoogleMap             // 地图对象
private lateinit var binding: ActivityMapsBinding // 视图绑定
private lateinit var fusedLocationClient: FusedLocationProviderClient // 位置服务客户端
private lateinit var locationCallback: LocationCallback // 位置回调
private var currentLocationMarker: Marker? = null // 当前位置标记
private var isTrackingEnabled = true             // 跟踪状态
```

## 主要功能

### 初始化流程
```mermaid
flowchart TD
    A[onCreate] --> B[初始化视图绑定]
    B --> C[初始化位置客户端]
    C --> D[获取地图片段]
    D --> E[设置按钮监听器]
    E --> F[等待地图就绪]
    F --> G[onMapReady]
    G --> H{检查权限}
    H -->|有权限| I[初始化位置服务]
    H -->|无权限| J[请求权限]
```

### 位置服务初始化
```mermaid
flowchart TD
    A[setupLocationServices] --> B{检查权限}
    B -->|失败| C[提示用户]
    B -->|成功| D{检查GPS}
    D -->|关闭| E[提示开启GPS]
    D -->|开启| F[配置地图UI]
    F --> G[设置位置请求]
    G --> H[注册位置回调]
    H --> I[请求位置更新]
```

## 实现细节

### 位置追踪
```kotlin
val locationRequest = LocationRequest.Builder(
    Priority.PRIORITY_HIGH_ACCURACY, 2000L
)
    .setMinUpdateDistanceMeters(1f)
    .setGranularity(Granularity.GRANULARITY_FINE)
    .setWaitForAccurateLocation(true)
    .build()
```
- 更新间隔：2秒
- 最小距离：1米
- 精度：高精度
- 等待准确位置

### 路径绘制
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

### 位置标记更新
```kotlin
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
```

## 位置服务

### 多重定位策略
```mermaid
sequenceDiagram
    participant A as Activity
    participant L as LocationClient
    participant G as GPS
    participant N as Network
    A->>L: 请求位置更新
    L->>G: 尝试GPS定位
    alt GPS成功
        G->>L: 返回位置
        L->>A: 更新UI
    else GPS失败
        L->>N: 尝试网络定位
        N->>L: 返回位置
        L->>A: 更新UI
    end
```

### 位置更新流程
1. 首先尝试获取最后已知位置
2. 如果失败，启动高精度GPS定位
3. 如果GPS定位失败，降级到网络定位
4. 持续更新位置信息

## 路线模拟

### 模拟点设置
```kotlin
val simulatedPoints = listOf(
    LatLng(39.916345, 116.397155), // 故宫
    LatLng(39.908722, 116.397499), // 天安门
    LatLng(39.900269, 116.397872), // 前门
    LatLng(39.893, 116.397)        // 天坛边
)
```

### 模拟动画流程
```mermaid
flowchart TD
    A[开始模拟] --> B[清空旧数据]
    B --> C[设置模拟点]
    C --> D[创建Handler]
    D --> E[定时更新位置]
    E --> F[更新路径]
    F --> G[更新标记]
    G --> H[移动相机]
    H --> E
```

## 错误处理

### 权限处理
```kotlin
private fun hasLocationPermissions(): Boolean {
    val fine = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
    val coarse = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
    return fine == PackageManager.PERMISSION_GRANTED && 
           coarse == PackageManager.PERMISSION_GRANTED
}
```

### 异常处理
- SecurityException：权限相关异常
- 位置服务未开启
- 位置获取失败
- 网络定位失败

## 日志系统

### 日志标签
使用 "MAP" 标签记录关键信息：
- 初始化状态
- 权限状态
- 位置更新
- 错误信息

### 日志级别
- D：调试信息
- E：错误信息
- I：重要状态变化

## 性能优化

### 位置更新优化
- 最小更新距离限制
- 智能电源管理
- 位置精度平衡

### 内存管理
- 及时清理位置更新监听
- 适当的路径点存储策略

## 用户界面

### 地图控件
- 位置按钮
- 缩放控制
- 指南针
- 手动刷新按钮
- 模拟路线按钮

### 交互反馈
- Toast 消息提示
- 位置更新动画
- 路径绘制动画 