package com.david0926.selfcheck;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.david0926.selfcheck.api.RetrofitAPI;
import com.david0926.selfcheck.databinding.ActivityCheckBinding;
import com.david0926.selfcheck.util.SharedPreferenceUtil;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CheckActivity extends AppCompatActivity {

    private String base_url;

    private Retrofit mRetrofit;
    private RetrofitAPI mRetrofitAPI;

    private ActivityCheckBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_check);
        binding.setIsSuccess(false);
        binding.setIsFailed(false);

        setSupportActionBar(binding.toolbarCheck);
        getSupportActionBar().setTitle("");

        base_url = SharedPreferenceUtil.getString(this, "base_url", "");

        mRetrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mRetrofitAPI = mRetrofit.create(RetrofitAPI.class);

        postCheck();
    }

    private void postCheck() {

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("qstnCrtfcNoEncpt", SharedPreferenceUtil.getString(this, "user_key", ""))
                .addFormDataPart("rspns01", "1")
                .addFormDataPart("rspns02", "1")
                .addFormDataPart("rspns07", "0")
                .addFormDataPart("rspns08", "0")
                .addFormDataPart("rspns09", "0")
                .addFormDataPart("rtnRsltCode", "SUCCESS")
                .build();

        Call<ResponseBody> mCallCheck = mRetrofitAPI.postCheck(requestBody);
        mCallCheck.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject object = new JSONObject(response.body().string());
                    JSONObject resultSVO = object.getJSONObject("resultSVO");
                    String result = resultSVO.getString("rtnRsltCode");

                    if (result.equals("SUCCESS")) {
                        binding.setIsSuccess(true);
                        postResult(resultSVO.getString("schulNm"), resultSVO.getString("stdntName"));
                    } else {
                        binding.setErrorCode(result);
                        binding.setIsFailed(true);
                    }

                } catch (Exception e) {
                    binding.setIsFailed(true);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                binding.setIsFailed(true);
            }
        });
    }

    private void postResult(String school, String name) {
        try {
            String post = "rspns01=1&rspns02=1&rspns07=0&rspns08=0&rspns09=0"
                    + "&schulNm=" + URLEncoder.encode(school, "UTF-8")
                    + "&stdntName=" + URLEncoder.encode(name, "UTF-8");
            binding.webCheck.postUrl(base_url + "/stv_cvd_co02_000.do", post.getBytes());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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
                SharedPreferenceUtil.putString(this, "user_key", "");
                SharedPreferenceUtil.putString(this, "base_url", "");
                Toast.makeText(this, "정상적으로 초기화 되었습니다.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CheckActivity.this, MainActivity.class));
                finish();
            });
            builder.setNegativeButton("취소", (dialogInterface, i) -> {
            });
            builder.show();
        } else if (item.getItemId() == R.id.action_info) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("개발자 정보").setMessage("앱 개발: 선린인터넷고등학교 정찬효\n문의: android-dev@kakao.com");
            builder.setPositiveButton("확인", (dialogInterface, i) -> {
            });
            builder.show();
        }
        return super.onOptionsItemSelected(item);
    }
}