package com.stfmit.instamessages;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class MainActivity extends Activity {

    private WebView webView;
    private ProgressBar progressBar;
    private ValueCallback<Uri[]> filePathCallback;
    private static final int FILE_CHOOSER_REQUEST_CODE = 1001;

    private float touchDownX = 0f;
    private float touchDownY = 0f;

    // Distraction-free CSS to inject into Instagram Web
    private static final String CSS_INJECTION = 
        "var style = document.createElement('style');" +
        "style.id = 'instamessages-cleaner';" +
        "style.innerHTML = `" +
        "  /* Hide Reels, Explore, Feed navigation */" +
        "  a[href*=\\\"/reels/\\\"]," +
        "  a[href*=\\\"/explore/\\\"]," +
        "  a[href*=\\\"threads.net\\\"]," +
        "  div[role=\\\"navigation\\\"] a[href=\\\"/reels/\\\"]," +
        "  div[role=\\\"navigation\\\"] a[href=\\\"/explore/\\\"]," +
        "  div[role=\\\"navigation\\\"] a[href=\\\"/\\\"]," +
        "  /* Hide Top-Left Back Button */" +
        "  header a[href=\\\"/\\\"]," +
        "  header a:has(svg[aria-label=\\\"Back\\\"])," +
        "  header a:has(svg[aria-label=\\\"Chevron left\\\"])," +
        "  header div[role=\\\"button\\\"]:has(svg[aria-label=\\\"Back\\\"])," +
        "  header svg[aria-label=\\\"Back\\\"]," +
        "  header svg[aria-label=\\\"Chevron left\\\"]," +
        "  /* Hide Notes Tray */" +
        "  div[aria-label*=\\\"Note\\\"]," +
        "  div[aria-label*=\\\"note\\\"]," +
        "  div[style*=\\\"overflow-x\\\"]," +
        "  div[style*=\\\"scroll-snap-type\\\"]," +
        "  div[role=\\\"menu\\\"]:has(ul)," +
        "  header div:has(canvas)," +
        "  /* Disable links to user profiles and suggestions inside posts */" +
        "  article header a[role=\\\"link\\\"]," +
        "  div[aria-label*=\\\"More posts\\\"]," +
        "  div[aria-label*=\\\"Suggested\\\"]," +
        "  button[aria-label*=\\\"Next\\\"] {" +
        "    display: none !important;" +
        "    visibility: hidden !important;" +
        "    pointer-events: none !important;" +
        "  }" +
        "  /* Disable vertical scrolling container on reels */" +
        "  div[style*=\\\"scroll-snap-type: y\\\"]," +
        "  div[style*=\\\"scroll-snap-type: mandatory\\\"]," +
        "  section[role=\\\"region\\\"]," +
        "  div:has(> video) {" +
        "    touch-action: none !important;" +
        "    overflow-y: hidden !important;" +
        "    scroll-snap-type: none !important;" +
        "  }" +
        "`;" +
        "if (!document.getElementById('instamessages-cleaner')) {" +
        "  document.head.appendChild(style);" +
        "}";

    private static final String ROUTE_ENFORCER = 
        "setInterval(function() {" +
        "  var p = window.location.pathname;" +
        "  /* Allowed routes: direct messages, accounts, single reel, single post */" +
        "  var isAllowed = p.startsWith('/direct') || p.startsWith('/accounts') || " +
        "                  p.startsWith('/challenge') || p.startsWith('/reel/') || p.startsWith('/p/');" +
        "  if (!isAllowed) {" +
        "    /* User clicked an external profile, explore, or feed -> immediately return to DM */" +
        "    window.location.replace('https://www.instagram.com/direct/inbox/');" +
        "  }" +
        "  /* Remove top-left back button and notes tray */" +
        "  if (p.includes('/direct/inbox')) {" +
        "    var backEls = document.querySelectorAll('header a[href=\"/\"], header svg[aria-label=\"Back\"], header svg[aria-label=\"Chevron left\"]');" +
        "    backEls.forEach(function(el) {" +
        "      var target = el.closest('header a') || el.closest('div[role=\"button\"]') || el;" +
        "      if (target) target.style.setProperty('display', 'none', 'important');" +
        "    });" +
        "    var noteTrays = document.querySelectorAll('div[style*=\"overflow-x\"], div[aria-label*=\"Note\"], div[aria-label*=\"note\"]');" +
        "    noteTrays.forEach(function(tray) {" +
        "      tray.style.setProperty('display', 'none', 'important');" +
        "    });" +
        "  }" +
        "  /* Inside posts & reels: disable author profile navigation clicks */" +
        "  if (p.startsWith('/p/') || p.startsWith('/reel/')) {" +
        "    document.querySelectorAll('article header a, a[role=\"link\"]:not([href*=\"/direct/\"])').forEach(function(link) {" +
        "      if (!link.href.includes('/direct/') && !link.href.includes('/reel/') && !link.href.includes('/p/')) {" +
        "        link.style.pointerEvents = 'none';" +
        "        link.onclick = function(e) { e.preventDefault(); e.stopPropagation(); return false; };" +
        "      }" +
        "    });" +
        "    document.querySelectorAll('video').forEach(function(v) { v.loop = true; });" +
        "  }" +
        "}, 300);";

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webview);
        progressBar = findViewById(R.id.progressBar);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        // Security hardening: Disable local file and content scheme access
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setMediaPlaybackRequiresUserGesture(false);

        // Mobile Chrome User-Agent
        settings.setUserAgentString("Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36");

        // Enable persistent cookies
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                String host = uri.getHost();
                String path = uri.getPath() != null ? uri.getPath() : "";

                // Security: Strict host verification
                boolean isInstagram = host != null && (host.equals("instagram.com") || host.endsWith(".instagram.com"));
                if (isInstagram) {
                    // 1. Allow DM inbox & chats
                    if (path.startsWith("/direct") || path.startsWith("/accounts") || path.startsWith("/challenge")) {
                        return false; // Handled inside WebView
                    }
                    // 2. Allow single reel or single post sent by a friend
                    if (path.startsWith("/reel/") || path.startsWith("/p/")) {
                        return false; // Handled inside WebView
                    }
                    // 3. Block all profile pages, explore, search, hashtags, feeds!
                    // Clicking on a user profile or explore is blocked:
                    return true; // Cancel navigation! User cannot browse away!
                }

                // External links (e.g. YouTube links sent in chat) open safely in default browser
                String scheme = uri.getScheme();
                if ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    } catch (Exception ignored) {}
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);

                // Inject CSS and route enforcement
                view.evaluateJavascript(CSS_INJECTION, null);
                view.evaluateJavascript(ROUTE_ENFORCER, null);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100) {
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);
                    view.evaluateJavascript(CSS_INJECTION, null);
                    view.evaluateJavascript(ROUTE_ENFORCER, null);
                }
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                            FileChooserParams fileChooserParams) {
                MainActivity.this.filePathCallback = filePathCallback;
                Intent intent = fileChooserParams.createIntent();
                try {
                    startActivityForResult(intent, FILE_CHOOSER_REQUEST_CODE);
                } catch (Exception e) {
                    MainActivity.this.filePathCallback = null;
                    return false;
                }
                return true;
            }
        });

        // Load Instagram Direct Messages directly
        webView.loadUrl("https://www.instagram.com/direct/inbox/");
    }

    /**
     * Native OS-level touch interception:
     * When viewing a Reel, any vertical dragging movement is swallowed by Android.
     * Chromium/Instagram physically CANNOT scroll to the next video!
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        String url = webView != null ? webView.getUrl() : "";
        boolean isReelPage = url != null && (url.contains("/reel/") || url.contains("/reels/"));

        if (isReelPage) {
            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    touchDownX = ev.getX();
                    touchDownY = ev.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float dx = Math.abs(ev.getX() - touchDownX);
                    float dy = Math.abs(ev.getY() - touchDownY);
                    // If vertical movement exceeds 10px and is primarily vertical:
                    // Swallowing it natively prevents Chromium/Android from scrolling to the next video!
                    if (dy > 10f && dy > dx) {
                        return true; // Consumed natively! Reels cannot scroll!
                    }
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_CHOOSER_REQUEST_CODE) {
            if (filePathCallback != null) {
                Uri[] results = null;
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
                filePathCallback.onReceiveValue(results);
                filePathCallback = null;
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
