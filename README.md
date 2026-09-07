# 小桃工作台 Android · Local 1.2

这是“小桃工作台”的本地核心版 Android 应用。

## 为什么改成本地版

原来的 1.1 版直接在 Android WebView 中打开 `chatgpt.site`，部分手机会被 Cloudflare 安全策略拦截。1.2 版把核心页面直接内置进 APK，因此启动、计划、习惯、日历、复盘、主题与数据管理不依赖该站点。

## 数据

- 任务、习惯、专注、主题等数据保存在应用 WebView 的本地存储。
- 正常退出、重启手机、覆盖安装同包名新版通常会保留。
- 卸载 App 或系统设置中“清除数据”会删除本地记录。
- 1.2 新增 JSON 导出备份 / 导入恢复。
- App 不内置 API Key。

## 本地功能

- 首页总览与今日待办
- DDL 风险判断
- 周习惯追踪（默认包含“吃药”）
- 专注分钟与 28 天热力图
- 月历和每周自动汇总
- 10 套本地主题
- JSON 数据备份/恢复
- 可用系统浏览器单独打开线上版（不嵌在 WebView 中）

## 构建

GitHub Actions：`Actions → Build Android APK → Run workflow`

产物：`xiaotao-workbench-apk` → `app-debug.apk`

当前版本：`1.2.0 (3)`。
