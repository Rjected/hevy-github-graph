# Hevy GitHub Graph

Display your [Hevy](https://hevy.com) workout history as a GitHub-style contribution graph.

![Terminal Example](https://img.shields.io/badge/Terminal-CLI-green) ![Android Widget](https://img.shields.io/badge/Android-Widget-blue)

## Features

- **CLI Tool**: Terminal-based contribution graph with color/ASCII output
- **Android Widget**: Home screen widget showing your workout activity at a glance
- Intensity levels based on training volume (weight × reps)
- Quantile-based normalization (like GitHub's contribution graph)

## CLI Usage

```bash
# Set your Hevy API key
export HEVY_API_KEY="your-api-key"

# Run with default settings (52 weeks)
cargo run

# Customize weeks shown
cargo run -- --weeks 26

# ASCII mode (for terminals without color support)
cargo run -- --ascii
```

### Example Output

```
     ████████████████████████████████████████████████
Mon  ██  ████████  ████  ██████████  ████  ██████████
     ████████████████████████████████████████████████
Wed  ██████  ██████████████████  ██████████████  ████
     ████████████████████████████████████████████████
Fri  ████████████  ██████████████████  ██████████████
     ████████████████████████████████████████████████

     Less ██████████ More
     156 days with workouts shown
```

## Android Widget

The Android app provides a home screen widget that displays your workout contribution graph.

### Features

- Resizable widget (4x2 recommended)
- Multiple color themes (GitHub Green, Blue, Purple, Orange, Red)
- Background sync via WorkManager
- Encrypted API key storage
- Tap to refresh

### Building

```bash
cd android
./gradlew assembleDebug
```

The APK will be at `android/app/build/outputs/apk/debug/app-debug.apk`.

## Getting Your Hevy API Key

1. Log in to [Hevy](https://hevy.com)
2. Go to Settings → Developer Settings
3. Generate an API key

## Project Structure

```
.
├── src/                    # Rust CLI
│   ├── main.rs             # Entry point
│   ├── api.rs              # Hevy API client
│   ├── models.rs           # Data models
│   ├── aggregate.rs        # Volume aggregation
│   ├── normalize.rs        # Quantile normalization
│   ├── render.rs           # Terminal graph rendering
│   └── cli.rs              # CLI argument parsing
└── android/                # Android widget app
    └── app/src/main/java/com/hevy/graphwidget/
        ├── data/           # API, database, repository
        ├── widget/         # Widget provider, renderer
        ├── work/           # Background sync worker
        └── ui/             # Configuration activity
```

## TODO

- [ ] **iPhone Support**: iOS widget with WidgetKit
- [ ] **Other Fitness Apps**: Support for additional workout tracking apps
  - Strong
  - Fitbod
  - Apple Health
  - Strava (for cardio-focused users)

## License

MIT
