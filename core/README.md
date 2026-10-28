# ktIztro Core (Kotlin Multiplatform 紫微斗数排盘计算引擎)

基于开源项目 [SylarLong/iztro](https://github.com/SylarLong/iztro) 深度重构的 **100% 纯 Kotlin Multiplatform (KMP)** 高性能紫微斗数排盘计算引擎。

- **100% 跨平台历法支持**：基于跨平台 `cn.6tail:tyme4kt:1.5.0`，无任何 JVM / Android SDK 强绑定。
- **支持全平台**：无缝支持 **Android (AAR)**、**JVM (Java / Spring Boot / Ktor / Desktop)**、**iOS (Arm64 / Simulator klib)** 等所有 Kotlin 平台。
- **毫秒级性能**：纯内存数学矩阵与干支历法推算，单次全盘推演耗时 < 1ms。
- **标准 Maven 发布**：支持发布到本地 Maven 仓库 (`publishToMavenLocal`)、私有 Nexus、GitHub Packages 或 Maven Central。

---

## 📦 引入依赖

### 1. Kotlin Multiplatform (KMP) 工程
在 `commonMain` 引入根模块坐标，Gradle 会根据目标平台自动匹配对应制品：
```kotlin
// build.gradle.kts
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.silascui:core:1.0.0")
        }
    }
}
```

### 2. 纯 Android 原生工程 (Groovy / Kotlin DSL)
```kotlin
dependencies {
    implementation("io.github.silascui:core-android:1.0.0")
}
```

### 3. 纯 Java / Spring Boot 后端工程
```kotlin
// Gradle Kotlin DSL
implementation("io.github.silascui:core-jvm:1.0.0")

// Gradle Groovy
implementation 'io.github.silascui:core-jvm:1.0.0'
```

### 4. Apache Maven (`pom.xml`)
```xml
<dependency>
    <groupId>io.github.silascui</groupId>
    <artifactId>core-jvm</artifactId>
    <version>1.0.0</version>
</dependency>
```

---

## 🚀 核心使用指南

### 1. 公历（阳历）排盘
```kotlin
import io.github.silascui.ktIztro.engine.IztroEngine
import io.github.silascui.ktIztro.model.Gender

// 2000年8月16日 寅时(序号2) 女命
val astrolabe = IztroEngine.bySolar(
    solarDateStr = "2000-8-16",
    timeIndex = 2,           // 0:早子, 1:丑, 2:寅, ... 12:夜子
    gender = Gender.FEMALE,
    fixLeap = true           // 闰月是否按十五分前后月调整
)

println("八字: ${astrolabe.chineseDate}")          // 庚辰 甲申 丙午 庚寅
println("命宫: ${astrolabe.earthlyBranchOfSoulPalace}宫") // 午
println("五行局: ${astrolabe.fiveElementsClass}")    // 木三局
println("命主: ${astrolabe.soul}, 身主: ${astrolabe.body}") // 破军, 文昌
```

### 2. 农历（阴历）排盘
```kotlin
import io.github.silascui.ktIztro.engine.IztroEngine
import io.github.silascui.ktIztro.model.Gender

// 农历 2000年七月十七 寅时 女命
val astrolabe = IztroEngine.byLunar(
    lunarYear = 2000,
    lunarMonth = 7,
    lunarDay = 17,
    timeIndex = 2,
    gender = Gender.FEMALE,
    isLeapMonth = false
)
```

### 3. 三方四正查询
```kotlin
val mingGong = astrolabe.palace("命宫")!!
val sanFang = astrolabe.getSanFangSiZheng(mingGong.index)

println("本宫: ${sanFang.self.name}")       // 命宫
println("对宫: ${sanFang.opposite.name}")   // 迁移
println("官禄位: ${sanFang.career.name}")   // 官禄
println("财帛位: ${sanFang.wealth.name}")   // 财帛
```

### 4. 运限推演（大限 / 小限 / 流年 / 流月 / 流日 / 流时）
```kotlin
import io.github.silascui.ktIztro.engine.IztroEngine

// 推算目标时刻 2026-10-27 的运限星曜与四化
val horoscope = IztroEngine.horoscope(astrolabe, "2026-10-27", targetTimeIndex = 2)

println("虚岁: ${horoscope.age.nominalAge} 岁")
println("流年地支: ${horoscope.yearly.earthlyBranch}")
println("流年四化: ${horoscope.yearly.mutagen}") // [禄, 权, 科, 忌] 星曜列表
```

---

## 🛠️ 打包与发布命令

### 1. 发布到本地 Maven 仓库 (`~/.m2/repository`)
```bash
./gradlew :core:publishToMavenLocal
```
构件将自动安装至本机的 `~/.m2/repository/io/github/silascui/`：
- `core/1.0.0/`（Kotlin Multiplatform 根元数据与 Gradle Module Metadata）
- `core-android/1.0.0/`（包含 `core-android-1.0.0.aar`）
- `core-jvm/1.0.0/`（包含 `core-jvm-1.0.0.jar`，带 sources.jar）
- `core-iosarm64/1.0.0/`（包含 iOS arm64 架构的 klib 与 cinterop）
- `core-iossimulatorarm64/1.0.0/`（包含 iOS 模拟器架构的 klib）

### 2. 导出到本地文件目录 (`core/build/repo`)
```bash
./gradlew :core:publishAllPublicationsToLocalBuildRepoRepository
```

### 3. 运行跨平台单元测试
```bash
# 运行 JVM / Android / iOS 原生全平台单元测试
./gradlew :core:allTests
```
