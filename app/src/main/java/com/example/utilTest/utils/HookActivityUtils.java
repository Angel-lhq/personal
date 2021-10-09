package com.example.utilTest.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class HookActivityUtils {
    private static final String TAG = "HookActivityUtils";
    public static final String ORIGINALLY_INTENT = AMSHookInvocationHandler.ORIGINALLY_INTENT;
    public static void replaceInstrumentation(Activity activity) throws Exception {
        Class<?> k = Activity.class;
        //通过Activity.class 拿到 mInstrumentation字段
        Field field = k.getDeclaredField("mInstrumentation");
        field.setAccessible(true);
        //根据activity内mInstrumentation字段 获取Instrumentation对象
        Instrumentation instrumentation = (Instrumentation) field.get(activity);
        //创建代理对象
        Instrumentation instrumentationProxy = new ActivityProxyInstrumentation(instrumentation);
        //进行替换
        field.set(activity, instrumentationProxy);
    }

    public static class ActivityProxyInstrumentation extends Instrumentation {

        private static final String TAG = "ActivityProxyInstrumentation";

        // ActivityThread中原始的对象, 保存起来
        Instrumentation mBase;

        public ActivityProxyInstrumentation(Instrumentation base) {
            mBase = base;
        }

        public ActivityResult execStartActivity(
                Context who, IBinder contextThread, IBinder token, Activity target,
                Intent intent, int requestCode, Bundle options) {

            // Hook之前, 可以输出你想要的!
            Log.d(TAG,"xxxx: 执行了startActivity, 参数如下: " + "who = [" + who + "], " +
                    "contextThread = [" + contextThread + "], token = [" + token + "], " +
                    "target = [" + target + "], intent = [" + intent +
                    "], requestCode = [" + requestCode + "], options = [" + options + "]");

            // 开始调用原始的方法, 调不调用随你,但是不调用的话, 所有的startActivity都失效了.
            // 由于这个方法是隐藏的,因此需要使用反射调用;首先找到这个方法
            try {
                Method execStartActivity = Instrumentation.class.getDeclaredMethod(
                        "execStartActivity",
                        Context.class, IBinder.class, IBinder.class, Activity.class,
                        Intent.class, int.class, Bundle.class);
                execStartActivity.setAccessible(true);
                return (ActivityResult) execStartActivity.invoke(mBase, who,
                        contextThread, token, target, intent, requestCode, options);
            } catch (Exception e) {
                // rom修改了 需要手动适配
                throw new RuntimeException("do not support!!! pls adapt it");
            }
        }


    }

    public static void attachContext() throws Exception {
        Log.i(TAG, "attachContext: ");
        // 先获取到当前的ActivityThread对象
        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
        Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
        currentActivityThreadMethod.setAccessible(true);
        //currentActivityThread是一个static函数所以可以直接invoke，不需要带实例参数
        Object currentActivityThread = currentActivityThreadMethod.invoke(null);

        // 拿到原始的 mInstrumentation字段
        Field mInstrumentationField = activityThreadClass.getDeclaredField("mInstrumentation");
        mInstrumentationField.setAccessible(true);
        Instrumentation mInstrumentation = (Instrumentation) mInstrumentationField.get(currentActivityThread);
        // 创建代理对象
        Instrumentation evilInstrumentation = new ApplicationInstrumentation(mInstrumentation);
        // 偷梁换柱
        mInstrumentationField.set(currentActivityThread, evilInstrumentation);
    }

    public static class ApplicationInstrumentation extends Instrumentation {

        private static final String TAG = "ApplicationInstrumentation";

        // ActivityThread中原始的对象, 保存起来
        Instrumentation mBase;

        public ApplicationInstrumentation(Instrumentation base) {
            mBase = base;
        }

        public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token,
                                                Activity target, Intent intent, int requestCode,
                                                Bundle options) {

            // Hook之前, 可以输出你想要的!
            Log.d(TAG, "xxxx: 执行了startActivity, 参数如下: " + "who = [" + who + "], " + "contextThread = " +
                    "" + "" + "[" + contextThread + "], token = [" + token + "], " + "target = [" +
                    target + "], intent = [" + intent + "], requestCode = [" + requestCode + "], " +
                    "options = " + "[" + options + "]");

            // 开始调用原始的方法, 调不调用随你,但是不调用的话, 所有的startActivity都失效了.
            // 由于这个方法是隐藏的,因此需要使用反射调用;首先找到这个方法
            try {
                Method execStartActivity = Instrumentation.class.getDeclaredMethod
                        ("execStartActivity", Context.class, IBinder.class, IBinder.class, Activity
                                .class, Intent.class, int.class, Bundle.class);
                execStartActivity.setAccessible(true);
                return (ActivityResult) execStartActivity.invoke(mBase, who, contextThread, token,
                        target, intent, requestCode, options);
            } catch (Exception e) {
                // rom修改了 需要手动适配
                throw new RuntimeException("do not support!!! pls adapt it");
            }
        }

    }

    public static void hookAMSAfter26() throws Exception {
        // 第一步：获取 IActivityManagerSingleton
        Class<?> aClass = Class.forName("android.app.ActivityManager");
        Field declaredField = aClass.getDeclaredField("IActivityManagerSingleton");
        declaredField.setAccessible(true);
        Object value = declaredField.get(null);

        Class<?> singletonClz = Class.forName("android.util.Singleton");
        Field instanceField = singletonClz.getDeclaredField("mInstance");
        instanceField.setAccessible(true);
        Object iActivityManagerObject = instanceField.get(value);

        // 第二步：获取我们的代理对象，这里因为 IActivityManager 是接口，我们使用动态代理的方式
        Class<?> iActivity = Class.forName("android.app.IActivityManager");
        InvocationHandler handler = new AMSInvocationHandler(iActivityManagerObject);
        Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new
                Class<?>[]{iActivity}, handler);

        // 第三步：偷梁换柱，将我们的 proxy 替换原来的对象
        instanceField.set(value, proxy);

    }

    public static class AMSInvocationHandler implements InvocationHandler {

        private static final String TAG = "AMSInvocationHandler";

        Object iamObject;

        public AMSInvocationHandler(Object iamObject) {
            this.iamObject = iamObject;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //            Log.e(TAG, method.getName());
            if ("startActivity".equals(method.getName())) {
                Log.i(TAG, "ready to startActivity");
                for (Object object : args) {
                    Log.d(TAG, "invoke: object=" + object);
                }
            }
            return method.invoke(iamObject, args);
        }
    }

    public static void hookAmsBefore26() throws Exception {
        // 第一步：获取 IActivityManagerSingleton
        Class<?> forName = Class.forName("android.app.ActivityManagerNative");
        Field defaultField = forName.getDeclaredField("gDefault");
        defaultField.setAccessible(true);
        Object defaultValue = defaultField.get(null);

        Class<?> forName2 = Class.forName("android.util.Singleton");
        Field instanceField = forName2.getDeclaredField("mInstance");
        instanceField.setAccessible(true);
        Object iActivityManagerObject = instanceField.get(defaultValue);

        // 第二步：获取我们的代理对象，这里因为 IActivityManager 是接口，我们使用动态代理的方式
        Class<?> iActivity = Class.forName("android.app.IActivityManager");
        InvocationHandler handler = new AMSInvocationHandler(iActivityManagerObject);
        Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{iActivity}, handler);

        // 第三步：偷梁换柱，将我们的 proxy 替换原来的对象
        instanceField.set(defaultValue, proxy);
    }

    /**
     * 这里我们通过反射获取到AMS的代理本地代理对象
     * Hook以后动态串改Intent为已注册的来躲避检测
     *
     * @param context             上下文
     * @param isAppCompatActivity 是否是 AppCompatActivity
     */
    public static void hookActivity(Context context, boolean isAppCompatActivity) {
        if (context == null) {
            return;
        }
        try {
            // hook AMS
            hookAMS(context);
            // 在 activity launch 的时候欺骗 AMS
            hookLaunchActivity(context, isAppCompatActivity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void hookAMS(Context context) throws ClassNotFoundException,
            NoSuchFieldException, IllegalAccessException {
        // 第一步，  API 26 以后，hook android.app.ActivityManager.IActivityManagerSingleton，
        //  API 25 以前，hook android.app.ActivityManagerNative.gDefault
        Field gDefaultField = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Class<?> activityTaskManagerClass = Class.forName("android.app.ActivityTaskManager");
            gDefaultField = activityTaskManagerClass.getDeclaredField("IActivityTaskManagerSingleton");
        } else {
            Class<?> activityManagerNativeClass = Class.forName("android.app.ActivityManagerNative");
            gDefaultField = activityManagerNativeClass.getDeclaredField("gDefault");
        }
        gDefaultField.setAccessible(true);
        Object gDefaultObj = gDefaultField.get(null); //所有静态对象的反射可以通过传null获取。如果是实列必须传实例
        Class<?> singletonClazz = Class.forName("android.util.Singleton");
        Field amsField = singletonClazz.getDeclaredField("mInstance");
        amsField.setAccessible(true);
        Object amsObj = amsField.get(gDefaultObj);

        //
        String pmName = getPMName(context);
        String hostClzName = getHostClzName(context, pmName);

        // 第二步，获取我们的代理对象，这里因为是接口，所以我们使用动态代理的方式
        amsObj = Proxy.newProxyInstance(context.getClass().getClassLoader(), amsObj.getClass()
                .getInterfaces(), new AMSHookInvocationHandler(amsObj, pmName, hostClzName));

        // 第三步：设置为我们的代理对象
        amsField.set(gDefaultObj, amsObj);
    }

    public static class AMSHookInvocationHandler implements InvocationHandler {

        public static final String ORIGINALLY_INTENT = "originallyIntent";
        private Object mAmsObj;
        private String mPackageName;
        private String cls;

        public AMSHookInvocationHandler(Object amsObj, String packageName, String cls) {
            this.mAmsObj = amsObj;
            this.mPackageName = packageName;
            this.cls = cls;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //  对 startActivity进行Hook
            if (method.getName().equals("startActivity")) {
                int index = 0;
                //  找到我们启动时的intent
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof Intent) {
                        index = i;
                        break;
                    }
                }

                // 取出在真实的Intent
                Intent originallyIntent = (Intent) args[index];
                Log.i("AMSHookUtil", "AMSHookInvocationHandler:" + originallyIntent.getComponent()
                        .getClassName());
                // 自己伪造一个配置文件已注册过的Activity Intent
                Intent proxyIntent = new Intent();
                //  因为我们调用的Activity没有注册，所以这里我们先偷偷换成已注册。使用一个假的Intent
                ComponentName componentName = new ComponentName(mPackageName, cls);
                proxyIntent.setComponent(componentName);
                // 在这里把未注册的Intent先存起来 一会儿我们需要在Handle里取出来用
                proxyIntent.putExtra(ORIGINALLY_INTENT, originallyIntent);
                args[index] = proxyIntent;
            }
            return method.invoke(mAmsObj, args);
        }
    }

    /**
     *
     * @param context
     * @param isAppCompatActivity 表示是否是 AppCompatActivity
     * @throws Exception
     */
    private static void hookLaunchActivity(Context context, boolean isAppCompatActivity) throws
            Exception {
        Class<?> aClass = Class.forName("android.app.Activity");
        Field mMainThread = aClass.getDeclaredField("mMainThread");
        mMainThread.setAccessible(true);
        Object mActivityThread = mMainThread.get(context);

        Class<?> activityThreadClazz = Class.forName("android.app.ActivityThread");
        Field mHField = activityThreadClazz.getDeclaredField("mH");
        mHField.setAccessible(true);
        Handler mH = (Handler) mHField.get(mActivityThread);
        Field callBackField = Handler.class.getDeclaredField("mCallback");
        callBackField.setAccessible(true);
        callBackField.set(mH, new ActivityThreadHandlerCallBack(context,mActivityThread,isAppCompatActivity));
    }

    public static class ActivityThreadHandlerCallBack implements Handler.Callback {

        private final boolean mIsAppCompatActivity;
        private final Context mContext;
        private final Object mActivityThread;

        public ActivityThreadHandlerCallBack(Context context, Object activityThread, boolean isAppCompatActivity) {
            mIsAppCompatActivity = isAppCompatActivity;
            mContext = context;
            mActivityThread = activityThread;
        }

        @Override
        public boolean handleMessage(Message msg) {
            int EXECUTE_TRANSACTION = 159;
//            try {
//                Class<?> clazz = Class.forName("android.app.ActivityThread$H");
//                Field field = clazz.getDeclaredField("BIND_APPLICATION");
//                EXECUTE_TRANSACTION = field.getInt(mActivityThread);
//            } catch (Exception e) {
//                ExceptionUtil.printStackTrace(e);
//            }
            if (msg.what == EXECUTE_TRANSACTION) {
                handleLaunchActivity(mContext, msg, mIsAppCompatActivity);
            }
            return false;
        }
    }

    private static void handleLaunchActivity(Context context, Message msg, boolean
            isAppCompatActivity) {
        try {
            Object obj = msg.obj;
            Field intentField = obj.getClass().getDeclaredField("intent");
            intentField.setAccessible(true);
            Intent proxyIntent = (Intent) intentField.get(obj);
            //拿到之前真实要被启动的Intent 然后把Intent换掉
            Intent originallyIntent = proxyIntent.getParcelableExtra(ORIGINALLY_INTENT);
            if (originallyIntent == null) {
                return;
            }
            proxyIntent.setComponent(originallyIntent.getComponent());

            Log.e(TAG, "handleLaunchActivity:" + originallyIntent.getComponent().getClassName());

            // 如果不需要兼容 AppCompatActivity
            if (!isAppCompatActivity) {
                return;
            }

            //兼容AppCompatActivity，假如不加上该方法，当 activity instanceOf AppCompatActivity 时，会抛出  PackageManager$NameNotFoundException 异常。
            hookPM(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private static void hookPM(Context context) throws ClassNotFoundException,
            NoSuchFieldException, IllegalAccessException, NoSuchMethodException,
            InvocationTargetException {
        String pmName = getPMName(context);
        String hostClzName = getHostClzName(context, pmName);

        Class<?> forName = Class.forName("android.app.ActivityThread");
        Field field = forName.getDeclaredField("mAppThread");
        field.setAccessible(true);
        Object activityThread = field.get(null);
        Method getPackageManager = activityThread.getClass().getDeclaredMethod("getPackageManager");
        Object iPackageManager = getPackageManager.invoke(activityThread);
        PackageManagerHandler handler = new PackageManagerHandler(iPackageManager, pmName, hostClzName);
        Class<?> iPackageManagerIntercept = Class.forName("android.content.pm.IPackageManager");
        Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new
                Class<?>[]{iPackageManagerIntercept}, handler);
        // 获取 sPackageManager 属性
        Field iPackageManagerField = activityThread.getClass().getDeclaredField("sPackageManager");
        iPackageManagerField.setAccessible(true);
        iPackageManagerField.set(activityThread, proxy);
    }


    private static class PackageManagerHandler implements InvocationHandler {
        private final String mPmName;
        private final String mHostClzName;
        private Object mActivityManagerObject;

        PackageManagerHandler(Object mActivityManagerObject, String pmName, String hostClzName) {
            this.mActivityManagerObject = mActivityManagerObject;
            mPmName = pmName;
            mHostClzName = hostClzName;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("getActivityInfo")) {
                ComponentName componentName = new ComponentName(mPmName, mHostClzName);
                args[0] = componentName;
            }
            return method.invoke(mActivityManagerObject, args);
        }
    }

    private static String getHostClzName(Context context, String pmName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pmName, PackageManager
                    .GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
        ActivityInfo[] activities = packageInfo.activities;
        if (activities == null || activities.length == 0) {
            return "";
        }
        ActivityInfo activityInfo = activities[0];
        return activityInfo.name;

    }

    private static String getPMName(Context context) {
        // 获取当前进程已经注册的 activity
        Context applicationContext = context.getApplicationContext();
        return applicationContext.getPackageName();
    }




}
