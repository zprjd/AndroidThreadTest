package com.example.androidthreadtest;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

//public class MainActivity extends AppCompatActivity implements View.OnClickListener,DownLoadAsyncTask.UpdateUI{
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

//    private static int WRITE_EXTERNAL_STORAGE_REQUEST_CODE=0x11;
//    private static String DOWNLOAD_FILE_JPG_URL="http://img2.3lian.com/2014/f6/173/d/51.jpg";
//    private ProgressBar progressBar;
//
//    private Button downloadBtn;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        progressBar= findViewById(R.id.progressbar);
//        downloadBtn= (Button) findViewById(R.id.downloadBtn);
//        //create DownLoadAsyncTask
//        final DownLoadAsyncTask  downLoadAsyncTask= new DownLoadAsyncTask(MainActivity.this);
//        //set Interface
//        downLoadAsyncTask.setUpdateUIInterface(this);
//        //start download
//        downloadBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //execute
//                downLoadAsyncTask.execute();
//            }
//        });
//
//        //android 6.0 权限申请
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            //android 6.0 API 必须申请WRITE_EXTERNAL_STORAGE权限
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
//        }
//    }
//
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        doNext(requestCode,grantResults);
//    }
//
//    private void doNext(int requestCode, int[] grantResults) {
//        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission Granted
//                Log.e("","Permission Granted");
//            } else {
//                // Permission Denied
//                Log.e("","Permission Denied");
//            }
//        }
//    }
//
//    /**
//     * update progressBar
//     * @param values
//     */
//    @Override
//    public void UpdateProgressBar(Integer values) {
//        progressBar.setProgress(values);
//    }
//
//    @Override
//    public void onClick(View v) {
//
//    }

    TextView textView;
    ProgressBar progressBar;

    ImageView mImageView;

    /**
     * 步骤1：创建AsyncTask子类
     * 注：
     *   a. 继承AsyncTask类
     *   b. 为3个泛型参数指定类型；若不使用，可用java.lang.Void类型代替
     *      此处指定为：输入参数 = String类型、执行进度 = Integer类型、执行结果 = String类型
     *   c. 根据需求，在AsyncTask子类内实现核心方法
     */
    final String TAG = "MainActivityTAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File filesDir = getApplicationContext().getFilesDir();
        String absolutePath = filesDir.getAbsolutePath();

        File fs = new File(absolutePath, "newText.txt");

        boolean firstStart = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("first", true);

        if(firstStart)
        {
            try(FileWriter fileWriter = new FileWriter(fs);) {
                fileWriter.write("Hello, World");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            try(Scanner in = new Scanner(fs.getAbsolutePath()))
            {
                Toast.makeText(this, in.nextLine(), Toast.LENGTH_SHORT).show();
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }


        findViewById(R.id.button1).setOnClickListener(this);
        textView = (TextView) findViewById(R.id.textView1);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        progressBar.setProgress(0);
        mImageView = findViewById(R.id.img);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.WAKE_LOCK,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }
        createFile(Uri.parse(getExternalFilesDirs("application/apk")+File.separator +"invoice.apk"));

    }

    private void createFile(Uri pickerInitialUri) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/apk");
        intent.putExtra(Intent.EXTRA_TITLE, "invoice.apk");

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when your app creates the document.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

     //   startActivityForResult(intent, 43);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.button1:
                DownloadTask asyncTask = new DownloadTask(this);
                DownLoadAsyncTask asyncTask2 = new DownLoadAsyncTask(this);
                asyncTask.execute();
                //asyncTask.execute("https://api.dujin.org/bing/1366.php");//可以通过此处传参数给AsyncTask,execute方法只能在UI线程里使用
                break;

            default:
                break;
        }
    }
    /**
     * Android 11 及以上选择文件导出
     */
    private static final int REQUEST_CODE_ANDROID_11_OR_HIGHER_FILE_SELECT_FOR_OUTPUT = 5;

    /**
     * 文件后缀
     */
    private static final String POSTFIX = ".xlsx";
    private static final int WRITE_REQUEST_CODE = 43;

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {

        super.startActivityForResult(intent, requestCode);
        Log.d(TAG,"requestCode"+requestCode);
    }
}