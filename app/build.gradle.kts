plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    compileSdk = 28
    useLibrary("org.apache.http.legacy")
    defaultConfig {
        applicationId = "com.abclab.abcereports"
        minSdk = 21
        //noinspection EditedTargetSdkVersion,ExpiredTargetSdkVersion
        targetSdk = 28
    }

    buildTypes {
        release {
            isMinifyEnabled = false
//            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.txt")
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
//    implementation("androidx.appcompat:appcompat:1.0.0")
    implementation("com.android.support:support-v4:28.0.0")
    implementation("com.android.support:appcompat-v7:28.0.0")
    implementation(files("libs/ksoap2-android-assembly-2.4-jar-with-dependencies.jar"))
    implementation("com.readystatesoftware.sqliteasset:sqliteassethelper:2.0.1")
//    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
//    implementation("androidx.core:core-ktx:1.15.0")

}
