package com.example.androidthreadtest;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadTask extends AsyncTask<Void,Integer,Boolean> {
    MainActivity a;
    Bitmap bitmap = null;
    final String TAG = "DownloadTaskTAG";
    InputStream is ;
    OutputStream os;
    public DownloadTask(MainActivity mainActivity) {
        a = mainActivity;
    }

    @Override
    protected void onPreExecute() {

    }
    int count = 0;
    @Override
    protected Boolean doInBackground(Void... arg0) {
        URL url = null;
        HttpURLConnection connection = null;
        //String FilePath = Environment.getExternalStorageDirectory()  + File.separator +"download.apk";
        //String FilePath = a.getExternalFilesDirs("application/apk")+File.separator +"invoice.apk";
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();

        try {
            //url = new URL("https://api.dujin.org/bing/1366.php");
            url = new URL("https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk");
            String fileName = url.toString().substring(url.toString().lastIndexOf("/"));
            path += fileName;
            File downloadFile = new File(path);
//            connection = (HttpURLConnection) url.openConnection();
//            connection.setConnectTimeout(5 * 1000);
//            connection.setReadTimeout(10 * 1000);

            long totalSize = getContentLength(url.toString());
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Toast.makeText(a.getApplicationContext(), "SD卡不可用~", Toast.LENGTH_SHORT).show();
            }
            int len=0;

            a.progressBar.setMax(100);//设置进度条最大长度
            //Log.i(TAG, "max" + a.progressBar.getMax());
            //publishProgress(len);
            //Log.i(TAG, ",0," + a.progressBar.getMax());
            is = connection.getInputStream();
           // File dFile = rootDir;
            //Log.i(TAG, ",1," + a.progressBar.getMax());
            int result = 0;
            if (downloadFile.exists()) {
                downloadFile.delete();
            }else{
                result = 2;
            }
            Log.i(TAG, ",2," + result);
            int downloadSize = 0;//已经下载的大小
            byte[] bytes = new byte[1024];
            int length = 1024;
            Log.i(TAG, ",3," + a.progressBar.getMax());
            OutputStream out = new FileOutputStream(downloadFile);
            Log.i(TAG, ",4," + a.progressBar.getMax());
            while ((length = is.read(bytes)) != -1) {
                out.write(bytes, 0, length);
                downloadSize += length;
                publishProgress((int) ((downloadSize * 100) / ((double)totalSize)));
            }
            is.close();
            out.close();
            os.close();
//
//            byte bs[] = new byte[1024];
//            while ((len = is.read(bs)) != -1) {
//                os.write(bs, 0, len);//写入文件
//                count += len;
//                publishProgress(count/totalSize);
//                Log.d(TAG,"doInBackgroundTAGdoing"+len);
//            }
//
//            Log.d(TAG,"doInBackgroundTAG"+len);
//            os.flush();
//            is.close();
//            os.close();//最后关闭输入输出流
            //bitmap = BitmapFactory.decodeStream(connection.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        a.progressBar.setProgress(values[0]);//每次更新进度条

        String str = Integer.toString(values[0]);
        Log.d(TAG,"onProgressUpdate"+str);
        //a.textView.setText(str);
    }



    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        //a.mImageView.setImageBitmap(bitmap);
        //在此方法执行main线程操作
        //a.progressBar.setVisibility(View.GONE);//下载完成后，隐藏进度条
        //a.textView.setText("finish");

    }
    private long getContentLength(String address) throws IOException
    {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request build = new Request.Builder().url(address).build();
        Response execute = okHttpClient.newCall(build).execute();
        if(execute != null && execute.isSuccessful()){
            long contentLength = execute.body().contentLength();
            execute.body().close();
            return contentLength;
        }
        return 0;
    }
}
