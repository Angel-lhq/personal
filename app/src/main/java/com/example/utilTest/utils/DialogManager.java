package com.example.utilTest.utils;

import android.app.Dialog;
import android.content.Context;

import com.example.utilTest.R;
import com.example.utilTest.views.CustomDialog1;
import com.example.utilTest.views.LoadingDialog;

public class DialogManager {

    public static final int LOADINGDIALOG = 0;
    public static final int CUSTOMDIALOG1 = 1;

    private static Dialog dialog;

    private DialogManager() {
    }
    private static DialogManager dialogManager;

    public static DialogManager getInstance(){
        if (dialogManager == null){
            dialogManager = new DialogManager();
        }
        return dialogManager;
    }

    public Dialog create(Context context,int dialogType){
        switch (dialogType){
            case LOADINGDIALOG://加载弹窗
                LoadingDialog.Builder loadBuilder = new LoadingDialog.Builder(context)
                        .setMessage("加载中...")
                        .setCancelable(true)//返回键是否可点击
                        .setCancelOutside(false);//窗体外是否可点击
                dialog = loadBuilder.create();
                break;
            case CUSTOMDIALOG1:
                dialog=new CustomDialog1(context, R.style.CustomDialog1);
                break;
            default:
                dialog = new Dialog(context);
                break;
        }
        return dialog;
    }
}
