plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "ca.lynix.lynxvr"
    compileSdk = 34

    defaultConfig {
        applicationId = "ca.lynix.lynxvr"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        vectorDrawables {
            useSupportLibrary = true
        }

    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/*.kotlin_module"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/license.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/NOTICE.txt"
            excludes += "META-INF/notice.txt"
        }
    }
}

dependencies {

    // General compose dependencies
    implementation(platform(libs.compose.bom))
    //implementation(libs.horologist.composables)
    implementation(libs.horologist.compose.layout)
    //implementation(libs.horologist.compose.material)
    implementation(libs.androidx.activity.compose)
    implementation(libs.ui.tooling.preview)
    implementation(libs.play.services.wearable)
    implementation(libs.ui)
    implementation(libs.compose.material)
    implementation(libs.compose.foundation)
    implementation(libs.activity.compose)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.core.splashscreen)
    implementation(libs.wear.compose.foundation)
    implementation(libs.wear.compose.material)
    implementation(libs.wear.compose.navigation)
    implementation(libs.androidx.wear.input)
    implementation(libs.androidx.lifecycle.service)
    implementation(libs.androidx.health.services)
    implementation(libs.volley)
    implementation(files("Libs\\java-android-websocket-client-master.jar"))
    implementation("androidx.fragment:fragment-ktx:1.5.6")
   // implementation(libs.localbroadcastmanager)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

}