package com.cose.easywu.app;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.cose.easywu.R;
import com.cose.easywu.base.ActivityCollector;

public class Main2Activity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
