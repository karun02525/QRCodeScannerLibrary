import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    id("maven-publish")
}

// ─────────────────────────────────────────
// Local Properties
// ─────────────────────────────────────────
val localProperties = Properties().apply {
    rootProject.file("local.properties")
        .takeIf { it.exists() }
        ?.inputStream()
        ?.let { load(it) }
}

fun localOrProject(key: String): String? =
    localProperties.getProperty(key)
        ?: project.findProperty(key) as String?

// ─────────────────────────────────────────
// Library Info
// ─────────────────────────────────────────
val libraryGroup: String    = localOrProject("LIBRARY_GROUP")!!
val libraryArtifact: String = localOrProject("LIBRARY_ARTIFACT")!!
val libraryVersion: String  = localOrProject("LIBRARY_VERSION")!!

// Library POM Info
val libraryName: String          = localOrProject("LIBRARY_NAME")!!
val libraryDescription: String   = localOrProject("LIBRARY_DESCRIPTION")!!
val libraryUrl: String           = localOrProject("LIBRARY_URL")!!
val libraryLicenseName: String   = localOrProject("LIBRARY_LICENSE_NAME")!!
val libraryLicenseUrl: String    = localOrProject("LIBRARY_LICENSE_URL")!!
val libraryScmUrl: String        = localOrProject("LIBRARY_SCM_URL")!!
val libraryScmConnection: String = localOrProject("LIBRARY_SCM_CONNECTION")!!
val libraryScmDevConn: String    = localOrProject("LIBRARY_SCM_DEV_CONNECTION")!!
val libraryDevId: String         = localOrProject("LIBRARY_DEVELOPER_ID")!!
val libraryDevName: String       = localOrProject("LIBRARY_DEVELOPER_NAME")!!

// GitHub Credentials
val githubUser: String?  = localOrProject("GITHUB_USER")
val githubToken: String? = localOrProject("GITHUB_TOKEN")

group   = libraryGroup
version = libraryVersion

// ─────────────────────────────────────────
// Dependency Fixes
// ─────────────────────────────────────────
configurations.all {
    resolutionStrategy {
        force("androidx.annotation:annotation:1.9.1")
    }
    exclude(
        group  = "org.jetbrains.compose.annotation-internal",
        module = "annotation"
    )
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
        publishLibraryVariants("release")
    }

    iosArm64()
    iosSimulatorArm64()

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.material.icons.extended)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.lifecycle.viewmodel)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.lifecycle.runtime.compose)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.coroutines.core)
        }

        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.camerax.camera2)
            implementation(libs.camerax.lifecycle)
            implementation(libs.camerax.view)
            implementation(libs.mlkit.barcode)
            implementation(libs.koin.android)
            implementation(libs.accompanist.permissions)
            implementation(libs.coroutines.android)
            implementation(libs.play.services.code.scanner)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

// ─────────────────────────────────────────
// Publishing
// ─────────────────────────────────────────
publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url  = uri("https://maven.pkg.github.com/karun02525/QRCodeScannerLibrary")
            credentials {
                username = githubUser
                password = githubToken
            }
        }
    }
}

afterEvaluate {
    publishing.publications.withType<MavenPublication>().forEach { pub ->

        pub.groupId = libraryGroup
        pub.version = libraryVersion
        pub.artifactId = when (pub.name) {
            "kotlinMultiplatform" -> libraryArtifact
            "androidRelease"      -> "$libraryArtifact-android"
            "iosArm64"            -> "$libraryArtifact-iosarm64"
            "iosSimulatorArm64"   -> "$libraryArtifact-iossimulatorarm64"
            else                  -> "$libraryArtifact-${pub.name.lowercase()}"
        }

        pub.pom {
            name.set(libraryName)
            description.set(libraryDescription)
            url.set(libraryUrl)
            licenses {
                license {
                    name.set(libraryLicenseName)
                    url.set(libraryLicenseUrl)
                }
            }
            developers {
                developer {
                    id.set(libraryDevId)
                    name.set(libraryDevName)
                }
            }
            scm {
                url.set(libraryScmUrl)
                connection.set(libraryScmConnection)
                developerConnection.set(libraryScmDevConn)
            }
        }
    }
}

// ─────────────────────────────────────────
// Android
// ─────────────────────────────────────────
android {
    namespace  = "org.qrcode.scanner"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk    = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
}