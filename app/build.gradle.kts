plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.ecom.app"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
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

    buildFeatures {
        compose = true
        buildConfig = true
        resValues = true
    }

    flavorDimensions += "brand"

    productFlavors {
        create("abitomodaLocal") {
            dimension = "brand"
            applicationId = "com.abitomoda.app.local"
            resValue("string", "app_name", "ABITOMODA Local")
            buildConfigField("String", "BRAND_NAME", "\"ABITOMODA\"")
            buildConfigField("String", "BASE_URL", "\"http://192.168.64.5/\"")
            buildConfigField("String", "HERO_IMAGE", "\"http://192.168.64.5/static/cores/media/am/am_hero_image.png\"")
            buildConfigField("String", "LOGO_FULL_IMAGE", "\"http://192.168.64.5/static/cores/media/am/am_brand_logo_white.png\"")
        }

        create("abitomodaProd") {
            dimension = "brand"
            applicationId = "com.abitomoda.app"
            resValue("string", "app_name", "ABITOMODA")
            buildConfigField("String", "BRAND_NAME", "\"ABITOMODA\"")
            buildConfigField("String", "BASE_URL", "\"https://abitomoda.com/\"")
            buildConfigField("String", "HERO_IMAGE", "\"https://abitomoda.com/static/cores/media/am/am_hero_image.png\"")
            buildConfigField("String", "LOGO_FULL_IMAGE", "\"https://abitomoda.com/static/cores/media/am/am_brand_logo_white.png\"")
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.gson)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.coil.compose)
    implementation(libs.okhttp)
    implementation(libs.okhttp.urlconnection)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}