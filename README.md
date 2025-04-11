# RouteTracker

<div align="center">
  <h2>基于 Google Maps SDK 的 Android 位置追踪应用</h2>
  <h2>Android Location Tracking App based on Google Maps SDK</h2>
</div>

## 功能特点 | Features
- 实时位置追踪 | Real-time Location Tracking
- 路径绘制 | Route Drawing
- 位置模拟 | Location Simulation
- 多重定位策略（GPS + 网络）| Multiple Location Strategies (GPS + Network)
- 完整的错误处理 | Comprehensive Error Handling

## 技术文档 | Technical Documentation
详细的实现说明和技术文档请参考：
Detailed implementation and technical documentation can be found in:
- [中文文档](docs/MapsActivity_Documentation_CN.md)
- [English Documentation](docs/MapsActivity_Documentation_EN.md)

## 快速开始 | Quick Start

### 环境要求 | Requirements
- Android Studio
- Google Play Services
- Android SDK 24+
- Google Maps API Key

### 配置步骤 | Setup Steps
1. 克隆项目 | Clone the repository
2. 在 Android Studio 中打开项目 | Open project in Android Studio
3. 在 `local.properties` 中配置你的 Google Maps API Key | Configure your Google Maps API Key in `local.properties`:
   ```properties
   MAPS_API_KEY=your_api_key_here
   ```
4. 运行项目 | Run the project

## 权限要求 | Permissions
- `ACCESS_FINE_LOCATION`
- `ACCESS_COARSE_LOCATION`
- `INTERNET`
- `ACCESS_NETWORK_STATE`

## 目录结构 | Project Structure
```
.
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/example/routetracker/
│   │       │   ├── MapsActivity.kt        # 主要活动类 | Main Activity
│   │       │   └── MainActivity.kt        # 入口活动 | Entry Activity
│   │       └── res/                       # 资源文件 | Resource files
│   └── build.gradle.kts                   # 应用级构建配置 | App-level build config
└── build.gradle.kts                       # 项目级构建配置 | Project-level build config
```

## 贡献 | Contributing
欢迎提交 Issue 和 Pull Request。
Issues and Pull Requests are welcome.

## 许可证 | License
[MIT License](LICENSE) 