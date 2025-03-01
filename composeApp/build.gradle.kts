import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    kotlin("plugin.serialization") version "2.1.0"
    alias(libs.plugins.sqlDelight)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.sqldelight.android.driver)

            // For dependency injection
            implementation(libs.koin.android)

            // For encryption
            implementation(libs.cryptography.provider.jdk)

            // For bio-metric authentication
            implementation(libs.androidx.biometric)
        }
        appleMain.dependencies {
            implementation(libs.sqldelight.native.driver)

            // For encryption
            implementation(libs.cryptography.provider.apple)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            api(libs.mvvm.core) // only ViewModel, EventsDispatcher, Dispatchers.UI
            api(libs.mvvm.compose) // api mvvm-core, getViewModel for Compose Multiplatform

            implementation(libs.kotlinx.datetime)

            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines.extensions)

            // Dependency injection
            api(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)

            // Navigator
            implementation(libs.voyager.navigator)

            // Screen Model
            implementation(libs.voyager.screenModel)

            // BottomSheetNavigator
            implementation(libs.voyager.bottomSheetNavigator)

            // TabNavigator
            implementation(libs.voyager.tabNavigator)

            // Transitions
            implementation(libs.voyager.transitions)

            implementation(compose.material3)
            implementation(compose.material)
            implementation(compose.materialIconsExtended)
            implementation(libs.material3.window.size.multiplatform)

            // For storing settings across sessions
            implementation(libs.multiplatform.settings)

            // For beautiful text animations
            implementation(libs.texty)

            // For working with JSON
            implementation(libs.kotlinx.serialization.json)

            // For encryption
            implementation(libs.cryptography.core)

            // For file handling
            implementation(libs.filekit.compose)

            // For local notifications
            implementation(libs.alarmee)

            // For permission management
            implementation(libs.calf.permissions)
        }
    }
}

android {
    namespace = "org.zcorp.zidary"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.zcorp.zidary"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.ui.android)
    implementation(libs.androidx.startup.runtime)
    debugImplementation(compose.uiTooling)
}

sqldelight {
    databases {
        create("ZidaryDatabase") {
            packageName.set("org.zcorp.zidary.db")
        }
    }
}

