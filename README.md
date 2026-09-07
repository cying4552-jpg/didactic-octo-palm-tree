# 小桃工作台 Android 封装

这是小桃工作台最新版的 Android WebView 封装工程，应用固定打开公开站点：
`https://xiaotao-workbench.cying4552.chatgpt.site/workbench`

## 隐私说明

- APK 不内置任何 API Key。
- 网页中的 API Key 只保留在当前页面内存，刷新或关闭后清除。
- 应用禁止明文 HTTP、文件访问、内容访问与系统备份。
- 学习数据仍保存在应用 WebView 的本地存储中。

## 一键生成 APK

将本文件夹内容上传到 GitHub 仓库，打开 **Actions → Build Android APK → Run workflow**。构建完成后，在该次任务底部下载 `xiaotao-workbench-apk`，解压即可获得 `app-debug.apk`。

也可以在安装了 Android SDK 35 和 Gradle 8.7 的电脑中运行：

```bash
gradle assembleDebug
```

生成位置：`app/build/outputs/apk/debug/app-debug.apk`。

当前应用版本：`1.1.0 (2)`。因为应用读取公开网站，之后网站主题和功能更新无需重新打包 APK。
