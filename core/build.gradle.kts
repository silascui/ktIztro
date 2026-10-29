plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    `maven-publish`
}

group = "io.github.silascui"
version = "1.0.0"

kotlin {
    androidTarget {
        publishLibraryVariants("release")
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    jvm()
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    )

    sourceSets {
        commonMain.dependencies {
            implementation(libs.tyme4kt)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

android {
    namespace = "io.github.silascui.ktIztro.core"
    compileSdk = 35
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

publishing {
    publications.withType<MavenPublication> {
        pom {
            name.set("ktIztro Core")
            description.set("Kotlin Multiplatform Ziwei Doushu (紫微斗数) Astrolabe calculation engine supporting Android, JVM, iOS.")
            url.set("https://github.com/silascui/ktIztro")
            licenses {
                license {
                    name.set("MIT License")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }
            developers {
                developer {
                    id.set("silascui")
                    name.set("Silas Cui")
                }
            }
            scm {
                connection.set("scm:git:git://github.com/silascui/ktIztro.git")
                developerConnection.set("scm:git:ssh://github.com:silascui/ktIztro.git")
                url.set("https://github.com/silascui/ktIztro")
            }
        }
    }
    repositories {
        maven {
            name = "localBuildRepo"
            url = uri(layout.buildDirectory.dir("repo"))
        }
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/silascui/ktIztro")
            credentials {
                username = System.getenv("GITHUB_ACTOR") ?: project.findProperty("gpr.user") as String? ?: ""
                password = System.getenv("GITHUB_TOKEN") ?: project.findProperty("gpr.key") as String? ?: ""
            }
        }
    }
}
