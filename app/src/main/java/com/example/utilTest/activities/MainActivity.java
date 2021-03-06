package com.example.utilTest.activities;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
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
    private GridView mGv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initGridView();
//        showLoadingDialog();
//        initData();
//        test();
//        initPermissions();
    }

    private void initGridView() {
        String[] titles = new String[]{"1111","2222","33333","44444","55555","66666"};
        mGv.setNumColumns(4);
        mGv.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return titles.length;
            }

            @Override
            public Object getItem(int position) {
                return titles[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder viewHolder;
                if (convertView == null){
                    convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.adapter_item,null);
                    viewHolder = new ViewHolder();
                    viewHolder.textView = convertView.findViewById(R.id.tv);
                    convertView.setTag(viewHolder);
                }else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                viewHolder.textView.setText(titles[position]);
                return convertView;
            }

            class ViewHolder{
                TextView textView;
            }
        });
    }

    /**
     * ????????????AndroidManifest.xml?????????????????????
     */
    private void initPermissions() {
        //?????????????????????????????????????????????
        String[] permissions = new String[]{
                CAMERA,
                WRITE_CALENDAR,
                READ_CALENDAR,
                WRITE_EXTERNAL_STORAGE};
//        PermissionsUtils.showSystemSetting = false;//??????????????????????????????????????????????????????
        //?????????this?????????????????????Activity?????????                                    //?????????????????????????????????
        PermissionUtil.getInstance().checkPermissions(this, permissions, new PermissionUtil.IPermissionsResult() {
            @Override
            public void passPermissons() {
                Toast.makeText(MainActivity.this, "????????????????????????????????????!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void forbitPermissons() {
//            finish();
                Toast.makeText(MainActivity.this, "?????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
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
//        //????????????
//        LoadingDialog.Builder loadBuilder = new LoadingDialog.Builder(this)
//                .setMessage("?????????...")
//                .setCancelable(true)//????????????????????????
//                .setCancelOutside(false);//????????????????????????
//        dialog = loadBuilder.create();
        dialog = (LoadingDialog) DialogManager.getInstance().create(this,DialogManager.LOADINGDIALOG);
        dialog.show();//????????????
        handler.postDelayed(runnable,1000);
    }

    //??????????????? 10????????????????????????
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
        mGv = findViewById(R.id.gv);
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
        //??????????????????this
        PermissionUtil.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }


    public void match(){
        //wxp://f2f0SiaEzWuTXqOMOOV8lLKpd4hdegGLpiSO8i4qWdKGi0Y
//        ^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$"
        String regex = "^wxp://(0-9|A-Z|a-z){46}$";
    }
}