plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id ("kotlin-parcelize")
}

android {
    namespace = "com.example.ecojourney"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ecojourney"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.activity:activity-compose:1.9.1")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui:1.6.8")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material:1.6.8")
    implementation("androidx.compose.material3:material3")
    implementation("com.google.firebase:firebase-auth:23.0.0")
    implementation("com.google.firebase:firebase-firestore:25.1.0")
    implementation ("com.google.firebase:firebase-database-ktx:20.0.5")
    implementation (platform("com.google.firebase:firebase-bom:32.2.0"))
    implementation ("com.google.firebase:firebase-storage-ktx")
    implementation ("com.google.android.gms:play-services-auth:21.2.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation ("com.google.android.gms:play-services-base:18.7.2")
    implementation("androidx.appcompat:appcompat:1.7.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation ("androidx.compose.runtime:runtime-livedata:1.6.8")

    //splashscreen
    implementation ("androidx.core:core-splashscreen:1.0.1")

    //navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    //facebook
    implementation ("com.facebook.android:facebook-login:latest.release")

    //accompanist
    implementation ("com.google.accompanist:accompanist-pager:0.30.0")
    implementation ("com.google.accompanist:accompanist-pager-indicators:0.30.0")

    // Retrofit untuk API calls
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    // Coil untuk loading images
    implementation ("io.coil-kt:coil-compose:2.4.0")

    // Youtube Player
    implementation ("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.2")

    // Cloudinary
    implementation ("com.cloudinary:cloudinary-android:3.0.2")

}