# 🔥 STREAKKEEPER

> **Consistency over perfection.**  
> A high-performance, offline-first habit consistency & daily discipline tracking application crafted with **Dark Swiss Minimalism** and Bento Grid architecture. Available as both a native Android application (Jetpack Compose) and an interactive Web Twin.

---

## ⚡ Core Philosophy & Architecture

StreakKeeper eliminates superficial gamification in favor of raw, unambiguous daily execution:
* **Zero-Freeze Binary Tracking**: No streak freeze inventory, no pay-to-maintain tricks, and no false positives. Every habit is strictly **Done** or **Pending**.
* **100% Offline-First & Zero Cloud**: Your habits, journal reflections, and metrics exist strictly on your device storage (`streakkeeper_habits_vault`).
* **Tactile Mechanical Feedback**: Every check-in triggers physical haptic resonance (`HapticFeedbackType.LongPress` / `navigator.vibrate`) paired with crisp acoustic key clicks (`AudioManager.FX_KEY_CLICK` and 880Hz→1500Hz Web Audio sine sweeps).

---

## 🌟 Key Features

### 1. 📳 Amber Flame Streak Counter & Micro-Pulse
* Dynamic habit card badges that elevate from a muted dormant indicator (`🔥 0 D`) to radiant amber embers upon completion.
* Spring-physics micro-pulse (`@keyframes flamePop` / `animateFloatAsState`) that scales and illuminates the flame badge upon daily completion.

### 2. 🎖️ Habit-Specific Discipline Milestones
* Milestones are uniquely tied to **individual habits**, prominently tagging the habit's name on earned badges:
  * **🔥 7D FIRE**: 7 consecutive days of unbroken discipline.
  * **🛡️ 30D TITAN**: 30 days of cemented neural habit formation.
  * **⚡ 50D RELENTLESS**: 50 days of unyielding focus.
  * **⚔️ 70D CHAMPION**: 70 days of elite execution.
  * **👑 100D CENTURY MASTER**: 100 days of absolute lifestyle mastery.
* Displays current progress counters (e.g. `42 / 50 DAYS`) directly within the Profile dossier.

### 3. 🗓️ 12-Month Discipline Dossier (GitHub Contribution Matrix)
* Full 365-day annual matrix for every habit, mapping daily check-ins across January through December.
* Single-tap inspection to view completion status and historical notes for any day of the year.

### 4. 📝 Daily Session Log & Reflection Notes
* **Strict Per-Day Date Isolation**: Notes written on a specific date (e.g., `2026-11-07`) strictly belong to that day—zero cross-day data bleeding.
* Interactive mood tags (🔥 Crushed it, ⚡ Energetic, 🎯 Focused, 🧘 Calm, 🌧️ Tough session) + 140-character recap logs.
* Direct one-tap modal access from habit cards or the dedicated habit view.

### 5. 📦 Local Vault Backup & Restore (Zero Cloud)
* **Android Storage Access Framework (SAF)**: Export and restore complete habit vaults via `ActivityResultContracts.CreateDocument` and `OpenDocument` without legacy storage permissions.
* **Web Twin**: Instant JSON export and restoration via native file download blobs and `FileReader`.

---

## 🏗️ Tech Stack

### 📱 Android Native
* **Language & Runtime**: Kotlin 2.0 (JVM 17 toolchain)
* **UI Toolkit**: Jetpack Compose, Material 3, Compose Navigation
* **Audio & Haptics**: `android.media.AudioManager` (`FX_KEY_CLICK`), `LocalHapticFeedback`
* **Storage**: Local Scoped Storage (`HabitStorageManager`), Android SAF
* **Build System**: Gradle 8.13 with Version Catalogs (`libs.versions.toml`)

### 🌐 Web Twin
* **Markup & Styling**: Semantic HTML5, Tailwind CSS, Bebas Neue & Tinos typography
* **Audio Engine**: Synthesized Web Audio API (Sine wave oscillator + exponential gain sweeps)
* **State Management**: `localStorage` vault persistence with schema migration safeguards

---

## 📁 Repository Structure

```text
STREAK-KEEPER/
├── app/
│   ├── src/main/kotlin/com/example/streakkeeper/
│   │   ├── MainActivity.kt               # Single Activity Compose entry point
│   │   ├── Navigation.kt                 # Compose navigation graph & routes
│   │   ├── NavigationKeys.kt             # Type-safe navigation keys
│   │   ├── data/
│   │   │   ├── DataRepository.kt         # Repository layer
│   │   │   └── HabitStorageManager.kt    # Offline JSON persistence & SAF
│   │   ├── notifications/
│   │   │   ├── HabitAlarmReceiver.kt     # AlarmManager broadcast receiver
│   │   │   └── HabitNotificationScheduler.kt
│   │   └── ui/
│   │       ├── habit/                    # Create habit & dedicated 12-month matrix
│   │       ├── home/                     # Home screen, habit cards & flame badges
│   │       ├── onboarding/               # Account setup & passcode creation
│   │       ├── profile/                  # Bento grid dossier & habit milestones
│   │       ├── splash/                   # Kinetic scribble logo animation
│   │       └── theme/                    # Swiss dark color palette & typography
│   └── build.gradle.kts                  # App module configuration & release signing
├── web_preview/
│   └── index.html                        # Complete standalone Web Twin application
├── build.gradle.kts                      # Root Gradle configuration
├── settings.gradle.kts                   # Project module declarations
└── README.md                             # Project documentation
```

---

## 🚀 Getting Started

### Prerequisites
* JDK 17 (`JAVA_HOME` pointing to Java 17)
* Android SDK 36 (Build tools 36.0.0+)

### 1. Running the Android Application
```powershell
# Compile Kotlin sources
.\gradlew.bat compileDebugKotlin

# Assemble Debug APK
.\gradlew.bat assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk

# Assemble Signed Production Release APK
.\gradlew.bat assembleRelease
# Output: app/build/outputs/apk/release/app-release.apk
```

### 2. Running the Web Twin
```bash
python -m http.server 8080 --directory web_preview
# Open http://localhost:8080/ in any modern browser
```

---

## 🔒 Security & Privacy

* **Zero Tracking / Zero Telemetry**: StreakKeeper collects no user data, sends no network requests, and requires no third-party account registration.
* **Scoped Storage**: Backups are written exclusively through Android's system document picker, ensuring zero broad storage access requirements.

---

## 📄 License
This project is open-source and available under the [MIT License](LICENSE).
