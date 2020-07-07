package com.david0926.selfcheck;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.david0926.selfcheck.databinding.ActivityMainBinding;
import com.david0926.selfcheck.model.SettingModel;
import com.david0926.selfcheck.util.SharedPreferenceUtil;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setIsLoading(true);
        binding.setLink("");
        binding.setIsLinkValid(false);

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
                        builder.setCancelable(model.getCancelable()&&model.getEnable());
                        builder.show();
                    } else checkVersion(model);

                });

        binding.edtMainLink.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().contains(getString(R.string.base_person_link))) {
                    binding.setIsLinkValid(true);
                } else binding.setIsLinkValid(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.btnMain.setOnClickListener(view -> {
            try {
                String key = URLDecoder.decode(binding.getLink(), "UTF-8").replace(getString(R.string.base_person_link), "");
                SharedPreferenceUtil.putString(this, "user_key", key);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("설정 완료").setMessage("다음에 앱을 시작하면 자동으로 자가진단이 시작됩니다.");
                builder.setPositiveButton("확인", (dialogInterface, i) -> finish());
                builder.setCancelable(false).show();

            } catch (UnsupportedEncodingException e) {
                Toast.makeText(this, "올바른 주소가 아닙니다.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }


        });

        binding.txtMainNoLink.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("바로참여 링크가 없다면?").setMessage("현재 인증번호나 학생정보로 자가진단을 진행하는 기능을 개발하고 있습니다. \n그 전까지는 바로참여 링크를 학교에 문의하거나, 업데이트를 기다려 주세요!");
            builder.setPositiveButton("알겠습니다!", (dialogInterface, i) -> {
            });
            builder.show();
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
