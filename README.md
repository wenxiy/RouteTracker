# RouteTracker

一个基于 Google Maps SDK 的 Android 位置追踪应用。

## 功能特点
- 实时位置追踪
- 路径绘制
- 位置模拟
- 多重定位策略（GPS + 网络）
- 完整的错误处理

## 技术文档
- [中文文档](docs/MapsActivity_Documentation_CN.md)
- [English Documentation](docs/MapsActivity_Documentation_EN.md)

## 快速开始

### 环境要求
- Android Studio
- Google Play Services
- Android SDK 24+
- Google Maps API Key

### 配置步骤
1. 克隆项目
2. 在 Android Studio 中打开项目
3. 在 `local.properties` 中配置你的 Google Maps API Key:
   ```properties
   MAPS_API_KEY=your_api_key_here
   ```
4. 运行项目

## 权限要求
- `ACCESS_FINE_LOCATION`
- `ACCESS_COARSE_LOCATION`
- `INTERNET`
- `ACCESS_NETWORK_STATE`

## 目录结构
```
.
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/example/routetracker/
│   │       │   ├── MapsActivity.kt        # 主要活动类
│   │       │   └── MainActivity.kt        # 入口活动
│   │       └── res/                       # 资源文件
│   └── build.gradle.kts                   # 应用级构建配置
├── docs/                                  # 技术文档
│   ├── MapsActivity_Documentation_CN.md   # 中文文档
│   └── MapsActivity_Documentation_EN.md   # 英文文档
└── build.gradle.kts                       # 项目级构建配置
```

## 贡献
欢迎提交 Issue 和 Pull Request。

## 许可证
[MIT License](LICENSE) 