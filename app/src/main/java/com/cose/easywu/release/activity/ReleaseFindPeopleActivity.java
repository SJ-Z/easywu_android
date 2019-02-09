package com.cose.easywu.release.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cose.easywu.R;
import com.cose.easywu.base.BaseActivity;
import com.cose.easywu.db.FindType;
import com.cose.easywu.db.ReleaseFindGoods;
import com.cose.easywu.db.ReleaseGoods;
import com.cose.easywu.db.Type;
import com.cose.easywu.gson.msg.ReleaseMsg;
import com.cose.easywu.release.util.KeyboardUtil;
import com.cose.easywu.utils.CacheUtils;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.HttpUtil;
import com.cose.easywu.utils.ImageUtils;
import com.cose.easywu.utils.NoEmojiEditText;
import com.cose.easywu.utils.ToastUtil;
import com.cose.easywu.utils.Utility;
import com.cose.easywu.widget.MessageDialog;
import com.zhy.http.okhttp.callback.StringCallback;

import org.litepal.LitePal;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

public class ReleaseFindPeopleActivity extends BaseActivity {

    private ImageView mIvBack;
    private List<ImageView> mIvPicList = new ArrayList<>();
    private List<CircleImageView> mIvDeletePicList = new ArrayList<>();
    private NoEmojiEditText mEtName, mEtDesc;
    private List<RelativeLayout> mRlPicList = new ArrayList<>();
    private LinearLayout mLlChoosePic;
    private TextView mTvType, mTvRelease;
    private ProgressDialog progressDialog;

    private int picIndex = -1; // 图片索引
    public static final int CHOOSE_PHOTO = 1;

    private List<Bitmap> photoBitmapList = new ArrayList<>();
    private List<File> photoFileList = new ArrayList<>();
    private List<String> photoPathList = new ArrayList<>();

    private BroadcastReceiver receiver;
    private LocalBroadcastManager localBroadcastManager;

    private FindType findType;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private String u_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_find_people);

        initView();
        initListener();
        initData();
    }

    public void initData() {
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();
        u_id = pref.getString("u_id", "");

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        // 注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.RELEASE_CHOOSE_TYPE);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                findType = new FindType(intent.getStringExtra("findType_id"),
                        intent.getStringExtra("findType_name"),
                        intent.getStringExtra("findType_pic"));
                mTvType.setText(findType.getFt_name());
            }
        };
        localBroadcastManager.registerReceiver(receiver, intentFilter);

        // 如果上次有保存的内容，则加载到界面上
        if (pref.getBoolean(u_id + "_hasSaveContent_findPeople", false)) {
            loadSavedContent();
        }
    }

    // 加载上一次保存的内容
    private void loadSavedContent() {
        mEtName.setText(pref.getString(u_id + "_name_findPeople", ""));
        mEtDesc.setText(pref.getString(u_id + "_desc_findPeople", ""));
        String findTypeId = pref.getString(u_id + "_findTypeId_findPeople", "");
        if (!TextUtils.isEmpty(findTypeId)) {
            findType = LitePal.where("ft_id=?", findTypeId).findFirst(FindType.class);
            mTvType.setText(findType.getFt_name());
        }
        // 加载图片
        int picLen = pref.getInt(u_id + "_findPeople_picLen", 0);
        for (int i = 0; i < picLen; i++) {
            photoPathList.add(pref.getString(u_id + "_findPeople_pic" + i, ""));
            picIndex++;
            photoFileList.add(picIndex, new File(photoPathList.get(i)));
            photoBitmapList.add(picIndex, ImageUtils.getBitmapFromPath(photoPathList.get(i), 100, 100));
            RelativeLayout relativeLayout = mRlPicList.get(picIndex);
            Glide.with(this).load(photoBitmapList.get(picIndex)).into(mIvPicList.get(picIndex));
            relativeLayout.setVisibility(View.VISIBLE);
        }
        if (picLen == 3) {
            mLlChoosePic.setVisibility(View.GONE);
        }
    }

    private void initListener() {
        mLlChoosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 添加图片
                choosePic();
            }
        });
        for (int i = 0; i < 3; i++) {
            final int index = i;
            mIvDeletePicList.get(index).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRlPicList.get(index).setVisibility(View.GONE);
                    mLlChoosePic.setVisibility(View.VISIBLE);
                    if (photoBitmapList.size() > 1) {
                        // 删除缓存目录下的图片
                        File file = photoFileList.get(index <= picIndex ? index : index - photoFileList.size());
                        if (file.exists()) {
                            file.delete();
                        }
                        // 从List中移除
                        photoBitmapList.remove(index <= picIndex ? index : index - photoBitmapList.size());
                        photoFileList.remove(index <= picIndex ? index : index - photoFileList.size());
                        photoPathList.remove(index <= picIndex ? index : index - photoPathList.size());
                    } else {
                        // 删除缓存目录下的图片
                        File file = photoFileList.get(0);
                        if (file.exists()) {
                            file.delete();
                        }
                        // 从List中移除
                        photoBitmapList.remove(0);
                        photoFileList.remove(0);
                        photoPathList.remove(0);
                    }
                    picIndex--;
                }
            });
        }
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleExitKey();
            }
        });

        // 设置分类按钮的监听
        mTvType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReleaseFindPeopleActivity.this, ChooseFindTypeActivity.class));
            }
        });

        // 设置确定发布按钮的监听
        mTvRelease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                release();
            }
        });
    }

    // 发布
    private void release() {
        if (!checkContent()) { // 检查内容是否填写完整
            return;
        }
        // 显示提示窗体
        showProgressDialog();
        // 上传到服务器
        Map<String, String> params = new HashMap<>();
        final String fg_name = mEtName.getText().toString().trim();
        final String fg_desc = mEtDesc.getText().toString().trim();
        final String fg_ft_id = findType.getFt_id();
        params.put("fg_name", fg_name);
        params.put("fg_desc", fg_desc);
        params.put("fg_ft_id", fg_ft_id);
        params.put("fg_u_id", u_id);
        final String fg_id = pref.getString("fg_id_findPeople", "");
        if (!TextUtils.isEmpty(fg_id)) {
            params.put("fg_id", fg_id);
        }

        final int len = photoBitmapList.size();

        List<String> keys = new ArrayList<>();
        final List<String> filenames = new ArrayList<>();
        final List<File> files = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            keys.add("pic" + i);
            filenames.add(photoFileList.get(i).getName());
            files.add(photoFileList.get(i));
        }

        HttpUtil.upLoadImageListToServer(Constant.RELEASE_FIND_PEOPLE_URL, keys, filenames, files, params,
                new StringCallback() {
                    @Override
                    public void onError(Call call, final Exception e, int id) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                ToastUtil.showMsgOnCenter(ReleaseFindPeopleActivity.this, "发布失败", Toast.LENGTH_SHORT);
                                Log.e("ReleaseFindPeopleActiv", "发布失败: " + e.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        final ReleaseMsg msg;
                        try {
                            String responseText = URLDecoder.decode(response, "utf-8");
                            msg = Utility.handleReleaseResponse(responseText);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            return;
                        }

                        if (msg == null) {
                            return;
                        }
                        if (msg.getCode().equals("1")) {
                            progressDialog.dismiss();
                            clearUIData();
                            clearReleaseContent();
                            // 发送广播
                            localBroadcastManager.sendBroadcast(new Intent(Constant.RELEASE_NEW_RELEASE));
                            // 删除图片
                            for (File file : photoFileList) {
                                if (file.exists()) {
                                    file.delete();
                                }
                            }
                            // 保存发布的商品到本地
                            if (!TextUtils.isEmpty(fg_id)) { // 商品不是新发布的
                                ReleaseFindGoods goods = LitePal.where("fg_id=?", fg_id).findFirst(ReleaseFindGoods.class);
                                goods.setFg_name(fg_name);
                                goods.setFg_desc(fg_desc);
                                goods.setFg_pic1(filenames.get(0));
                                if (len == 2) {
                                    goods.setFg_pic2(filenames.get(1));
                                } else if (len == 3) {
                                    goods.setFg_pic2(filenames.get(1));
                                    goods.setFg_pic3(filenames.get(2));
                                }
                                goods.setFg_updateTime(new Date(msg.getG_updateTime()));
                                goods.setFg_ft_id(fg_ft_id);
                                goods.setFg_state(0);
                                goods.save();
                            } else { // 商品是新发布的
                                ReleaseFindGoods goods = new ReleaseFindGoods();
                                goods.setFg_id(msg.getG_id());
                                goods.setFg_name(fg_name);
                                goods.setFg_desc(fg_desc);
                                goods.setFg_pic1(filenames.get(0));
                                if (len == 2) {
                                    goods.setFg_pic2(filenames.get(1));
                                } else if (len == 3) {
                                    goods.setFg_pic2(filenames.get(1));
                                    goods.setFg_pic3(filenames.get(2));
                                }
                                goods.setFg_updateTime(new Date(msg.getG_updateTime()));
                                goods.setFg_ft_id(fg_ft_id);
                                goods.setFg_state(0);
                                goods.save();
                            }
                            finish();
                            ToastUtil.showMsgOnCenter(ReleaseFindPeopleActivity.this, "发布成功", Toast.LENGTH_SHORT);
                        } else if (msg.getCode().equals("0")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    ToastUtil.showMsgOnCenter(ReleaseFindPeopleActivity.this,
                                            msg.getMsg(), Toast.LENGTH_SHORT);
                                }
                            });
                        }
                    }
                });
    }

    // 处理退出键
    public void handleExitKey() {
        MessageDialog messageDialog = new MessageDialog(this, R.style.MessageDialog);
        messageDialog.setTitle("提示").setContent("是否保存草稿？")
                .setCancel("不保存", new MessageDialog.IOnCancelListener() {
                    @Override
                    public void onCancel(MessageDialog dialog) {
                        // 清空界面数据
                        clearUIData();
                        // 清空SharedPreferences存储的内容（如果有）
                        clearReleaseContent();
                        // 删除图片
                        for (File file : photoFileList) {
                            if (file.exists()) {
                                file.delete();
                            }
                        }
                        finish();
                    }
                }).setConfirm("保存", new MessageDialog.IOnConfirmListener() {
            @Override
            public void onConfirm(MessageDialog dialog) {
                saveReleaseContent();
                finish();
            }
        }).show();
    }

    // 清空界面数据
    private void clearUIData() {
        mEtName.setText("");
        mEtDesc.setText("");
        for (RelativeLayout rl : mRlPicList) {
            rl.setVisibility(View.GONE);
        }
        mLlChoosePic.setVisibility(View.VISIBLE);
        picIndex = -1;
        photoBitmapList.clear();
        photoFileList.clear();
    }

    private void clearReleaseContent() {
        if (pref.getBoolean(u_id + "_hasSaveContent_findPeople", false)) { // 说明含有保存的内容
            // 修改标志位
            editor.putBoolean(u_id + "_hasSaveContent_findPeople", false);
            // 清空已保存的内容
            editor.remove(u_id + "_name_findPeople");
            editor.remove(u_id + "_desc_findPeople");
            editor.remove(u_id + "_findTypeId_findPeople");
            editor.remove("fg_id_findPeople");
            int picLen = pref.getInt(u_id + "_findPeople_picLen", 0);
            for (int i = 0; i < picLen; i++) {
                editor.remove(u_id + "_findPeople_pic" + i);
            }

            // 提交修改
            editor.apply();
        }
    }

    private void saveReleaseContent() {
        // 记录有保存内容的标志位
        editor.putBoolean(u_id + "_hasSaveContent_findPeople", true);
        // 写入数据
        editor.putString(u_id + "_name_findPeople", mEtName.getText() != null ? mEtName.getText().toString() : "");
        editor.putString(u_id + "_desc_findPeople", mEtDesc.getText() != null ? mEtDesc.getText().toString() : "");
        if (findType != null) {
            editor.putString(u_id + "_findTypeId_findPeople", findType.getFt_id());
        }
        // 写入图片的路径
        int picLen = photoPathList.size();
        editor.putInt(u_id + "_findPeople_picLen", picLen);
        int i;
        for (i = 0; i < picLen; i++) {
            editor.putString(u_id + "_findPeople_pic" + i, photoPathList.get(i));
        }
        for (; i < 3; i++) {
            editor.remove(u_id + "_findPeople_pic" + i);
        }
        // 提交修改
        editor.apply();
    }

    private void choosePic() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CHOOSE_PHOTO);
        } else {
            openAlbum();
        }
    }

    // 打开相册
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
        }
    }

    // 解析封装过的Uri
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String photoPath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                photoPath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri
                        .parse("content://downloads/public_downloads"), Long.valueOf(docId));
                photoPath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            photoPath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            photoPath = uri.getPath();
        }
        photoPathList.add(photoPath);
        displayImage(photoPath); // 根据图片路径显示图片
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String photoPath) {
        if (photoPath != null) {
            picIndex++;
            photoFileList.add(picIndex, new File(ImageUtils.compressImage(photoPath, 800, 800)));
            photoBitmapList.add(picIndex, ImageUtils.getBitmapFromPath(photoPath, 100, 100));
            RelativeLayout relativeLayout = mRlPicList.get(picIndex);
            Glide.with(this).load(photoBitmapList.get(picIndex)).into(mIvPicList.get(picIndex));
            relativeLayout.setVisibility(View.VISIBLE);
            if (picIndex == 2) {
                mLlChoosePic.setVisibility(View.GONE);
            }
        } else {
            ToastUtil.showMsg(this, "读取图片失败", Toast.LENGTH_SHORT);
        }
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private boolean checkContent() {
        if (TextUtils.isEmpty(mEtName.getText().toString().trim())) {
            ToastUtil.showMsgOnCenter(this, "名称不能为空", Toast.LENGTH_SHORT);
            return false;
        }
        if (TextUtils.isEmpty(mEtDesc.getText().toString().trim())) {
            ToastUtil.showMsgOnCenter(this, "描述不能为空", Toast.LENGTH_SHORT);
            return false;
        }
        if (findType == null) {
            ToastUtil.showMsgOnCenter(this, "必须选择物品分类", Toast.LENGTH_SHORT);
            return false;
        }
        if (photoBitmapList.size() < 1) {
            ToastUtil.showMsgOnCenter(this, "请至少上传一张图片", Toast.LENGTH_SHORT);
            return false;
        }

        return true;
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在发布中，请等待...");
        }
        progressDialog.show();
    }

    private void initView() {
        mIvBack = findViewById(R.id.iv_release_back);
        mIvPicList.add(0, (ImageView) findViewById(R.id.iv_release_pic1));
        mIvPicList.add(1, (ImageView) findViewById(R.id.iv_release_pic2));
        mIvPicList.add(2, (ImageView) findViewById(R.id.iv_release_pic3));

        mIvDeletePicList.add(0, (CircleImageView) findViewById(R.id.iv_release_pic1_delete));
        mIvDeletePicList.add(1, (CircleImageView) findViewById(R.id.iv_release_pic2_delete));
        mIvDeletePicList.add(2, (CircleImageView) findViewById(R.id.iv_release_pic3_delete));

        mEtName = findViewById(R.id.et_release_name);
        mEtDesc = findViewById(R.id.et_release_desc);

        mRlPicList.add(0, (RelativeLayout) findViewById(R.id.rl_release_pic1));
        mRlPicList.add(1, (RelativeLayout) findViewById(R.id.rl_release_pic2));
        mRlPicList.add(2, (RelativeLayout) findViewById(R.id.rl_release_pic3));
        mLlChoosePic = findViewById(R.id.ll_release_choosePic);

        mTvType = findViewById(R.id.tv_release_type);
        mTvRelease = findViewById(R.id.tv_release_release);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (Bitmap bitmap : photoBitmapList) {
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        CacheUtils.clearImageAllCache(this);
        Glide.get(this).clearMemory();
        if (receiver != null) {
            localBroadcastManager.unregisterReceiver(receiver);
            receiver = null;
        }
    }
}
