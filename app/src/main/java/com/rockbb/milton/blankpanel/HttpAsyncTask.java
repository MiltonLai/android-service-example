package com.rockbb.milton.blankpanel;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class HttpAsyncTask extends AsyncTask<String, Void, Void> {
    public static final int METHOD_GET = 0;
    public static final int METHOD_POST = 1;

    private static final String TAG = HttpAsyncTask.class.getSimpleName();

    private String postData;
    private int method;
    private int connectTimeout;
    private int readTimeout;
    private String encoding;

    private int type;
    private HttpAsyncCallback callback;

    public HttpAsyncTask(int method, String encoding) {
        this(null, method, encoding, 10000, 10000, 0, null);
    }

    public HttpAsyncTask(int method, String encoding, int type, HttpAsyncCallback callback) {
        this(null, method, encoding, 10000, 10000, type, callback);
    }

    public HttpAsyncTask(String postData, int method, String encoding, int type, HttpAsyncCallback callback) {
        this(postData, method, encoding, 10000, 10000, type, callback);
    }

    public HttpAsyncTask(String postData, int method, String encoding, int connectTimeout, int readTimeout, int type, HttpAsyncCallback callback) {
        this.postData = postData;
        this.method = method;
        this.encoding = encoding;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;

        this.type = type;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(String... strings) {
        Log.d(TAG, "Timestamp:" + System.currentTimeMillis());
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(strings[0]).openConnection();
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);
            if (method == METHOD_GET) {
                connection.setRequestMethod("GET");
            } else {
                // get请求的话默认就行了，post请求需要setDoOutput(true)，这个默认是false的。
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                if (this.postData != null) {
                    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                    writer.write(postData);
                    writer.flush();
                }
            }

            int statusCode = connection.getResponseCode();
            if (statusCode ==  200) {
                InputStream in = connection.getInputStream();
                byte[] bytes = getBytesByInputStream(in);
                String response = new String(bytes, encoding);
                Log.d(TAG, response);
                // From here you can convert the string to JSON with whatever JSON parser you like to use
                // After converting the string to JSON, I call my custom callback. You can follow this
                // process too, or you can implement the onPostExecute(Result) method
                // Use the response to create the object you need
                if (callback != null) {
                    callback.completionHandler(true, type, "Timestamp:" + System.currentTimeMillis() + ", " + response);
                }
            } else {
                Log.d(TAG, statusCode+"");
                if (callback != null) {
                    callback.completionHandler(false, type, statusCode);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (connection != null){
                connection.disconnect();
            }
        }
        return null;
    }

    private byte[] getBytesByInputStream(InputStream is) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        try {
            while ((length = is.read(buffer)) != -1) {
                bos.write(buffer, 0, length);
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return bos.toByteArray();
    }

    public static String formDataToString(Map<String, String> data, String encoding) {
        StringBuilder sb = new StringBuilder();
        String con = "";
        for (String key : data.keySet()) {
            String value = data.get(key);
            try {
                key = URLEncoder.encode(key, encoding);
                value = URLEncoder.encode(value, encoding);
                sb.append(con).append(key).append("=").append(value);
                con = "&";
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "UnsupportedEncodingException " + encoding + " in processing:" + key);
            }
        }
        return sb.toString();
    }

    public static String formDataToJson(Map<String, String> data, String encoding) {
        if (data != null) {
            JSONObject jsonObject = new JSONObject(data);
            return jsonObject.toString();
        }
        return null;
    }
}
