package com.cose.easywu.release.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.cose.easywu.app.MainActivity;
import com.cose.easywu.base.ActivityCollector;
import com.cose.easywu.base.BaseFragment;
import com.cose.easywu.user.activity.EditPhotoActivity;
import com.cose.easywu.utils.ImageUtils;
import com.cose.easywu.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ReleaseFragment extends BaseFragment {

    private ImageView mIvBack;
    private List<ImageView> mIvPicList = new ArrayList<>();
    private List<CircleImageView> mIvDeletePicList = new ArrayList<>();
    private EditText mEtName, mEtDesc;
    private List<RelativeLayout> mRlPicList = new ArrayList<>();
    private LinearLayout mLlChoosePic;
    private TextView mTvPrice, mTvType, mTvRelease;

    private int picIndex = -1; // 图片索引
    public static final int CHOOSE_PHOTO = 1;

    private List<Bitmap> photoBitmapList = new ArrayList<>();
    private List<File> photoFileList = new ArrayList<>();

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
                ((MainActivity) mContext).showHomeFragment();
                ((MainActivity) mContext).showFragmentChoose();
            }
        });
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
}
