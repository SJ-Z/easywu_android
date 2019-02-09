package com.cose.easywu.release.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.cose.easywu.R;
import com.cose.easywu.base.BaseActivity;
import com.cose.easywu.db.FindType;
import com.cose.easywu.db.Type;
import com.cose.easywu.release.util.ListViewForScrollView;
import com.cose.easywu.utils.Constant;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class ChooseFindTypeActivity extends BaseActivity {

    private ImageView mIvBack;
    private ListViewForScrollView mLvType;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    private List<FindType> typeList;

    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_find_type);

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
                Intent intent = new Intent(Constant.RELEASE_CHOOSE_TYPE);
                intent.putExtra("findType_id", typeList.get(position).getFt_id());
                intent.putExtra("findType_name", typeList.get(position).getFt_name());
                intent.putExtra("findType_pic", typeList.get(position).getFt_pic());
                localBroadcastManager.sendBroadcast(intent);
                finish();
            }
        });
    }

    private void initData() {
        // 从本地数据库获取Type信息
        typeList = LitePal.findAll(FindType.class);
        for (FindType type : typeList) {
            dataList.add(type.getFt_name());
        }
        adapter.notifyDataSetChanged();

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    private void initView() {
        mIvBack = findViewById(R.id.iv_chooseType_back);
        mLvType = findViewById(R.id.lv_chooseType);
        adapter = new ArrayAdapter<>(ChooseFindTypeActivity.this, android.R.layout.simple_list_item_1, dataList);
        mLvType.setAdapter(adapter);
    }

}
