package com.david0926.selfcheck;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.david0926.selfcheck.databinding.ActivityWebViewBinding;

public class WebViewActivity extends AppCompatActivity {

    private static final String TAG = "webview_debug";
    private ActivityWebViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web_view);

        WebSettings webSettings = binding.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        //WebStorage.getInstance().deleteAllData();

        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                switch (url) {
                    case "https://hcs.eduro.go.kr/#/relogin":
                        fillFirstInputByClassName("input_text_common", "0926");
                        clickElementById("btnConfirm");
                        break;
                    case "https://hcs.eduro.go.kr/#/main":
                        clickFirstElementByClassName("btn");
                        break;
                    case "https://hcs.eduro.go.kr/#/survey":
                        doSurvey();
                        break;
                }
            }
        });

        binding.webView.loadUrl("https://hcs.eduro.go.kr/#/loginHome");
    }

    private void doSurvey() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            for (int i = 1; i <= 5; i++)
                clickElementByQuerySelector("[for=\"survey_q" + i + "a1\"]");
            clickElementById("btnConfirm");
        }, 1000);
    }

    private void clickElementByQuerySelector(String selector) {
        String js = "javascript:(function(){" +
                "document.querySelector('" + selector + "').click()" +
                "})()";
        binding.webView.evaluateJavascript(js, null);
    }

    private void clickFirstElementByClassName(String cls) {
        String js = "javascript:(function(){" +
                "l=document.getElementsByClassName('" + cls + "')[0];" +
                "e=document.createEvent('HTMLEvents');" +
                "e.initEvent('click',true,true);" +
                "l.dispatchEvent(e);" +
                "})()";
        binding.webView.evaluateJavascript(js, null);
    }

    private void fillFirstInputByClassName(String cls, String text) {
        String js = "javascript:(function(){" +
                "l=document.getElementsByClassName('" + cls + "')[0];" +
                "l.value='" + text + "'" +
                "})()";
        binding.webView.evaluateJavascript(js, null);
    }

    private void clickElementById(String id) {
        String js = "javascript:(function(){" +
                "l=document.getElementById('" + id + "');" +
                "e=document.createEvent('HTMLEvents');" +
                "e.initEvent('click',true,true);" +
                "l.dispatchEvent(e);" +
                "})()";
        binding.webView.evaluateJavascript(js, null);
    }
}