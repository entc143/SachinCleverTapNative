// Top-level build file. Plugin versions live in gradle/libs.versions.toml.
plugins {
    alias(libs.plugins.android.application) apply false
    // Google Services plugin (Firebase / FCM). It is applied in app/build.gradle.kts
    // only when app/google-services.json is present, so the project builds out of the box.
    alias(libs.plugins.google.services) apply false
}
