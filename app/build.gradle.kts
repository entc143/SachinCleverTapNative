import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    // NOTE: No Kotlin plugin needed – AGP 9.x ships built-in Kotlin support.
}

// ---------------------------------------------------------------------------
// CleverTap credentials
// ---------------------------------------------------------------------------
// Values are read from <project-root>/clevertap.properties (see that file) and
// injected into AndroidManifest.xml as manifest placeholders. Nothing is
// hard-coded in source. A Gradle property (-P / ~/.gradle/gradle.properties)
// with the same key overrides the file, which is handy for CI.
val cleverTapProps = Properties().apply {
    val file = rootProject.file("clevertap.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

fun cleverTapValue(key: String): String =
    (project.findProperty(key) as String?)?.trim()
        ?: cleverTapProps.getProperty(key)?.trim()
        ?: ""

android {
    namespace = "com.example.rudderclevertapsample"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.rudderclevertapsample"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        // --- CleverTap dashboard credentials -> AndroidManifest.xml <meta-data> ---
        manifestPlaceholders["CLEVERTAP_ACCOUNT_ID"] = cleverTapValue("CLEVERTAP_ACCOUNT_ID")
        manifestPlaceholders["CLEVERTAP_TOKEN"] = cleverTapValue("CLEVERTAP_TOKEN")
        // Region code: in1 / us1 / sg1 / aps3 / mec1. Leave EMPTY for the default EU region.
        manifestPlaceholders["CLEVERTAP_REGION"] = cleverTapValue("CLEVERTAP_REGION")
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
        // JDK 17 is the minimum for AGP 9.x; Kotlin's jvmTarget follows targetCompatibility.
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // CleverTap Native Android SDK
    implementation(libs.clevertap.android.sdk)

    // Firebase Cloud Messaging (push). Versions are pinned by the Firebase BoM.
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)

    // AndroidX / Material
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.constraintlayout)

    // Dependencies required by CleverTap's built-in App Inbox UI (appcompat,
    // recyclerview, viewpager, material, glide). Glide is also used for Native Display images.
    implementation(libs.bundles.clevertap.inbox.ui)
}

// ---------------------------------------------------------------------------
// Firebase (FCM) configuration
// ---------------------------------------------------------------------------
// Drop your Firebase project's google-services.json into the app/ folder.
// The Google Services plugin is applied only when that file exists so the
// project compiles before Firebase has been configured.
if (file("google-services.json").exists()) {
    apply(plugin = libs.plugins.google.services.get().pluginId)
} else {
    logger.warn(
        "[SachinCleverTapNative] app/google-services.json not found – Firebase/FCM push is disabled. " +
            "Download it from the Firebase console and place it in app/ to enable push notifications."
    )
}

// Friendly reminder if CleverTap credentials have not been filled in yet.
if (cleverTapValue("CLEVERTAP_ACCOUNT_ID").isEmpty() || cleverTapValue("CLEVERTAP_TOKEN").isEmpty()) {
    logger.warn(
        "[SachinCleverTapNative] CleverTap Account ID / Token are empty. " +
            "Fill them in <project-root>/clevertap.properties (Dashboard > Settings > Project)."
    )
}
