/*
 * Created by Naimish Trivedi on 01/02/24, 3:24 pm
 * Copyright (c) 2024 . All rights reserved.
 * Last modified 01/02/24, 3:24 pm
 */

plugins {
    id("com.android.library")
}

android {
    namespace = "naimishtrivedi.in.googleadsmanager"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}


dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.google.firebase:firebase-analytics:21.5.1")
    implementation ("com.google.android.gms:play-services-ads:22.6.0")
    implementation ("com.facebook.shimmer:shimmer:0.5.0@aar")
}