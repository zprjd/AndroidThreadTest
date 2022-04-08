package com.example.androidthreadtest;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownLoadAsyncTask extends AsyncTask<Void,Integer,Boolean> {
    private PowerManager.WakeLock mWakeLock;
    private int ValueProgress=100;
    private Context context;
    final String TAG = "DownLoadAsyncTaskTAG";

    public DownLoadAsyncTask(Context context){
        this.context=context;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL("https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk");
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(TAG,"Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage());
                return true;
            }
            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();
            Log.d(TAG,"fileLength"+fileLength);
            // download the file
            input = connection.getInputStream();
            //create output
            output = new FileOutputStream(getSDCardDir());
            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    Log.d(TAG,"cancel");
                    return null;
                }
                total += count;
                Log.d(TAG,"total"+total);
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                {
                    publishProgress((int) (total * 100 / fileLength));
                    Log.d(TAG,"progress"+(int) (total * 100 / fileLength));
                }
                //
                Thread.sleep(100);
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }
            if (connection != null)
                connection.disconnect();
        }

        return true;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();
        //Display progressBar
//        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        Log.e(TAG,"onPostExecute+"+aBoolean);
        mWakeLock.release();
        if (aBoolean != true)
            Log.e(TAG,"onPostExecute+"+aBoolean);
        else {
            Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * set progressBar
     * @param values
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        //progressBar.setmProgress(values[0]);
        //update progressBar
        if(updateUI!=null){
            Log.d(TAG,"update"+values[0]);
            updateUI.UpdateProgressBar(values[0]);
        }
    }

    /**
     * get SD card path
     * @return
     */
    public File getSDCardDir(){
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            // 创建一个文件夹对象，赋值为外部存储器的目录
            String dirName = Environment.getExternalStorageDirectory()+"/MyDownload/";
            File f = new File(dirName);
            if(!f.exists()){
                f.mkdir();
            }
            File downloadFile=new File(f,"new.jpg");
            return downloadFile;
        }
        else{
            Log.e(TAG,"NO SD Card!");
            return null;

        }

    }

    public UpdateUI updateUI;


    public interface UpdateUI{
        void UpdateProgressBar(Integer values);
    }

    public void setUpdateUIInterface(UpdateUI updateUI){
        this.updateUI=updateUI;
    }


}
