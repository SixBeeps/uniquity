// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    val room_version = "2.7.2"

    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    id("androidx.room") version room_version apply false
}