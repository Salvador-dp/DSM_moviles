plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.weatherapp"
    compileSdk = 36

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.example.weatherapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // --- Dependencias de AndroidX/Material (Manteniendo tu sintaxis 'libs') ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // --- Dependencias de Testing ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // --- DEPENDENCIAS DE FIREBASE (¡Corregidas!) ---
    // ⚠️ ATENCIÓN: Solo debes usar una vez la plataforma BOM.
    // Usamos la versión más reciente (32.7.0) que tenías
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))

    // Core Firebase (ya incluye Analytics si usas la BOM)
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")

    // --- DEPENDENCIAS DE CLIMA y LOTTIE ---
    // Lottie (manteniendo tu versión)
    implementation("com.airbnb.android:lottie:6.7.0")

    // Retrofit y GSON
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp y Logging Interceptor
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
}