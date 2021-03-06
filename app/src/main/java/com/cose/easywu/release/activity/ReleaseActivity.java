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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cose.easywu.R;
import com.cose.easywu.base.BaseActivity;
import com.cose.easywu.db.ReleaseGoods;
import com.cose.easywu.db.Type;
import com.cose.easywu.gson.msg.ReleaseMsg;
import com.cose.easywu.release.util.KeyboardUtil;
import com.cose.easywu.release.util.MyKeyBoardView;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

public class ReleaseActivity extends BaseActivity {

    private ImageView mIvBack;
    private List<ImageView> mIvPicList = new ArrayList<>();
    private List<CircleImageView> mIvDeletePicList = new ArrayList<>();
    private NoEmojiEditText mEtName, mEtDesc;
    private List<RelativeLayout> mRlPicList = new ArrayList<>();
    private LinearLayout mLlChoosePic;
    private TextView mTvPrice, mTvType, mTvRelease;
    private ProgressDialog progressDialog;

    // 键盘的控件
    private EditText mEtPrice, mEtOriginalPrice;
    private MyKeyBoardView myKeyBoardView;
    private LinearLayout mLlPriceSelect;

    private int picIndex = -1; // 图片索引
    public static final int CHOOSE_PHOTO = 1;

    private List<Bitmap> photoBitmapList = new ArrayList<>();
    private List<File> photoFileList = new ArrayList<>();
    private List<String> photoPathList = new ArrayList<>();

    private BroadcastReceiver receiver;
    private LocalBroadcastManager localBroadcastManager;

    private Type type;
    private double price = -1;
    private double originalPrice = -1;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private String u_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release);

        initView();
        initListener();
        initData();
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

        // 设置数字键盘的监听
        final KeyboardUtil keyboardUtil = new KeyboardUtil(this);
        keyboardUtil.setOnOkClick(new KeyboardUtil.OnOkClick() {
            @Override
            public void onOkClick() {
                if (validate()) {
                    mLlPriceSelect.setVisibility(View.GONE);
                    if (TextUtils.isEmpty(mEtOriginalPrice.getText().toString())) {
                        mTvPrice.setText("￥" + String.valueOf(price));
                    } else {
                        mTvPrice.setText("￥" + String.valueOf(price) + "， 原价：￥" + String.valueOf(originalPrice));
                    }
                } else {

                }
            }
        });

        keyboardUtil.setOnCancelClick(new KeyboardUtil.onCancelClick() {
            @Override
            public void onCancellClick() {
                mLlPriceSelect.setVisibility(View.GONE);
            }
        });

        mTvPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardUtil.attachTo(mEtPrice);
                mEtPrice.setFocusable(true);
                mEtPrice.setFocusableInTouchMode(true);
                mEtPrice.requestFocus();
                mLlPriceSelect.setVisibility(View.VISIBLE);
            }
        });
        mEtPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    keyboardUtil.attachTo(mEtPrice);
                    mLlPriceSelect.setVisibility(View.VISIBLE);
                } else {
                    keyboardUtil.hideKeyboard();
                    mLlPriceSelect.setVisibility(View.GONE);
                }
            }
        });
        mEtOriginalPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    keyboardUtil.attachTo(mEtOriginalPrice);
                    mLlPriceSelect.setVisibility(View.VISIBLE);
                } else {
                    keyboardUtil.hideKeyboard();
                    mLlPriceSelect.setVisibility(View.GONE);
                }
            }
        });

        // 设置分类按钮的监听
        mTvType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReleaseActivity.this, ChooseTypeActivity.class));
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
        final String g_name = mEtName.getText().toString().trim();
        final String g_desc = mEtDesc.getText().toString().trim();
        final String g_t_id = type.getT_id();
        String g_u_id = pref.getString("u_id", "");
        params.put("g_name", g_name);
        params.put("g_desc", g_desc);
        params.put("g_price", String.valueOf(price));
        params.put("g_originalPrice", String.valueOf(originalPrice));
        params.put("g_t_id", g_t_id);
        params.put("g_u_id", g_u_id);
        final String g_id = pref.getString("g_id", "");
        if (!TextUtils.isEmpty(g_id)) {
            params.put("g_id", g_id);
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

        HttpUtil.upLoadImageListToServer(Constant.RELEASE_GOODS_URL, keys, filenames, files, params,
                new StringCallback() {
                    @Override
                    public void onError(Call call, final Exception e, int id) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                ToastUtil.showMsgOnCenter(ReleaseActivity.this, "发布失败", Toast.LENGTH_SHORT);
                                Log.e("ReleaseActivity", "发布失败: " + e.getMessage());
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
                            if (!TextUtils.isEmpty(g_id)) { // 商品不是新发布的
                                ReleaseGoods releaseGoods = LitePal.where("g_id=?", g_id).findFirst(ReleaseGoods.class);
                                releaseGoods.setG_name(g_name);
                                releaseGoods.setG_desc(g_desc);
                                releaseGoods.setG_price(price);
                                releaseGoods.setG_originalPrice(originalPrice);
                                releaseGoods.setG_pic1(filenames.get(0));
                                if (len == 2) {
                                    releaseGoods.setG_pic2(filenames.get(1));
                                } else if (len == 3) {
                                    releaseGoods.setG_pic2(filenames.get(1));
                                    releaseGoods.setG_pic3(filenames.get(2));
                                }
                                releaseGoods.setG_updateTime(new Date(msg.getG_updateTime()));
                                releaseGoods.setG_t_id(g_t_id);
                                releaseGoods.setG_state(0);
                                releaseGoods.save();
                            } else { // 商品是新发布的
                                ReleaseGoods releaseGoods = new ReleaseGoods();
                                releaseGoods.setG_id(msg.getG_id());
                                releaseGoods.setG_name(g_name);
                                releaseGoods.setG_desc(g_desc);
                                releaseGoods.setG_price(price);
                                releaseGoods.setG_originalPrice(originalPrice);
                                releaseGoods.setG_pic1(filenames.get(0));
                                if (len == 2) {
                                    releaseGoods.setG_pic2(filenames.get(1));
                                } else if (len == 3) {
                                    releaseGoods.setG_pic2(filenames.get(1));
                                    releaseGoods.setG_pic3(filenames.get(2));
                                }
                                releaseGoods.setG_updateTime(new Date(msg.getG_updateTime()));
                                releaseGoods.setG_t_id(g_t_id);
                                releaseGoods.setG_state(0);
                                releaseGoods.save();
                            }
                            finish();
                            ToastUtil.showMsgOnCenter(ReleaseActivity.this, "发布成功", Toast.LENGTH_SHORT);
                        } else if (msg.getCode().equals("0")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    ToastUtil.showMsgOnCenter(ReleaseActivity.this,
                                            msg.getMsg(), Toast.LENGTH_SHORT);
                                }
                            });
                        }
                    }
                });
    }

    private boolean checkContent() {
        if (TextUtils.isEmpty(mEtName.getText().toString().trim())) {
            ToastUtil.showMsgOnCenter(this, "物品名称不能为空", Toast.LENGTH_SHORT);
            return false;
        }
        if (TextUtils.isEmpty(mEtDesc.getText().toString().trim())) {
            ToastUtil.showMsgOnCenter(this, "物品描述不能为空", Toast.LENGTH_SHORT);
            return false;
        }
        if (price == -1) {
            ToastUtil.showMsgOnCenter(this, "物品价格不能为空", Toast.LENGTH_SHORT);
            return false;
        }
        if (type == null) {
            ToastUtil.showMsgOnCenter(this, "必须选择物品分类", Toast.LENGTH_SHORT);
            return false;
        }
        if (photoBitmapList.size() < 1) {
            ToastUtil.showMsgOnCenter(this, "请至少上传一张图片", Toast.LENGTH_SHORT);
            return false;
        }

        return true;
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

    private void saveReleaseContent() {
        // 记录有保存内容的标志位
        editor.putBoolean(u_id + "_hasSaveContent", true);
        // 写入数据
        editor.putString(u_id + "_name", mEtName.getText() != null ? mEtName.getText().toString() : "");
        editor.putString(u_id + "_desc", mEtDesc.getText() != null ? mEtDesc.getText().toString() : "");
        editor.putString(u_id + "_price", String.valueOf(price));
        editor.putString(u_id + "_originalPrice", String.valueOf(originalPrice));
        if (type != null) {
            editor.putString(u_id + "_typeId", type.getT_id());
        }
        // 写入图片的路径
        int picLen = photoPathList.size();
        editor.putInt(u_id + "_picLen", picLen);
        int i;
        for (i = 0; i < picLen; i++) {
            editor.putString(u_id + "_pic" + i, photoPathList.get(i));
        }
        for (; i < 3; i++) {
            editor.remove(u_id + "_pic" + i);
        }
        // 提交修改
        editor.apply();
    }

    private void clearReleaseContent() {
        if (pref.getBoolean(u_id + "_hasSaveContent", false)) { // 说明含有保存的内容
            // 修改标志位
            editor.putBoolean(u_id + "_hasSaveContent", false);
            // 清空已保存的内容
            editor.remove(u_id + "_name");
            editor.remove(u_id + "_desc");
            editor.remove(u_id + "_price");
            editor.remove(u_id + "_originalPrice");
            editor.remove(u_id + "_typeId");
            editor.remove("g_id");
            int picLen = pref.getInt(u_id + "_picLen", 0);
            for (int i = 0; i < picLen; i++) {
                editor.remove(u_id + "_pic" + i);
            }

            // 提交修改
            editor.apply();
        }
    }

    // 清空界面数据
    private void clearUIData() {
        mEtName.setText("");
        mEtDesc.setText("");
        mTvPrice.setText("价格");
        for (RelativeLayout rl : mRlPicList) {
            rl.setVisibility(View.GONE);
        }
        mLlChoosePic.setVisibility(View.VISIBLE);
        picIndex = -1;
        photoBitmapList.clear();
        photoFileList.clear();
    }

    // 验证价格
    private boolean validate() {
        if (TextUtils.isEmpty(mEtPrice.getText().toString())) {
            ToastUtil.showMsgOnCenter(this, "价格不能为空", Toast.LENGTH_SHORT);
            return false;
        }
        try {
            price = Double.valueOf(mEtPrice.getText().toString());
        } catch (NumberFormatException e) {
            ToastUtil.showMsgOnCenter(this, "价格错误", Toast.LENGTH_SHORT);
            return false;
        }
        if (!TextUtils.isEmpty(mEtOriginalPrice.getText().toString())) {
            try {
                originalPrice = Double.valueOf(mEtOriginalPrice.getText().toString());
            } catch (NumberFormatException e) {
                ToastUtil.showMsgOnCenter(this, "原价错误", Toast.LENGTH_SHORT);
                return false;
            }
        }
        return true;
    }

    // 对返回键的处理
    @Override
    public void onBackPressed() {
        // 如果小键盘已显示，则隐藏
        if (ifKeyboardVisible()) {
            mLlPriceSelect.setVisibility(View.GONE);
        } else {
            handleExitKey();
        }
    }

    public boolean ifKeyboardVisible() {
        if (mLlPriceSelect.getVisibility() == View.VISIBLE) {
            return true;
        }
        return false;
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
                type = new Type(intent.getStringExtra("type_id"),
                        intent.getStringExtra("type_name"),
                        intent.getStringExtra("type_pic"));
                mTvType.setText(type.getT_name());
            }
        };
        localBroadcastManager.registerReceiver(receiver, intentFilter);

        // 如果上次有保存的内容，则加载到界面上
        if (pref.getBoolean(u_id + "_hasSaveContent", false)) {
            loadSavedContent();
        }
    }

    // 加载上一次保存的内容
    private void loadSavedContent() {
        mEtName.setText(pref.getString(u_id + "_name", ""));
        mEtDesc.setText(pref.getString(u_id + "_desc", ""));
        double price = Double.valueOf(pref.getString(u_id + "_price", "-1"));
        if (price != -1) {
            this.price = price;
        }
        double originalPrice = Double.valueOf(pref.getString(u_id + "_originalPrice", "-1"));
        if (originalPrice != -1) {
            this.originalPrice = originalPrice;
        }
        setPriceTextView();
        String typeId = pref.getString(u_id + "_typeId", "");
        if (!TextUtils.isEmpty(typeId)) {
            type = LitePal.where("t_id=?", typeId).findFirst(Type.class);
            mTvType.setText(type.getT_name());
        }
        // 加载图片
        int picLen = pref.getInt(u_id + "_picLen", 0);
        for (int i = 0; i < picLen; i++) {
            photoPathList.add(pref.getString(u_id + "_pic" + i, ""));
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

    private void setPriceTextView() {
        mLlPriceSelect.setVisibility(View.GONE);
        if (originalPrice == -1 && price != -1) {
            mTvPrice.setText("￥" + String.valueOf(price));
        } else if (originalPrice != -1 && price != -1) {
            mTvPrice.setText("￥" + String.valueOf(price) + "， 原价：￥" + String.valueOf(originalPrice));
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

        mTvPrice = findViewById(R.id.tv_release_price);
        mTvType = findViewById(R.id.tv_release_type);
        mTvRelease = findViewById(R.id.tv_release_release);

        mEtPrice = findViewById(R.id.et_release_price);
        mEtOriginalPrice = findViewById(R.id.et_release_orginal_price);
        myKeyBoardView = findViewById(R.id.keyboard_view);
        mLlPriceSelect = findViewById(R.id.ll_release_price_select);

    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在发布中，请等待...");
        }
        progressDialog.show();
    }
}
