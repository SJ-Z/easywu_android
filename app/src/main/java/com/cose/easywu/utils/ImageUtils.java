package com.cose.easywu.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cose.easywu.R;
import com.cose.easywu.base.MyApplication;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.text.DecimalFormat;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ImageUtils {

    // 从sd卡中获取用户头像
    public static Bitmap getPhotoFromStorage(String u_id) {
        String photoPath = android.os.Environment.getExternalStorageDirectory() + "/photo/" + u_id + ".jpg";
        return getBitmapFromPath(photoPath, 80, 80);
    }

    // 从sd卡中删除用户头像
    public static void deletePhotoFromStorage(String u_id) {
        String photoPath = android.os.Environment.getExternalStorageDirectory() + "/photo/";
        File file = new File(photoPath + u_id + ".jpg");
        if (file.exists()) {
            file.delete();
        }
    }

    // 保存图片到sd卡
    public static void savePhotoToStorage(Bitmap photoBitmap, String u_id) {
        //更改的名字
        String photoName = u_id + ".jpg";
        String photoPath = android.os.Environment.getExternalStorageDirectory() +
                "/photo";

        File fileDir = new File(photoPath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        FileOutputStream fos = null;
        File photoFile = null;
        try {
            //重命名并保存
            photoFile = new File(photoPath, photoName);
            photoFile.createNewFile();

            fos = new FileOutputStream(photoFile);
            photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Bitmap getBitmapFromPath(String imgPath, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        //避免出现内存溢出的情况，进行相应的属性设置。
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;

        return BitmapFactory.decodeFile(imgPath, options);
    }

    public static Bitmap getBitmapFromUri(Context context, Uri uri, int reqWidth, int reqHeight) throws Exception {
        InputStream input = context.getContentResolver().openInputStream(uri);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(input, null, options);

        input = context.getContentResolver().openInputStream(uri);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        //避免出现内存溢出的情况，进行相应的属性设置。
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;

        Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);

        if (input != null) {
            input.close();
        }

        return bitmap;
    }

    public static Bitmap getBitmapFromByte(byte[] imgByte, int reqWidth, int reqHeight) {
        InputStream input = null;
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        input = new ByteArrayInputStream(imgByte);
        SoftReference softRef = new SoftReference(BitmapFactory.decodeStream(
                input, null, options));
        bitmap = (Bitmap) softRef.get();
        if (imgByte != null) {
            imgByte = null;
        }

        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static int calculateInSampleSize( //参2和3为ImageView期待的图片大小
                                             BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 图片的实际大小
        final int height = options.outHeight;
        final int width = options.outWidth;
        //默认值
        int inSampleSize = 1;
        //动态计算inSampleSize的值
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * 图片压缩-质量压缩
     *
     * @param filePath 源图片路径
     * @return 压缩后的路径
     */
    public static String compressImage(String filePath, int reqWidth, int reqHeight) {
        // 原文件
        File oldFile = new File(filePath);
        // 压缩文件路径 照片路径
        String photoName = oldFile.getName();
        String photoPath = android.os.Environment.getExternalStorageDirectory() + "/picCache";
        File outputFile = new File(photoPath, photoName);
        FileOutputStream out = null;
        try {
            if (!outputFile.exists()) {
                outputFile.getParentFile().mkdirs();
            } else {
                outputFile.delete();
            }
            int quality = 100 / (getFileSize(oldFile) + 1); // 压缩比例0-100
            Bitmap bm = getBitmapFromPath(filePath, reqWidth, reqHeight); // 获取一定尺寸的图片
            out = new FileOutputStream(outputFile);
            bm.compress(Bitmap.CompressFormat.JPEG, quality, out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return outputFile.getPath();
    }

    /**
     * 获取指定文件大小 —— MB，只精确到个位
     *
     * @param file
     */
    private static int getFileSize(File file) {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                size = fis.available();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            Log.e("ImageUtils", "getFileSize: 文件不存在");
        }
        return (int) size / 1048576;
    }

    /**
     * 将图片保存到本地缓存目录
     * @param bitmap
     * @param picName
     * @return 图片的存储地址
     */
    public static String savePhotoToCache(Bitmap bitmap, String picName) {
        String photoName = picName + ".jpg";
        String basePath = android.os.Environment.getExternalStorageDirectory() + "/picCache";
        File fileDir = new File(basePath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        File outputFile = new File(basePath, photoName);
        FileOutputStream fos = null;
        try {
            if (!outputFile.exists()) {
                outputFile.getParentFile().mkdirs();
            } else {
                outputFile.delete();
            }
            fos = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            return outputFile.getPath();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}