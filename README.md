# Unseen and Strong

An Android app built with Jetpack Compose to support people living with chronic, invisible illness. Offline-first, empathetic, and built around the reality of managing invisible illness day-to-day.

Unseen and Strong is intentionally low-pressure: no streak shame, no punitive reminders, and no productivity-first health tracking. The app is designed to help users notice patterns, preserve energy, self-advocate, and find comfort when symptoms are hard to explain or invisible to others.

## Current status

Phase 1 is now treated as the active MVP baseline. Phase 2 advocacy tools are also partially implemented, but Phase 3+ features are intentionally out of scope for the current MVP.

## Phase 1 — The Cocoon MVP

Status: implemented as the core MVP, with the exception of voice journaling, which has been moved to Phase 3.

Implemented Phase 1 features:

- Daily Symptom & Spoon Tracker
  - Pain slider
  - Energy/spoons slider
  - Mood note on standard days
  - Simplified check-in flow on Flare Day Mode
- Flare Day Mode
  - App-level toggle
  - Softer Night Lavender visual mode
  - Simplified versions of major screens
  - Rest-first copy that lowers expectations instead of adding pressure
- Digital Comfort Box
  - Gentle, Direct, and Uplifting comfort tones
  - Local comfort photo picker and saved comfort photo cards
  - Gentle reminders
  - Offline coping strategies
  - Intent links to YouTube and Spotify on standard days
  - Reduced-stimulation comfort view on Flare Day Mode
- Invisible Illness Journal
  - Guided survival-oriented journaling
  - Unseen Wins tracker
  - Saved entry history
  - Simplified one-small-win flow on Flare Day Mode
- Gentle Routine Builder
  - Default tiny goals
  - Custom tiny goal creation
  - Completion toggles
  - Flare Day rest-only state that hides goal creation and gives explicit permission to rest

Moved out of Phase 1:

- Voice journaling is now planned for Phase 3 because it adds audio permissions, recording/playback UX, and more testing complexity than the offline MVP needs right now.

## Phase 2 — Speak Strong / Advocacy

Status: partially implemented, not complete.

Currently implemented:

- Speak Strong script hub
- Gentle / Direct / Firm script tone switching
- Seeded scripts for Doctor, Work, and Boundary scenarios
- Boundary Builder with practice-oriented scripts
- Accommodation request generator with copy-to-clipboard
- Advocacy Resources screen with eligibility-style checklists and validation content
- Interaction Log with person, organization, category, notes, follow-up tracking, and timeline display
- Document Vault for local image-based medical, insurance, and work documents

Still needed before Phase 2 can be called complete:

- Dedicated benefits tracker screen
- Fuller ADA/FMLA/STD/LTD request tracking workflow
- Stronger before-and-after advocacy support flow
- More meaningful script practice mode
- Local notification reminders for follow-ups

## Later phases

Phase 3 will focus on medical intelligence and deeper health tooling, including:

- Voice journaling
- Advanced medication tracking
- Doctor-ready exports
- Chronic-friendly cycle tracking
- Safer pattern insights

Phase 4+ features like shop integration, physical care boxes, community support, cloud sync, and advanced AI insights are intentionally not part of the current MVP.

## Tech stack

- Android
- Kotlin
- Jetpack Compose
- Material 3
- Room / SQLite local storage
- Offline-first architecture

## Local development

From the repo root:

```bash
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
./gradlew :app:lintDebug
```

Instrumentation tests require a connected device or emulator:

```bash
./gradlew :app:connectedDebugAndroidTest
```
