# InstaMessages ⚡ (Android APK)

A native Android app designed specifically for Instagram Direct Messages without algorithmic feeds, Reels doomscrolling, or recommendations.

---

## 🛡️ Security & Privacy Hardening

- **Isolated WebView Engine**: Uses Android's system Chromium container without middleman servers or proxies.
- **Strict Host Verification**: Rejects sub-domain spoofing and phishing URL substrings; only true `*.instagram.com` domains are handled internally.
- **Zero Local File Access**: `allowFileAccess` and `allowContentAccess` are disabled to block local file-system leaks.
- **Safe Intent Dispatch**: External links sent in messages are strictly validated against `http` and `https` before opening in your default browser.
- **Single-Reel Lock**: You can watch Reels sent to you in chat, but vertical swipe-up gestures to subsequent algorithmic recommendations are disabled.
- **End-to-End Privacy**: All logins, 2FA codes, and message sessions are stored in Android's encrypted native `CookieManager`.

---

## 📲 How to Install

1. Download the latest release: **[`InstaMessages.apk` (v1.0.3)](https://github.com/stfMit/instamessages/releases/download/v1.0.3/InstaMessages.apk)**.
2. Open the downloaded file on your Android phone and select **Install** / **Update**.
3. Open InstaMessages and enjoy distraction-free Direct Messages!

---

## 📁 Repository Structure

```
instamessages/
├── android/                     # Native Android App source code
│   ├── app/src/main/            # Android Manifest, Java logic, and Layouts
│   ├── build.gradle             # Android build configuration
│   └── gradlew                  # Gradle build tool
├── .github/workflows/
│   └── build-apk.yml            # Automated APK compilation workflow
└── README.md
```
