# 🌍 Language Learner — AI-Powered Speaking App

An interactive Android application built with **Jetpack Compose**, **Firebase**, and **Google Gemini AI** designed to help users master conversational language fluency through dynamic real-world roleplay and real-time speech evaluation.

---

## ✨ Key Features

- **🗣️ Free-form Roleplay Practice**: Instead of repeating rigid textbook sentences, an AI partner presents dynamic, unpredictable scenarios with task goals and vocabulary hints.
- **🤖 Real-time Gemini AI Evaluation**:
  - Fluency score (0–100%)
  - Detailed grammar feedback and sentence structure corrections
  - Constructive AI fluency and pronunciation tips
  - *"How a Native Speaker Might Say It"* polished suggestions
- **🔊 Native Audio Pronunciation (TTS)**: Built-in Android Text-to-Speech allows learners to listen to native pronunciation before speaking.
- **⚡ Haptic Touch Feedback**: Tactile vibration responses when recording voice and receiving AI evaluations.
- **🌐 12 Supported Languages**: English, Hindi, Punjabi (ਪੰਜਾਬੀ), Spanish, French, German, Italian, Japanese, Korean, Mandarin, Portuguese, and Russian.
- **🎭 12 Realistic Situations**: Café & Restaurant, Travel & Airport, Shopping & Market, Meeting People & Small Talk, Hotel & Lodging, Taxi & Transit, Phone Reservations, Hobbies, Pharmacy & Emergencies, Work & Business, Banking, and Campus Life.
- **📊 Cloud Progress Tracking**: Firebase Firestore saves all practice history, displaying live average fluency, session counts, language breakdown, and calendar-accurate weekly activity.

---

## 🛠️ Tech Stack & Architecture

- **Platform**: Android (Native)
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: Single-Activity, Compose Navigation, AppState State Holder
- **AI Engine**: Google Generative AI SDK (`gemini-3.5-flash-lite`)
- **Speech & Audio**: Android `SpeechRecognizer` (dynamic BCP-47 locales) & `TextToSpeech`
- **Backend**: Firebase Authentication (Email/Password) & Cloud Firestore

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or newer
- JDK 17+
- Android Device or Emulator running Android 8.0 (API 26) or higher

### Setup Instructions
1. **Clone the repository**:
   ```bash
   git clone [https://github.com/kalpana222/Language-Learner.git](https://github.com/kalpana222/Language-Learner.git)
