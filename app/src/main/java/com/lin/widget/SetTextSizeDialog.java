package com.lin.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lin.R;

/**
 * Created by Lin on 2018/3/21.
 */

public class SetTextSizeDialog extends Dialog implements View.OnClickListener {
    private static EditText edtSetTextSize;
    private Button btnOK,btnCancel;
    private String textSize;
    private IOnOKListener oKListener;
    private IOnCancelListener cancelListener;

    public SetTextSizeDialog(@NonNull Context context) {
        super(context);
    }

    public SetTextSizeDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }
    public SetTextSizeDialog setTextSize(int textSize) {
//        this.textSize = textSize;
        edtSetTextSize.setText(Integer.toString(textSize));
        return this;
    }
    public SetTextSizeDialog setBtnOK(IOnOKListener listener) {
        oKListener = listener;
        return this;
    }

    public SetTextSizeDialog setBtnCancel(IOnCancelListener listener) {
        cancelListener = listener;
        return this;
    }
    public static int getTextSize() {
        return Integer.parseInt(edtSetTextSize.getText().toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        edtSetTextSize = findViewById(R.id.edt_set_text_size);
        btnOK = findViewById(R.id.btn_OK);
        btnCancel = findViewById(R.id.btn_Cancel);
        if(!TextUtils.isEmpty(textSize))
            edtSetTextSize.setText(textSize);
        btnOK.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_OK:
                if(oKListener != null){
                    oKListener.onOK(this);
                }
                break;
            case R.id.btn_Cancel:
                if(cancelListener != null){
                    cancelListener.onCancel(this);
                }
                break;
        }
    }
    public interface IOnOKListener {
        void onOK(SetTextSizeDialog dialog);
    }
    public interface IOnCancelListener {
        void onCancel(SetTextSizeDialog dialog);
    }
}

