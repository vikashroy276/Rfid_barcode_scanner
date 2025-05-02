plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")

}

android {
    namespace = "com.mespl.emp_asset_mgmtapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mespl.emp_asset_mgmtapp"
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
    buildFeatures {
        viewBinding = true
    }

    flavorDimensions("backup")

    // Define product flavors and associate them with the flavor dimension
    productFlavors {
        create("backupEnabled") {
            dimension = "backup"  // Assign to flavor dimension
            manifestPlaceholders["allowBackup"] = "true"
        }
        create("backupDisabled") {
            dimension = "backup"  // Assign to flavor dimension
            manifestPlaceholders["allowBackup"] = "false"
        }
    }


}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation (libs.androidx.viewpager2)
    implementation (libs.material.v140)
    implementation (libs.converter.gson)
    implementation (libs.retrofit2.retrofit)
    implementation (libs.converter.gson)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.datastore.core.android)
    implementation(libs.androidx.recyclerview)
    implementation(libs.play.services.basement)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.permission)
    implementation (libs.sdp.android)
    implementation(libs.okhttp3.logging.interceptor)
    //Dagger
    implementation (libs.dagger)
    kapt (libs.google.dagger.compiler)

    implementation (files("libs/com.symbol.emdk.jar"))
    implementation (files("libs/ZSDK_ANDROID_API.jar"))
    implementation (files("libs/com.datecs.api.jar"))
    implementation (project(":materialbarcodescanner"))
    implementation (files("libs/datacollection.jar"))
    implementation (project(":RFIDAPI3Library"))

}