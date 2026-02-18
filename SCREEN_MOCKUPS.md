# Wellness Log - Screen Mockups and UI Description

## App Overview

The Wellness Log app consists of three main screens, all built with Material Design 3 and Jetpack Compose.

## Screen 1: Main Screen (Home)

### Layout
```
┌─────────────────────────────────────┐
│ Wellness Log      Weight Graph  Gym Stats │ ← Top Bar
├─────────────────────────────────────┤
│                                     │
│ ┌─────────────────────────────────┐ │
│ │ Date                            │ │
│ │ Dec 18, 2024                    │ ← Date Card (tappable)
│ └─────────────────────────────────┘ │
│                                     │
│ ┌─────────────────────────────────┐ │
│ │ Gym Visit               [ ✓ ]   │ │
│ │                                 │ │
│ │ Calorie Deficit         [   ]   │ ← Checkboxes Card
│ └─────────────────────────────────┘ │
│                                     │
│ ┌─────────────────────────────────┐ │
│ │ Weight (kg)                     │ │
│ │                                 │ │
│ │    (-)      75.3        (+)     │ ← Weight Control Card
│ │                                 │ │
│ └─────────────────────────────────┘ │
│                                     │
│ ┌─────────────────────────────────┐ │
│ │           SAVE                  │ ← Save Button
│ └─────────────────────────────────┘ │
│                                     │
└─────────────────────────────────────┘
```

### Components

1. **Top App Bar**
   - Title: "Wellness Log"
   - Actions: "Weight Graph" and "Gym Stats" text buttons
   - Material 3 theme with primary color

2. **Date Selection Card**
   - Shows currently selected date
   - Format: "MMM dd, yyyy" (e.g., "Dec 18, 2024")
   - Tappable - opens Material DatePicker
   - DatePicker only allows today and past dates
   - Default: Today's date

3. **Activity Tracking Card**
   - Two checkboxes in a vertical list:
     - "Gym Visit" checkbox
     - "Calorie Deficit" checkbox
   - Material 3 Checkbox components
   - State persists when switching dates

4. **Weight Control Card**
   - Title: "Weight (kg)"
   - Three elements in a row:
     - Minus button (Filled Tonal Icon Button)
     - Weight display (large text, centered)
     - Plus button (Filled Tonal Icon Button)
   - Buttons adjust weight by 0.1 kg increments
   - Weight pre-fills with previous day's value
   - Shows "-" if no weight data

5. **Save Button**
   - Full-width Material 3 Button
   - Primary color
   - Saves all data (date, gym visit, calorie deficit, weight)

### Behavior

- On load: Sets date to today, loads any existing data for today
- On date change: Loads data for selected date, or shows defaults
- On save: Persists all fields to local storage
- Weight field: Automatically populated from yesterday's entry
- Navigation: Buttons in top bar navigate to other screens

### Theme
- Primary color: Green (#4CAF50)
- Material 3 Design
- Light/Dark mode support
- Cards with elevation
- Consistent spacing (16dp padding)

---

## Screen 2: Weight Graph Screen

### Layout
```
┌─────────────────────────────────────┐
│ ← Back    Weight Graph              │ ← Top Bar
├─────────────────────────────────────┤
│                                     │
│ ┌──────────────┐ ┌────────────────┐ │
│ │ Start Date   │ │ End Date       │ │
│ │ Nov 18       │ │ Dec 18         │ ← Date Range Cards
│ └──────────────┘ └────────────────┘ │
│                                     │
│ ┌─────────────────────────────────┐ │
│ │                                 │ │
│ │        Weight Line Chart        │ │
│ │                                 │ │
│ │         /\                      │ │
│ │        /  \      /\             │ │
│ │       /    \    /  \            │ │ ← Chart Card
│ │  ----      \  /    ----         │ │
│ │             \/                  │ │
│ │                                 │ │
│ │  Nov  │  Nov  │  Dec  │  Dec    │ │
│ │   20  │   28  │   06  │   14    │ │
│ └─────────────────────────────────┘ │
│                                     │
└─────────────────────────────────────┘
```

### Components

1. **Top App Bar**
   - Back button (text button)
   - Title: "Weight Graph"

2. **Date Range Selection**
   - Two tappable cards side by side:
     - Start Date card
     - End Date card
   - Each shows date in "MMM dd" format
   - Tap to open DatePicker
   - Default: Last 30 days

3. **Chart Card**
   - Uses MPAndroidChart LineChart
   - Shows weight progression over selected date range
   - Features:
     - Green line (#4CAF50)
     - Circular markers at data points
     - Interactive (pinch to zoom, drag to pan)
     - X-axis: Dates
     - Y-axis: Weight in kg
   - Empty state: "No weight data in selected range"

### Behavior

- On load: Shows last 30 days of weight data
- On date range change: Updates chart automatically
- Chart only shows dates with weight data
- Smooth line interpolation between points
- Touch interaction enabled for detailed view

---

## Screen 3: Gym Contributions Screen

### Layout
```
┌─────────────────────────────────────┐
│ ← Back    Gym Contributions         │ ← Top Bar
├─────────────────────────────────────┤
│                                     │
│ Gym visits in 2024                  │
│                                     │
│ ┌─────────────────────────────────┐ │
│ │ ▢▢▢▢▢▢■▢▢■▢▢▢■▢▢▢■▢▢▢▢▢▢▢▢▢▢... │ │ ← Mon
│ │ ▢■▢▢▢▢▢▢▢▢■▢▢▢▢■▢▢■▢▢▢▢■▢▢▢... │ │ ← Tue
│ │ ▢▢▢■▢▢▢▢■▢▢▢■▢▢▢▢■▢▢▢■▢▢▢■... │ │ ← Wed
│ │ ▢▢▢▢▢■▢▢▢▢▢■▢▢▢■▢▢▢▢■▢▢▢▢▢... │ │ ← Thu
│ │ ■▢▢▢▢▢▢■▢▢▢▢▢■▢▢▢■▢▢▢▢▢■▢▢... │ │ ← Fri
│ │ ▢▢▢▢■▢▢▢▢▢■▢▢▢▢▢■▢▢▢■▢▢▢▢▢... │ │ ← Sat
│ │ ▢▢■▢▢▢▢▢▢■▢▢▢▢■▢▢▢▢■▢▢▢■▢▢... │ │ ← Sun
│ └─────────────────────────────────┘ │
│                                     │
│ ┌─────────────────────────────────┐ │
│ │ Statistics                      │ │
│ │                                 │ │
│ │ Total gym visits: 47            │ │
│ │ Attendance rate: 36%            │ │ ← Statistics Card
│ └─────────────────────────────────┘ │
│                                     │
└─────────────────────────────────────┘

Legend:
■ = Green square (gym visit)
▢ = Gray square (no gym visit)
```

### Components

1. **Top App Bar**
   - Back button
   - Title: "Gym Contributions"

2. **Year Header**
   - Text: "Gym visits in [current year]"
   - Large, prominent typography

3. **Contribution Grid Card**
   - GitHub-style visualization
   - Each square = one day
   - Grid layout:
     - 7 rows (one per day of week: Mon-Sun)
     - Multiple columns (one per week)
     - Starts from January 1st of current year
     - Ends at today's date
   - Colors:
     - Green (#4CAF50): Day with gym visit
     - Light gray (#EEEEEE): Day without gym visit
   - Square size: 12dp x 12dp
   - Spacing: 3dp between squares
   - Scrollable horizontally if needed

4. **Statistics Card**
   - Title: "Statistics"
   - Two metrics:
     - Total gym visits: Count of gym visit days
     - Attendance rate: Percentage (visits / days elapsed)

### Behavior

- On load: Fetches all entries from January 1 to today
- Grid updates automatically when data changes
- Shows current year only
- Calculates statistics dynamically
- Scrollable for full year view

---

## Color Scheme

### Light Theme
- **Primary**: #4CAF50 (Green 500)
- **Secondary**: #8BC34A (Light Green 500)
- **Tertiary**: #CDDC39 (Lime 500)
- **Background**: White
- **Surface**: White
- **On Primary**: White
- **On Background**: Black (87% opacity)

### Dark Theme
- **Primary**: #4CAF50 (Green 500)
- **Secondary**: #8BC34A (Light Green 500)
- **Tertiary**: #CDDC39 (Lime 500)
- **Background**: #121212
- **Surface**: #1E1E1E
- **On Primary**: White
- **On Background**: White (87% opacity)

---

## Typography

Material 3 default typography with Roboto font family:
- **Display Large**: 57sp
- **Display Medium**: 45sp
- **Display Small**: 36sp
- **Headline Large**: 32sp
- **Headline Medium**: 28sp
- **Headline Small**: 24sp
- **Title Large**: 22sp
- **Title Medium**: 16sp
- **Title Small**: 14sp
- **Body Large**: 16sp
- **Body Medium**: 14sp
- **Body Small**: 12sp
- **Label Large**: 14sp
- **Label Medium**: 12sp
- **Label Small**: 11sp

---

## Navigation Flow

```
         Main Screen (Home)
              │
      ┌───────┴───────┐
      │               │
      ▼               ▼
Weight Graph    Gym Contributions
      │               │
      └───────┬───────┘
              │
              ▼
         Main Screen
```

- Main screen is the entry point
- Two navigation actions from main screen
- Back navigation returns to main screen
- No deep linking between weight graph and gym contributions
- Simple stack-based navigation

---

## Accessibility

- All interactive elements have minimum 48dp touch target
- Color contrast meets WCAG AA standards
- Content descriptions for screen readers
- Keyboard navigation support
- Dynamic font sizing support
- High contrast mode compatible

---

## Responsive Design

- Adapts to different screen sizes
- Portrait orientation optimized
- Landscape support
- Tablet support (scales appropriately)
- Minimum screen width: 320dp

---

## Animations

- Screen transitions: Standard Material motion
- Card clicks: Ripple effect
- Button presses: State layer animation
- DatePicker: Slide-in from bottom
- Chart: Smooth line drawing
- Navigation: Crossfade transitions

---

## Error States

1. **No Data**
   - Weight Graph: "No weight data in selected range"
   - Gym Contributions: Empty grid (all gray squares)

2. **Invalid Input**
   - Weight cannot be negative
   - Date cannot be in the future

3. **Save Failed**
   - Snackbar: "Failed to save data"

---

## Loading States

- Initial load: Show placeholder UI
- Data loading: Shimmer effect on cards
- Chart loading: Centered progress indicator
- No blocking UI during saves (optimistic updates)
