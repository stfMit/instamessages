package com.stfmit.instamessages;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
        "  header div:has(canvas) {" +
        "    display: none !important;" +
        "    visibility: hidden !important;" +
        "    pointer-events: none !important;" +
        "  }" +
        "`;" +
        "if (!document.getElementById('instamessages-cleaner')) {" +
        "  document.head.appendChild(style);" +
        "}";

    private static final String ROUTE_ENFORCER = 
        "setInterval(function() {" +
        "  var p = window.location.pathname;" +
        "  if (p === '/' || p.startsWith('/reels') || p.startsWith('/explore')) {" +
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
        "}, 400);";

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
        settings.setAllowFileAccess(true);
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
                String url = request.getUrl().toString();

                // Keep all Instagram direct/accounts links inside this WebView
                if (url.contains("instagram.com")) {
                    return false; // Handled by this WebView, NEVER opened in native Instagram app!
                }

                // External links (e.g. YouTube, links sent by friends in chat) open in device browser
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } catch (Exception ignored) {}
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
