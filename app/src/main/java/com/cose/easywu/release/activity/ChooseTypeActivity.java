package com.cose.easywu.release.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.cose.easywu.R;
import com.cose.easywu.base.BaseActivity;
import com.cose.easywu.db.Type;
import com.cose.easywu.release.fragment.ReleaseFragment;
import com.cose.easywu.release.util.ListViewForScrollView;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class ChooseTypeActivity extends BaseActivity {

    private ImageView mIvBack;
    private ListViewForScrollView mLvType;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    private List<Type> typeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_type);

        initView();
        initData();
        initListener();
    }

    private void initListener() {
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mLvType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent("com.cose.easywu.release.chooseType");
                intent.putExtra("type_id", typeList.get(position).getT_id());
                intent.putExtra("type_name", typeList.get(position).getT_name());
                intent.putExtra("type_pic", typeList.get(position).getT_pic());
                sendBroadcast(intent);
                finish();
            }
        });
    }

    private void initData() {
        // 从本地数据库获取Type信息
        typeList = LitePal.findAll(Type.class);
        for (Type type : typeList) {
            dataList.add(type.getT_name());
        }
        adapter.notifyDataSetChanged();
    }

    private void initView() {
        mIvBack = findViewById(R.id.iv_chooseType_back);
        mLvType = findViewById(R.id.lv_chooseType);
        adapter = new ArrayAdapter<>(ChooseTypeActivity.this, android.R.layout.simple_list_item_1, dataList);
        mLvType.setAdapter(adapter);
    }
}
