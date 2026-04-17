# AI Assistant Guide

This is a minimal Jetpack Compose app that tracks the time elapsed from or remaining to a target date.

## Architecture & Data Flow
- **Structure:** Single-module Android app (`app/`) with a simple MVVM-style approach.
- **Core Components:**
  - `MainActivity.kt`: Contains all UI composables (`MainScreen`, `DateSelectionScreen`).
  - `TimerViewModel.kt`: Holds and updates the app's state using `StateFlow`.
  - `TimeDisplay.kt`: Contains the core time calculation logic using `java.time`.
- **Data Flow:** UI observes `StateFlow` from `TimerViewModel` and recomposes on change. Data flows one-way: `ViewModel` -> `UI`.

## Key Patterns & How to Contribute
- **Adding Logic:** Extend `TimerViewModel` for new state/events and `calculateTimeDisplay` for new time computations. Avoid adding new architectural layers.
- **State Management:** Use `StateFlow` in `TimerViewModel`. Do not introduce other state management libraries (Redux, MVI frameworks, etc.).
- **Time Calculation:** All duration math belongs in `calculateTimeDisplay`. This function is bidirectional—it handles both past (count up) and future (count down) dates.
- **UI Changes:** Modify composables in `MainActivity.kt`. New UI should use Material 3 and the existing `TimeLESSTheme`.
- **Theming:** Dark mode state is owned by `MainActivity` and passed down. Do not move this state into the `ViewModel`.

## Build & Test
- This is a standard Gradle project. Use Android Studio to build and run.
- Key commands for reference:
  - `./gradlew assembleDebug`
  - `./gradlew testDebugUnitTest`
  - `./gradlew connectedDebugAndroidTest`

## AI Assistant Rules
- **Stay Minimal:** Keep changes small and localized. Prefer modifying existing files over creating new ones.
- **Follow Patterns:** Mirror the existing MVVM and `StateFlow` patterns.
- **No New Dependencies:** Do not add new libraries, especially for architecture or DI.
- **Respect Core Logic:** `calculateTimeDisplay` is the heart of the app. Be cautious when modifying it and ensure it remains bidirectional.
- **Code Style:** Follow existing conventions. Code should be self-documenting, so avoid adding comments.
- **Memory Safety:** Ensure `LaunchedEffect` is properly keyed to prevent leaks when the composition exits.
