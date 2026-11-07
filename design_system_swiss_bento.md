# Design System Specification: Dark Swiss Minimalism + Modern Bento Grid

## 1. Overview & Architectural Identity
StreakKeeper adopts **Dark Swiss Minimalism combined with a Modern Bento Grid layout**. 
The system emphasizes typographic precision (International Typographic Style), modular card architecture, high contrast monochrome surfaces, and purposeful semantic accents.

---

## 2. Token Architecture

### Surfaces & Backgrounds
| Token Name | Hex Code | Purpose |
|---|---|---|
| `BackgroundPrimary` | `#090909` | Screen canvas & root background |
| `BackgroundSecondary` | `#101010` | Inset sections, grouping containers |
| `CardPrimary` | `#141414` | Default interactive Bento grid cards |
| `CardElevated` | `#1B1B1B` | Raised cards, active states, modals |
| `BackgroundHover` | `#222222` | Desktop hover, pressed state overlay |

### Borders & Dividers (1px solid)
| Token Name | Hex Code | Purpose |
|---|---|---|
| `BorderPrimary` | `#292929` | Default 1px card and container border |
| `BorderSubtle` | `#202020` | Inner separators, table/list dividing lines |

### Monochromatic Typography
| Token Name | Hex Code | Purpose |
|---|---|---|
| `TextPrimary` | `#F5F5F5` | Headings, primary metrics, active labels |
| `TextSecondary` | `#A0A0A0` | Body text, card subtitles |
| `TextMuted` | `#737373` | Metadata, captions, secondary timestamps |
| `TextDisabled` | `#505050` | Inactive controls, placeholder text |

### Typographic Pairing Architecture
* **Display & Streak Metrics: `Bebas Neue` & `Anton`**
  * Condensed, powerful, assertive display font.
  * Used for: Giant streak numbers (`🔥 24`, `100 DAYS`), primary brand titles (`STREAKKEEPER`), and completion percentages.
* **Editorial & Philosophical Body: `Tinos` (Serif / Italic)**
  * Humanistic, balanced, publication-grade book serif.
  * Used for: Philosophical quotes, taglines (*"Consistency over perfection."*), and recovery reflections.
* **Functional Interface UI: `Inter` (Sans-Serif)**
  * Clean, neutral Swiss sans-serif with tabular figures.
  * Used for: Bento card labels, button text, navigation tabs, timestamps.

### Swiss High-Contrast Action Elements
| Token Name | Hex Code | Purpose |
|---|---|---|
| `ButtonPrimaryBg` | `#F5F5F5` | Stark white primary action button |
| `ButtonPrimaryText` | `#090909` | Deep black text on primary button |

### Restrained Semantic Accents (Used Sparingly)
| Token Name | Hex Code | Purpose |
|---|---|---|
| `AccentSuccess` | `#86C99A` | Muted sage green (completion, streak success) |
| `AccentWarning` | `#E5B96B` | Muted amber (minimum goal achieved, streak at risk) |
| `AccentError` | `#E47777` | Muted salmon red (relapse, broken commit) |
| `AccentInfo` | `#83A9E8` | Muted soft blue (planned freeze, vacation mode) |

---

## 3. Spatial & Component Standards
* **Grid Unit:** 8px base grid (`8dp`, `16dp`, `24dp`, `32dp`).
* **Card Border Radius:** `16dp` to `20dp`.
* **Button Border Radius:** `12dp` to `14dp`.
* **Touch Targets:** Minimum `48dp` x `48dp`.
* **No Unnecessary Gradients / Shadows:** Depth is produced through surface lightness differences (`#090909` -> `#141414` -> `#1B1B1B`) and 1px borders (`#292929`), never heavy blurred drop shadows or neon glows.
