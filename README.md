# Midnight Crystal Music Player

A premium, modern Android music player built with Kotlin and Material 3. This application features a stunning "Midnight Crystal" aesthetic with glassmorphic UI elements, high-performance audio processing, and a fully customizable 5-band equalizer.

![Modern UI](https://img.shields.io/badge/UI-Material_3-blueviolet)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-orange)
![MVVM](https://img.shields.io/badge/Architecture-MVVM-blue)

## ✨ Features

- **Midnight Crystal Theme**: A deep, vibrant dark-mode aesthetic utilizing Material 3 components and glassmorphic design principles.
- **Audio Equalizer**: Fully functional 5-band equalizer with presets (Rock, Pop, Jazz, Classical, Vocal).
- **Pro Audio Effects**: Integrated **Bass Boost** and **Virtualizer** with custom rotary knob controls.
- **Dynamic Waveform**: Real-time waveform visualization synced with audio playback.
- **Robust Media Engine**: Hardened `MediaPlayer` implementation with asynchronous loading, state guards, and error handling.
- **Optimized Performance**: Large album art handling with bitmap scaling to prevent memory-related crashes (SIGKILL 9).
- **User-Friendly Navigation**: Smooth transitions between the Player, Equalizer, and Loading screens.

## 🛠 Tech Stack

- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel) with Clean Architecture principles.
- **UI Framework**: Material Design 3, XML Layouts, Custom Views (KnobView, WaveformView).
- **Jetpack Components**:
  - **Navigation Component**: For seamless fragment transitions.
  - **ViewModel & StateFlow**: Reactive state management.
  - **Lifecycle**: Lifecycle-aware UI updates.
- **Audio API**: Android `MediaPlayer` and Audio Effects (`Equalizer`, `BassBoost`, `Virtualizer`).
- **Concurrency**: Kotlin Coroutines.

## 🏗 Architecture Overview

The project follows a strict MVVM pattern to ensure maintainability and testability:

- **Model/Data Layer**: `AudioRepository` handles track loading and bitmap optimization. `AudioTrack` defines the data structure.
- **Manager Layer**: 
    - `MediaManager`: Wraps `MediaPlayer`, handles playback logic, and exposes state via `StateFlow`.
    - `EqualizerManager`: Manages audio effects and audio session synchronization.
- **ViewModel Layer**: `SharedMediaViewModel` acts as the bridge between the UI and managers, persisting user preferences like EQ settings and bass/treble levels.
- **UI/View Layer**: Fragments (`PlayerFragment`, `EqualizerFragment`) observe the ViewModel state and update the UI reactively.

## 🚀 Getting Started

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/hidurmuhammad/Music-Player.git
    ```
2.  **Open in Android Studio**:
    Ensure you have the latest version of Android Studio.
3.  **Sync Gradle**:
    Let the project download dependencies (Material 3, Navigation, etc.).
4.  **Run**:
    Deploy to an emulator or physical device (Min SDK 24, Target SDK 35).

---

Developed with Kotlin language as a modern take on the classic Android music player.