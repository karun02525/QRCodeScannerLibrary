import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.plugins.signing.SigningExtension
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    id("com.vanniktech.maven.publish") version "0.29.0"
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
val libraryGroup: String         = localOrProject("LIBRARY_GROUP")!!
val libraryArtifact: String      = localOrProject("LIBRARY_ARTIFACT")!!
val libraryVersion: String       = localOrProject("LIBRARY_VERSION")!!
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

// ─────────────────────────────────────────
// Credentials
// ─────────────────────────────────────────
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

// ─────────────────────────────────────────
// Android — MUST be before kotlin{}
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

// ─────────────────────────────────────────
// Kotlin Multiplatform
// ─────────────────────────────────────────
kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
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
// GitHub Packages
// ─────────────────────────────────────────
publishing {
    repositories {
        mavenLocal()
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

// ─────────────────────────────────────────
// Maven Central — Vanniktech
// ─────────────────────────────────────────
mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    coordinates(
        groupId    = libraryGroup,
        artifactId = libraryArtifact,
        version    = libraryVersion
    )

    pom {
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

// ─────────────────────────────────────────
// ✅ Explicit Signing — reads directly from local.properties
// Overrides vanniktech's property lookup (which was failing)
// ─────────────────────────────────────────
afterEvaluate {
    extensions.configure<SigningExtension> {
        val key      = localOrProject("SIGNING_KEY")
        val password = localOrProject("SIGNING_PASSWORD")

        if (!key.isNullOrBlank() && !password.isNullOrBlank()) {
            useInMemoryPgpKeys(key, password)
            sign(publishing.publications)
            logger.lifecycle("✅ Signing configured successfully")
        } else {
            logger.warn("⚠️ SIGNING_KEY or SIGNING_PASSWORD missing in local.properties")
        }
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
}