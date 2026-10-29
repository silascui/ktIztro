# 紫微斗数 (ktIztro) - Compose Multiplatform 跨平台版

[中文文档](README.md) | [English](README_EN.md)

参考开源项目 [iztro](https://github.com/SylarLong/iztro) 与 [react-iztro](https://github.com/SylarLong/react-iztro)，使用 **Kotlin Multiplatform (KMP) + Compose Multiplatform (CMP)** 全新打造的高性能、现代美观的紫微斗数排盘与运限分析系统。

一套代码全栈跨端，支持 **Android**、**Desktop (macOS / Windows / Linux 桌面端)** 以及 **iOS**！

---

## 🌟 核心特性

### 1. 完整严谨的跨平台算法核心 (`:core`)
- **100% 纯 Kotlin 跨平台历法**：基于 `cn.6tail:tyme4kt`，支持阳历公历与传统农历（阴历）精准互转，支持农历闰月调整规则（十五前后分月）。
- **四柱八字**：自动计算年柱、月柱（五虎遁起月）、日柱、时柱（五鼠遁起时），结合生肖与西方十二星座。
- **命身宫与五行局**：
  - 顺生月逆生时定命宫，顺生月顺生时定身宫。
  - 根据命宫干支六十甲子纳音取数，精准确立五行局（水二局、木三局、金四局、土五局、火六局）。
- **十四主星系**：
  - 紫微星系（紫微、天机、太阳、武曲、天同、廉贞）逆时针布局。
  - 天府星系（天府、太阴、贪狼、巨门、天相、天梁、七杀、破军）顺时针布局。
- **十四辅星与六煞星**：
  - 六吉星（左辅、右弼、文昌、文曲、天魁、天钺）。
  - 六煞星（擎羊、陀罗、火星、铃星、地空、地劫）。
  - 禄存、天马。
- **三十余颗杂曜与神煞**：
  - 桃花星（红鸾、天喜、天姚、咸池）。
  - 日系星（三台、八座、恩光、天贵）。
  - 时系星（台辅、封诰）。
  - 月系星（解神、阴煞、天刑、天月、天巫）。
  - 年系星（龙池、凤阁、天才、天寿、华盖、天官、天福、天厨、天德、月德、天空、旬空、截路、空亡、孤辰、寡宿、蜚廉、破碎、天哭、天虚、天使、天伤）。
- **神煞序列**：
  - 长生十二神（长生、沐浴、冠带、临官、帝旺、衰、病、死、墓、绝、胎、养）。
  - 博士十二神（博士、力士、青龙、小耗、将军、奏书、飞廉、喜神、病符、大耗、伏兵、官府）。
  - 岁前十二神（岁建、晦气、丧门、贯索、官符、小耗、大耗、龙德、白虎、天德、吊客、病符）。
  - 将前十二神（将星、攀鞍、岁驿、息神、华盖、劫煞、灾煞、天煞、指背、咸池、月煞、亡神）。
- **十天干四化**：甲乙丙丁戊己庚辛壬癸对应的【化禄、化权、化科、化忌】规则。
- **星曜亮度速查**：各大星曜在十二地支宫位的【庙、旺、得、利、平、不、陷】亮度体系。
- **大限与小限**：起运年龄区间与十年一大限推算；男顺女逆起小限虚岁。
- **动态运限分析 (Horoscope)**：
  - 大限、流年、流月、流日、流时动态十二宫推导。
  - 流曜计算（流禄、流羊、流陀、流马、流魁、流钺、流昌、流曲、流鸾、流喜）。
  - 运限四化（大限四化、流年四化、流月四化等）。

---

### 2. 现代优雅的 Compose Multiplatform 交互界面 (`:app`)
- **全平台共享 UI**：Android、Desktop (macOS / Windows)、iOS 共享一套 Material 3 深金国风主题与动效。
- **4x4 经典星盘布局**：
  - 外圈十二宫：按照传统顺时针地支排布（巳午未申 / 辰酉 / 卯戌 / 寅丑子亥）。
  - 中心 2x2 中宫：展示主客八字、农历、生辰、命主身主、生肖星座与运限控制板。
- **三方四正动态可视化**：
  - 点击任意宫位，即刻高亮该宫位的【本宫】（朱砂红）、【对宫】（琥珀金）、【官禄三合位】与【财帛三合位】（霁蓝）。
  - 中宫 Canvas 连线层动态绘制对冲虚线与三合黄金三角连线，美观优雅且绝不遮挡四周宫位星曜。
- **AI 智能解盘 (Streaming)**：
  - 内置 OpenAI 兼容协议流式问答客户端，完美支持 DeepSeek-V3 / DeepSeek-R1、Qwen-2.5-72B 或本地 Ollama 模型。
  - 支持 DeepSeek-R1 深度思考过程 (`reasoning_content`) 动态折叠展示。
  - 提供全盘总论、事业官禄、财运财富、婚恋感情、流年运势、健康疾厄等一键快捷命理提示词。
- **宫干飞星 (Flying Mutagens)**：
  - 点击任意宫位的宫干（如“丙寅”之“丙”），即刻触发该宫天干引发的飞星四化，受化星曜徽章动态响应亮起！
- **立太极转换 (Tai Chi Shift)**：
  - 长按任意宫位，以该宫位作为新的“命宫”，整盘动态重塑十二宫关系，便于深入分析六亲与运势微观视角。
- **运限推演面板**：
  - 支持快捷切换【本盘 | 大限 | 流年 | 流月 | 流日 | 流时】。
  - 配备 `-10年`、`-1年`、`当前年`、`+1年`、`+10年` 步进控制按钮。
  - 实时更新对应流年的流曜分布与流年四化星。
- **星曜详情 BottomSheet**：
  - 点击盘上任意星曜，弹出星曜文化背景、五行属性、当前宫位亮度剖析与四化意涵说明。
- **排盘设定与名盘预设**：
  - 支持公历/农历年月日时自定义设置。
  - 内置经典名人命盘预设（如破军文昌木三局、紫微火六局、贪狼火星横发格等），一键秒级排盘验证。

---

## 🛠️ 项目架构

```text
zwds/
├── core/                                # 核心排盘引擎 (Kotlin Multiplatform，支持打 Maven 库)
│   ├── src/commonMain/kotlin/           # 100% 共享算法 (公历农历转换、干支排盘、主辅煞神煞星曜、四化亮度、三方四正)
│   └── src/commonTest/kotlin/           # 跨平台单元测试 (JVM / Android / iOS 原生全通过)
│
├── app/                                 # Compose Multiplatform 共享应用
│   ├── src/commonMain/kotlin/           # 100% 共享 Compose UI、主题 (Material 3 Tokens)、AI 模块
│   │   └── io/github/silascui/ktIztro/
│   │       ├── App.kt                   # 跨平台共享根 UI (App / ZwdsApp)
│   │       ├── ui/                      # 十二宫天盘、三方四正连线、中宫、底部推演控制面板
│   │       ├── ui/theme/                # 深金国风主题与配色
│   │       └── ai/                      # AI 命理大模型对话、提示词与持久化配置抽象
│   ├── src/androidMain/kotlin/          # Android 平台适配 (MainActivity, SharedPreferences, OkHttp SSE)
│   ├── src/desktopMain/kotlin/          # 桌面端适配 (main.kt, Java Preferences, OkHttp SSE)
│   └── src/iosMain/kotlin/              # iOS 适配 (MainViewController, NSUserDefaults)
│
└── iosApp/                              # iOS SwiftUI 壳工程
    └── iosApp/
        ├── iOSApp.swift                 # SwiftUI 启动入口
        └── ContentView.swift            # ComposeUIViewController 桥接封装
```

---

## 📦 依赖引入与 Maven 发布 (`:core`)

`:core` 紫微斗数算法引擎支持全平台（Android / JVM / iOS），已发布版本可以直接引入：

### 1. 公开在线引入 (通过 JitPack，推荐)
在项目的根目录 `settings.gradle.kts` 添加 JitPack 仓库源：
```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

在对应模块的 `build.gradle.kts` 引入依赖：
- **Kotlin Multiplatform (KMP) 工程**：
  ```kotlin
  kotlin {
      sourceSets {
          commonMain.dependencies {
              implementation("com.github.silascui.ktIztro:core:v0.1.0")
          }
      }
  }
  ```
- **纯 Android 原生工程**：
  ```kotlin
  dependencies {
      implementation("com.github.silascui.ktIztro:core:v0.1.0")
  }
  ```
- **纯 Java / Spring Boot 后端工程**：
  ```groovy
  dependencies {
      implementation 'com.github.silascui.ktIztro:core:v0.1.0'
  }
  ```

---

### 2. 发布到本地 Maven 仓库
```bash
./gradlew :core:publishToMavenLocal
```
构件将自动发布至 `~/.m2/repository/io/github/silascui/`：
- `core-1.0.0.module` / `core-1.0.0.jar`：Kotlin Multiplatform 根元数据
- `core-android-1.0.0.aar`：Android 原生 AAR 包
- `core-jvm-1.0.0.jar`：JVM 独立 Jar 包（桌面端、Spring Boot 等）
- `core-iosarm64-1.0.0.klib` & `core-iossimulatorarm64-1.0.0.klib`：iOS 静态 Klib 库

### 3. 发布到 GitHub Packages
已配置 GitHub Actions 自动构建，每当在 GitHub 发布新 Release 时会自动触发发布。
本地手动发布命令：
```bash
GITHUB_ACTOR=silascui GITHUB_TOKEN="<your-token>" ./gradlew :core:publishAllPublicationsToGitHubPackagesRepository
```

---

## 🚀 编译与多端运行

### 运行全平台单元测试
```bash
./gradlew :core:allTests
```

### 运行 Desktop 桌面端 (macOS / Windows / Linux)
```bash
./gradlew :app:run
```
打包桌面端独立 Jar：
```bash
./gradlew :app:desktopJar
```

### 运行 Android 端
```bash
# 编译并安装到连接设备
./gradlew :app:installDebug

# 启动应用
adb shell am start -n io.github.silascui.ktIztro/.MainActivity
```

### 编译 iOS 平台
```bash
# 编译 iOS 模拟器架构
./gradlew :app:compileKotlinIosSimulatorArm64

# 编译 iOS 真机架构
./gradlew :app:compileKotlinIosArm64

# 构建 iOS 静态 Framework
./gradlew :app:linkDebugFrameworkIosSimulatorArm64
```
