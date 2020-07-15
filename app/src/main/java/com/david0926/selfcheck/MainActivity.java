package com.david0926.selfcheck;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.david0926.selfcheck.api.RetrofitAPI;
import com.david0926.selfcheck.databinding.ActivityMainBinding;
import com.david0926.selfcheck.databinding.DialogSearchBinding;
import com.david0926.selfcheck.model.SettingModel;
import com.david0926.selfcheck.util.SharedPreferenceUtil;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

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

    private SettingModel model;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setIsLoading(true);
        binding.setSchool("");
        binding.setName("");
        binding.setBirth("");
        binding.setCityList(getResources().getStringArray(R.array.city));

        firebaseFirestore
                .collection("setting")
                .document("setting")
                .get()
                .addOnCompleteListener(runnable -> {

                    DocumentSnapshot documentSnapshot = runnable.getResult();
                    if (documentSnapshot == null) return;

                    model = documentSnapshot.toObject(SettingModel.class);
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

        String base_url = getResources().getStringArray(R.array.city_link)[binding.spinnerMain.getSelectedItemPosition()];
        mRetrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mRetrofitAPI = mRetrofit.create(RetrofitAPI.class);

        binding.setSchool(binding.getSchool().trim());

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
                        getUserKey(resultSVO.getString("schulCode"), binding.getName(), binding.getBirth(), base_url);
                    } else {
                        String key = model.getSchool().get(binding.getSchool());
                        if (key != null) {
                            getUserKey(key, binding.getName(), binding.getBirth(), base_url);
                        } else {
                            searchSchool(binding.getSchool(), base_url);
                        }
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

    private void searchSchool(String word, String base_url) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("schulNm", word)
                .build();

        Call<ResponseBody> mCallSearch = mRetrofitAPI.postSearch(requestBody);
        mCallSearch.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    List<String> nameList = new ArrayList<>();
                    List<String> codeList = new ArrayList<>();

                    String responseString = response.body().string();
                    Document document = Jsoup.parse(responseString);
                    Elements elements = document.select("a");
                    for (Element element : elements) {
                        try {
                            String[] value = element.attr("onclick")
                                    .replace("javscript:selectSchul(", "")
                                    .replace(");", "")
                                    .replace("'", "")
                                    .split(", ");

                            String code = value[0];
                            String name = value[1];

                            codeList.add(code);
                            nameList.add(name);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (codeList.isEmpty() || codeList.size() != nameList.size()) failedSearch();
                    else {
                        DialogSearchBinding searchBinding = DataBindingUtil.inflate(LayoutInflater.from(MainActivity.this),
                                R.layout.dialog_search, null, false);

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setView(searchBinding.getRoot());
                        AlertDialog dialog = builder.create();

                        searchBinding.setItems(nameList);
                        searchBinding.btnDialogSearchConfirm.setOnClickListener(view -> {
                            binding.setSchool(nameList.get(searchBinding.spinnerDialogSearch.getSelectedItemPosition()));
                            String key = codeList.get(searchBinding.spinnerDialogSearch.getSelectedItemPosition());
                            getUserKey(key, binding.getName(), binding.getBirth(), base_url);
                            dialog.dismiss();
                        });

                        dialog.show();

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    failedSearch();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                failedSearch();
            }
        });
    }

    private void failedSearch() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("학교 검색 실패").setMessage("해당하는 이름의 학교를 찾지 못했습니다. 선택한 지역이 올바른지, 오타나 불필요한 띄어쓰기가 없는지 다시 확인해 주세요!" +
                "\n\n(문제가 없을 시, 메일로 정확한 학교명을 남겨 주시면 실시간으로 수정하도록 하겠습니다!)");

        builder.setPositiveButton("메일 보내기", (dialogInterface, i) -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent
                    .setData(Uri.parse("mailto:"))
                    .putExtra(Intent.EXTRA_EMAIL, new String[]{"android-dev@kakao.com"})
                    .putExtra(Intent.EXTRA_SUBJECT, "1초 자가진단 학교명 오류 문의" +
                            "(" + binding.getSchool() + ")")
                    .putExtra(Intent.EXTRA_TEXT, "학교명 오류 문의드립니다!\n\n학교명: " + binding.getSchool()
                            + "\n지역: " + binding.spinnerMain.getSelectedItem());
            if (intent.resolveActivity(getPackageManager()) != null)
                startActivity(intent);
            else
                Toast.makeText(MainActivity.this, "이메일 앱이 없습니다. android-dev@kakao.com으로 남겨주세요.", Toast.LENGTH_LONG).show();
        });
        builder.setNegativeButton("취소", (dialogInterface, i) -> {
        }).show();
    }

    private void getUserKey(String code, String name, String birth, String url) {
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
                        String key = resultSVO.getString("qstnCrtfcNoEncpt");
                        SharedPreferenceUtil.putString(MainActivity.this, "user_key", key);
                        SharedPreferenceUtil.putString(MainActivity.this, "base_url", url);

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
        if (!SharedPreferenceUtil.getString(this, "user_key", "").equals("")
                && !SharedPreferenceUtil.getString(this, "base_url", "").equals("")) {
            Intent intent = new Intent(MainActivity.this, CheckActivity.class);
            intent.putExtra("setting_model", model);
            startActivity(intent);
            finish();
        } else binding.setIsLoading(false);
    }

    private void checkVersion(SettingModel model) {
        if (Double.parseDouble(model.getVersion()) > Double.parseDouble(BuildConfig.VERSION_NAME)
                && model.getUpdate()) {
            doUpdate(model.getLink());
        } else startCheck();
    }

    private void doUpdate(String link) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("업데이트 필요").setMessage("새로운 버전이 출시되었습니다! 업데이트를 진행해 주세요." +
                "\n(업데이트가 보이지 않을 경우, 플레이스토어 캐시를 비워보세요.)");
        builder.setPositiveButton("업데이트", (dialogInterface, i) -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
            finish();
        });
        builder.setCancelable(false);
        builder.show();
    }
}
