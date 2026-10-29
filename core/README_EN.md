# ktIztro Core (Kotlin Multiplatform Zi Wei Dou Shu Engine)

[中文文档](README.md) | [English](README_EN.md)

A high-performance, **100% Pure Kotlin Multiplatform (KMP)** Zi Wei Dou Shu (Purple Star Astrology) calculation engine based on [SylarLong/iztro](https://github.com/SylarLong/iztro).

- **100% Cross-Platform Calendar**: Powered by `cn.6tail:tyme4kt:1.5.0`, with zero JVM / Android SDK lock-in.
- **Universal Multiplatform Support**: Runs on **Android (AAR)**, **JVM (Java / Spring Boot / Ktor / Desktop)**, and **iOS (Arm64 & Simulator klibs)**.
- **Sub-millisecond Performance**: In-memory matrix computation; full chart calculation executes in < 1ms.
- **Standard Maven Publishing**: Supports JitPack, Local Maven (`publishToMavenLocal`), GitHub Packages, and Maven Central.

---

## 📦 Dependency Setup

### 1. Quick Online Dependency (via JitPack, No Auth Required)
Add JitPack to your root `settings.gradle.kts`:
```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

Add the dependency to your module's `build.gradle.kts`:
- **Kotlin Multiplatform (KMP) Project**:
  ```kotlin
  commonMain.dependencies {
      implementation("com.github.silascui.ktIztro:core:v0.1.0")
  }
  ```
- **Android Native Project**:
  ```kotlin
  dependencies {
      implementation("com.github.silascui.ktIztro:core:v0.1.0")
  }
  ```
- **Java / Spring Boot Project**:
  ```groovy
  dependencies {
      implementation 'com.github.silascui.ktIztro:core:v0.1.0'
  }
  ```

---

### 2. GitHub Packages / Local Maven
```kotlin
// KMP Project
implementation("io.github.silascui:core:1.0.0")

// Android Project
implementation("io.github.silascui:core-android:1.0.0")

// JVM / Server Project
implementation("io.github.silascui:core-jvm:1.0.0")
```

---

## 🚀 Quick Start Guide

### 1. Calculate Chart by Solar (Gregorian) Date
```kotlin
import io.github.silascui.ktIztro.engine.IztroEngine
import io.github.silascui.ktIztro.model.Gender

// August 16, 2000, Yin Hour (Index 2), Female
val astrolabe = IztroEngine.bySolar(
    solarDateStr = "2000-8-16",
    timeIndex = 2,           // 0: Early Zi, 1: Chou, 2: Yin ... 12: Late Zi
    gender = Gender.FEMALE,
    fixLeap = true           // Split leap months on the 15th
)

println("BaZi: ${astrolabe.chineseDate}")
println("Soul Palace: ${astrolabe.earthlyBranchOfSoulPalace}")
println("Five Elements: ${astrolabe.fiveElementsClass}")
println("Soul Star: ${astrolabe.soul}, Body Star: ${astrolabe.body}")
```

### 2. Calculate Chart by Lunar Date
```kotlin
import io.github.silascui.ktIztro.engine.IztroEngine
import io.github.silascui.ktIztro.model.Gender

// Lunar calendar: Year 2000, Month 7, Day 17, Yin Hour, Female
val astrolabe = IztroEngine.byLunar(
    lunarYear = 2000,
    lunarMonth = 7,
    lunarDay = 17,
    timeIndex = 2,
    gender = Gender.FEMALE,
    isLeapMonth = false
)
```

### 3. Query Sanfang Sizheng (Three Harmonies & Opposite)
```kotlin
val mingGong = astrolabe.palace("命宫")!!
val sanFang = astrolabe.getSanFangSiZheng(mingGong.index)

println("Self: ${sanFang.self.name}")         // Soul Palace (Ming Gong)
println("Opposite: ${sanFang.opposite.name}") // Travel Palace (Qian Yi)
println("Career: ${sanFang.career.name}")     // Career Palace (Guan Lu)
println("Wealth: ${sanFang.wealth.name}")     // Wealth Palace (Cai Bo)
```

### 4. Calculate Horoscopes (Decadal, Yearly, Daily, Hourly)
```kotlin
import io.github.silascui.ktIztro.engine.IztroEngine

// Calculate horoscope for 2026-10-27
val horoscope = IztroEngine.horoscope(astrolabe, "2026-10-27", targetTimeIndex = 2)

println("Nominal Age: ${horoscope.age.nominalAge}")
println("Yearly Branch: ${horoscope.yearly.earthlyBranch}")
println("Yearly Si Hua: ${horoscope.yearly.mutagen}") // [Lu, Quan, Ke, Ji]
```

---

## 🛠️ Build & Publication Commands

### 1. Publish to Local Maven Repository (`~/.m2/repository`)
```bash
./gradlew :core:publishToMavenLocal
```

### 2. Export to Local Directory Repository (`core/build/repo`)
```bash
./gradlew :core:publishAllPublicationsToLocalBuildRepoRepository
```

### 3. Run Multiplatform Unit Tests
```bash
./gradlew :core:allTests
```
