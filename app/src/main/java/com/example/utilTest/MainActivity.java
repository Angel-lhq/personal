package com.example.utilTest;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.kotlinlibrary.Kotlin;
import com.example.utilTest.views.LoadingDialog;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        initView();
        showLoadingDialog();
    }

    private void showLoadingDialog() {
        //加载弹窗
        LoadingDialog.Builder loadBuilder = new LoadingDialog.Builder(this)
                .setMessage("加载中...")
                .setCancelable(true)//返回键是否可点击
                .setCancelOutside(false);//窗体外是否可点击
        dialog = loadBuilder.create();
        dialog.show();//显示弹窗
        handler.postDelayed(runnable,1000);
    }

    //简单倒计时 10秒后加载弹窗消失
    private int time = 10;
    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (time == 0){
                dialog.dismiss();
                return;
            }
            time --;
            handler.postDelayed(runnable,1000);
        }
    };

    private void initView() {
        Kotlin kotlin = new Kotlin();
        kotlin.add(1);
        kotlin.add(2);
        kotlin.add(3);
        kotlin.add(4);
        kotlin.add(5);
    }
}