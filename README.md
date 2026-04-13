# VocabField - IELTS Vocabulary Learning App

An Android educational app built with Jetpack Compose that helps users learn IELTS vocabulary through an interactive multiple-choice quiz game, daily check-in tracking, and score statistics.

## Core Features

### 1. Home Screen - Vocabulary Book Library

**Implementation:** `MainActivity.kt` (`HomeScreen` composable)

- Displays the user's downloaded vocabulary books in a scrollable list
- Each book card shows the name, description, word count, and category
- Supports **swipe-to-delete** using Material 3 `SwipeToDismissBox` to remove books
- Shows an empty state with a bookshelf background and prompt to add books when no books are downloaded
- Enforces a **maximum limit of 5 books** with an alert dialog when the limit is reached

### 2. Book Download - Online Book Search

**Implementation:** `screen/AddBookScreen.kt`, `data/VocabularyApiService.kt`

- Fetches IELTS vocabulary book sets from a API using **Retrofit 3.0**
- Each book card has three button states: **Download**, **Downloading** (with spinner), and **Downloaded** (disabled)
- Duplicate download prevention and 5-book limit enforcement

### 3. Learning Screen - MCQ Quiz Game

**Implementation:** `screen/McqScreen.kt`, `viewmodel/VocabularyViewModel.kt`

- Users select a downloaded book to start a multiple-choice quiz
- Each question shows a word definition and **4 shuffled answer options** (1 correct + 3 random distractors)
- **Lives system:** Players start with 3 lives (hearts). Wrong answers reduce lives; the game ends at 0 lives
- **Score tracking:** Each correct answer earns 1 point (star). Scores are accumulated per day for the statistics chart
- **Game state persistence:** Progress (level, score, lives, shuffle order) is saved when exiting mid-game via `SavedMcqProgress` and restored on resume
- **Game over screen** shows the final score with a "Keep your determination!" message
- **Themed backgrounds:** The game screen uses the user's selected seasonal background (Spring, Summer, Autumn, or Winter)

### 4. Settings Screen - Theme Customization

**Implementation:** `screen/SettingScreen.kt`

- Displays a 2x2 grid of four seasonal background themes: **Spring**, **Summer** (default), **Autumn**, and **Winter**
- Each theme shows a preview image; the selected theme is highlighted with a blue border
- Selection is persisted immediately to SharedPreferences and applied to the MCQ game screen in real time

### 5. Statistics Screen - Check-in Calendar & Score Chart

**Implementation:** `screen/StatisticsScreen.kt`, `viewmodel/VocabularyViewModel.kt`

#### Daily Check-in Calendar
- Displays a full monthly calendar synchronized with real-world dates
- **Automatic check-in:** The app records the current date every time it is opened
- Check-in days are marked with a **red hand-drawn pencil circle** rendered via Compose `Canvas` with a wobble effect for a sketch-like appearance
- Today's date is highlighted in teal with bold text
- **Month navigation:** Left (`<`) and right (`>`) buttons allow browsing to previous and future months
- Day-of-week headers (Sun through Sat) with proper first-day-of-month offset calculation

#### Daily Score Line Chart
- A custom-drawn line chart below the calendar using Compose `Canvas`
- **X-axis:** Days of the displayed month (1-31), with adaptive label intervals to prevent crowding
- **Y-axis:** Score values, auto-scaled to the maximum daily score with 4 grid lines
- A teal line connects daily data points; days with scores show white-filled circle dots
- The chart synchronizes with the calendar's month navigation, so switching months updates both views

### 6. Bottom Navigation

**Implementation:** `MainActivity.kt` (`MainScreen` composable, `AppDestinations` enum)

- Uses Material 3 `NavigationSuiteScaffold` for adaptive navigation that adjusts layout based on screen size (bottom bar on phones, navigation rail on larger devices)
- Four tabs: **Home**, **Learning**, **Setting**, **Statistics**, each with a custom vector icon
- Navigation state is preserved across tab switches using `rememberSaveable`

## Architecture

```
com.edu.vocabularyfield/
├── MainActivity.kt                  # Navigation host, Home/Learning screens
├── data/
│   ├── VocabularyApiService.kt      # Retrofit API interface
│   ├── VocabularyBook.kt            # Book data class
│   ├── VocabularyWord.kt            # Word data class
│   └── VocabularyRepository.kt      # SharedPreferences persistence layer
├── screen/
│   ├── AddBookScreen.kt             # Online book download screen
│   ├── McqScreen.kt                 # Quiz game screen
│   ├── SettingScreen.kt             # Theme selection screen
│   └── StatisticsScreen.kt          # Calendar + line chart screen
├── ui/theme/                        # Material 3 theme configuration
└── viewmodel/
    └── VocabularyViewModel.kt       # Central ViewModel with all state management
```

**Pattern:** MVVM (Model-View-ViewModel) using `AndroidViewModel` with `StateFlow` for reactive state management.

**Data persistence:** All user data (downloaded books, game progress, check-in dates, daily scores, theme preference) is stored in `SharedPreferences` via `VocabularyRepository`, serialized with Gson.

**API layer:** `VocabularyApiService` is a Retrofit interface, which returns IELTS vocabulary data come from a remote API.

## Tech Stack

| Component | Technology |
|-----------|-----------|
| Language | Kotlin 2.3.20 |
| UI Framework | Jetpack Compose (BOM 2026.03.01) |
| Design System | Material 3 with Adaptive Navigation Suite |
| Navigation | Compose Navigation 2.9.7 |
| Architecture | MVVM with AndroidViewModel |
| State Management | Kotlin StateFlow |
| Networking | Retrofit 3.0  |
| Local Storage | SharedPreferences with Gson serialization |
| Custom Graphics | Compose Canvas |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 36 |

## Testing

The project includes **88 tests** covering both model logic and GUI code.

### Local Unit Tests (`app/src/test/`)

| Test Class | Tests | Coverage |
|-----------|-------|----------|
| `McqGameStateTest` | 9 | Game state defaults, state transitions (correct/wrong answer, game over, completion), data class equality |
| `DataModelTest` | 13 | VocabularyWord, VocabularyBook, SavedMcqProgress properties, equality, copy behavior |

### Instrumented Tests (`app/src/androidTest/`)

| Test Class | Tests | Coverage |
|-----------|-------|----------|
| `VocabularyRepositoryTest` | 23 | Book CRUD, MCQ progress persistence, background preference, check-in date tracking, daily score accumulation |
| `VocabularyViewModelTest` | 30 | Init behavior, FIXED_WORDS validation, game start/answer/game-over/completion logic, save/resume, book management limits, daily score updates, background persistence |
| `StatisticsScreenTest` | 7 | Calendar title, stars section, month/year display, day-of-week headers, month navigation |
| `HomeScreenTest` | 7 | Welcome text, empty/populated states, book card content, add button |
| `McqScreenTest` | 8 | Level/score/lives display, question definition, answer options, correct/wrong answer UI updates, game over screen |
| `NavigationTest` | 7 | Tab labels, default Home tab, navigation to all 4 screens, round-trip navigation |

**Run local tests:**
```bash
./gradlew testDebugUnitTest
```

**Run instrumented tests (requires connected device or emulator):**
```bash
./gradlew connectedDebugAndroidTest
```

## Build & Run

**Prerequisites:** Android Studio with SDK 36 installed.

```bash
# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug
```
