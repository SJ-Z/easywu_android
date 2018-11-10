package com.cose.easywu.release.fragment;

import android.Manifest;
import android.annotation.TargetApi;
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
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cose.easywu.R;
import com.cose.easywu.app.MainActivity;
import com.cose.easywu.base.BaseFragment;
import com.cose.easywu.db.Type;
import com.cose.easywu.gson.msg.BaseMsg;
import com.cose.easywu.release.activity.ChooseTypeActivity;
import com.cose.easywu.release.util.KeyboardUtil;
import com.cose.easywu.release.util.MyKeyBoardView;
import com.cose.easywu.user.activity.EditPhotoActivity;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.HttpUtil;
import com.cose.easywu.utils.ImageUtils;
import com.cose.easywu.utils.ToastUtil;
import com.cose.easywu.utils.Utility;
import com.cose.easywu.widget.MessageDialog;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

import static android.app.Activity.RESULT_OK;

public class ReleaseFragment extends BaseFragment {

    private ImageView mIvBack;
    private List<ImageView> mIvPicList = new ArrayList<>();
    private List<CircleImageView> mIvDeletePicList = new ArrayList<>();
    private EditText mEtName, mEtDesc;
    private List<RelativeLayout> mRlPicList = new ArrayList<>();
    private LinearLayout mLlChoosePic;
    private TextView mTvPrice, mTvType, mTvRelease;
    private ProgressBar mPb;

    // 键盘的控件
    private EditText mEtPrice, mEtOriginalPrice;
    private MyKeyBoardView myKeyBoardView;
    private LinearLayout mLlPriceSelect;

    private int picIndex = -1; // 图片索引
    public static final int CHOOSE_PHOTO = 1;

    private List<Bitmap> photoBitmapList = new ArrayList<>();
    private List<File> photoFileList = new ArrayList<>();

    private BroadcastReceiver receiver;
    private Type type;
    private double price = -1;
    private double originalPrice = -1;
    private SharedPreferences pref;

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.fragment_release, null);

        mIvBack = view.findViewById(R.id.iv_release_back);
        mIvPicList.add(0, (ImageView) view.findViewById(R.id.iv_release_pic1));
        mIvPicList.add(1, (ImageView) view.findViewById(R.id.iv_release_pic2));
        mIvPicList.add(2, (ImageView) view.findViewById(R.id.iv_release_pic3));

        mIvDeletePicList.add(0, (CircleImageView) view.findViewById(R.id.iv_release_pic1_delete));
        mIvDeletePicList.add(1, (CircleImageView) view.findViewById(R.id.iv_release_pic2_delete));
        mIvDeletePicList.add(2, (CircleImageView) view.findViewById(R.id.iv_release_pic3_delete));

        mEtName = view.findViewById(R.id.et_release_name);
        mEtDesc = view.findViewById(R.id.et_release_desc);

        mRlPicList.add(0, (RelativeLayout) view.findViewById(R.id.rl_release_pic1));
        mRlPicList.add(1, (RelativeLayout) view.findViewById(R.id.rl_release_pic2));
        mRlPicList.add(2, (RelativeLayout) view.findViewById(R.id.rl_release_pic3));
        mLlChoosePic = view.findViewById(R.id.ll_release_choosePic);

        mTvPrice = view.findViewById(R.id.tv_release_price);
        mTvType = view.findViewById(R.id.tv_release_type);
        mTvRelease = view.findViewById(R.id.tv_release_release);

        mEtPrice = view.findViewById(R.id.et_release_price);
        mEtOriginalPrice = view.findViewById(R.id.et_release_orginal_price);
        myKeyBoardView = view.findViewById(R.id.keyboard_view);
        mLlPriceSelect = view.findViewById(R.id.ll_release_price_select);

        mPb = view.findViewById(R.id.pb_release);

        // 设置点击事件
        initListener();

        return view;
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
                        photoBitmapList.remove(index<=picIndex?index:index-photoBitmapList.size());
                        photoFileList.remove(index<=picIndex?index:index-photoFileList.size());
                    } else {
                        photoBitmapList.remove(0);
                        photoFileList.remove(0);
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
        final KeyboardUtil keyboardUtil = new KeyboardUtil(getActivity());
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
                startActivity(new Intent(mContext, ChooseTypeActivity.class));
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
        // 显示进度条
        mPb.setVisibility(View.VISIBLE);
        // 上传到服务器
        Map<String, String> params = new HashMap<>();
        params.put("g_name", mEtName.getText().toString().trim());
        params.put("g_desc", mEtDesc.getText().toString().trim());
        params.put("g_price", String.valueOf(price));
        params.put("g_originalPrice", String.valueOf(originalPrice));
        params.put("g_t_id", type.getT_id());
        params.put("g_u_id", pref.getString("u_id", ""));

        int len = photoBitmapList.size();
        List<String> keys = new ArrayList<>();
        List<String> filenames = new ArrayList<>();
        List<File> files = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            keys.add("pic" + i);
            filenames.add(photoFileList.get(i).getName());
            files.add(photoFileList.get(i));
        }

        HttpUtil.upLoadImageListToServer(Constant.RELEASE_GOODS_URL, keys, filenames, files, params,
                new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mPb.setVisibility(View.GONE);
                                    ToastUtil.showMsgOnCenter(mContext, "发布失败", Toast.LENGTH_SHORT);
                                }
                            });
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        final BaseMsg msg;
                        try {
                            String responseText = URLDecoder.decode(response, "utf-8");
                            msg = Utility.handleBaseMsgResponse(responseText);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            return;
                        }

                        if (msg == null) {
                            return;
                        }
                        if (msg.getCode().equals("1")) {
                            mPb.setVisibility(View.GONE);
                            clearUIData();
                            // 跳转界面
                            ((MainActivity) mContext).showHomeFragment();
                            ((MainActivity) mContext).showFragmentChoose();
                            ToastUtil.showMsgOnCenter(mContext, "发布成功", Toast.LENGTH_SHORT);
                        } else if (msg.getCode().equals("0")) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mPb.setVisibility(View.GONE);
                                        ToastUtil.showMsgOnCenter(mContext, msg.getMsg(), Toast.LENGTH_SHORT);
                                    }
                                });
                            }
                        }
                    }
                });
    }

    private boolean checkContent() {
        if (TextUtils.isEmpty(mEtName.getText().toString().trim())) {
            ToastUtil.showMsgOnCenter(mContext, "物品名称不能为空", Toast.LENGTH_SHORT);
            return false;
        }
        if (TextUtils.isEmpty(mEtDesc.getText().toString().trim())) {
            ToastUtil.showMsgOnCenter(mContext, "物品描述不能为空", Toast.LENGTH_SHORT);
            return false;
        }
        if (price == -1) {
            ToastUtil.showMsgOnCenter(mContext, "物品价格不能为空", Toast.LENGTH_SHORT);
            return false;
        }
        if (type == null) {
            ToastUtil.showMsgOnCenter(mContext, "必须选择物品分类", Toast.LENGTH_SHORT);
            return false;
        }

        return true;
    }

    // 处理退出键
    public void handleExitKey() {
        MessageDialog messageDialog = new MessageDialog(mContext, R.style.MessageDialog);
        messageDialog.setTitle("提示").setContent("是否保存草稿？")
                .setCancel("不保存", new MessageDialog.IOnCancelListener() {
                    @Override
                    public void onCancel(MessageDialog dialog) {
                        // 清空界面数据
                        clearUIData();
                        // 跳转界面
                        ((MainActivity) mContext).showHomeFragment();
                        ((MainActivity) mContext).showFragmentChoose();
                    }
                }).setConfirm("保存", new MessageDialog.IOnConfirmListener() {
            @Override
            public void onConfirm(MessageDialog dialog) {
                // 直接跳转界面
                ((MainActivity) mContext).showHomeFragment();
                ((MainActivity) mContext).showFragmentChoose();
            }
        }).show();
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
            ToastUtil.showMsgOnCenter(mContext, "价格不能为空",  Toast.LENGTH_SHORT);
            return false;
        }
        try {
            price = Double.valueOf(mEtPrice.getText().toString());
        } catch (NumberFormatException e) {
            ToastUtil.showMsgOnCenter(mContext, "价格错误", Toast.LENGTH_SHORT);
            return false;
        }
        if (!TextUtils.isEmpty(mEtOriginalPrice.getText().toString())) {
            try {
                originalPrice = Double.valueOf(mEtOriginalPrice.getText().toString());
            } catch (NumberFormatException e) {
                ToastUtil.showMsgOnCenter(mContext, "原价错误", Toast.LENGTH_SHORT);
                return false;
            }
        }
        return true;
    }

    // 对返回键的处理
    @Override
    public boolean onBackPressed() {
        // 如果小键盘已显示，则隐藏
        if (ifKeyboardVisible()) {
            mLlPriceSelect.setVisibility(View.GONE);
        } else {
            handleExitKey();
        }
        return true;
    }

    public boolean ifKeyboardVisible() {
        if (mLlPriceSelect.getVisibility() == View.VISIBLE) {
            return true;
        }
        return false;
    }

    private void choosePic() {
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (getActivity() != null) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CHOOSE_PHOTO);
            }
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

    @Override
    public void initData() {
        super.initData();
        pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        // 注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.cose.easywu.release.chooseType");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                type = new Type(intent.getStringExtra("type_id"),
                        intent.getStringExtra("type_name"),
                        intent.getStringExtra("type_pic"));
                mTvType.setText(type.getT_name());
            }
        };
        mContext.registerReceiver(receiver, intentFilter);
    }

    // 解析封装过的Uri
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String photoPath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(mContext, uri)) {
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
        displayImage(photoPath); // 根据图片路径显示图片
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = mContext.getContentResolver().query(uri, null, selection, null, null);
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
            photoFileList.add(picIndex, new File(photoPath));
            photoBitmapList.add(picIndex, ImageUtils.getBitmapFromPath(photoPath, 100, 100));
            RelativeLayout relativeLayout = mRlPicList.get(picIndex);
            Glide.with(this).load(photoBitmapList.get(picIndex)).into(mIvPicList.get(picIndex));
            relativeLayout.setVisibility(View.VISIBLE);
            if (picIndex == 2) {
                mLlChoosePic.setVisibility(View.GONE);
            }
        } else {
            ToastUtil.showMsg(mContext, "读取图片失败", Toast.LENGTH_SHORT);
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
        if (receiver != null) {
            mContext.unregisterReceiver(receiver);
            receiver = null;
        }
    }
}
