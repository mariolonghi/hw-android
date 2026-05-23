# HelloWearDroid

Android + Wear OS proof-of-concept. Learning project — toolchain shakedown before building a real Wear OS product.

**Current status:** phone scaffold only. Wear module and Data Layer wiring land in follow-up commits.

## Planned features

- **Phone:** on/off toggle, text input, live battery percentage readout.
- **Watch (bundled):** shows the watch's own battery and the phone's battery, relayed live via the Wear OS Data Layer.
- Debug-only "setup mode" with a stubbed Pro entitlement (real Play Billing comes later).
- English (anchor) + Spanish.

## Stack

- Kotlin + Jetpack Compose (phone), Wear Compose Material (watch, planned).
- Material 3.
- `compileSdk` 36 / `minSdk` 26 (phone). Wear module will use `minSdk` 30.
- No network, no PII, no cloud.

## Build

```bash
./gradlew :app:assembleDebug
./gradlew :app:installDebug    # installs to whichever emulator/device is connected
```

Requires Android Studio Panda 4 (or newer), a paired phone + Wear OS emulator pair.
