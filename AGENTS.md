# AGENTS.md

## Scope and intent

- This repository is a **single-module Android app** (`:app`) using **Jetpack Compose + Material 3**.
- The app is written in Kotlin and uses Room/SQLite for local persistence.
- Keep changes minimal, direct, consistent with existing patterns, and easy to run in Android Studio.
- Do not introduce new modules or architecture layers unless the requested work clearly requires them.

## Canonical product guidance

Before making meaningful product, feature, or UI changes, consult:

- [`docs/PRODUCT_VISION.md`](docs/PRODUCT_VISION.md) for product fit, philosophy, privacy direction, and the product north star.
- [`docs/BUILD_PHASES.md`](docs/BUILD_PHASES.md) for roadmap status, scope, timing, and the current v1 boundary.
- [`docs/DESIGN_SYSTEM.md`](docs/DESIGN_SYSTEM.md) for visual language, accessibility, emotional design, and Flare Day behavior.

This file controls repository implementation conventions. If it conflicts with a canonical product document on product intent, phase scope, or design behavior, the relevant canonical document controls.

## Big-picture architecture

- `app/src/main/java/com/example/unseenandstrong/MainActivity.kt` is the main activity and currently coordinates app-level state and navigation across multiple Phase 1–3 screens.
- Feature UI and related view models are organized in substantial packages under `app/src/main/java/com/example/unseenandstrong/ui/`.
- Room entities, DAOs, migrations, and the database live under `app/src/main/java/com/example/unseenandstrong/data/local/`.
- Compose theme composition, color tokens, and typography are centralized under `app/src/main/java/com/example/unseenandstrong/ui/theme/`.
- App metadata and the activity declaration live in `app/src/main/AndroidManifest.xml`.

## Build and test workflows

- Use the Gradle wrapper from repo root (`./gradlew`), not a global Gradle install.
- Core local build: `./gradlew :app:assembleDebug`.
- Local JVM unit tests: `./gradlew :app:testDebugUnitTest`.
- Instrumentation tests (device/emulator required): `./gradlew :app:connectedDebugAndroidTest`.
- Lint check: `./gradlew :app:lintDebug`.
- After code changes, run the relevant checks for the affected area. Do not claim a check passed unless it was run in the current work.

## Project-specific conventions

- Dependencies and most plugin versions are managed in `gradle/libs.versions.toml`; prefer catalog entries instead of adding hard-coded versions.
- Gradle scripts use Kotlin DSL (`build.gradle.kts`), with project repos locked via `RepositoriesMode.FAIL_ON_PROJECT_REPOS` in `settings.gradle.kts`.
- `namespace` is `com.example.unseenandstrong`, while `applicationId` is `com.madebytommi.unseenandstrong`; the current app configuration uses `minSdk = 29`, `targetSdk = 35`, `compileSdk = 35`, and Java 11 compatibility.
- Prefer existing Compose, state, view-model, DAO, and Room migration patterns over parallel abstractions.
- Inspect nearby code and current repository state before editing; preserve unrelated work in a dirty worktree.

## Product and phase boundaries

- Treat `docs/BUILD_PHASES.md` as the canonical roadmap: Phase 1 is complete, Phase 2 is substantially implemented, and Phase 3 is underway.
- Protect the current v1 finish line. Do not pull Phase 4 or Phase 5 features into active work unless the user explicitly requests that scope.
- Care Closet and commerce work belongs to Phase 4. Cloud sync, community features, and advanced AI belong to Phase 5.
- Do not turn a later-phase idea into an implementation dependency for existing offline features.

## Privacy and offline-first behavior

- Core personal, health, and advocacy data is stored locally through Room/SQLite. Preserve useful offline behavior.
- Android backup is currently disabled in `AndroidManifest.xml` with `android:allowBackup="false"`; the XML backup-rule templates present in the repository are not referenced by the manifest.
- Do not claim that cloud backup, cloud sync, Firebase, backend services, or remote health-data storage exist.
- Do not introduce cloud or backend architecture casually. Any deliberate move of sensitive data off-device requires explicit scope plus privacy and security review.

## UI, accessibility, and emotional design

- `docs/DESIGN_SYSTEM.md` is the canonical source for palette, accessibility, semantic colors, copy tone, and Flare Day behavior.
- Design for low sensory load: predictable layouts, clear hierarchy, restrained motion, manageable choice counts, and readable contrast.
- Use existing semantic theme tokens instead of arbitrary hard-coded colors. Accessibility takes precedence when a token does not provide adequate readability.
- Treat Flare Day Mode as a lower-demand interaction state, not only a color change. User-facing screens should preserve essential actions while reducing optional work, visual noise, and cognitive load where appropriate.
- Never add streak shame, punitive reminders, gamified health compliance, or productivity-first health UX.

## Working rules for agents

- Inspect relevant implementation and canonical guidance before editing.
- Make the smallest coherent change that satisfies the request; avoid opportunistic refactors and feature expansion.
- Reuse existing project patterns and keep related previews or tests near the code they cover where practical.
- For user-facing changes, check both standard and Flare Day behavior, larger text/readability, touch targets, screen-reader labeling where needed, and meaning that does not rely on color alone.
- Keep medical insights cautious and descriptive. Do not introduce diagnosis, treatment direction, or false certainty from user-entered data.
- Run relevant build, unit, instrumentation, and lint checks after implementation changes, proportional to the change and available device/emulator support.
