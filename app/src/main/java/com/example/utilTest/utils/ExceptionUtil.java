package com.example.utilTest.utils;

/**
 * try-catch结构中输出exception（e）信息
 */
public class ExceptionUtil {

    private static final String TAG = ExceptionUtil.class.getSimpleName();

    private static boolean showException = true;

    public static void setDebug(boolean isDebug)
    {
        showException = isDebug;
    }

    public static void  printStackTrace(Exception exception)
    {
        if(showException)
        {
            exception.printStackTrace();
        }
    }
}
