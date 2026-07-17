# Camera2 Magic 画面适配修复测试指南

## 本次修复

- Camera2 新旧会话接口都会读取真实的前后摄和传感器方向。
- 屏幕旋转、折叠状态变化时会重新同步 Camera2 状态。
- Camera1 在宿主应用没有设置预览方向时会计算默认方向。
- 占位相机输出跟随目标 Surface 尺寸，不再固定为 1280x720。
- WebRTC 旋转使用检测到的真实角度，不再始终强制为 90 度。
- 设置页新增 0/90/180/270 度手动旋转按钮，作为特殊应用的兼容兜底。

## 构建

1. 用 Android Studio 打开本目录。
2. 在 SDK Manager 安装 Android SDK Platform 36、Build-Tools 和 Platform-Tools。
3. Gradle JDK 选择 Android Studio 自带的 Embedded JDK。
4. Build Variant 选择 `modernDebug`。
5. 执行 `Build > Build APK(s)`。
6. APK 位于 `app/build/outputs/apk/modern/debug/`。

当前项目直接使用 `app/src/main/jniLibs/arm64-v8a/libcamera3.so`，普通构建不需要 NDK/CMake。

## 安装

- 支持 Android 10+ 和 arm64-v8a 设备。
- 调试 APK 的签名可能与已安装版本不同。遇到签名冲突时，备份设置后卸载旧版再安装。
- 安装后在 LSPosed 中启用模块并勾选需要测试的应用，然后强制停止该应用再重新打开。

## 建议测试

1. 前置和后置摄像头各测一次。
2. 竖屏、左横屏、右横屏各测一次。
3. 分别测试竖版和横版视频。
4. 测试 1:1、4:3 和 16:9 预览区域。
5. 如果个别应用仍旋转错误，返回模块设置，按“画面旋转”切换角度，然后重启目标应用。

## 已知限制

压缩包中只有预编译的 `libcamera3.so`，没有原始 C++ 源码。因此本次修复了 Kotlin 层的方向、尺寸和配置传递，但无法更改原生库内部的 GPU 裁剪算法。
