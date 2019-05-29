package com.lin;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.lin.widget.SetTextSizeDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    final int NIGHT_MODE = 1;
    final int Day_MODE = 0;
    private static int lines = 0;
    private ImageView imDelete, imSave, imMenu;
    private EditText edtContent;
    private LinearLayout layoutParent, layoutActionBar, layoutPop, layoutHalvingLine;
    private ScrollView layoutEdt;
    private SharedPreferences sharedPreferences, setSharedPreferences;
    private SharedPreferences.Editor editor, setEditor;
    private PopupWindow pop;
    private String textFileName = "data", setFileName = "set",
            modeTag = "mode", textSizeTag = "textSize", linesTag = "lines";
    private static String sharedPreferencesLines = "1";
    private View viewPop, viewHalvingLine, viewLine;
    private TextView tvLines;
    private TextView tvSetMode, tvSetTextSize, tvAbout, tvExport, tvImport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!checkPermission()) {
            Toast.makeText(this, "请授予储存权限后重启程序", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        init();
        initUI();
        setClickListener();
        edtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edtContent.getText().toString().isEmpty())
                    imDelete.setVisibility(View.GONE);
                else
                    imDelete.setVisibility(View.VISIBLE);
                //显示行
                int nowLines = edtContent.getLineCount();
                String textLines = "";
                if (nowLines != lines) {
                    lines = nowLines;
                    for (int j = 1; j <= lines; j++) {
                        if (j != lines)
                            textLines += j + "\n";
                        else
                            textLines += j;

                    }
                    tvLines.setText(textLines);
                }
                sharedPreferencesLines = textLines;
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save:
                onSaveClick();
                break;
            case R.id.delete:
                lines = 0;
                edtContent.setText(null);
                break;
            case R.id.edt:
                onEdtContentClick();
                break;
            case R.id.layout_parent:
                edtContent.setFocusable(false);
                break;
            case R.id.menu:
                onMenuClick();
                break;
            case R.id.set_mode:
                exchangeUI();
                pop.dismiss();
                break;
            case R.id.text_size:
                pop.dismiss();
                onTvSetTextSizeClick();
                break;
            case R.id.text_export:
                pop.dismiss();
                if (checkPermission())
                    onExportClick();
                else Toast.makeText(MainActivity.this, "请授予储存权限", Toast.LENGTH_LONG).show();
                break;
            case R.id.text_import:
                pop.dismiss();
                if (checkPermission()) {
                    Intent intentOpenFile = new Intent(MainActivity.this, OpenNewFileActivity.class);
                    startActivityForResult(intentOpenFile, 1);
                } else Toast.makeText(MainActivity.this, "请授予储存权限", Toast.LENGTH_LONG).show();
                break;
            case R.id.about:
                pop.dismiss();
                Intent intentAbout = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intentAbout);
                break;

        }
    }

    public boolean checkPermission() {
        int writeExternalPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //是否拥有权限，PackageManager.PERMISSION_GRANTED为拥有
        if (writeExternalPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void onSaveClick() {
        editor.putString(textFileName, edtContent.getText().toString());
        setEditor.putString(linesTag, sharedPreferencesLines);
        editor.apply();
        setEditor.apply();
        Toast.makeText(MainActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
    }

    private void onMenuClick() {
        pop = new PopupWindow(viewPop, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //这四行实现点击其他地方pop消失
        pop.setOutsideTouchable(true);
        pop.setFocusable(true);
        pop.setTouchable(true);//这个控制PopupWindow内部控件的点击事件
        pop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pop.update();

        int[] location = new int[2];
        //获取控件的位置
        layoutActionBar.getLocationOnScreen(location);
        //在空间的右下方显示
        pop.showAtLocation(layoutActionBar, Gravity.NO_GRAVITY,
                location[0] + layoutActionBar.getWidth() - pop.getWidth(),
                location[1] + layoutActionBar.getHeight());
    }

    private void onEdtContentClick() {
        edtContent.setFocusable(true);
        edtContent.setFocusableInTouchMode(true);
        edtContent.requestFocus();
        edtContent.findFocus();
    }

    private void onTvSetTextSizeClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final String item[] = new String[]{"16", "18", "20", "22", "24", "26", "28", "30", "35", "40"};
        builder.setTitle("字体大小").setItems(item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                edtContent.setTextSize(Integer.parseInt(item[i]));
                tvLines.setTextSize(Integer.parseInt(item[i]));
                setEditor.putInt(textSizeTag, Integer.parseInt(item[i]));
                setEditor.apply();
            }
        }).show();
    }

    private void onExportClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View viewDialogExport = getLayoutInflater().inflate(R.layout.dialog_export, null);
        builder.setView(viewDialogExport);
        builder.create();
        final AlertDialog dialog = builder.show();

        LinearLayout linearLayout = viewDialogExport.findViewById(R.id.linear_layout);
        final EditText edtPath = viewDialogExport.findViewById(R.id.edt_path);
        final EditText edtFilename = viewDialogExport.findViewById(R.id.edt_filename);
        TextView tvPath = viewDialogExport.findViewById(R.id.tv_path);
        TextView tvFilename = viewDialogExport.findViewById(R.id.tv_filename);
        TextView tvSelectPath = viewDialogExport.findViewById(R.id.tv_select_path);
        Button OK = viewDialogExport.findViewById(R.id.btn_OK);
        Button Cancel = viewDialogExport.findViewById(R.id.btn_Cancel);
        //设置夜间模式
        if (setSharedPreferences.getInt(modeTag, 0) == NIGHT_MODE) {
            linearLayout.setBackgroundColor(getResources().getColor(R.color.colorEdtNight));
            edtPath.setTextColor(getResources().getColor(R.color.colorTextNight));
            edtFilename.setTextColor(getResources().getColor(R.color.colorTextNight));
            tvPath.setTextColor(getResources().getColor(R.color.colorTextNight));
            tvFilename.setTextColor(getResources().getColor(R.color.colorTextNight));
            tvSelectPath.setTextColor(getResources().getColor(R.color.colorTextNight));
            OK.setTextColor(getResources().getColor(R.color.colorTextNight));
            Cancel.setTextColor(getResources().getColor(R.color.colorTextNight));
            OK.setBackgroundColor(getResources().getColor(R.color.colorEdtNight));
            Cancel.setBackgroundColor(getResources().getColor(R.color.colorEdtNight));
        }

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = edtPath.getText().toString();
                String name = edtFilename.getText().toString();
                File filePath = new File(path);
                File fileName = new File(filePath.getPath() + File.separator + name);
                if (filePath.exists()) {
                    if (fileName.exists()) {
                        Toast.makeText(MainActivity.this, "文件已存在", Toast.LENGTH_SHORT).show();
                    } else {
                        exportText(fileName);
                        dialog.dismiss();
                    }
                } else {
                    filePath.mkdirs();
                    exportText(fileName);
                    dialog.dismiss();
                }
            }
        });

    }

    //打开文件返回数据
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK)
                    importText(data.getExtras().getString("path"));
                break;
        }
    }

    //导出
    public void exportText(File file) {
        FileOutputStream fileOutputStream = null;
        String exportFileName;
        try {
            file.createNewFile();
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(edtContent.getText().toString().getBytes());
            Toast.makeText(this, "已保存在" + file.getPath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //导入
    public void importText(String path) {
        FileInputStream fileInputStream = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                Toast.makeText(this, "文件不存在", Toast.LENGTH_LONG).show();
            } else {
                fileInputStream = new FileInputStream(file);
                byte[] buff = new byte[1024];
                StringBuilder sb = new StringBuilder();
                int len;
                while ((len = fileInputStream.read(buff)) > 0) {
                    sb.append(new String(buff, 0, len));
                }
                edtContent.setText(sb.toString());
                Toast.makeText(this, "成功打开" + path, Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setClickListener() {
        layoutParent.setOnClickListener(this);
        imDelete.setOnClickListener(this);
        imSave.setOnClickListener(this);
        imMenu.setOnClickListener(this);
        edtContent.setOnClickListener(this);
        tvSetMode.setOnClickListener(this);
        tvSetTextSize.setOnClickListener(this);
        tvExport.setOnClickListener(this);
        tvImport.setOnClickListener(this);
        tvAbout.setOnClickListener(this);
    }

    //控件初始化
    private void init() {
        imDelete = findViewById(R.id.delete);
        imSave = findViewById(R.id.save);
        imMenu = findViewById(R.id.menu);
        edtContent = findViewById(R.id.edt);
        tvLines = findViewById(R.id.tv_lines);
        viewLine = findViewById(R.id.view_line);
        layoutParent = findViewById(R.id.layout_parent);
        layoutActionBar = findViewById(R.id.layout_action_bar);
        layoutEdt = findViewById(R.id.layout_edt);

        viewPop = getLayoutInflater().inflate(R.layout.layoyt_pop, null);
        tvSetMode = viewPop.findViewById(R.id.set_mode);
        layoutPop = viewPop.findViewById(R.id.layout_pop);
        tvSetTextSize = viewPop.findViewById(R.id.text_size);
        tvExport = viewPop.findViewById(R.id.text_export);
        tvImport = viewPop.findViewById(R.id.text_import);
        tvAbout = viewPop.findViewById(R.id.about);

        viewHalvingLine = getLayoutInflater().inflate(R.layout.layout_halving_line, null);
        layoutHalvingLine = viewHalvingLine.findViewById(R.id.layout_halving_line);

        sharedPreferences = getSharedPreferences(textFileName, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        setSharedPreferences = getSharedPreferences(setFileName, MODE_PRIVATE);
        setEditor = setSharedPreferences.edit();
    }

    //UI及数据初始化
    public void initUI() {
        if (setSharedPreferences.getInt(modeTag, 0) == Day_MODE) {
            dayModeUI();
        } else {
            nightModeUI();
        }
        edtContent.setText(sharedPreferences.getString(textFileName, null));
        edtContent.setTextSize(setSharedPreferences.getInt(textSizeTag, 18));
        edtContent.setFocusable(false);

        //隐藏删除图标
        if (edtContent.getText().toString().isEmpty())
            imDelete.setVisibility(View.GONE);

        tvLines.setText(setSharedPreferences.getString(linesTag, sharedPreferencesLines));
        tvLines.setTextSize(setSharedPreferences.getInt(textSizeTag, 18));
    }

    //模式切换
    public void exchangeUI() {
        if (setSharedPreferences.getInt(modeTag, 0) == Day_MODE) {
            nightModeUI();
        } else {
            dayModeUI();
        }
    }

    //夜间模式
    public void nightModeUI() {
        layoutActionBar.setBackgroundColor(getResources().getColor(R.color.colorActionBarNight));
        layoutEdt.setBackgroundColor(getResources().getColor(R.color.colorEdtNight));
        layoutPop.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_pop_night));
        layoutHalvingLine.setBackgroundDrawable(getResources().getDrawable(R.color.colorTextNight));
        viewLine.setBackgroundDrawable(getResources().getDrawable(R.color.colorTextNight));
        edtContent.setTextColor(getResources().getColor(R.color.colorTextNight));
        tvLines.setTextColor(getResources().getColor(R.color.colorTextNight));
        tvSetMode.setTextColor(getResources().getColor(R.color.colorTextNight));
        tvSetTextSize.setTextColor(getResources().getColor(R.color.colorTextNight));
        tvExport.setTextColor(getResources().getColor(R.color.colorTextNight));
        tvImport.setTextColor(getResources().getColor(R.color.colorTextNight));
        tvAbout.setTextColor(getResources().getColor(R.color.colorTextNight));
        tvSetMode.setText("日间模式");
        setEditor.putInt(modeTag, NIGHT_MODE);
        setEditor.apply();
    }

    //日间模式
    public void dayModeUI() {
        layoutActionBar.setBackgroundColor(getResources().getColor(R.color.colorActionBarDay));
        layoutEdt.setBackgroundColor(getResources().getColor(R.color.colorEdtDay));
        layoutPop.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_pop_day));
        layoutHalvingLine.setBackgroundDrawable(getResources().getDrawable(R.color.colorTextDay));
        viewLine.setBackgroundDrawable(getResources().getDrawable(R.color.colorTextDay));
        edtContent.setTextColor(getResources().getColor(R.color.colorTextDay));
        tvLines.setTextColor(getResources().getColor(R.color.colorTextDay));
        tvSetMode.setTextColor(getResources().getColor(R.color.colorTextDay));
        tvSetTextSize.setTextColor(getResources().getColor(R.color.colorTextDay));
        tvExport.setTextColor(getResources().getColor(R.color.colorTextDay));
        tvImport.setTextColor(getResources().getColor(R.color.colorTextDay));
        tvAbout.setTextColor(getResources().getColor(R.color.colorTextDay));
        tvSetMode.setText("夜间模式");
        setEditor.putInt(modeTag, Day_MODE);
        setEditor.apply();
    }

    //退出确认对话框
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
            if (!edtContent.getText().toString().equals(sharedPreferences.getString(textFileName, null))) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("提示").setMessage("是否保存？")
//                  是否可以返回退出
//                  .setCancelable(false)
//                  自定义
//                  View view = LayoutInflater.from(LoginActivity.this).inflate(R.layout.xxxxx,null);
//
//                  Button btn = view.findViewById()
//                  .setView(view)
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                editor.putString(textFileName, edtContent.getText().toString());
                                editor.apply();
                                Toast.makeText(MainActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                                MainActivity.this.finish();
                            }
                        }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.this.finish();
                    }
                }).show();
            } else
                MainActivity.this.finish();
        }
        return true;
    }
}