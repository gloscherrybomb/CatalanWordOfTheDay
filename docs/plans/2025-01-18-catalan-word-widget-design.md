# Catalan Word of the Day Widget - Design Document

## Overview

An Android widget displaying a daily Catalan word with translation and example sentence, designed for the Cozyla family calendar tablet.

## Requirements

- **Target users:** Teens/adults learning beginner Catalan
- **Translation language:** English
- **Word database:** 365 words/phrases (one per day of year)
- **Visual style:** Match Cozyla launcher aesthetic
- **Interaction:** Show Catalan by default, tap to reveal translations

## Architecture

```
CatalanWordWidget/
├── app/
│   ├── src/main/
│   │   ├── java/com/cozyla/catalanword/
│   │   │   ├── WordWidgetProvider.kt    # Widget logic
│   │   │   ├── Word.kt                  # Data class
│   │   │   └── WordRepository.kt        # Loads words from JSON
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   └── widget_layout.xml    # Widget UI
│   │   │   ├── xml/
│   │   │   │   └── widget_info.xml      # Widget metadata
│   │   │   ├── drawable/                # Rounded card background
│   │   │   └── values/                  # Colors, strings
│   │   └── assets/
│   │       └── words.json               # 365 words database
│   └── build.gradle.kts
└── build.gradle.kts
```

### How It Works

1. Widget registered in Android manifest
2. On first display (and daily at midnight), `WordWidgetProvider` triggers update
3. `WordRepository` loads `words.json`, picks word based on day-of-year (1-365)
4. Widget layout inflated with Catalan content
5. Tap toggles translation visibility
6. `AlarmManager` schedules next midnight update

### Word Selection Logic

- Uses `dayOfYear % 365` to pick word index
- Same word shows all day, changes at midnight
- Deterministic - same word on same calendar day each year

## Visual Design

### Cozyla Design Language

- White cards with ~16dp rounded corners
- Subtle shadow (elevation 2-4dp)
- Clean sans-serif font (Roboto)
- Teal accent color (#2DB3A2)
- Section headers in dark gray, bold
- Body text in medium gray

### Widget States

**Default state (Catalan only):**
```
┌─────────────────────────────────────┐
│  📖  Paraula del Dia           ↻   │
├─────────────────────────────────────┤
│                                     │
│  Bon dia                            │
│                                     │
│  "Bon dia! Com estàs avui?"         │
│                                     │
│              Tap to reveal ›        │
└─────────────────────────────────────┘
```

**Tapped state (with translations):**
```
┌─────────────────────────────────────┐
│  📖  Paraula del Dia           ↻   │
├─────────────────────────────────────┤
│                                     │
│  Bon dia                            │
│  Good morning                       │
│                                     │
│  "Bon dia! Com estàs avui?"         │
│  "Good morning! How are you today?" │
│                                     │
└─────────────────────────────────────┘
```

### Typography

| Element | Size | Weight | Color |
|---------|------|--------|-------|
| Header | 14sp | Medium | #333333 |
| Catalan word | 24sp | Bold | #333333 |
| Word translation | 16sp | Regular | #666666 |
| Example (Catalan) | 14sp | Italic | #2DB3A2 |
| Example (English) | 13sp | Italic | #888888 |
| Hint text | 12sp | Regular | #AAAAAA |

### Responsive Behavior

- Minimum size: 2x1 cells (word + translation only)
- Medium size: 3x2 cells (adds example sentence)
- Large size: 4x2 cells (comfortable spacing)

## Word Database

### Categories (365 words total)

| Category | Count | Examples |
|----------|-------|----------|
| Greetings & Basics | 30 | Bon dia, Adéu, Si us plau, Gràcies |
| Numbers & Time | 25 | Un, dos, avui, demà, hora |
| Food & Drink | 40 | Pa, aigua, cervesa, esmorzar |
| Family & People | 25 | Mare, pare, amic, noia |
| Places & Directions | 30 | Casa, carrer, esquerra, dreta |
| Weather & Nature | 25 | Sol, pluja, platja, muntanya |
| Common Verbs | 50 | Ser, tenir, anar, voler, poder |
| Adjectives | 35 | Gran, petit, bonic, nou |
| Shopping & Money | 20 | Preu, barat, car, botiga |
| Travel & Transport | 25 | Tren, cotxe, aeroport, bitllet |
| Home & Objects | 30 | Llit, taula, porta, finestra |
| Useful Phrases | 30 | Quant costa?, On és...?, No entenc |

### Data Structure

```json
[
  {
    "word": "Gràcies",
    "translation": "Thank you",
    "example": "Gràcies per la teva ajuda!",
    "exampleTranslation": "Thank you for your help!"
  }
]
```

### Example Sentence Guidelines

- Short (5-10 words)
- Practical context a tourist/learner would encounter
- Uses the word naturally, not forced

## Build & Installation

### Technical Details

- Android Studio project targeting API 26+ (Android 8.0+)
- Kotlin as primary language
- No external dependencies (pure Android SDK)
- Estimated APK size: ~2-3MB

### Permissions

None required - widget runs entirely offline with embedded data.

### Installation Steps

1. Enable "Install from unknown sources" in tablet settings
2. Install the APK via USB transfer, ADB, or cloud download
3. Long-press on Cozyla home screen → Widgets
4. Find "Paraula del Dia" widget
5. Drag to desired position, resize as needed

### Updates

To update word database: rebuild APK with new `words.json`. No auto-update mechanism.
