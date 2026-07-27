# Unseen and Strong

An Android app built with Jetpack Compose to support people living with chronic, invisible illness. Offline-first, empathetic, and built around the reality of managing invisible illness day-to-day.

Unseen and Strong is intentionally low-pressure: no streak shame, no punitive reminders, and no productivity-first health tracking. The app is designed to help users notice patterns, preserve energy, self-advocate, and find comfort when symptoms are hard to explain or invisible to others.

## Current status

Phase 1 is the completed revised MVP baseline. Phase 2 advocacy tools are substantially implemented but not complete. Early Phase 3 work has begun with medication and cycle tracking.

## Phase 1 — The Cocoon MVP

Status: completed as the revised core MVP. Voice journaling was intentionally moved to Phase 3 because recording, playback, permissions, and additional testing are outside the offline-first baseline.

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

- Voice journaling is planned for Phase 3.

## Phase 2 — Speak Strong / Advocacy

Status: substantially implemented, not complete.

Currently implemented and accessible:

- Speak Strong script hub
- Gentle / Direct / Firm script tone switching
- Category filtering for All, Doctor, Work, Insurance, Family, Strangers, and Boundary
- Seeded scripts for Doctor, Work, Boundary, Insurance, Family, and Stranger scenarios
- Text-based rehearsal with full-script and section-by-section focus modes
- Saved before-conversation preparation with a goal, desired outcome, private notes, and follow-up planning
- After-conversation reflection with outcome, emotional reflection, goal status, and optional follow-up date
- User-controlled Interaction Log linking that updates an existing linked entry instead of creating duplicates
- Boundary Builder with practice-oriented scripts
- Accommodation request generator with copy-to-clipboard
- Advocacy Resources screen with eligibility-style checklists and validation content
- Interaction Log with person, organization, category, notes, follow-up tracking, and timeline display
- Document Vault for local image-based medical, insurance, and work documents
- Request Log for FMLA, ADA, and disability requests
- Disability Benefits Tracker with persisted stages, statuses, notes, completed dates, deadlines, and approaching-deadline support

Still needed before Phase 2 can be called complete:

- Local notification reminders for follow-ups
- Fuller dedicated STD/LTD workflows
- Audio-based rehearsal or recording, if retained in the final Phase 2 scope
- Broader final testing and CI hardening

## Phase 3 — Medical Intelligence

Status: early implementation has begun, but Phase 3 is not complete.

Implemented or underway:

- Medication tracking
- PRN medication logs
- Medication reaction tracking
- Chronic-friendly cycle tracking
- Initial local pattern-insight data flows

Still planned:

- Voice journaling
- Doctor-ready exports
- Deeper and safer pattern insights
- Additional medical-intelligence workflows defined for Phase 3

Phase 4+ features such as shop integration, physical care boxes, community support, cloud sync, and advanced AI insights are intentionally outside the current MVP.

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
