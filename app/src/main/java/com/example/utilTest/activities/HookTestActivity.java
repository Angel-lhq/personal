package com.example.utilTest.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utilTest.R;
import com.example.utilTest.utils.HookActivityUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class HookTestActivity extends AppCompatActivity {

    private Button btn;
    private Button btnHook;
    private Button btnHookActivity;
    private static TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hook_test);
        initView();
    }

    private void initView() {
        btn = findViewById(R.id.btn);
        btnHook = findViewById(R.id.btn_hook);
        btnHookActivity = findViewById(R.id.btn_hook_activity);
        tv = findViewById(R.id.tv);
        btn.setOnClickListener(customClickListener);
        btnHook.setOnClickListener(customClickListener);
        btnHookActivity.setOnClickListener(customClickListener);
    }

    View.OnClickListener customClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn:
                    tv.setText("点击了按钮");
                    break;
                case R.id.btn_hook:
                    try {
                        hookOnClickListener(btn);
                        Toast.makeText(HookTestActivity.this,"hook模式",Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.btn_hook_activity:
//                    //第一种方式hook startActivity
//                    try {
//                        HookActivityUtils.replaceInstrumentation(HookTestActivity.this);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    startActivity(new Intent(HookTestActivity.this,TestActivityStart.class));
//
//                    //第二种方式hook getApplicationContext startActivity
//                    try {
//                        HookActivityUtils.attachContext();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    Intent intent = new Intent(HookTestActivity.this, TestActivityStart.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    getApplicationContext().startActivity(intent);
//
//                    //第三种方式hook AMS
//                    try {
//                        HookActivityUtils.hookAMSAfter26();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    startActivity(new Intent(HookTestActivity.this,TestActivityStart.class));

                    HookActivityUtils.hookActivity(HookTestActivity.this,true);
                    startActivity(new Intent(HookTestActivity.this,TestActivityStart.class));
                    break;
                default:
                    break;
            }
        }
    };

    public static void hookOnClickListener(View view) throws Exception {
        // 第一步：反射得到 ListenerInfo 对象
        Method getListenerInfo = View.class.getDeclaredMethod("getListenerInfo");
        getListenerInfo.setAccessible(true);
        Object listenerInfo = getListenerInfo.invoke(view);
        //动态代理
        View.OnClickListener hookedOnClickListener = (View.OnClickListener) Proxy.newProxyInstance(
                View.OnClickListener.class.getClassLoader(),
                new Class[]{View.OnClickListener.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        tv.setText("这个按钮被hook了");
                        return null;
                    }
                }
        );
        // 第二步：得到原始的 OnClickListener事件方法
        Class<?> listenerInfoClz = Class.forName("android.view.View$ListenerInfo");
        Field mOnClickListener = listenerInfoClz.getDeclaredField("mOnClickListener");
        mOnClickListener.setAccessible(true);
//        View.OnClickListener originOnClickListener = (View.OnClickListener) mOnClickListener.get(listenerInfo);
        // 第三步：用 Hook代理类 替换原始的 OnClickListener
//        View.OnClickListener hookedOnClickListener = new HookedClickListenerProxy(originOnClickListener);
        mOnClickListener.set(listenerInfo, hookedOnClickListener);
    }

    public static class HookedClickListenerProxy implements View.OnClickListener {

        private View.OnClickListener origin;

        public HookedClickListenerProxy(View.OnClickListener origin) {
            this.origin = origin;
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "Hook Click Listener", Toast.LENGTH_SHORT).show();
            tv.setText("这个按钮被hook了");
            if (origin != null) {
                origin.onClick(v);
            }
            tv.setText("这个按钮被hook了");
        }

    }

}