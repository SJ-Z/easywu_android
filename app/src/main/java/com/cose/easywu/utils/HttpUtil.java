package com.cose.easywu.utils;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtil {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public static void sendGetRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendPostRequest(String address, String json, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(address).post(requestBody).build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * @param address 服务器的地址
     * @param key 获取图片的key
     * @param filename 图片的文件名称
     * @param file 图片文件
     * @param params 字符参数
     * @param stringCallback 回调接口
     */
    public static void upLoadImageToServer(String address, String key, String filename, File file,
                                     Map<String, String> params, StringCallback stringCallback) {
        OkHttpUtils.post()
                .addFile(key, filename, file)
                .params(params)
                .url(address)
                .build()
                .execute(stringCallback);
    }

    /**
     * @param address 服务器的地址
     * @param keys 获取图片的key
     * @param filenames 图片的文件名称
     * @param files 图片文件
     * @param params 字符参数
     * @param stringCallback 回调接口
     */
    public static void upLoadImageListToServer(String address,
                                               List<String> keys, List<String> filenames, List<File> files,
                                               Map<String, String> params,
                                               StringCallback stringCallback) {
        int len = files.size();
        if (len == 1) {
            upLoadImageToServer(address, keys.get(0), filenames.get(0), files.get(0), params, stringCallback);
        } else if (len == 2) {
            OkHttpUtils.post()
                    .addFile(keys.get(0), filenames.get(0), files.get(0))
                    .addFile(keys.get(1), filenames.get(1), files.get(1))
                    .params(params)
                    .url(address)
                    .build()
                    .execute(stringCallback);
        } else if (len == 3) {
            OkHttpUtils.post()
                    .addFile(keys.get(0), filenames.get(0), files.get(0))
                    .addFile(keys.get(1), filenames.get(1), files.get(1))
                    .addFile(keys.get(2), filenames.get(2), files.get(2))
                    .params(params)
                    .url(address)
                    .build()
                    .execute(stringCallback);
        }
    }
}
