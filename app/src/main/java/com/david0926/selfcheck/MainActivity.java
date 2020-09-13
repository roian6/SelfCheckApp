package com.david0926.selfcheck;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.david0926.selfcheck.databinding.ActivityMainBinding;
import com.david0926.selfcheck.model.SettingModel;
import com.david0926.selfcheck.util.SharedPreferenceUtil;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private SettingModel model;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setIsLoading(true);

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

        binding.btnMain.setOnClickListener(view -> {
            SharedPreferenceUtil.putString(this, "user_code", binding.getCode());
            startCheck();
        });

    }

    private void startCheck() {
        if (!SharedPreferenceUtil.getString(this, "user_code", "").equals("")) {
            startActivity(new Intent(MainActivity.this, CheckActivity.class));
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
