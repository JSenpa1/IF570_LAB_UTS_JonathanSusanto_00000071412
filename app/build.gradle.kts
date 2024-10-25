plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.if570_lab_uts_jonathansusanto_00000071412"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.if570_lab_uts_jonathansusanto_00000071412"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.5.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.android.material:material:1.9.0")
    implementation("com.google.firebase:firebase-auth:22.0.0")
    implementation("com.google.firebase:firebase-firestore:24.7.1")
    implementation("com.google.firebase:firebase-storage:20.2.0")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.github.bumptech.glide:glide:4.14.2")
    implementation("com.android.support:cardview-v7:26.1.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}