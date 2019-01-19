package com.hyphenate.easeui.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MyFileUtils {

    /**
     * sd卡的根目录
     */
    private static String mSdRootPath = Environment.getExternalStorageDirectory().getPath();
    /**
     * 手机的缓存根目录
     */
    private static String mDataRootPath = null;
    /**
     * 保存Image的目录名
     */
    private final static String FOLDER_NAME = "/easywu";


    public MyFileUtils (Context context) {
        mDataRootPath = context.getCacheDir().getPath();
    }

    /**
     * 获取储存Image的目录
     *
     * @return
     */
    private static String getStorageDirectory() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?
                mSdRootPath + FOLDER_NAME : mDataRootPath + FOLDER_NAME;
    }

    /**
     * 根据文件名获得或者创建一个文件
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    private static File getFirstFile(String fileName) throws IOException {
        String path = getStorageDirectory();
        File folderFile = new File(path);
        if (!folderFile.exists()) {
            folderFile.mkdirs();
        }
        File file = new File(path + File.separator + fileName);
        file.createNewFile();
        return file;
    }

    /**
     * 创建一个文件夹
     */
    public void makeDirs() {
        String path = getStorageDirectory();
        File folderFile = new File(path);
        if (!folderFile.exists()) {
            folderFile.mkdirs();
        }
    }

    /**
     * 保存Image的方法，有sd卡存储到sd卡，没有就存储到手机目录
     *
     * @param bitmap
     */
    public File save2local(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        String currentTime = String.valueOf(System.currentTimeMillis());
        File file = null;
        FileOutputStream fos = null;
        try {
            file = getFirstFile(currentTime + ".jpg");
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }
}
