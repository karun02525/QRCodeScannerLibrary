plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false

    // ✅ Declare here, applied in composeApp module only
    id("com.vanniktech.maven.publish") version "0.29.0" apply false

    // ❌ REMOVE nexus-publish — no longer needed with vanniktech
    // id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
}

// ❌ REMOVE the entire nexusPublishing {} block — no longer needed