# Unseen & Strong Build Phases

> **Living roadmap for product scope, implementation status, and future expansion**

This document is the canonical build-phase roadmap for **Unseen & Strong**.

It preserves the original product plan while reflecting the application that now exists in the repository.

The roadmap is intentionally phased to protect the project from feature bloat. A feature appearing in a later phase does **not** mean it must be pulled forward simply because it sounds useful.

---

## Status Legend

- ✅ **Complete** — implemented and part of the intended phase baseline
- 🟡 **Substantially implemented** — core functionality exists, but important finishing work remains
- 🟠 **In progress / early implementation** — meaningful work exists, but the phase is not close to complete
- ⚪ **Planned** — not yet implemented
- ⏸ **Deferred** — intentionally moved out of an earlier phase
- 🚫 **Outside current v1 scope** — should not block the first stable public release

---

# Current Product Status

## Phase 1 — The Cocoon MVP

**Status: ✅ Complete**

The revised Phase 1 baseline is complete.

The original Phase 1 goal remains:

> **Immediate survival and comfort tools stored locally.**

The app is offline-first and keeps its core user data on-device.

### Implemented

#### Symptom & Spoon Tracker

- ✅ Daily pain tracking
- ✅ Daily energy / spoon tracking
- ✅ Mood note on standard days
- ✅ Simplified check-in experience during Flare Day Mode

#### Flare Day Mode

- ✅ App-level Flare Day toggle
- ✅ Night Lavender reduced-stimulation theme
- ✅ Simplified versions of major screens
- ✅ Reduced cognitive load
- ✅ Rest-first language
- ✅ Optional actions hidden where appropriate
- ✅ Goals and productivity pressure reduced on flare days

Flare Day Mode is intentionally more than a visual theme. It is a lower-demand interaction state.

#### Digital Comfort Box

- ✅ “I’m Struggling” support area
- ✅ Gentle / Direct / Uplifting comfort tones
- ✅ Local comfort photos
- ✅ Saved comfort photo cards
- ✅ Gentle reminders
- ✅ Offline coping strategies
- ✅ YouTube intent link on standard days
- ✅ Spotify intent link on standard days
- ✅ Reduced-stimulation Flare Day version

#### Invisible Illness Journal

- ✅ Guided survival-oriented journaling
- ✅ Saved journal history
- ✅ Unseen Wins
- ✅ Simplified “one small win” Flare Day flow

#### Gentle Routine Builder

- ✅ Default tiny goals
- ✅ Custom tiny goals
- ✅ Completion toggles
- ✅ Rest-only Flare Day state
- ✅ No streaks
- ✅ No punitive missed-task behavior

### Deferred from the original Phase 1 plan

#### Voice Journaling

- ⏸ Moved to Phase 3

Voice recording introduces additional work around:

- Audio recording
- Playback
- Runtime permissions
- Storage behavior
- Accessibility
- Testing

It is not required for the offline-first Cocoon baseline.

---

# Phase 2 — Speak Strong

**Status: 🟡 Substantially implemented**

Original goal:

> **Managing the administrative, legal, medical, workplace, and social burden of illness.**

Phase 2 has grown into one of the strongest areas of the app.

Most of the originally planned advocacy system now exists.

---

## Speak Strong Hub

### What Do I Say? Scripts

- ✅ Speak Strong script hub
- ✅ Doctor scripts
- ✅ Work scripts
- ✅ Insurance scripts
- ✅ Family scripts
- ✅ Stranger scripts
- ✅ Boundary scripts
- ✅ Category filtering

### Script Tone

- ✅ Gentle
- ✅ Direct
- ✅ Firm

### Practice / Rehearsal Mode

- ✅ Text-based rehearsal
- ✅ Full-script view
- ✅ Section-by-section focus

#### Audio rehearsal

- ⚪ Not yet implemented
- Should only be retained if it provides meaningful value beyond text rehearsal

Audio rehearsal should **not** block v1 unless it is deliberately restored to the Phase 2 release requirement.

---

## Before & After Support

### Before a conversation

- ✅ Save advocacy preparation
- ✅ Goal
- ✅ Desired outcome
- ✅ Private notes
- ✅ Follow-up planning

### After a conversation

- ✅ Outcome reflection
- ✅ Emotional reflection
- ✅ Goal status
- ✅ Optional follow-up date
- ✅ Existing Interaction Log entries can be linked and updated without duplication

---

## Boundary Builder

- ✅ Practice-oriented boundary scripts
- ✅ Low-pressure support for saying no
- ✅ Boundary-specific Speak Strong content

---

# Advocacy Tracker

## Interaction Log

- ✅ Date / interaction history
- ✅ Person
- ✅ Organization
- ✅ Category
- ✅ Notes
- ✅ Follow-up tracking
- ✅ Timeline-style history
- ✅ User-controlled linking to advocacy workflows

---

## Request Log

- ✅ FMLA tracking
- ✅ ADA tracking
- ✅ Disability request tracking
- ✅ Request status
- ✅ Notes
- ✅ Local persistence

---

## Disability Benefits Tracker

- ✅ Persisted benefits stages
- ✅ Statuses
- ✅ Notes
- ✅ Completed dates
- ✅ Deadlines
- ✅ Approaching-deadline support
- ✅ Gentle reminder language

---

## Short-Term / Long-Term Disability Workflows

The current implementation goes beyond the earliest Phase 2 roadmap.

- ✅ Dedicated STD/LTD claim list
- ✅ Claim creation and editing
- ✅ Claim details
- ✅ Claim tasks / checklists
- ✅ Interaction linking
- ✅ Document linking
- ✅ Local persistence

This is considered a valid Phase 2 expansion because it supports the original advocacy goal rather than creating a new product direction.

---

## Document Vault

- ✅ Local image-based document storage
- ✅ Medical document support
- ✅ Insurance document support
- ✅ Work / advocacy document support
- ✅ Linking into disability workflows

The vault remains local-first.

---

## Smart Follow-Up

### Current

- ✅ Follow-up dates can be recorded
- ✅ Deadlines can be tracked
- ✅ Approaching-deadline information exists in parts of the advocacy system
- ✅ User-controlled local notifications can be enabled for advocacy follow-ups and deadlines

Notifications should remain:

- Gentle
- User-controlled
- Non-punitive
- Easy to disable
- Free of streak or compliance language

---

# Advocacy Resources

### Eligibility-style guidance

- ✅ Plain-language advocacy resources
- ✅ Eligibility-style checklists
- ✅ Validation-oriented content

### Draft Request Generator

- ✅ Accommodation request generator
- ✅ Copy-to-clipboard support

### “Invisible Not Imaginary” / validation layer

- ✅ Validation content exists within advocacy resources and supportive copy

### Emotional Support Layer

- ✅ Present throughout the advocacy experience
- ✅ Rose Glow is used selectively for reassurance after difficult advocacy work

---

# Remaining Phase 2 Work

Before Phase 2 is called fully complete:

- [x] Add user-controlled local follow-up notifications
- [ ] Decide whether audio rehearsal belongs in final Phase 2 scope
- [ ] Complete broader UI and workflow testing
- [ ] Continue CI hardening
- [x] Complete design-system normalization
- [x] Run accessibility review across advocacy screens
- [x] Verify Flare Day behavior across every advocacy workflow
- [x] Remove or resolve stale implementation/documentation inconsistencies

---

# Phase 3 — Medical Intelligence

**Status: 🟠 Early implementation / in progress**

Original goal:

> **Data-backed insights to help users understand patterns and communicate with professionals.**

Phase 3 has already begun even though Phase 2 is not formally closed.

That is acceptable, but Phase 3 should not keep expanding the v1 finish line.

Medication and cycle foundations remain available in the codebase, but their screens are intentionally omitted from normal v1 top-level navigation while this phase is incomplete. The Insights screen remains internal without a top-level navigation entry.

---

# Advanced Medication Tracker

## Medication List / Med Log

- ✅ Medication tracking foundation
- ✅ Medication records
- ✅ Medication log records
- ✅ Status and timing-related data structures

### Original planned behavior

- 🟡 Taken / skipped / delayed workflow is partially represented by the current medication system but should receive a dedicated completion review
- 🟡 A “Snooze/Delay” action currently records a medication as `Delayed`; true snooze scheduling or reminder behavior is not yet complete

---

## PRN Medication Log

- ✅ PRN medication data model
- ✅ PRN logging
- ✅ Reason / usage context support
- ✅ Effect-related tracking foundation

---

## Reaction Journal

- ✅ Medication reaction tracking
- ✅ Reaction data model
- ✅ Severity / effect tracking foundation

---

## Medication History & Allergies

- 🟡 Medication history is partially represented through medication records and logs
- ⚪ A dedicated allergies / sensitivities workflow is not yet established as complete

---

## Pattern Recognition

- 🟠 Initial local pattern-insight data flows exist
- 🟠 `InsightsScreen` and its local 7-day trend/adherence data flows exist, but the screen is not currently wired into the app's `HomeScreen` navigation
- ⚪ Deeper medication / fatigue / pain correlation logic remains future work

Pattern insights must remain cautious and descriptive.

The app should not imply medical diagnosis, causation, or treatment advice from simple correlations.

---

## Safety Awareness

- ⚪ Additional safety-aware messaging remains planned

Any future safety messaging should encourage appropriate professional discussion without pretending the app can diagnose or evaluate medical emergencies.

---

# Chronic-Friendly Cycle Tracker

**Status: 🟠 Early implementation**

### Implemented

- ✅ Cycle log foundation
- ✅ Cycle settings
- ✅ Gentle phase naming
- ✅ “Rest & Restore”
- ✅ “Emerging”
- ✅ “Bloom”
- ✅ “Retreat”
- ✅ TTC mode
- ✅ Avoidance mode
- ✅ Standard mode
- ✅ Flow tracking
- ✅ Flare Day support

### Still planned / incomplete

- ⚪ Stronger symptom / spoon overlay
- ⚪ Flare trend analysis
- ⚪ Predictive support
- ⚪ Cycle-linked fatigue pattern support
- ⚪ Additional compassionate context messaging
- ⚪ Deeper integration with local insights

Predictive features should remain conservative and clearly framed as pattern-based support rather than medical prediction.

---

# Voice Journaling

**Status: ⚪ Planned**

Moved here from Phase 1.

Potential scope:

- Audio journal entry recording
- Playback
- Local-only audio storage
- Delete / retention controls
- Permission handling
- Flare Day-friendly recording flow

Voice journaling should remain offline-first unless the project scope is deliberately changed.

---

# Doctor-Ready Exports

**Status: ⚪ Planned**

Original goal:

Generate understandable summaries that a user can bring to a healthcare appointment.

Potential exported information:

- Symptom patterns
- Spoon / energy trends
- Medication adherence history
- PRN usage
- Medication reactions
- Cycle information
- User-selected notes

Exports must clearly distinguish:

- User-entered data
- App-generated summaries
- Pattern observations

They should not present app-generated insights as medical conclusions.

---

# Remaining Phase 3 Work

- [ ] Complete medication workflow review against the original specification
- [ ] Add medication history / allergy support if retained
- [ ] Wire the existing Insights screen into user navigation if it is retained
- [ ] Strengthen local pattern insights safely
- [ ] Expand cycle-to-symptom / spoon integration
- [ ] Implement voice journaling
- [ ] Implement doctor-ready exports
- [ ] Add clear limitations and safety framing to insights
- [ ] Complete accessibility and Flare Day review
- [ ] Add tests for Phase 3 data flows and user-facing calculations

---

# Phase 4 — The Care Closet

**Status: 🚫 Outside current v1 scope**

Original goal:

> **Integrating the physical business and real-world support tools.**

No Phase 4 functionality should block the current application release.

---

## The Shop

### Care Boxes

⚪ Planned concepts:

- Flare Day Care Box
- Emotional Support Box
- Signature Butterfly Box
- Cycle Care Box

### Butterfly Medical Binder

⚪ Planned physical product for:

- Logs
- Lab results
- Appointment notes
- Medical paperwork

### Physical Advocacy Tools

⚪ Planned:

- Wallet-sized script cards
- Appointment checklists
- Advocacy reference tools

### Merchandise

⚪ Possible future:

- Butterfly-themed clothing
- Blankets
- Water bottles
- Stickers

---

# Shop Features

⚪ Future concepts:

- Custom Care Builder
- Gift-A-Box
- Accessibility filters
- Allergen-aware inventory
- Supplement-safety restrictions
- Budget-friendly essential boxes

Original budget target:

**$25–$40 essential box options**

Phase 4 should only begin after the digital product has a stable identity and there is a real reason to integrate commerce.

---

# Phase 5 — Flight Expansion

**Status: 🚫 Outside current v1 scope**

Original goal:

> **Scaling the app and building advanced technical or community capabilities.**

These features introduce substantially different privacy, infrastructure, moderation, security, and operational requirements.

They must not be casually pulled into earlier phases.

---

## Cloud Sync

⚪ Future concept

Potential purpose:

- Multi-device backup
- Secure restore
- Cross-device continuity

Because Unseen & Strong handles sensitive health and advocacy information, cloud sync would require a dedicated privacy and security architecture review before implementation.

“HIPAA-compliant” should never be treated as a simple feature label.

---

## Printable Pages

⚪ Future concept

Potential examples:

- Journal entries
- Symptom summaries
- Binder-ready logs
- Advocacy timelines

Some printable/export functionality may eventually overlap with Phase 3 doctor-ready exports without requiring full Phase 5 infrastructure.

---

## Community Support Circles

⚪ Future concept

Potential features:

- Anonymous support spaces
- Moderated discussion
- “Today I Need…” posts

Community features introduce:

- Moderation
- Abuse prevention
- Privacy risk
- Safety escalation
- Content governance
- Account systems
- Backend infrastructure

They remain intentionally deferred.

---

## Advanced AI Insights

⚪ Future concept

Potential long-term signals:

- Weather
- Medication
- Cycle
- Activity
- Flare history

Advanced AI should only be considered after:

- Sufficient user value is established
- Privacy architecture is appropriate
- Data limitations are explicit
- False-confidence risks are addressed

AI must never turn the app into a diagnostic system.

---

# Monetization & Accessibility

**Status: ⚪ Conceptual only**

The original roadmap included:

### Free

Potentially:

- Basic tracking
- Limited scripts
- Essential comfort tools

### Supporter

Original concept:

**$4.99–$7.99/month**

Potentially:

- Full Speak Strong
- Medical insights
- Exportable reports

### Advocate

Original concept:

**$12.99–$19.99/month**

Potentially:

- Advanced advocacy templates
- SSDI timeline tools
- Multi-case tracking

### Lifetime

Original concept:

**$99–$199 one-time**

### Pay It Forward

Potential donation-supported access for users who cannot afford paid features.

---

## Monetization guardrail

Monetization must never undermine the product's core values.

Essential safety, comfort, accessibility, or basic self-advocacy support should not be made deliberately frustrating in order to force an upgrade.

No monetization decision has been finalized.

---

# Current v1.0 Boundary

The project needs a stable finish line.

For the first strong public v1, the recommended boundary is:

## Required for v1

### Phase 1

- ✅ Complete Cocoon baseline

### Phase 2

Complete the remaining release-quality work:

- [x] Local follow-up notifications
- [ ] Final decision on audio rehearsal
- [ ] Final testing
- [ ] CI hardening
- [x] Accessibility pass
- [x] Design-system normalization
- [ ] Documentation cleanup
- [ ] Release configuration / packaging review

### Phase 3

Phase 3 features that already exist may remain in the app if stable.

However, **finishing all of Phase 3 is not required for v1.0**.

Do not delay v1 solely to add:

- Voice journaling
- Doctor-ready exports
- Advanced prediction
- Additional pattern intelligence

Those can ship in later releases.

---

# Post-v1 Direction

A sensible sequence after v1:

1. Finish remaining Phase 3 medical-intelligence features
2. Improve doctor-ready exports
3. Expand safe local insights
4. Revisit voice journaling
5. Evaluate whether Phase 4 has a real business case
6. Reassess cloud/community/AI only after the core product proves useful and stable

---

# Scope Protection Rules

When evaluating a new feature, ask:

1. Which phase does this belong to?
2. Does it support the original purpose of that phase?
3. Does it increase cognitive load?
4. Does it require cloud infrastructure?
5. Does it introduce privacy or safety risk?
6. Does it delay the current release unnecessarily?
7. Is there already a simpler feature that solves most of the same problem?

If a new idea belongs to Phase 4 or Phase 5, document it there rather than pulling it into the active build.

---

# Product North Star

Unseen & Strong is not being built to maximize features.

It is being built to make difficult days a little easier.

Each phase should strengthen one of four things:

- **Comfort**
- **Understanding**
- **Organization**
- **Self-advocacy**

If a feature does not meaningfully support one of those goals, it probably does not belong in the product.

---

# Roadmap Summary

| Phase | Name | Current status |
|---|---|---|
| Phase 1 | The Cocoon MVP | ✅ Complete |
| Phase 2 | Speak Strong | 🟡 Substantially implemented |
| Phase 3 | Medical Intelligence | 🟠 Early / in progress |
| Phase 4 | The Care Closet | 🚫 Outside current v1 scope |
| Phase 5 | Flight Expansion | 🚫 Outside current v1 scope |
| Monetization | Access / sustainability | ⚪ Conceptual |

The immediate goal is **not** to begin Phase 4.

The immediate goal is to turn the current application into a stable, coherent **v1.0**, then continue Phase 3 deliberately.
