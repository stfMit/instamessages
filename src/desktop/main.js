const { app, BrowserWindow, shell, session } = require('electron');
const path = require('path');

let mainWindow;

function createWindow() {
  mainWindow = new BrowserWindow({
    width: 1100,
    height: 780,
    minWidth: 480,
    minHeight: 600,
    title: 'Instagram Direct',
    backgroundColor: '#000000',
    autoHideMenuBar: true,
    webPreferences: {
      preload: path.join(__dirname, 'preload.js'),
      contextIsolation: false,
      nodeIntegration: false,
      partition: 'persist:instagram_dm_session',
      spellcheck: true
    }
  });

  // Set modern Chrome user agent to ensure full web experience
  mainWindow.webContents.setUserAgent(
    'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36'
  );

  // Load Instagram Direct inbox
  mainWindow.loadURL('https://www.instagram.com/direct/inbox/');

  // Open external links in user's default browser instead of the DM window
  mainWindow.webContents.setWindowOpenHandler(({ url }) => {
    if (!url.startsWith('https://www.instagram.com/direct/')) {
      shell.openExternal(url);
      return { action: 'deny' };
    }
    return { action: 'allow' };
  });

  mainWindow.on('page-title-updated', (e) => {
    e.preventDefault();
    mainWindow.setTitle('Instagram Direct');
  });

  mainWindow.on('closed', () => {
    mainWindow = null;
  });
}

// Ensure single instance lock
const gotTheLock = app.requestSingleInstanceLock();

if (!gotTheLock) {
  app.quit();
} else {
  app.on('second-instance', () => {
    if (mainWindow) {
      if (mainWindow.isMinimized()) mainWindow.restore();
      mainWindow.focus();
    }
  });

  app.whenReady().then(() => {
    createWindow();

    app.on('activate', () => {
      if (BrowserWindow.getAllWindows().length === 0) createWindow();
    });
  });
}

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit();
  }
});
