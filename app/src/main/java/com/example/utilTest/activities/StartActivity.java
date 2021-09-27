package com.example.utilTest.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.utilTest.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class StartActivity extends AppCompatActivity {

    private Button mBtnMain;
    private Button mBtnHook;
    private Button mBtnReflex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        initView();
    }

    private void initView() {
        mBtnMain = findViewById(R.id.btn_main);
        mBtnHook = findViewById(R.id.btn_hook);
        mBtnReflex = findViewById(R.id.btn_reflex);

        mBtnMain.setOnClickListener(localClickListener);
        mBtnHook.setOnClickListener(localClickListener);
        mBtnReflex.setOnClickListener(localClickListener);

        hookOnClickListener(mBtnMain);
    }

    public void hookOnClickListener(View view){
        try {
            Method getListenerInfo = View.class.getDeclaredMethod("getListenerInfo");
            getListenerInfo.setAccessible(true);
            Object listenerInfo = getListenerInfo.invoke(view);
            Class<?> aClass = Class.forName("android.view.View$ListenerInfo");
            Field mOnClickListener = aClass.getDeclaredField("mOnClickListener");
            View.OnClickListener listener = (View.OnClickListener) mOnClickListener.get(listenerInfo);
            mOnClickListener.set(listenerInfo,new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(StartActivity.this,HookTestActivity.class));
                }
            });
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener localClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_main:
                    startActivity(new Intent(StartActivity.this,MainActivity.class));
                    break;
                case R.id.btn_hook:
                    startActivity(new Intent(StartActivity.this,HookTestActivity.class));
                    break;
                case R.id.btn_reflex:
                    startActivity(new Intent(StartActivity.this,ReflexActivity.class));
                    break;
                default:
                    break;
            }
        }
    };
}