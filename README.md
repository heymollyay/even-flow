# Even Flow 🌙
Even Flow is a privacy-first period and cycle tracker for Android, built with Kotlin and Jetpack Compose. It helps you log your cycle, understand your patterns, and keep your data secure and on-device.

## Features
- **Cycle tracking & predictions** - log periods and get calculated cycle insights via a built-in cycle logic engine
- **Calendar view** - see logged days, predicted periods, and fertile windows at a glance
- **Journal** - record symptoms, mood, and notes alongside your cycle data
- **Onboarding quiz** - a guided setup flow to personalize predictions from the start
- **Biometric lock** - secure the app with fingerprint/face unlock (AndroidX Biometric)
- **Encrypted local storage** - sensitive data is encrypted at rest (AndroidX Security Crypto) and stored locally in a Room database — nothing leaves your device
- **Data export** - export your data for backup or personal use
- **Cycle reminders** - local notifications for upcoming periods/events
- **User profiles** - manage personal settings within the app

## Tech Stack
- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3 (adaptive navigation suite)
- **Database:** Room
- **Security:** AndroidX Security Crypto (encrypted storage), AndroidX Biometric
- **Build:** Gradle (Kotlin DSL), KSP
- **Min SDK:** 31 **Target/Compile SDK:** 36

## Getting Started
### Prerequisites
- Android Studio (latest stable recommended)
- JDK 11+
- An Android device or emulator running API 31+

### Setup
```
  git clone https://github.com/heymollyay/even-flow.git
  cd even-flow
```
Open the project in Android Studio and let Gradle sync, or build from the command line:
```
  ./gradlew assembleDebug
```
Install on a connected device/emulator:
```
  ./gradlew installDebug
```
## Project Structure 
```
app/src/main/java/com/example/periodtracker/
├── MainActivity.kt
├── CycleLogic.kt          # Cycle calculation/prediction logic
├── CycleNotifications.kt  # Reminder notifications
├── Biometrics.kt          # Biometric authentication
├── EncryptionManager.kt   # Encrypted storage helpers
├── data/                  # Room entities, DAO, database, export
├── onboarding/            # Onboarding quiz flow
└── ui/                    # Compose screens (Home, Calendar, Journal, Profile, Login, Welcome, theme)
```
## Privacy 
Even Flow stores cycle data locally on your device using encrypted storage — no account or internet connection is required for core tracking features.
