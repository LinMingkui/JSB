package com.lin;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OpenNewFileActivity extends AppCompatActivity {

    private List<File> fileList;
    private File sdcard;
    private List<HashMap<String, Object>> list;
    private String fileName = "fileName", icon = "icon";

    private ListView lv;
    private LinearLayout layoutReturn, layoutBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_new_file);
        lv = findViewById(R.id.lv);
        layoutReturn = findViewById(R.id.ll_return);
        layoutBack = findViewById(R.id.layout_action_bar);

        sdcard = new File("/sdcard");
        fileList = getFile(sdcard);

        list = getMapData(fileList);
        SimpleAdapter adapter = new SimpleAdapter(this, list,
                R.layout.list_item, new String[]{fileName, icon},
                new int[]{R.id.tv_file_name, R.id.iv_icon});
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File item = fileList.get(position);
                String name;
                if (item.isDirectory()) {
                    name = item.getName();
                    sdcard = new File(sdcard.getPath() + "/" + name);
                    fileList = getFile(sdcard);
                    list = getMapData(fileList);
                    SimpleAdapter adapter = new SimpleAdapter(OpenNewFileActivity.this, list,
                            R.layout.list_item, new String[]{fileName, icon},
                            new int[]{R.id.tv_file_name, R.id.iv_icon});
                    lv.setAdapter(adapter);
                } else {
                    returnPath(item);
                    finish();
                }
            }
        });
        //返回上一目录
        layoutReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("路径..........", sdcard.getParent());
                if (!sdcard.getParent().equals("/")) {
                    sdcard = new File(sdcard.getParent());
                    fileList = getFile(sdcard);
                    List<HashMap<String, Object>> list = getMapData(fileList);
                    SimpleAdapter adapter = new SimpleAdapter(OpenNewFileActivity.this, list,
                            R.layout.list_item, new String[]{fileName, icon},
                            new int[]{R.id.tv_file_name, R.id.iv_icon});
                    lv.setAdapter(adapter);
                } else
                    Toast.makeText(OpenNewFileActivity.this, "已在根目录", Toast.LENGTH_SHORT).show();
            }
        });
        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenNewFileActivity.this.finish();
            }
        });

    }

//    public boolean checkAndRequestPermissions(){
//        int writeExternalPermission = ContextCompat.checkSelfPermission(OpenNewFileActivity.this,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        //是否拥有权限，PackageManager.PERMISSION_GRANTED为拥有
//        if (writeExternalPermission != PackageManager.PERMISSION_GRANTED) {
//
//            /**
//             * 判断该权限请求是否已经被 Denied(拒绝)过。  返回：true 说明被拒绝过 ; false 说明没有拒绝过
//             *
//             * 注意：
//             * 如果用户在过去拒绝了权限请求，并在权限请求系统对话框中选择了 Don't ask again 选项，此方法将返回 false。
//             * 如果设备规范禁止应用具有该权限，此方法也会返回 false。
//             */
//            if (ActivityCompat.shouldShowRequestPermissionRationale(OpenNewFileActivity.this,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                //权限被拒绝过，弹出对话框，告诉用户申请此权限的理由，然后再次请求该权限。
//                AlertDialog.Builder builder = new AlertDialog.Builder(OpenNewFileActivity.this);
//                builder.setTitle("提示").setMessage("没有权限将无法工作").
//                        setPositiveButton("请求权限", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                ActivityCompat.requestPermissions(OpenNewFileActivity.this,
//                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//                            }
//                        }).setNegativeButton("退出", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        OpenNewFileActivity.this.finish();
//                    }
//                }).show();
//                int permission = ContextCompat.checkSelfPermission(OpenNewFileActivity.this,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
//                if(permission==PackageManager.PERMISSION_GRANTED)
//                    return true;
//                else
//                    return false;
//
//            } else {
//                //权限未被拒绝过，请求权限
//                ActivityCompat.requestPermissions(OpenNewFileActivity.this,
//                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//                int permission = ContextCompat.checkSelfPermission(OpenNewFileActivity.this,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
//                if(permission==PackageManager.PERMISSION_GRANTED)
//                    return true;
//                else
//                    return false;
//            }
//
//        }else{
//            return true;
//        }
//    }

    public List<File> getFile(File file) {
        if (file.exists()) {
            File[] fileArray = file.listFiles();
            List<File> fileList = new ArrayList<>();
            for (File f : fileArray) {
                if (f.isDirectory()) {
                    fileList.add(f);
                }
            }
            for (File f : fileArray) {
                if (f.isFile()) {
                    //只添加txt文件
                    String fileName = f.getName();
                    String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
                    if (suffix.equals("txt")) {
                        fileList.add(f);
                    }
                }
            }
            return fileList;
        }
        return null;
    }

    private List<HashMap<String, Object>> getMapData(List<File> fileList) {
        List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        for (File f : fileList) {
            HashMap<String, Object> item = new HashMap<String, Object>();
            if (f.isDirectory()) {
                item.put(fileName, f.getName());
                item.put(icon, R.drawable.icon_folder);
                list.add(item);
            } else {
                item.put(fileName, f.getName());
                item.put(icon, R.drawable.icon_file);
                list.add(item);
            }
        }
        return list;
    }

    //返回选择的文件路径
    private void returnPath(File file) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("path", file.getPath());
        intent.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent);
    }
}
