package com.david0926.selfcheck;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.david0926.selfcheck.databinding.ActivityMainBinding;
import com.david0926.selfcheck.util.SharedPreferenceUtil;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setLink("");
        binding.setIsLinkValid(false);

        if(!SharedPreferenceUtil.getString(this, "user_key", "").equals("")){
            startActivity(new Intent(MainActivity.this, CheckActivity.class));
            finish();
        }

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
            String key = binding.getLink().replace(getString(R.string.base_person_link), "")
                    .replace("%3D", "=");
            SharedPreferenceUtil.putString(this, "user_key", key);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("설정 완료").setMessage("다음에 앱을 시작하면 자동으로 자가진단이 시작됩니다.");
            builder.setPositiveButton("확인", (dialogInterface, i) -> finish());
            builder.setCancelable(false).show();

        });
    }
}
