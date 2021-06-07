package com.example.utilTest;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.kotlinlibrary.Kotlin;
import com.example.utilTest.net.HttpClientUtil;
import com.example.utilTest.task.Action;
import com.example.utilTest.task.Builder;
import com.example.utilTest.task.Task;
import com.example.utilTest.task.TaskBuilder;
import com.example.utilTest.utils.Log;
import com.example.utilTest.views.LoadingDialog;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private LoadingDialog dialog;
    private TextView mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        showLoadingDialog();
        initData();
//        test();
    }

    private void initData() {
        TaskBuilder.create(null).task(new Task<Object,String>() {
            @Override
            public String call(Builder builder, Object o) {
                String result = "";
                try {
                    result = HttpClientUtil.create("https://www.wanandroid.com/banner/json").get();
//                    result = HttpClientUtil.create("https://wanandroid.com/article/listproject/0/json").get();
//                    result = HttpClientUtil.create("https://www.wanandroid.com/user/logout/json").get();
//                    result = HttpClientUtil.create("https://www.wanandroid.com/user/register").post("username=testforpost&password=123456&repassword=123456");
//                    result = HttpClientUtil.create("https://www.wanandroid.com/user/login").post("username=testforpost&password=123456");
                    Log.i(TAG,result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return result;
            }
        }).execute(new Action<String>() {
            @Override
            public void call(String o) {
                mText.setText(o);
            }
        });
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
        mText = findViewById(R.id.text);
    }

    private void test() {
        Kotlin kotlin = new Kotlin();
        kotlin.add(1);
        kotlin.add(2);
        kotlin.add(3);
        kotlin.add(4);
        kotlin.add(5);
    }
}