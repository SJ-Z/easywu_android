package com.cose.easywu.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cose.easywu.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnExit;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }

    private void initData() {
        editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
    }

    private void initView() {
        mBtnExit = findViewById(R.id.btn_main_exit);

        mBtnExit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_main_exit:
                editor.remove("u_id");
                editor.putBoolean("autoLogin", false);
                editor.apply();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                break;
        }
    }
}
