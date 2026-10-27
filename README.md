# InstaDM ⚡ Distraction-Free Instagram Direct Messages

An installable **Progressive Web App (PWA)**, **Desktop App**, and **Browser Companion** designed specifically for Instagram Direct Messages.

Eliminates algorithmic feeds, the Reels tab, Explore recommendations, and infinite scrolling so you can message friends and clients with zero distractions.

---

## 🚀 3 Ways to Use InstaDM

### 1. Dedicated Windows Desktop App
A standalone native desktop window focused strictly on your Direct inbox:
1. Open this folder in your terminal (PowerShell, CMD, or bash).
2. Install dependencies:
   ```bash
   npm install
   ```
3. Run the app:
   ```bash
   npm start
   ```
*Features:* Custom Chrome session, automatically injects CSS to hide Reels/Feed/Explore, persists login session, and redirects any accidental feed clicks back to `/direct/inbox/`.

---

### 2. Progressive Web App (PWA) on Mobile & Desktop
Install directly onto iOS, Android, or Windows/macOS taskbars:

* **On iPhone / iPad (Safari):**
  1. Open the hosted PWA link in Safari.
  2. Tap the **Share** button (box with upward arrow).
  3. Select **Add to Home Screen**.
* **On Android (Chrome):**
  1. Open the hosted PWA in Chrome.
  2. Tap the three dots menu (⋮) -> **Install App**.
* **On Windows / Mac (Chrome or Edge):**
  1. Click the **Install** icon in the browser address bar.
  2. The app now lives in your Start Menu and Taskbar.

To preview the PWA locally:
```bash
npm run serve
```
Then visit `http://localhost:3000` in your browser.

---

### 3. Chrome / Edge / Brave Extension
Enforces strict "DM-Only Mode" across any browser tab or installed Chrome PWA:
1. Open `chrome://extensions` or `edge://extensions` in your browser.
2. Enable **Developer mode** (toggle in top right).
3. Click **Load unpacked**.
4. Select the `extension/` folder from this repository.
5. All Reels, Explore links, and the Home feed are automatically stripped from Instagram.

---

## 🛡️ Privacy & Security
- **No Middleman Servers:** All requests connect directly to `instagram.com`.
- **Zero Credential Access:** Your login and two-factor authentication remain completely between you and Instagram.
- **End-to-End Encryption:** Messages and voice notes use Instagram's native encrypted protocol.

---

## 📁 Repository Structure

```
instagram-dm-only/
├── pwa/                         # Progressive Web App (Mobile & Web)
│   ├── index.html               # Launcher dashboard & install UX
│   ├── styles.css               # Dark theme aesthetic UI
│   ├── app.js                   # PWA registration & install logic
│   ├── sw.js                    # Service worker for offline caching
│   ├── manifest.json            # Web App Manifest
│   └── icons/                   # App icons (192px, 512px, SVG)
├── src/desktop/                 # Native Desktop App (Electron)
│   ├── main.js                  # Main window configuration
│   ├── preload.js               # Route enforcement & script injection
│   └── distraction-free.css     # CSS rules hiding Reels, Feed, Explore
├── extension/                   # Chrome / Edge / Brave Extension
│   ├── manifest.json            # Manifest V3
│   ├── rules.css                # Style rules stripping distractions
│   └── content.js               # Auto-redirect to /direct/inbox/
├── .github/workflows/
│   └── deploy.yml               # Automated GitHub Pages PWA deployment
├── package.json
└── README.md
```

---

## 🌐 Deploying to GitHub Pages

This repo includes a preconfigured GitHub Actions workflow:
1. Push this repository to GitHub.
2. In your GitHub repository, go to **Settings** > **Pages**.
3. Under **Build and deployment > Source**, select **GitHub Actions**.
4. Every push to `main` will automatically deploy the PWA to `https://<your-username>.github.io/<repo-name>/`.
