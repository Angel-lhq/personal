package com.example.dosomething;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.kotlinlibrary.Kotlin;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        Kotlin kotlin = new Kotlin();
        kotlin.add(1);
        kotlin.add(2);
        kotlin.add(3);
        kotlin.add(4);
        kotlin.add(5);
    }
}