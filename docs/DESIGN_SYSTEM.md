# Unseen & Strong Design System

> **Soft strength. Quiet healing. Becoming.**

This document is the canonical visual and interaction design reference for **Unseen & Strong**.

The app is designed for people navigating chronic and invisible illness, including users who may be dealing with pain, fatigue, migraines, brain fog, anxiety, or limited energy. The interface should feel calm, supportive, emotionally safe, and easy to understand without requiring sustained attention.

The design system should never prioritize visual novelty, gamification, or productivity pressure over comfort and clarity.

---

## 1. Design Principles

### 1.1 Soft contrast

Avoid harsh black-and-white interfaces.

The app should use soft greys, lavender, blush, mauve, and warm accent colors to create readable contrast without feeling clinical or visually aggressive.

Use pure black or pure white only when an accessibility or platform constraint genuinely requires it. Prefer the semantic tokens defined in this document.

### 1.2 Low sensory load

Users may be experiencing migraines, pain, exhaustion, or cognitive overload.

Interfaces should therefore favor:

- Calm surfaces
- Predictable layouts
- Generous spacing
- Clear hierarchy
- Limited simultaneous choices
- Minimal decorative motion
- No flashing or attention-demanding effects
- No unnecessary badges, streaks, urgency, or gamification

### 1.3 Emotional signaling

Color is not merely decoration.

Colors should communicate emotional intent:

- **Blush** = warmth and care
- **Lavender** = calm strength
- **Mauve** = grounded stability
- **Rose** = emotional reassurance
- **Butterfly Glow** = hope, progress, and meaningful moments
- **Night Lavender** = rest and reduced stimulation

Do not use emotionally charged colors merely because they are available.

### 1.4 Zero-guilt interaction design

The interface should never shame the user for missing a task, medication log, journal entry, routine, check-in, or advocacy follow-up.

Prefer language such as:

- “You can come back when you’re ready.”
- “One small thing is enough.”
- “Rest counts.”
- “You don’t have to finish everything today.”

Avoid:

- Streak loss
- Failure states framed as personal shortcomings
- Red warning indicators for routine non-completion
- Productivity-first language
- Artificial urgency

### 1.5 Flare Day is a cognitive-load mode

Flare Day Mode is not simply a dark theme.

When active, the app should:

- Reduce the number of visible actions
- Hide optional or nonessential tasks
- Increase simplicity
- Use quieter copy
- Reduce visual stimulation
- Prefer comfort and rest over productivity
- Preserve access to essential functions

The current implementation of this behavior is intentional and should be preserved.

---

## 2. Brand Palette

### Primary Brand Colors

#### Soft Blush Pink

**Token:** `SoftBlushPink`  
**Hex:** `#F6C1D1`

Use for:

- Primary supportive actions
- Warm highlights
- Comfort UI
- Gentle emphasis
- Selected emotional-support elements

Feeling:

**Warmth, care, gentleness**

---

#### Lavender Purple

**Token:** `LavenderPurple`  
**Hex:** `#B9A6FF`

Use for:

- Key branding
- Navigation accents
- Speak Strong
- Active selections
- General calm emphasis
- Secondary actions

Feeling:

**Calm strength, emotional depth**

---

#### Dusty Mauve Purple

**Token:** `DustyMauve`  
**Hex:** `#8E7CC3`

Use for:

- Secondary buttons
- Section headers
- Grounded visual emphasis
- Cycle-tracking visuals
- Stable informational states

Feeling:

**Grounded calm, stability**

Dusty Mauve should be restored to the implementation rather than replaced with Lavender everywhere.

---

## 3. Neutral Palette

### Soft Cloud Grey

**Token:** `SoftCloudGrey`  
**Hex:** `#E6E6EA`

Use for:

- Standard backgrounds
- Cards
- Panels
- Quiet surfaces

Feeling:

**Quiet, non-intrusive space**

---

### Warm Mist Grey

**Token:** `WarmMistGrey`  
**Hex:** `#B8B8C2`

Use for:

- Borders
- Dividers
- Disabled states
- Neutral status indicators

Feeling:

**Subtle structure, softness**

This should be the preferred neutral-border token.

---

### Deep Fog Grey

**Token:** `DeepFogGrey`  
**Hex:** `#6E6E78`

Use for:

- Standard body text
- Icons
- High-readability labels
- Strong neutral emphasis

Feeling:

**Clarity without harshness**

---

### Pale Cloud White

**Token:** `PaleCloudWhite`  
**Hex:** `#F5F5F5`

This is an implementation-era addition that fits the design system and is retained intentionally.

Use for:

- Text on Night Lavender or other deep Flare Day surfaces
- High-readability content where Soft Cloud Grey does not provide enough contrast
- Limited light foreground use

Feeling:

**Soft clarity**

Do not treat this as the default surface color. It exists mainly to provide readable light contrast without falling back to pure white.

---

## 4. Accent and Emotional Colors

### Butterfly Glow

**Token:** `ButterflyGlow`  
**Hex:** `#D8B4FF`

Use for:

- Meaningful active states
- “Unseen Wins”
- Progress moments
- Important-but-gentle reminders
- Care Closet accents
- Positive milestones

Feeling:

**Transformation, hope**

Butterfly Glow should not become a generic accent used everywhere. Its meaning is stronger when reserved for moments of progress, encouragement, or transformation.

---

### Rose Glow

**Token:** `RoseGlow`  
**Hex:** `#FF9BB3`

Use for:

- Emotional-support prompts
- Reassurance after difficult interactions
- Comfort messages
- Validation
- Warm post-advocacy reflection states

Feeling:

**Reassurance, tenderness**

Rose Glow does **not** require a separate global application mode. It should function primarily as a semantic emotional-support accent within existing screens.

---

### Night Lavender

**Token:** `NightLavender`  
**Hex:** `#4B3F72`

Use for:

- Flare Day background
- Reduced-stimulation surfaces
- Rest-oriented UI states

Feeling:

**Rest, safety, retreat**

Night Lavender is the defining surface color of Flare Day Mode.

---

## 5. UI Mood System

The interface can shift based on the user's state without becoming visually inconsistent.

### Good Day / Standard Mode

Primary palette:

- Soft Blush Pink
- Lavender Purple
- Soft Cloud Grey
- Deep Fog Grey

Desired feeling:

**Open, light, hopeful, calm**

Standard mode may surface the app's full set of relevant actions while still maintaining low-pressure design.

---

### Flare Day Mode

Primary palette:

- Night Lavender
- Lavender Purple
- Soft Blush Pink
- Pale Cloud White
- Deep Fog Grey where appropriate

Desired feeling:

**Rest, safety, retreat, reduced demand**

Behavior matters as much as color.

Flare Day screens should simplify the experience, reduce optional actions, and replace productivity-oriented expectations with supportive language.

---

### Emotional Support State

Primary accents:

- Rose Glow
- Soft Blush Pink
- Butterfly Glow when appropriate

Desired feeling:

**Warm, reassuring, held**

This is a contextual visual treatment, not necessarily a global theme.

Use it for situations such as:

- “I’m struggling” content
- Difficult journal moments
- After-conversation reflections
- Advocacy stress
- Boundary-setting support
- Compassionate validation

---

## 6. Butterfly Transformation Language

The butterfly metaphor is part of the visual identity.

### Cocoon

Associated colors:

- Soft Cloud Grey
- Warm Mist Grey
- Deep Fog Grey

Meaning:

**Rest, protection, surviving, conserving energy**

---

### Emerging

Associated colors:

- Lavender Purple
- Dusty Mauve
- Butterfly Glow

Meaning:

**Finding language, noticing patterns, building confidence**

---

### Flight

Associated colors:

- Soft Blush Pink
- Rose Glow
- Butterfly Glow

Meaning:

**Advocacy, self-trust, expression, growth**

The metaphor should remain subtle. Avoid turning it into gamification or a mandatory progression system.

---

## 7. Semantic Color Guidance

Prefer semantic intent over hard-coded colors inside individual screens.

Suggested roles:

| Semantic role | Preferred token |
|---|---|
| Standard app background | `SoftCloudGrey` |
| Standard body text | `DeepFogGrey` |
| Warm primary action | `SoftBlushPink` |
| Calm secondary action | `LavenderPurple` |
| Grounded secondary emphasis | `DustyMauve` |
| Neutral border/divider | `WarmMistGrey` |
| Flare background | `NightLavender` |
| Flare foreground text | `PaleCloudWhite` |
| Emotional reassurance | `RoseGlow` |
| Progress / meaningful success | `ButterflyGlow` |

Avoid direct `Color.White`, `Color.Black`, or arbitrary color literals inside feature screens when an existing semantic token can serve the same purpose.

---

## 8. Cards and Surfaces

### Standard mode

Prefer:

- Soft Cloud Grey surfaces
- Very subtle tonal variation
- Warm Mist Grey borders where separation is needed

Cards should feel layered without appearing stark against the background.

Avoid default pure-white Material cards unless there is a specific accessibility reason.

### Flare Day

Prefer:

- Night Lavender
- Slight tonal variations of Night Lavender
- Deep Fog Grey only when it produces a calm, readable surface
- Pale Cloud White foreground text

Flare Day cards should reduce visual separation rather than create many competing panels.

---

## 9. Buttons and Actions

Use hierarchy carefully.

### Warm primary action

Use **Soft Blush Pink** when the action should feel welcoming or supportive.

Examples:

- Save a comfort item
- Add something personal
- Return to a supportive hub

### Calm primary / secondary action

Use **Lavender Purple** for:

- Navigation
- Advocacy actions
- Selection states
- General secondary emphasis

### Grounded secondary action

Use **Dusty Mauve** for:

- Less-prominent secondary actions
- Cycle-related actions
- Section-level actions

### Emotional action

Use **Rose Glow** sparingly for:

- Reassurance
- Emotional support
- Reflection-related emphasis

Buttons should not imply that optional actions are urgent.

---

## 10. Status and Progress

Do not rely on conventional red/green success-failure systems unless the information truly requires safety-critical distinction.

Prefer:

- `ButterflyGlow` for positive progress
- `LavenderPurple` for completed or stable states
- `WarmMistGrey` for neutral/pending states
- `SoftBlushPink` for needs-attention states that are not emergencies
- Text labels alongside color

Color should never be the only carrier of meaning.

---

## 11. Cycle Tracking

Cycle tracking has a distinct visual identity within the broader brand.

Prefer:

- Dusty Mauve for cycle-specific emphasis
- Lavender for selected controls
- Soft Blush for warm highlights
- Soft Cloud Grey for background surfaces
- Night Lavender during Flare Day

The cycle tracker should feel grounded and compassionate, not fertility-app clinical.

Original gentle phase language should be retained where appropriate, such as:

- Rest & Restore
- Emerging
- Bloom
- Retreat

---

## 12. Comfort and Emotional Support

Comfort experiences should be the warmest part of the app.

Prefer:

- Soft Blush Pink
- Rose Glow
- Lavender
- Butterfly Glow for especially meaningful supportive moments

The Comfort Box should prioritize:

- Immediate readability
- Minimal friction
- Very few decisions
- Personalized content
- Clear escape from overwhelming screens

On Flare Days, optional actions may disappear entirely.

---

## 13. Speak Strong / Advocacy

Speak Strong should visually communicate calm confidence rather than confrontation.

Prefer:

- Lavender Purple
- Dusty Mauve
- Soft Blush Pink
- Deep Fog Grey
- Rose Glow for emotional support after difficult interactions

Avoid overly aggressive legal or bureaucratic styling.

The user should feel:

**Prepared, supported, and capable — not placed into another stressful administrative interface.**

---

## 14. Accessibility Rules

Every new screen should be evaluated for:

- Readable contrast
- Large enough touch targets
- Clear labels
- Reduced cognitive load
- Screen-reader-friendly content descriptions where needed
- Meaning that does not depend solely on color
- Layout behavior with larger font sizes
- Flare Day simplification
- No unnecessary animation
- No punitive notifications

Accessibility takes priority over strict visual purity. If a palette choice cannot produce sufficient readability, adjust the implementation deliberately and document the exception.

---

## 15. Things We Do Not Do

Do not introduce:

- Streak counters
- Shame-based reminders
- Punitive missed-day states
- Loud red error-heavy interfaces for normal life events
- Achievement systems tied to health compliance
- High-contrast black-and-white visual themes
- Excessive gradients
- Neon accents
- Dense dashboards
- Constant pop-ups
- Decorative animation that increases fatigue
- Arbitrary colors outside the design system without documenting why

---

## 16. Implementation Notes

The current Compose implementation already includes:

- `SoftBlushPink`
- `LavenderPurple`
- `SoftCloudGrey`
- `DeepFogGrey`
- `NightLavender`
- `ButterflyGlow`
- `PaleCloudWhite`

The implementation should restore:

- `DustyMauve`
- `WarmMistGrey`
- `RoseGlow`

`SoftBorderGray` should normally be replaced by `WarmMistGrey` unless a distinct border token is intentionally retained and documented.

Direct uses of pure `Color.White` should be reviewed and replaced with a softer semantic token where accessibility allows.

The legacy Android XML color palette should either be removed if unused or reconciled with this design system so that the repository does not appear to contain two competing palettes.

---

## 17. Design Review Checklist

Before merging a new user-facing screen or major visual change, verify:

- [ ] Uses defined semantic palette tokens
- [ ] Avoids unnecessary pure black or white
- [ ] Works in Standard Mode
- [ ] Works in Flare Day Mode
- [ ] Flare Day reduces cognitive load where appropriate
- [ ] Text remains readable at increased font sizes
- [ ] Color is not the sole indicator of status
- [ ] Actions are clearly prioritized
- [ ] Copy avoids guilt and productivity pressure
- [ ] Emotional colors are used intentionally
- [ ] UI does not become visually busier than necessary
- [ ] New colors, if absolutely necessary, are added to this document before spreading across the codebase

---

## 18. Design North Star

Unseen & Strong should feel like:

**A safe, soft place to land when the rest of life already asks too much.**

The visual system should communicate that the user's pain, fatigue, limitations, advocacy needs, and small victories are all worth taking seriously.

The app does not exist to optimize a healthy person.

It exists to **comfort, validate, organize, and empower someone whose struggle may be unseen.**
