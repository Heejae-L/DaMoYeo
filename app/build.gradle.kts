plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "android.org.firebasetest"
    compileSdk = 34

    defaultConfig {
        applicationId = "android.org.firebasetest"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    packaging {
        excludes.add("META-INF/DEPENDENCIES")
        // Add more excludes or other packaging configurations as needed
    }


}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("com.github.bumptech.glide:glide:5.0.0-rc01")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-analytics")
    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    // Also add the dependency for the Google Play services library and specify its version
    implementation("com.google.android.gms:play-services-auth:21.1.1")
    implementation("com.google.firebase:firebase-auth:23.0.0")  // Firebase Auth
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")//swipe refresh
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-firestore:23.0.3")

    implementation("androidx.legacy:legacy-support-v4:1.0.0")
//    implementation("com.google.android.gms:play-services-auth:18.0.0")
    implementation("pub.devrel:easypermissions:3.0.0")
    implementation("com.google.api-client:google-api-client-android:1.22.0") {
        exclude(group = "org.apache.httpcomponents")
    }
    implementation("com.google.apis:google-api-services-calendar:v3-rev235-1.22.0") {
        exclude(group = "org.apache.httpcomponents")
    }

    //링크대로 넣고 이거 추가로 넣어줘야 함
    configurations.all {
        exclude(group = "com.google.guava", module = "listenablefuture")
    }

    implementation("com.google.api-client:google-api-client:1.31.5")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.31.5")
    implementation("com.google.http-client:google-http-client-jackson2:1.40.1")


    implementation ("com.google.android.material:material:1.4.0")

    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("com.google.android.gms:play-services-places:17.0.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.libraries.places:places:3.4.0")



    implementation ("androidx.core:core-ktx:1.7.0") // 노티피케이션을 위한 패키지

    annotationProcessor("com.github.bumptech.glide:compiler:4.13.0")





}