package com.cose.easywu.utils;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.cose.easywu.base.MyApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils {

    // 从内存中获取用户头像
    public static Bitmap getPhotoFromStorage(String u_id) {
        String photoPath = android.os.Environment.getExternalStorageDirectory() + "/photo/";
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath + u_id + ".jpg", null);
        return bitmap;
    }

    // 从内存中删除用户头像
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
            photoBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
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

    //保存图片到SharedPreferences
    public static void saveBitmapToSharedPreferences(String key, Bitmap bitmap) {
        //第一步:将Bitmap压缩至字节数组输出流ByteArrayOutputStream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
        //第二步:利用Base64将字节数组输出流中的数据转换成字符串String
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String photoString = Base64.encodeToString(byteArray, Base64.DEFAULT);
        //第三步:将String保存至SharedPreferences
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
                MyApplication.getContext()).edit();
        editor.putString(key, photoString);
        editor.apply();
    }

    // 从SharedPreferences中取出图片
    public static Bitmap getBitmapFromSharedPreferences(String key) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(
                MyApplication.getContext());
        String pic = pref.getString(key,"");
        Bitmap bitmap = null;
        if (!TextUtils.isEmpty(pic)) {
            byte[] bytes = Base64.decode(pic.getBytes(), Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        return bitmap;
    }

}