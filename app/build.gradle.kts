plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)

    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.jmr.coasterappwatch"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.jmr.coasterappwatch"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        vectorDrawables {
            useSupportLibrary = true
        }

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.play.services.wearable)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.tooling.preview)
    implementation(libs.compose.material)
    implementation(libs.compose.foundation)
    implementation(libs.activity.compose)
    implementation(libs.core.splashscreen)
    androidTestImplementation(platform(libs.compose.bom))
    debugImplementation(libs.ui.tooling)

    // Test
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.test.manifest)

    // Android X and Kotlin
//    implementation("androidx.compose.material:material:1.3.0")
//    implementation("androidx.recyclerview:recyclerview:1.2.1")
//    implementation("com.google.android.material:material:1.8.0")

    // Injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    // Network
    implementation(libs.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.converter.moshi)
    implementation(libs.converter.gson)
    implementation(libs.adapter.rxjava2)

    // Images
    implementation(libs.picasso)
}

kapt {
    correctErrorTypes = true
}

hilt {
    enableAggregatingTask = true
}