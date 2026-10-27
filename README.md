# InstaMessages ⚡ Distraction-Free Instagram Direct Messages

A dedicated **Android App (`.apk`)**, **Windows Desktop App**, and **Browser Companion** designed specifically for Instagram Direct Messages.

Eliminates algorithmic feeds, the Reels tab, Explore recommendations, and infinite scrolling so you can message friends and clients with zero distractions.

---

## 🚀 How to Use InstaMessages

### 1. Android Phone (Native APK)
The Android app runs in an isolated container so Android **cannot** hijack your links into the official Instagram app:
1. Download **[`InstaMessages.apk`](https://github.com/stfMit/instamessages/releases/download/v1.0.0/InstaMessages.apk)** on your phone.
2. Tap the file and select **Install**.
3. Open InstaMessages and enjoy Direct Messages with Reels, Feed, and Explore completely removed!

---

### 2. Dedicated Windows Desktop App
A standalone native desktop window focused strictly on your Direct inbox:
1. Open this folder in your terminal.
2. Install dependencies:
   ```bash
   npm install
   ```
3. Run the app:
   ```bash
   npm start
   ```

---

### 3. Chrome / Edge / Brave Extension
Enforces strict "DM-Only Mode" across desktop web browsing:
1. Open `chrome://extensions` or `edge://extensions` in your browser.
2. Enable **Developer mode** (toggle in top right).
3. Click **Load unpacked**.
4. Select the `extension/` folder from this repository.

---

## 🛡️ Privacy & Security
- **Direct Encrypted Connection:** All requests connect directly to `instagram.com`.
- **Zero Credential Access:** Your login and two-factor authentication remain completely between you and Instagram.
- **No Third-Party Intermediaries:** No middleman servers or proxies.

---

## 📁 Repository Structure

```
instamessages/
├── android/                     # Native Android App source code
│   ├── app/src/main/            # Android Manifest, WebView, Layouts
│   ├── build.gradle             # Android build setup
│   └── gradlew                  # Gradle build tool
├── src/desktop/                 # Native Windows Desktop App (Electron)
│   ├── main.js                  # Main window configuration
│   ├── preload.js               # Route enforcement & script injection
│   └── distraction-free.css     # CSS rules hiding Reels, Feed, Explore
├── extension/                   # Chrome / Edge / Brave Extension
│   ├── manifest.json            # Manifest V3
│   ├── rules.css                # Style rules stripping distractions
│   └── content.js               # Auto-redirect to /direct/inbox/
├── .github/workflows/
│   └── build-apk.yml            # Automated APK compilation workflow
├── package.json
└── README.md
```
