# Family Dashboard for Cozyla Calendar

## Overview

A custom family dashboard web application designed for the Cozyla 24" digital calendar, mounted in portrait orientation. The dashboard displays calendars, shopping list, todos, weather, photos, and a daily Catalan word for language learning.

**Target Device:** Cozyla Calendar+ 2 (24" touchscreen, 1920x1080, portrait = 1080x1920)

**Hosting:** Cloudflare Pages + Cloudflare Workers (serverless functions)

## Features

| Feature | Data Source | Interaction |
|---------|-------------|-------------|
| Calendars (James, Katy, Lottie, All) | Google Calendar + Outlook | View, Add, Edit |
| Shopping List | Bring! (unofficial API) | View, Check, Add |
| TODO List | Todoist | View, Check, Add |
| Weather | Open-Meteo (free) | View only |
| Catalan Word of the Day | Local JSON (365 words) | View only |
| Daily Photo | Google Photos album | View only |
| Date & Time | System | View only |

## Architecture

### Stack

- **Frontend:** HTML/CSS/JavaScript (vanilla or Preact)
- **Backend:** Cloudflare Workers for secure API proxying
- **Storage:** Cloudflare KV for OAuth tokens and caching
- **Deployment:** Git push to Cloudflare Pages

### Data Flow

```
Cozyla Browser  →  Cloudflare Pages (static HTML/CSS/JS)
                         ↓
                   Cloudflare Worker (secure API proxy)
                         ↓
              Todoist / Google / Bring / Weather APIs
```

The Worker holds API credentials securely, makes authenticated requests, and returns clean data to the frontend. This prevents exposing secrets in client-side JavaScript.

### API Authentication

| Service | Auth Method | Storage |
|---------|-------------|---------|
| Google Calendar | OAuth2 + refresh token | Cloudflare KV |
| Outlook Calendar | OAuth2 (Microsoft Graph) | Cloudflare KV |
| Google Photos | OAuth2 + refresh token | Cloudflare KV |
| Todoist | API token | Worker env secret |
| Bring! | Username/password (unofficial) | Worker env secret |
| Open-Meteo | None required | N/A |

## Layout Design (Portrait 1080x1920)

```
┌─────────────────────────────────────┐
│                                     │
│      Daily Photo Header             │  ~300px
│      (from Google Photos album)     │
│                                     │
├─────────────────────────────────────┤
│                                     │
│      14:32        Sun 19 January    │  ~120px
│                                     │
├───────────┬───────────┬─────────────┤
│  Today    │  Monday   │  Tuesday    │
│   ☀️ 18°  │   🌧️ 14°  │   ⛅ 16°    │  ~150px
│  H:21 L:12│  H:15 L:10│  H:18 L:11  │
├───────────┴───────────┴─────────────┤
│  James  │  Katy  │ Lottie │  All   │
├─────────────────────────────────────┤
│                                     │
│  09:00  Standup call               │
│  12:30  Lunch with client          │  ~400px
│  15:00  School pickup              │
│                                     │
│         [ + Add Event ]            │
├────────────────┬────────────────────┤
│  🛒 Shopping   │  ✅ To-Do          │
├────────────────┼────────────────────┤
│ ☐ Milk        │ ☐ Book flights     │
│ ☐ Bread       │ ☐ Call dentist     │  ~550px
│ ☐ Nappies     │ ☑ Fix bike         │
│               │                    │
│  [ + Add ]    │   [ + Add ]        │
├────────────────┴────────────────────┤
│  🇪🇸 Paraula del dia                │
│                                     │
│  Papallona  →  Butterfly            │  ~150px
│  "La papallona vola pel jardí"     │
│  (The butterfly flies through the   │
│   garden)                           │
└─────────────────────────────────────┘
```

## Component Details

### 1. Photo Header (300px)

- Displays one photo per day from a specified Google Photos album
- Selection is deterministic based on date (cycles through album)
- Soft gradient fade at bottom for visual blending
- Optional: tap to view fullscreen

### 2. Time & Date (120px)

- Large, readable digital clock (updates every minute)
- Full date with day name: "Sun 19 January"
- Centered, minimal styling

### 3. Weather Strip (150px)

- **Source:** Open-Meteo API (free, no key required)
- Shows today + 2 days forecast
- Each day: weather icon, temperature, high/low
- Location: Home (configured in Worker)

### 4. Calendar Tabs (400px)

**Four separate calendars:**

| Tab | Owner | Source |
|-----|-------|--------|
| James | Personal + Work | Google Calendar + Outlook (Microsoft Graph) |
| Katy | Personal | Google Calendar |
| Lottie | Activities/appointments | Google Calendar |
| All | Family-wide events | Shared Google Calendar |

**Color coding:**
- James: Blue
- Katy: Purple
- Lottie: Pink/Coral
- All: Green

**Interactions:**
- Tap tabs to switch calendar view
- Tap event to view details / edit
- Tap "+ Add Event" to create new event (modal with calendar selector)
- Swipe left/right to change day

**Display:**
- Shows today's events by default
- Timeline format with times on left
- Events from "All" calendar could show as banner on other tabs (e.g., "🏖️ Holiday")

### 5. Shopping List - Bring! (275px, left column)

- Displays items from shared Bring! list
- Tap item to check off (strikes through)
- Tap "+ Add" opens input modal with on-screen keyboard
- Uses unofficial Bring API (credentials stored in Worker)
- Syncs every 60 seconds + optimistic UI updates

### 6. TODO List - Todoist (275px, right column)

- Displays items from a designated Todoist project (e.g., "Family")
- Shows task name, due date indicator, priority color
- Tap to mark complete
- Tap "+ Add" to create new task
- Todoist API is official and well-documented
- Syncs every 60 seconds + optimistic UI updates

### 7. Catalan Word of the Day (150px)

- **Source:** Local JSON file with 365 entries
- Each entry contains:
  - Catalan word
  - English translation
  - Example sentence in Catalan
  - Example sentence translation
- Word selection based on day of year (deterministic)
- Same word shows for entire day, cycles annually

**Content categories for 365 words:**
- Common nouns (animals, food, household items)
- Verbs (daily actions)
- Adjectives (colors, descriptions)
- Family & relationship terms
- Numbers and time
- Weather and nature
- Useful phrases and expressions

## Technical Implementation

### Cloudflare Worker Endpoints

```
GET /api/calendar/:person     - Fetch calendar events
POST /api/calendar/:person    - Create event
PUT /api/calendar/:person/:id - Update event

GET /api/shopping             - Fetch Bring! list
POST /api/shopping            - Add item
PUT /api/shopping/:id         - Check/uncheck item

GET /api/todos                - Fetch Todoist tasks
POST /api/todos               - Create task
PUT /api/todos/:id            - Complete/update task

GET /api/weather              - Fetch weather (cached)
GET /api/photo                - Get today's photo URL
GET /api/word                 - Get today's Catalan word
```

### Refresh & Caching Strategy

| Data | Cache Duration | Refresh Trigger |
|------|----------------|-----------------|
| Calendar events | 5 minutes | Poll + on interaction |
| Shopping list | 1 minute | Poll + on interaction |
| TODO list | 1 minute | Poll + on interaction |
| Weather | 30 minutes | Poll |
| Photo | 24 hours | Daily at midnight |
| Catalan word | 24 hours | Daily at midnight |

### Error Handling

- **API failure:** Show cached data with "Last updated X mins ago" indicator
- **Stale data:** Subtle warning icon on affected component
- **Network offline:** Full offline indicator, show all cached data
- **Auto-retry:** Every 30 seconds on failure

### OAuth Setup (One-time)

1. Create Google Cloud project with Calendar and Photos APIs enabled
2. Create Microsoft Azure app registration for Outlook access
3. Configure OAuth consent screens
4. Run initial auth flow to obtain refresh tokens
5. Store tokens in Cloudflare KV

## Touchscreen Considerations

- **Minimum tap target:** 44x44px for all interactive elements
- **Scroll areas:** Smooth scrolling with momentum
- **Modals:** Large input fields, on-screen keyboard friendly
- **Feedback:** Visual tap feedback (ripple/highlight effect)
- **Swipe gestures:** Calendar day navigation

## Security

- All API credentials stored in Cloudflare Worker secrets (never client-side)
- OAuth refresh tokens in Cloudflare KV (encrypted at rest)
- HTTPS only
- No sensitive data in localStorage
- Bring! credentials: stored securely, unofficial API risk accepted

## Future Enhancements (Out of Scope)

- Voice commands via device microphone
- Multiple language word-of-the-day rotation
- Meal planning integration
- Chore assignment and tracking
- Screen dimming based on time of day
- Integration with smart home devices

## Implementation Plan

1. **Project setup:** Cloudflare Pages + Workers scaffolding
2. **Static layout:** HTML/CSS for portrait display (use frontend-design skill)
3. **Time/Date/Weather:** Simplest components first
4. **Catalan word list:** Create 365-word JSON file
5. **Todoist integration:** API setup and CRUD operations
6. **Bring! integration:** Unofficial API research and implementation
7. **Google Calendar:** OAuth flow and API integration
8. **Outlook Calendar:** Microsoft Graph API integration
9. **Google Photos:** OAuth and album access
10. **Polish:** Error handling, loading states, transitions
11. **Testing:** On actual Cozyla device
12. **Deployment:** Production Cloudflare setup
