plugins {
    alias(libs.plugins.gptmap.android.library.compose)
    alias(libs.plugins.gptmap.android.feature)
    alias(libs.plugins.secrets)
}

android {
    namespace = "com.espressodev.gptmap.feature.map"

}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.mongodb)
    implementation(projects.api.unsplash)
    implementation(projects.api.gemini)
    implementation(projects.feature.screenshot)
    implementation(projects.core.screenCapture)

    implementation(libs.maps.compose)
    implementation(libs.coil.compose)
    implementation(libs.lottie.compose)
    implementation(libs.androidx.activity.compose)
}