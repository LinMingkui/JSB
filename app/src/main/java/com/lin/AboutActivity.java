package com.lin;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class AboutActivity extends AppCompatActivity {

    final int NIGHT_MODE = 1;
    final int Day_MODE = 0;
    private LinearLayout layoutActionBar,layoutBack;
    private RelativeLayout layoutParent;
    private TextView tvAbout,tv1,tv2,tv3,tv4;
    private SharedPreferences setSharedPreferences;
    private String fileName = "set";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        init();
        initUI();
        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AboutActivity.this.finish();
            }
        });
    }
    public void init(){
        layoutParent = findViewById(R.id.layout_parent);
        layoutActionBar = findViewById(R.id.layout_action_bar);
        layoutBack = findViewById(R.id.layout_back);
        tvAbout = findViewById(R.id.tv_about);
        tv1 = findViewById(R.id.tv_1);
        tv2 = findViewById(R.id.tv_2);
        tv3 = findViewById(R.id.tv_3);
        tv4 = findViewById(R.id.tv_4);

        setSharedPreferences = getSharedPreferences(fileName,MODE_PRIVATE);
    }
    public void initUI(){
        if(setSharedPreferences.getInt("mode",0) == NIGHT_MODE)
            nightMode();
        else
            dayMode();
    }

    public void nightMode(){
        layoutParent.setBackgroundColor(getResources().getColor(R.color.colorEdtNight));
        layoutActionBar.setBackgroundColor(getResources().getColor(R.color.colorActionBarNight));
        tv1.setTextColor(getResources().getColor(R.color.colorTextNight));
        tv2.setTextColor(getResources().getColor(R.color.colorTextNight));
        tv3.setTextColor(getResources().getColor(R.color.colorTextNight));
        tv4.setTextColor(getResources().getColor(R.color.colorTextNight));
    }
    public void dayMode(){
        layoutParent.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        layoutActionBar.setBackgroundColor(getResources().getColor(R.color.colorActionBarDay));
        tv1.setTextColor(getResources().getColor(R.color.colorTextDay));
        tv2.setTextColor(getResources().getColor(R.color.colorTextDay));
        tv3.setTextColor(getResources().getColor(R.color.colorTextDay));
        tv4.setTextColor(getResources().getColor(R.color.colorTextDay));

    }
}
