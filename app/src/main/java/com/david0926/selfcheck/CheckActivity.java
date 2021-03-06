package com.david0926.selfcheck;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.david0926.selfcheck.databinding.ActivityCheckBinding;
import com.david0926.selfcheck.model.SettingModel;
import com.david0926.selfcheck.util.SharedPreferenceUtil;

public class CheckActivity extends AppCompatActivity {

    private ActivityCheckBinding binding;

    private SettingModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_check);
        binding.setIsSuccess(false);
        binding.setIsFailed(false);

        model = (SettingModel) getIntent().getSerializableExtra("setting");

        setSupportActionBar(binding.toolbarCheck);
        getSupportActionBar().setTitle("");

        WebSettings webSettings = binding.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                switch (url) {
                    case "https://hcs.eduro.go.kr/#/loginHome":
                        binding.setIsSuccess(true);
                        Toast.makeText(CheckActivity.this,
                                R.string.first_setup_toast, Toast.LENGTH_SHORT).show();
                        break;
                    case "https://hcs.eduro.go.kr/#/relogin":
                        fillFirstInputByClassName("input_text_common", SharedPreferenceUtil
                                .getString(CheckActivity.this, "user_code", ""));
                        clickElementById("btnConfirm");
                        break;
                    case "https://hcs.eduro.go.kr/#/main":
                        if (binding.getIsSuccess()) finishSetup();
                        else new Handler(getMainLooper()).postDelayed(() -> {
                            clickFirstElementByClassName("btn");
                        }, 1000);
                        break;
                    case "https://hcs.eduro.go.kr/#/survey":
                        doSurvey();
                        break;
                }
            }
        });

        binding.webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                new AlertDialog.Builder(CheckActivity.this)
                        .setMessage(message)
                        .setPositiveButton("확인", (dialog, which) -> finish())
                        .setOnCancelListener(dialog -> finish())
                        .show();
                result.cancel();
                return true;
            }
        });

        binding.webView.loadUrl("https://hcs.eduro.go.kr");
    }

    private void finishSetup() {
        SharedPreferenceUtil.putBoolean(CheckActivity.this, "is_first", false);
        Toast.makeText(CheckActivity.this,
                R.string.first_setup_finish_toast, Toast.LENGTH_LONG).show();
        finish();
    }

    private void doSurvey() {
        new Handler(getMainLooper()).postDelayed(() -> {
            for (int i = 1; i <= model.getQuestionCount(); i++)
                clickElementByQuerySelector("[for=\"survey_q" + i + "a1\"]");
            clickElementById("btnConfirm");
            if (!binding.getIsSuccess()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("자가진단 확인").setMessage(model.getQuestion().replace("\\n", "\n"));
                builder.setPositiveButton("확인", (dialogInterface, i) -> {
                });
                builder.show();
            }
            binding.setIsSuccess(true);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_check, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_reset) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("앱 초기화").setMessage("앱에 등록된 모든 정보를 초기화할까요?");
            builder.setPositiveButton("초기화", (dialogInterface, i) -> {
                SharedPreferenceUtil.putString(this, "user_code", "");
                WebStorage.getInstance().deleteAllData();
                Toast.makeText(this, "정상적으로 초기화 되었습니다.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CheckActivity.this, MainActivity.class));
                finish();
            });
            builder.setNegativeButton("취소", (dialogInterface, i) -> {
            });
            builder.show();
        } else if (item.getItemId() == R.id.action_info) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("개발자 정보").setMessage(model.getInfo().replace("\\n", "\n"));
            builder.setPositiveButton("확인", (dialogInterface, i) -> {
            });
            builder.show();
        }
        return super.onOptionsItemSelected(item);
    }
}