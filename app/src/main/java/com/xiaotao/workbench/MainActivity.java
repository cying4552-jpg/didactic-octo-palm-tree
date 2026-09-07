package com.xiaotao.workbench;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public final class MainActivity extends Activity {
    private static final int REQ_EXPORT = 4001;
    private static final int REQ_IMPORT = 4002;
    private WebView webView;
    private String pendingExport;

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webView = new WebView(this);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(false);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        settings.setMediaPlaybackRequiresUserGesture(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.addJavascriptInterface(new Bridge(), "AndroidBridge");
        setContentView(webView);
        if (savedInstanceState == null) loadLocalApp(); else webView.restoreState(savedInstanceState);
    }

    private void loadLocalApp() {
        try (InputStream in = getAssets().open("www/index.html")) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] chunk = new byte[8192];
            int n;
            while ((n = in.read(chunk)) != -1) buffer.write(chunk, 0, n);
            String html = new String(buffer.toByteArray(), StandardCharsets.UTF_8);
            webView.loadDataWithBaseURL("https://xiaotao.local/", html, "text/html", "UTF-8", null);
        } catch (Exception e) {
            webView.loadData("<h2>小桃工作台加载失败</h2><p>请重新安装最新版 APK。</p>", "text/html", "UTF-8");
        }
    }

    private final class Bridge {
        @JavascriptInterface public void exportBackup(String json) {
            pendingExport = json;
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/json");
            intent.putExtra(Intent.EXTRA_TITLE, "xiaotao-backup.json");
            startActivityForResult(intent, REQ_EXPORT);
        }

        @JavascriptInterface public void importBackup() {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/json");
            startActivityForResult(intent, REQ_IMPORT);
        }

        @JavascriptInterface public void openExternal(String url) {
            try {
                Uri uri = Uri.parse(url);
                if ("https".equals(uri.getScheme())) startActivity(new Intent(Intent.ACTION_VIEW, uri));
            } catch (Exception ignored) { }
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null || data.getData() == null) return;
        Uri uri = data.getData();
        if (requestCode == REQ_EXPORT && pendingExport != null) {
            try (OutputStream out = getContentResolver().openOutputStream(uri)) {
                if (out != null) out.write(pendingExport.getBytes(StandardCharsets.UTF_8));
            } catch (Exception ignored) { }
            pendingExport = null;
        } else if (requestCode == REQ_IMPORT) {
            try (InputStream in = getContentResolver().openInputStream(uri);
                 BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line).append('\n');
                String quoted = org.json.JSONObject.quote(sb.toString());
                webView.post(() -> webView.evaluateJavascript("window.receiveImportedBackup(" + quoted + ")", null));
            } catch (Exception ignored) { }
        }
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        webView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack(); else super.onBackPressed();
    }

    @Override protected void onDestroy() {
        if (webView != null) webView.destroy();
        super.onDestroy();
    }
}
