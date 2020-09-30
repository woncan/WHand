package com.woncan.whandlib;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.woncan.whand.WHandManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WHandManager.getInstance().init(BuildConfig.DEBUG);
    }

    public static String test(){
        return "success";
    }
}