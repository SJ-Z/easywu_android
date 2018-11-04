package com.cose.easywu.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;

public class ImageUtils {

    // 从sd卡中获取用户头像
    public static Bitmap getPhotoFromStorage(String u_id) {
        String photoPath = android.os.Environment.getExternalStorageDirectory() + "/photo/" + u_id + ".jpg";
        return getBitmapFromPath(photoPath, 80, 80);
    }

    // 从sd卡中删除用户头像
    public static void deletePhotoFromStorage(String u_id) {
        String photoPath = android.os.Environment.getExternalStorageDirectory() + "/photo/";
        File file = new File(photoPath + u_id +".jpg");
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
        if(!fileDir.exists()){
            fileDir.mkdirs();
        }
        FileOutputStream fos = null;
        File photoFile = null;
        try{
            //重命名并保存
            photoFile = new File(photoPath, photoName);
            photoFile.createNewFile();

            fos = new FileOutputStream(photoFile);
            photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(fos != null){
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
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);;
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
            final int halfHeight = height/2;
            final int halfWidth = width/2;
            while( (halfHeight/inSampleSize) >= reqHeight && (halfWidth/inSampleSize) >= reqWidth){
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

}