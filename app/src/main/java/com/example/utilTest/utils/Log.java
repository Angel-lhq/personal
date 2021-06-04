package com.example.utilTest.utils;

/**
 * 区分release包和debug包的log日志打印
 */
public class Log {

    private static final String TAG = Log.class.getSimpleName();

    private static boolean showLog = true;

    public static void setDebug(boolean isDebug)
    {
        showLog = isDebug;
    }

    public static void v(String tag, String msg) {
        if (showLog) {
            android.util.Log.v(TAG + tag, msg);
        }
    }

    public static void v(String tag, String msg, Throwable tr) {
        if (showLog) {
            android.util.Log.v(TAG + tag, msg, tr);
        }
    }

    public static void d(String tag, String msg) {
        if (showLog) {
            android.util.Log.d(TAG + tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (showLog) {
            android.util.Log.d(TAG + tag, msg, tr);
        }
    }

    public static void i(String tag, String msg) {
        if (showLog) {
            android.util.Log.i(TAG + tag, msg);
        }
    }


    public static void e(String tag, String msg) {
        if (showLog) {
            android.util.Log.e(TAG + tag, msg);
        }
    }
}
