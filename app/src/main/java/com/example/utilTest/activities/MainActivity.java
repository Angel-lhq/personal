package com.example.utilTest.activities;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.kotlinlibrary.Kotlin;
import com.example.utilTest.R;
import com.example.utilTest.net.HttpClientUtil;
import com.example.utilTest.task.Action;
import com.example.utilTest.task.Builder;
import com.example.utilTest.task.Task;
import com.example.utilTest.task.TaskBuilder;
import com.example.utilTest.utils.DialogManager;
import com.example.utilTest.utils.Log;
import com.example.utilTest.utils.PermissionUtil;
import com.example.utilTest.views.LoadingDialog;

import static com.example.utilTest.utils.PermissionUtil.CAMERA;
import static com.example.utilTest.utils.PermissionUtil.READ_CALENDAR;
import static com.example.utilTest.utils.PermissionUtil.WRITE_CALENDAR;
import static com.example.utilTest.utils.PermissionUtil.WRITE_EXTERNAL_STORAGE;

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
//        showLoadingDialog();
//        initData();
//        test();
//        initPermissions();
    }

    /**
     * 记得要在AndroidManifest.xml中添加相应权限
     */
    private void initPermissions() {
        //两个日历权限和一个数据读写权限
        String[] permissions = new String[]{
                CAMERA,
                WRITE_CALENDAR,
                READ_CALENDAR,
                WRITE_EXTERNAL_STORAGE};
//        PermissionsUtils.showSystemSetting = false;//是否支持显示系统设置权限设置窗口跳转
        //这里的this不是上下文，是Activity对象！                                    //创建监听权限的接口对象
        PermissionUtil.getInstance().checkPermissions(this, permissions, new PermissionUtil.IPermissionsResult() {
            @Override
            public void passPermissons() {
                Toast.makeText(MainActivity.this, "权限通过，可以做其他事情!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void forbitPermissons() {
//            finish();
                Toast.makeText(MainActivity.this, "权限未通过，请前往设置进行授予", Toast.LENGTH_SHORT).show();
            }
        });
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
//        //加载弹窗
//        LoadingDialog.Builder loadBuilder = new LoadingDialog.Builder(this)
//                .setMessage("加载中...")
//                .setCancelable(true)//返回键是否可点击
//                .setCancelOutside(false);//窗体外是否可点击
//        dialog = loadBuilder.create();
        dialog = (LoadingDialog) DialogManager.getInstance().create(this,DialogManager.LOADINGDIALOG);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //就多一个参数this
        PermissionUtil.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }


    public void match(){
        //wxp://f2f0SiaEzWuTXqOMOOV8lLKpd4hdegGLpiSO8i4qWdKGi0Y
//        ^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$"
        String regex = "^wxp://(0-9|A-Z|a-z){46}$";
    }
}