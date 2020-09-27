package com.james.autoinstallapk;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.james.autoinstallapk.databinding.ActivityMainBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding viewBinding;

    private List<File> fileList = new ArrayList<>();
    private List<String> fileNames = new ArrayList<>();

    private final String folderName = "Download";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());

        initPermission();

        //check permission and get all apk file
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                File root = new File(Environment.getExternalStorageDirectory().getPath() + "/" + folderName);

                File[] files= root.listFiles();

                fileList.clear();
                fileNames.clear();

                for (File file: files){
                    if (file.isFile()){
                        if (file.getAbsolutePath().lastIndexOf(".") != -1){
                            final String extension = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
                            if (extension.toLowerCase().equals(".apk")){
                                fileList.add(file);
                                fileNames.add(file.getName());
                            }
                        }
                    }

                }

                viewBinding.tvPath.setText("Path: " + root.getPath());
                viewBinding.btnInstall.setText("Install "+ fileList.size() +" File(s)");

            }
        }

        ArrayAdapter arrayAdapter
                = new ArrayAdapter(this, android.R.layout.simple_list_item_1 , fileNames);

        viewBinding.lvFileAPK.setAdapter(arrayAdapter);



        viewBinding.btnInstall.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                for(File file : fileList){

                    Uri uri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider",file);

                    Intent promptInstall = new Intent(Intent.ACTION_VIEW).setDataAndType(uri,"application/vnd.android.package-archive");
                    promptInstall.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    startActivityForResult(promptInstall,123);
                }

            }
        });

    }

    public void initPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.REQUEST_INSTALL_PACKAGES) != PackageManager.PERMISSION_GRANTED) {

                //Permission don't granted
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) ||
                        shouldShowRequestPermissionRationale(Manifest.permission.REQUEST_INSTALL_PACKAGES) ) {
                    Toast.makeText(MainActivity.this, "Permission isn't granted ", Toast.LENGTH_SHORT).show();
                }
                // Permission don't granted and don't show dialog again.
                else {
                    Toast.makeText(MainActivity.this, "Permission don't granted and don't show dialog again ", Toast.LENGTH_SHORT).show();
                }
                //Register permission
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.REQUEST_INSTALL_PACKAGES}, 1);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length == 2 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permission is Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Permission is Denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
