# Photo of the Day Widget - Design

## Overview

A simple Android widget that displays a random photo from a user-selected folder, changing daily at midnight. Tapping the photo opens it in the device's gallery app.

## Core Behavior

- **Photo selection:** Uses day-of-year as a random seed. Same photo shows all day, but the sequence isn't predictable. Changes at midnight.
- **Tap action:** Opens the photo in the device's default gallery/image viewer using `ACTION_VIEW` intent.
- **Display:** Photo fills the widget edge-to-edge (center-crop), no borders, headers, or overlays.
- **Empty state:** Shows "Tap to select folder" which opens the settings activity.

## Folder Configuration

- **Settings Activity:** Simple single-screen app launched from app icon or by tapping an unconfigured widget.
- **Folder picker:** Android's built-in `ACTION_OPEN_DOCUMENT_TREE` grants persistent read permission to the selected folder without requiring broad storage permissions.
- **Supported formats:** JPG, PNG, WEBP.
- **Storage:** Folder URI saved in SharedPreferences.
- **Reconfigure:** User can tap the app icon anytime to change the folder.

## Architecture

**Separate app** in the same repository:

```
Cozyla/
├── CatalanWordWidget/    (existing)
├── PhotoWidget/          (new)
└── .github/workflows/    (shared CI)
```

**Components:**

| Component | Purpose |
|-----------|---------|
| `PhotoWidgetProvider` | AppWidgetProvider that loads and displays the photo |
| `FolderPickerActivity` | Settings screen with folder selection |
| `PhotoRepository` | Handles folder access, lists images, picks today's random photo |

**Widget refresh:** Updates at midnight via `AlarmManager`.

## Error Handling

| Scenario | Behavior |
|----------|----------|
| Folder deleted/moved | Shows "Folder not found - tap to reconfigure" |
| Empty folder | Shows "No photos found - tap to select folder" |
| Corrupt image | Skip and pick another random photo |
| Permission revoked | Prompt to re-select folder |
| Any widget size | Photo scales with center-crop to fill space |

## Tech Stack

- Kotlin
- Android App Widgets (RemoteViews)
- Storage Access Framework (SAF) for folder picker
- SharedPreferences for settings
- AlarmManager for midnight updates
