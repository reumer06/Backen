---
name: TimeLESS
description: Minimal time-difference tracker with reactive updates.
---

# TimeLESS Development Standards

### 1. Core Technical Stack
* **Language:** Kotlin 2.0+
* **UI Framework:** Jetpack Compose (Material 3)
* **Architecture:** MVVM (ViewModel + StateFlow)
* **State Management:** StateFlow for reactive UI updates

### 2. Time Logic & Reactive Ticker
* **Calculation Engine:** `java.time.LocalDateTime` + `java.time.Duration` for precise time differences
* **Real-time Updates:** `LaunchedEffect(Unit)` with 1-second polling to refresh UI
* **Display Format:** Segmented breakdown (Years, Months, Days, Hours, Minutes, Seconds)

### 3. UI & Theming
* **Layout:** Single screen, centered ticker display
* **Dark Mode:** Boolean toggle (system default + user preference)
* **Material 3:** Dynamic colors on Android 12+, fallback scheme for older versions
* **TopAppBar:** Dark mode toggle button

### 4. Code Standards
* **No Comments:** Self-documenting code via clear naming conventions
* **Memory Safety:** Cancel `LaunchedEffect` on composition exit to prevent leaks

### 5. Development Notes
* Don't run the code here. Use Android Studio to build and test. Report results back for debugging/fixes.