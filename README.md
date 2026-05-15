# 🏆 Kabaddi-Arena
### Personal Performance Scout for Grassroots Kabaddi Players

---

## 📌 Overview

Kabaddi-Arena is an Android application designed to provide performance analytics for local “Matti” (mud) Kabaddi players.  

Grassroots Kabaddi tournaments often lack structured performance tracking. Players do not have access to professional statistics such as raid success rate, tackle efficiency, or match-wise performance history. This limits their ability to measure improvement and showcase talent to professional leagues like the Pro Kabaddi League (PKL).

Kabaddi-Arena solves this problem by acting as a Personal Performance Scout, allowing players to log match events and generate performance insights instantly.

---

## 🎯 Problem Statement

Local Kabaddi tournaments lack professional statistical tracking systems. Players:

- Do not know their Raid Success Percentage
- Cannot measure Tackle Efficiency
- Have no structured match performance records
- Struggle to showcase data-driven performance to scouts

This creates a gap between grassroots talent and professional opportunities.

---

## 🚀 Features

### ✅ Match Setup
- Add opponent team name
- Auto-generate match date

### ✅ Live Logger
- Raid Attempt
- Successful Raid
- Tackle Attempt
- Successful Tackle

### ✅ Performance Analytics
- Total Raids
- Raid Success Percentage
- Total Tackles
- Tackle Success Percentage

### ✅ Local Storage
- Match-wise history saved using Room Database

---

## 🛠 Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose
- **Architecture:** MVVM
- **Database:** Room Database
- **Async Handling:** Kotlin Coroutines
- **IDE:** Android Studio

---

## 📦 Installation

### Prerequisites
- Android Studio (latest version)
- Android SDK (Minimum SDK 24)
- Emulator or physical Android device

### Steps

1. Clone the repository:

```bash
git clone https://github.com/your-username/KabaddiArena.git

2.Open the project in Android Studio.
3.Allow Gradle to sync dependencies.
4.Connect emulator or device.
5.Click Run ▶.
```

### Project Strcuture



## Project Structure

```bash
📂 Kabaddi_Arena
│
├── 📂 app
│   └── 📂 src
│       └── 📂 main
│           ├── 📂 java/com/example/my_arena
│           │
│           ├── 📂 model
│           │   ├── Action.kt
│           │   ├── AppState.kt
│           │   ├── MatchDao.kt
│           │   ├── MatchDatabase.kt
│           │   ├── MatchEntity.kt
│           │   ├── MatchRepository.kt
│           │   ├── MatchStats.kt
│           │   └── Player.kt
│           │
│           ├── 📂 navigation
│           │   └── NavGraph.kt
│           │
│           ├── 📂 screen
│           │   ├── DashboardScreen.kt
│           │   ├── MatchScreen.kt
│           │   ├── PreviousMatchesScreen.kt
│           │   ├── ProfileScreen.kt
│           │   ├── ResultScreen.kt
│           │   └── ScoutScreen.kt
│           │
│           ├── 📂 ui/theme
│           └── MainActivity.kt
│
└── README.md
```

### 🔮 Future Enhancements

```
- 🗺️ Player Heatmap Visualization  
  Display raid and tackle positions on a Kabaddi court layout to analyze weak and strong zones.

- 👥 Multi-Player Team Mode  
  Allow tracking and comparison of performance statistics for multiple players within a team.

- 📤 Performance Report Export (PDF)  
  Generate and export a professional match performance report for sharing with coaches and scouts.

- 🏅 Player Ranking System  
  Introduce a rating system based on performance metrics to rank players across matches.
  
- 🔔 Match Reminder & Notification System  
  Notify players to log upcoming matches and training sessions.

- 🧾 Coach Review Notes  
  Allow coaches to add structured feedback and remarks to each match record.

- 📱 Dark Mode & Professional UI Upgrade  
  Improve user experience with customizable themes and enhanced visual design.

- 🔐 Player Profile Authentication  
  Add login and secure player accounts for personal data protection.

