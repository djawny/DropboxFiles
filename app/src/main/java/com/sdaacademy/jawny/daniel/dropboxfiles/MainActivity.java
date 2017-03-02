package com.sdaacademy.jawny.daniel.dropboxfiles;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();

//        try {
//            uploadFile();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

//        uploadChosenFile();

        downloadFile("https://developer.android.com/images/home/nougat_bg_2x.jpg");
    }

    private void uploadChosenFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        if (uri != null) {
            try {
                uploadFile(uri);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkPermissions() {
        int status = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (status == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permissions, 0);
            return false;
        }
    }

    private void uploadFile() throws JSONException {
        File file = new File("/storage/emulated/0/Download/piwo.jpg");
        String jsonPath = new JSONObject().put("path", "/piwo.jpg").toString();
        MediaType mediaType = MediaType.parse("application/octet-stream");
        RequestBody body = RequestBody.create(mediaType, file);
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer 3TS3KjVdr6AAAAAAAAAAFrf169ZowTZOabL7ZtZcq5P0Q7PjQf8hF6ar0thW2_gx")
                .addHeader("Content-Type", "application/octet-stream")
                .addHeader("Dropbox-API-Arg", jsonPath)
                .url("https://content.dropboxapi.com/2/files/upload")
                .post(body)
                .build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("TEST", "fail", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("TEST", "onResponse " + response.body().string());
            }
        });
    }

    private void uploadFile(Uri uri) throws JSONException {
        Log.i("TEST", uri.getPath());
        String jsonPath = new JSONObject().put("path", "/p.jpg").toString();
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer 3TS3KjVdr6AAAAAAAAAAFrf169ZowTZOabL7ZtZcq5P0Q7PjQf8hF6ar0thW2_gx")
                .addHeader("Content-Type", "application/octet-stream")
                .addHeader("Dropbox-API-Arg", jsonPath)
                .url("https://content.dropboxapi.com/2/files/upload")
                .post(new StreamRequestBody(getApplicationContext(), uri))
                .build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("TEST", "fail", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("TEST", "onResponse " + response.body().string());
            }
        });
    }

    private void downloadFile(String fileUrl) {
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(fileUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        downloadManager.enqueue(request);
        Log.i("TEST", "Downloding...");
    }
}
