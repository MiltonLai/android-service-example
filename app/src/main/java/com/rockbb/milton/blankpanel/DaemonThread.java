package com.rockbb.milton.blankpanel;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DaemonThread extends Thread {
    private static final String TAG = DaemonThread.class.getSimpleName();
    private String url;
    private String result;

    public DaemonThread(String url) {
        this.url = url;
    }

    @Override
    public void run() {
        Log.d(TAG, "Timestamp:" + System.currentTimeMillis());
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            InputStream in = connection.getInputStream();
            byte[] bytes = getBytesByInputStream(in);
            result = new String(bytes, "GBK");
            Log.d(TAG, result);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (connection != null){
                connection.disconnect();
            }
        }
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
}