const fs = require('fs');
const path = require('path');

window.addEventListener('DOMContentLoaded', () => {
  // 1. Inject distraction-free CSS
  try {
    const cssPath = path.join(__dirname, 'distraction-free.css');
    const cssContent = fs.readFileSync(cssPath, 'utf8');
    const styleEl = document.createElement('style');
    styleEl.id = 'insta-dm-distraction-free-styles';
    styleEl.textContent = cssContent;
    document.head.appendChild(styleEl);
  } catch (err) {
    console.error('Failed to inject distraction-free CSS:', err);
  }

  // 2. Add custom badge
  if (!document.getElementById('insta-dm-indicator')) {
    const badge = document.createElement('div');
    badge.id = 'insta-dm-indicator';
    badge.innerText = '⚡ InstaDM • Distraction-Free';
    document.body.appendChild(badge);
  }

  // 3. Auto-redirect logic to keep user in DM inbox
  const enforceDMRoute = () => {
    const currentPath = window.location.pathname;
    const allowedPrefixes = ['/direct/', '/accounts/login', '/accounts/two_factor', '/challenge/'];
    
    // Check if current route is allowed
    const isAllowed = allowedPrefixes.some(prefix => currentPath.startsWith(prefix));
    
    if (!isAllowed && currentPath !== '/direct/inbox/') {
      // If user navigates to feed (/), reels (/reels/...), explore (/explore/...)
      console.log(`[InstaDM] Redirecting from ${currentPath} to /direct/inbox/`);
      window.location.replace('https://www.instagram.com/direct/inbox/');
    }
  };

  // Run on initial load
  enforceDMRoute();

  // Intercept history navigation (pushState, replaceState, popstate)
  const originalPushState = history.pushState;
  history.pushState = function (...args) {
    originalPushState.apply(this, args);
    enforceDMRoute();
  };

  const originalReplaceState = history.replaceState;
  history.replaceState = function (...args) {
    originalReplaceState.apply(this, args);
    enforceDMRoute();
  };

  window.addEventListener('popstate', enforceDMRoute);

  // Periodically re-check route (in case Instagram updates via internal routers)
  setInterval(enforceDMRoute, 1000);
});
