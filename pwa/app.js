// Register Service Worker
if ('serviceWorker' in navigator) {
  window.addEventListener('load', () => {
    navigator.serviceWorker
      .register('./sw.js')
      .then((reg) => {
        console.log('[InstaDM] Service Worker registered with scope:', reg.scope);
      })
      .catch((err) => {
        console.warn('[InstaDM] Service Worker registration failed:', err);
      });
  });
}

// PWA Installation handling
let deferredPrompt;
const installBtn = document.getElementById('install-btn');

window.addEventListener('beforeinstallprompt', (e) => {
  // Prevent Chrome 67 and earlier from automatically showing the prompt
  e.preventDefault();
  // Stash the event so it can be triggered later.
  deferredPrompt = e;
  // Update UI notify the user they can install the PWA
  if (installBtn) {
    installBtn.style.display = 'inline-flex';
  }
});

if (installBtn) {
  installBtn.addEventListener('click', async () => {
    if (!deferredPrompt) return;
    // Show the install prompt
    deferredPrompt.prompt();
    // Wait for the user to respond to the prompt
    const { outcome } = await deferredPrompt.userChoice;
    console.log(`[InstaDM] User response to the install prompt: ${outcome}`);
    // Clear the deferredPrompt variable
    deferredPrompt = null;
    installBtn.style.display = 'none';
  });
}

window.addEventListener('appinstalled', () => {
  console.log('[InstaDM] App was installed successfully');
  if (installBtn) {
    installBtn.style.display = 'none';
  }
});

// Device Tabs Switching
const tabButtons = document.querySelectorAll('.tab-btn');
const tabContents = document.querySelectorAll('.tab-content');

tabButtons.forEach((btn) => {
  btn.addEventListener('click', () => {
    const targetTab = btn.getAttribute('data-tab');

    tabButtons.forEach((b) => b.classList.remove('active'));
    tabContents.forEach((c) => c.classList.remove('active'));

    btn.classList.add('active');
    const content = document.getElementById(`tab-${targetTab}`);
    if (content) {
      content.classList.add('active');
    }
  });
});
