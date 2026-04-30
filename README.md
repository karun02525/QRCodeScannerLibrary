# QRCode Scanner Library

![Version](https://img.shields.io/badge/version-1.0.0-blue)
![Platform](https://img.shields.io/badge/platform-Android%20%7C%20iOS-green)
![License](https://img.shields.io/badge/license-Apache%202.0-orange)
![KMP](https://img.shields.io/badge/Kotlin%20Multiplatform-%237F52FF?logo=kotlin&logoColor=white)

A Kotlin Multiplatform QR Code Scanner library for Android and iOS.

## Installation

### Step 1 — Add GitHub Packages repository
\```kotlin
// settings.gradle.kts
maven {
    url = uri("https://maven.pkg.github.com/karun02525/QRCodeScannerLibrary")
    credentials {
        username = "YOUR_GITHUB_USERNAME"
        password = "YOUR_GITHUB_TOKEN" // read:packages only
    }
}
\```

### Step 2 — Add dependency
\```kotlin
// Android
implementation("com.github.karun02525:qrcode-scanner-android:1.0.0")

// KMP commonMain
implementation("com.github.karun02525:qrcode-scanner:1.0.0")
\```

This is a Kotlin Multiplatform project targeting Android, iOS.

  ```shell
  ./gradlew :composeApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:assembleDebug
  ```
### Publish to GitHub Packages
```shell
  ./gradlew publishAllPublicationsToGitHubPackagesRepository
  ```