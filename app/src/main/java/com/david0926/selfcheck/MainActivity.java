package com.david0926.selfcheck;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.david0926.selfcheck.api.RetrofitAPI;
import com.david0926.selfcheck.databinding.ActivityMainBinding;
import com.david0926.selfcheck.model.SettingModel;
import com.david0926.selfcheck.util.SharedPreferenceUtil;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private Retrofit mRetrofit;
    private RetrofitAPI mRetrofitAPI;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setIsLoading(true);
        binding.setSchool("");
        binding.setName("");
        binding.setBirth("");

        mRetrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mRetrofitAPI = mRetrofit.create(RetrofitAPI.class);

        firebaseFirestore
                .collection("setting")
                .document("setting")
                .get()
                .addOnCompleteListener(runnable -> {

                    DocumentSnapshot documentSnapshot = runnable.getResult();
                    if (documentSnapshot == null) return;

                    SettingModel model = documentSnapshot.toObject(SettingModel.class);
                    if (model == null) return;

                    if (model.getNotice()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle(model.getTitle()).setMessage(model.getMessage());
                        builder.setPositiveButton("확인", (dialogInterface, i) -> {
                            if (!model.getEnable()) finish();
                            else checkVersion(model);
                        });
                        builder.setOnCancelListener(dialogInterface -> checkVersion(model));
                        builder.setCancelable(model.getCancelable() && model.getEnable());
                        builder.show();
                    } else checkVersion(model);

                });

        binding.btnMain.setOnClickListener(view -> checkSchool());

    }

    private void checkSchool() {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("schulNm", binding.getSchool())
                .build();

        Call<ResponseBody> mCallSchool = mRetrofitAPI.postSchool(requestBody);
        mCallSchool.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject object = new JSONObject(response.body().string());
                    JSONObject resultSVO = object.getJSONObject("resultSVO");
                    String result = resultSVO.getString("rtnRsltCode");

                    if (result.equals("SUCCESS")) {
                        getUserKey(resultSVO.getString("schulCode"), binding.getName(), binding.getBirth());
                    } else {
                        Toast.makeText(MainActivity.this, "학교 이름이 정확하지 않거나 2건 이상입니다.", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "학교 이름을 검색하는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(MainActivity.this, "학교 이름을 검색하는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserKey(String code, String name, String birth) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("schulCode", code)
                .addFormDataPart("pName", name)
                .addFormDataPart("frnoRidno", birth)
                .build();

        Call<ResponseBody> mCallInfo = mRetrofitAPI.postInfo(requestBody);
        mCallInfo.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject object = new JSONObject(response.body().string());
                    JSONObject resultSVO = object.getJSONObject("resultSVO");
                    String result = resultSVO.getString("rtnRsltCode");

                    if (result.equals("SUCCESS")) {
                        String key = URLDecoder.decode(resultSVO.getString("qstnCrtfcNoEncpt"), "UTF-8");
                        SharedPreferenceUtil.putString(MainActivity.this, "user_key", key);

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("설정 완료").setMessage("다음에 앱을 시작하면 자동으로 자가진단이 시작됩니다.");
                        builder.setPositiveButton("확인", (dialogInterface, i) -> finish());
                        builder.setCancelable(false).show();
                    } else {
                        Toast.makeText(MainActivity.this, "학생 정보가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "학생 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(MainActivity.this, "학생 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startCheck() {
        if (!SharedPreferenceUtil.getString(this, "user_key", "").equals("")) {
            startActivity(new Intent(MainActivity.this, CheckActivity.class));
            finish();
        } else binding.setIsLoading(false);
    }

    private void checkVersion(SettingModel model) {
        if (!model.getVersion().equals(BuildConfig.VERSION_NAME)) {
            doUpdate(model.getLink());
        } else startCheck();
    }

    private void doUpdate(String link) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("업데이트 필요").setMessage("새로운 버전이 출시되었습니다! 업데이트를 진행해 주세요.");
        builder.setPositiveButton("업데이트", (dialogInterface, i) -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
            finish();
        });
        builder.setCancelable(false);
        builder.show();
    }
}
