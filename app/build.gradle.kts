plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "com.abclab.abcereports"
    compileSdk = 34
    useLibrary("org.apache.http.legacy")
    defaultConfig {
        applicationId = "com.abclab.abcereports"
        minSdk = 21
        targetSdk = 34

        buildConfigField(
            type = "int",
            name = "BRANCH_ID",
            value = "2" // PH = 1, ID = 2, US = 0
        )

        buildConfigField(
            type = "String",
            name = "PREF_COUNTRY",
            value = "\"ID\"" // PH, ID, US
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
//            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.txt")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
//    implementation("com.android.support:support-v4:28.0.0")
//    implementation("com.android.support:appcompat-v7:28.0.0")
    implementation(files("libs/ksoap2-android-assembly-2.4-jar-with-dependencies.jar"))
    implementation(libs.sqliteassethelper)
//    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
//    implementation("androidx.core:core-ktx:1.15.0")

}
