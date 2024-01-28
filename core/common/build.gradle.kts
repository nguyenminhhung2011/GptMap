plugins {
    alias(libs.plugins.gptmap.android.library)
    alias(libs.plugins.gptmap.android.hilt)
}

android {
    namespace = "com.espressodev.gptmap.core.common"
}

dependencies {
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.firebase.crashlytics)
    testImplementation(libs.fixture)
    implementation(kotlin("reflect"))
    implementation(libs.androidx.compose.material3)
}
