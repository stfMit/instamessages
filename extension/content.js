// Auto-redirect to Direct Messages inbox if user navigates to feed or reels
(function () {
  function enforceDirectInbox() {
    const path = window.location.pathname;
    const allowed = [
      '/direct/',
      '/accounts/login',
      '/accounts/two_factor',
      '/challenge/'
    ];

    const isAllowed = allowed.some((prefix) => path.startsWith(prefix));

    if (!isAllowed) {
      console.log('[InstaDM Extension] Redirecting from', path, 'to /direct/inbox/');
      window.location.replace('https://www.instagram.com/direct/inbox/');
    }
  }

  // Run on start
  enforceDirectInbox();

  // Watch URL changes in SPA
  let lastUrl = location.href;
  new MutationObserver(() => {
    const url = location.href;
    if (url !== lastUrl) {
      lastUrl = url;
      enforceDirectInbox();
    }
  }).observe(document, { subtree: true, childList: true });

  // Add floating status badge once DOM is ready
  window.addEventListener('DOMContentLoaded', () => {
    if (!document.getElementById('insta-dm-pill')) {
      const badge = document.createElement('div');
      badge.id = 'insta-dm-pill';
      badge.innerText = '⚡ InstaDM: Distraction-Free';
      document.body.appendChild(badge);
    }
  });
})();
