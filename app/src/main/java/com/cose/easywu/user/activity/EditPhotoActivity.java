package com.cose.easywu.user.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cose.easywu.R;
import com.cose.easywu.base.ActivityCollector;
import com.cose.easywu.db.User;
import com.cose.easywu.gson.msg.BaseMsg;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.HttpUtil;
import com.cose.easywu.utils.ImageUtils;
import com.cose.easywu.utils.ToastUtil;
import com.cose.easywu.utils.Utility;
import com.zhy.http.okhttp.callback.StringCallback;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

public class EditPhotoActivity extends Activity implements View.OnClickListener {

    private static final int TAKE_PHOTO = 1;
    private static final int CHOOSE_PHOTO = 2;

    private CircleImageView mIvPhoto;
    private Button mBtnTakePhoto, mBtnChoosePhoto;
    private TextView mTvCancel, mTvSave;

    private User user;

    private Uri photoUri;
    private Bitmap photoBitmap;
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_photo);
        ActivityCollector.addActivity(this);

        initView();
        initData();
    }

    private void initData() {
        String u_id = PreferenceManager.getDefaultSharedPreferences(this).getString("u_id", "");
        user = LitePal.where("u_id=?", u_id).findFirst(User.class);
        if (!TextUtils.isEmpty(user.getU_photo())) {
            Bitmap bitmap = ImageUtils.getPhotoFromStorage(user.getU_id());
            Glide.with(this).load(bitmap)
                    .apply(new RequestOptions().placeholder(R.drawable.nav_icon))
                    .into(mIvPhoto);
        }
    }

    private void initView() {
        mIvPhoto = findViewById(R.id.iv_editphoto_photo);
        mBtnTakePhoto = findViewById(R.id.btn_editphoto_takephoto);
        mBtnChoosePhoto = findViewById(R.id.btn_editphoto_choosephoto);
        mTvCancel = findViewById(R.id.tv_editphoto_cancel);
        mTvSave = findViewById(R.id.tv_editphoto_save);

        mBtnTakePhoto.setOnClickListener(this);
        mBtnChoosePhoto.setOnClickListener(this);
        mTvCancel.setOnClickListener(this);
        mTvSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_editphoto_takephoto:
                takePhoto();
                break;
            case R.id.btn_editphoto_choosephoto:
                choosePhoto();
                break;
            case R.id.tv_editphoto_cancel:
                finish();
                break;
            case R.id.tv_editphoto_save:
                savePhoto();
                finish();
                break;
        }
    }

    // 保存图片到本地并上传到服务器
    private void savePhoto() {
        if (photoFile != null) {
            // 保存图片到sd卡
            ImageUtils.savePhotoToStorage(photoBitmap, user.getU_id());
            //上传头像
            upLoadImageToServer();
        }
    }

    private void upLoadImageToServer() {
        Map<String, String> params = new HashMap<>();
        params.put("u_id", user.getU_id());
        HttpUtil.upLoadImageToServer(Constant.EDITPHOTO_URL, "u_photo", photoFile.getName(),
                photoFile, params, new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ImageUtils.deletePhotoFromStorage(user.getU_id());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showMsgOnCenter(EditPhotoActivity.this, "头像上传失败", Toast.LENGTH_SHORT);
                            }
                        });
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
                            String photo = msg.getMsg();
                            user.setU_photo(photo);
                            user.save();
                        } else if (msg.getCode().equals("0")) {
                            ImageUtils.deletePhotoFromStorage(user.getU_id());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.showMsgOnCenter(EditPhotoActivity.this, msg.getMsg(), Toast.LENGTH_SHORT);
                                }
                            });
                        }
                    }
                });
    }

    private void choosePhoto() {
        if (ContextCompat.checkSelfPermission(EditPhotoActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(EditPhotoActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            openAlbum();
        }
    }

    // 打开相册
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    private void takePhoto() {
        // 创建File对象，用于存储拍照后的照片
        photoFile = new File(getExternalCacheDir(), "user_photo.jpg");
        try {
            if (photoFile.exists()) {
                photoFile.delete();
            }
            photoFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            photoUri = FileProvider.getUriForFile(EditPhotoActivity.this, "com.cose.easywu.fileprovider", photoFile);
        } else {
            photoUri = Uri.fromFile(photoFile);
        }
        // 启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        // 将拍摄的照片显示出来
                        photoBitmap = ImageUtils.getBitmapFromUri(this, photoUri, 80, 80);
                        Glide.with(this).load(photoBitmap)
                                .apply(new RequestOptions().placeholder(R.drawable.nav_icon))
                                .into(mIvPhoto);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
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
        displayImage(photoPath); // 根据图片路径显示图片
    }

    private void displayImage(String photoPath) {
        if (photoPath != null) {
            photoFile = new File(photoPath);
            photoBitmap = ImageUtils.getBitmapFromPath(photoPath, 80, 80);
            Glide.with(this).load(photoBitmap)
                    .apply(new RequestOptions().placeholder(R.drawable.nav_icon))
                    .into(mIvPhoto);
        } else {
            ToastUtil.showMsg(this, "读取图片失败", Toast.LENGTH_SHORT);
        }
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

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    ToastUtil.showMsgOnCenter(this, "权限被拒绝，无法访问相册", Toast.LENGTH_SHORT);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
