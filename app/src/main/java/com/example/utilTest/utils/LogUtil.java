package com.example.utilTest.utils;

/**
 * <p>
 * com.example.utilTest.utils.Log
 * </p>
 *
 * @author LU
 */
public class LogUtil {

    /*
     * Display: set showLog true Hide: set showLog false
     */
    private static boolean showLog = true; //Constants.SHOW_LOG;

    //Replace with the prompt information of your own project
    private static final String TAG = "LogMessage:";

    public static void setDebug(boolean isDebug)
    {
        showLog = isDebug;
        Log.setDebug(isDebug);
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

    public static void i(String tag, String msg, Throwable tr) {
        if (showLog) {
            android.util.Log.i(TAG + tag, msg, tr);
        }
    }

    public static void w(String tag, String msg) {
        if (showLog) {
            android.util.Log.w(TAG + tag, msg);
        }
    }

    public static void w(String tag, Throwable tr) {
        if (showLog) {
            android.util.Log.w(TAG + tag, tr);
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (showLog) {
            android.util.Log.w(TAG + tag, msg, tr);
        }
    }

    public static void e(String tag, String msg) {
        if (showLog) {
            android.util.Log.e(TAG + tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (showLog) {
            android.util.Log.e(TAG + tag, msg, tr);
        }
    }

    public static class System {
        public static void out(String msg) {
            if (showLog) android.util.Log.i("System.out", msg);
        }
    }

}
