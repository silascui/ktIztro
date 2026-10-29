# Zi Wei Dou Shu (ktIztro) - Compose Multiplatform

[中文文档](README.md) | [English](README_EN.md)

Inspired by open-source projects [iztro](https://github.com/SylarLong/iztro) and [react-iztro](https://github.com/SylarLong/react-iztro), **ktIztro** is a high-performance, modern, and aesthetic Zi Wei Dou Shu (Purple Star Astrology) astrolabe calculation and horoscope analysis system built with **Kotlin Multiplatform (KMP) + Compose Multiplatform (CMP)**.

A single unified codebase supporting **Android**, **Desktop (macOS / Windows / Linux)**, and **iOS**!

---

## 🌟 Key Features

### 1. Rigorous & Complete Calculation Engine (`:core`)
- **100% Pure Kotlin Multiplatform Calendar**: Powered by `cn.6tail:tyme4kt`, providing accurate bidirectional solar/lunar calendar conversions and leap month adjustment rules (15th cut-off).
- **Four Pillars of Destiny (BaZi)**: Automatically computes Year, Month (Five Tigers rule), Day, and Hour (Five Rats rule) pillars with Heavenly Stems, Earthly Branches, Chinese Zodiac, and Western Zodiac signs.
- **Soul/Body Palaces & Five Elements Bureau**:
  - Deterministic Soul Palace (*Ming Gong*) and Body Palace (*Shen Gong*) allocation.
  - Five Elements Bureau (Water 2nd, Wood 3rd, Metal 4th, Earth 5th, Fire 6th) computed via 60 JiaZi NaYin.
- **14 Major Stars (Stars of Destiny)**:
  - Ziwei constellation: Ziwei, Tianji, Taiyang, Wuqu, Tiantong, Lianzhen (counter-clockwise).
  - Tianfu constellation: Tianfu, Taiyin, Tanlang, Jumen, Tianxiang, Tianliang, Qisha, Pojun (clockwise).
- **14 Assistant Stars & 6 Malefic Stars**:
  - Six Auspicious Stars: Zuo Fu, You Bi, Wen Chang, Wen Qu, Tian Kui, Tian Yue.
  - Six Malefic Stars: Qing Yang, Tuo Luo, Huo Xing, Ling Xing, Di Kong, Di Jie.
  - Lu Cun and Tian Ma.
- **30+ Minor Stars & Deity Sequences**:
  - Romance Stars: Hong Luan, Tian Xi, Tian Yao, Xian Chi.
  - Day, Hour, Month, and Year minor star series (San Tai, Ba Zuo, En Guang, Tian Gui, Tai Fu, Feng Gao, Long Chi, Feng Ge, etc.).
  - 12 Longevity Deities (*Chang Sheng*, *Mu Yu*, *Guan Dai*, *Lin Guan*, *Di Wang*, *Shuai*, *Bing*, *Si*, *Mu*, *Jue*, *Tai*, *Yang*).
  - 12 Doctor Deities, 12 Year Deities, and 12 General Deities.
- **Four Transformations (Si Hua)**: Mutagen rules [Lu, Quan, Ke, Ji] across all ten Heavenly Stems.
- **Star Brightness Table**: Standard brightness levels [Temple, Flourishing, Gain, Advantage, Peace, Trap] across the 12 Earthly Branches.
- **Decadal & Yearly Horoscopes (Horoscope)**:
  - Dynamic 12-palace shift for Decadal (*Da Xian*), Yearly (*Liu Nian*), Monthly, Daily, and Hourly periods.
  - Transient stars (Liu Lu, Liu Yang, Liu Tuo, Liu Ma, Liu Chang, Liu Qu, etc.).
  - Transient Four Transformations for every timeframe.

---

### 2. Modern & Aesthetic Compose Multiplatform UI (`:app`)
- **Multiplatform Shared UI**: Android, Desktop (macOS / Windows / Linux), and iOS share a single Material 3 dark gold oriental aesthetic with fluid motion.
- **4x4 Astrolabe Matrix**:
  - Outer 12 Palaces: Arranged in standard clockwise Earthly Branches (Si, Wu, Wei, Shen / Chen, You / Mao, Xu / Yin, Chou, Zi, Hai).
  - Central 2x2 Matrix: Displays BaZi, birth date, Five Elements Bureau, Soul/Body stars, and Horoscope controls.
- **Dynamic Sanfang Sizheng (Three Harmonies & Opposite)**:
  - Tap any palace to highlight Self (Vermilion), Opposite (Amber Gold), Career Trine & Wealth Trine (Azure Blue).
  - Central Canvas renders dynamic dashed opposite lines and golden trine triangles without obscuring palace stars.
- **AI Astrological Interpretation (Streaming)**:
  - OpenAI-compatible streaming client supporting DeepSeek-V3, DeepSeek-R1, Qwen-2.5, or local Ollama models.
  - Collapsible display of DeepSeek-R1 reasoning thought process (`reasoning_content`).
  - Pre-built prompt presets: General Overview, Career, Wealth, Romance, Annual Horoscope, and Health.
- **Flying Mutagens (Palace Stem Transformation)**:
  - Tap any palace's Heavenly Stem to trigger flying mutagens and visualize activated stars across the astrolabe.
- **Tai Chi Shift (Microscopic Perspective)**:
  - Long press any palace to re-orient the astrolabe with that palace as the new "Soul Palace".
- **Horoscope Timeline Controller**:
  - Swiftly switch between [Birth Chart | Decadal | Yearly | Monthly | Daily | Hourly].
  - Step buttons: `-10y`, `-1y`, `Current Year`, `+1y`, `+10y`.
- **Star Detail Sheet**:
  - Tap any star to view its historical background, Five Elements attribute, brightness analysis, and transformation meaning.

---

## 🛠️ Architecture

```text
zwds/
├── core/                                # Calculation engine (Kotlin Multiplatform, Maven library)
│   ├── src/commonMain/kotlin/           # 100% Shared algorithms (Lunar/solar calendar, 12 palaces, stars, Si Hua)
│   └── src/commonTest/kotlin/           # Multiplatform unit tests (JVM / Android / iOS native)
│
├── app/                                 # Compose Multiplatform application
│   ├── src/commonMain/kotlin/           # 100% Shared UI, Material 3 theme & tokens, AI interpretation
│   │   └── io/github/silascui/ktIztro/
│   │       ├── App.kt                   # Root cross-platform application entry (App / ZwdsApp)
│   │       ├── ui/                      # 12 Palaces, Sanfang Sizheng lines, central view, control sheet
│   │       ├── ui/theme/                # Dark gold theme & colors
│   │       └── ai/                      # AI chat service, prompt generator, platform settings abstraction
│   ├── src/androidMain/kotlin/          # Android entry & services (MainActivity, SharedPreferences, OkHttp SSE)
│   ├── src/desktopMain/kotlin/          # Desktop entry & services (main.kt, Java Preferences, OkHttp SSE)
│   └── src/iosMain/kotlin/              # iOS entry & services (MainViewController, NSUserDefaults)
│
└── iosApp/                              # iOS SwiftUI wrapper project
    └── iosApp/
        ├── iOSApp.swift                 # SwiftUI entry point
        └── ContentView.swift            # UIViewControllerRepresentable hosting ComposeView
```

---

## 📦 Dependency & Maven Publication (`:core`)

The `:core` calculation engine supports all platforms (Android, JVM, iOS) and is ready for use:

### 1. Online Dependency (via JitPack, Recommended)
Add the JitPack repository to your root `settings.gradle.kts`:
```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

Add the dependency in your module's `build.gradle.kts`:
- **Kotlin Multiplatform (KMP) Project**:
  ```kotlin
  kotlin {
      sourceSets {
          commonMain.dependencies {
              implementation("com.github.silascui.ktIztro:core:v0.1.0")
          }
      }
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

### 2. Publish to Local Maven Repository
```bash
./gradlew :core:publishToMavenLocal
```
Artifacts published to `~/.m2/repository/io/github/silascui/`:
- `core-1.0.0.module` / `core-1.0.0.jar`: Kotlin Multiplatform root metadata
- `core-android-1.0.0.aar`: Android AAR package
- `core-jvm-1.0.0.jar`: JVM Jar (Desktop, Spring Boot)
- `core-iosarm64-1.0.0.klib` & `core-iossimulatorarm64-1.0.0.klib`: iOS native klibs

### 3. Publish to GitHub Packages
Automated via GitHub Actions on every GitHub Release.
Manual local publish:
```bash
GITHUB_ACTOR=silascui GITHUB_TOKEN="<your-token>" ./gradlew :core:publishAllPublicationsToGitHubPackagesRepository
```

---

## 🚀 Build & Run

### Run All Platform Tests
```bash
./gradlew :core:allTests
```

### Run Desktop Application (macOS / Windows / Linux)
```bash
./gradlew :app:run
```
Package standalone Desktop Jar:
```bash
./gradlew :app:desktopJar
```

### Run Android Application
```bash
# Build and install to connected device
./gradlew :app:installDebug

# Launch app
adb shell am start -n io.github.silascui.ktIztro/.MainActivity
```

### Compile iOS Target
```bash
# Compile iOS Simulator architecture
./gradlew :app:compileKotlinIosSimulatorArm64

# Compile iOS Device architecture
./gradlew :app:compileKotlinIosArm64

# Build iOS Debug Framework
./gradlew :app:linkDebugFrameworkIosSimulatorArm64
```

---

## 📄 License
[MIT License](LICENSE)
