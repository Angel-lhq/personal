package com.example.utilTest.task;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class TaskBuilder implements Builder {

    protected static final String TAG = TaskBuilder.class.getSimpleName();

//    public static final int CORE_POOL_SIZE = 8;

    // 线程池大小
    private static final int POOL_SIZE = 15;

    private static final int FINISH = 1;
    private static final int PROGRESS = 2;
    private static final int ERROR = 3;


    public static final int HIGH_LEVEL = 0;
    public static final int NORMAL_LEVEL = 1;


    private static Handler handler;


    // 创建一个可重用固定线程数的线程池
    private static ExecutorService executor;

//    private static ThreadPoolExecutor executor;

//    private static ExecutorService executor;

    private Object param;
    private Object progress;
    private Object result;

    private WeakReference<Context> weakContext;

    private boolean isBindContext;

    private Action initAction;
    private Action progressAction;
    private Action finishAction;
    private Action errorAction;

    private int priority = NORMAL_LEVEL;//优先级，默认为普通

    private Task task;

    static {

//        int processorNum = Runtime.getRuntime().availableProcessors();
//        executor = new ThreadPoolExecutor(processorNum, processorNum * 2, 2, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
//        executor = Executors.newCachedThreadPool();


//        executor = new ThreadPoolExecutor(CORE_POOL_SIZE, CORE_POOL_SIZE * 2, 0, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>());

        executor = Executors.newFixedThreadPool(POOL_SIZE);

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {

                TaskBuilder tl = (TaskBuilder) msg.obj;
                if (tl.isBindContext) {
                    if (tl.weakContext != null) {
                        Context context = tl.weakContext.get();
                        if (context == null) {
                            return;
                        }
                    } else {
                        return;
                    }
                }

                switch (msg.what) {
                    case FINISH:
                        if (tl.finishAction != null) {
                            tl.finishAction.call(tl.result);
                        }
                        break;
                    case PROGRESS:
                        if (tl.progressAction != null) {
                            tl.progressAction.call(tl.progress);
                        }
                    case ERROR:
                        if (tl.errorAction != null) {
                            tl.errorAction.call(tl.result);
                        }
                }
            }
        };
    }

    private TaskBuilder() {

    }

    @Override
    public TaskBuilder bind(Context context) {
        if (weakContext == null) {
            weakContext = new WeakReference<>(context);
        }
        isBindContext = true;
        return this;
    }

    @Override
    public TaskBuilder unbind() {
        if (weakContext != null) {
            Context context = weakContext.get();
            if (context != null) {
                weakContext.clear();
            }
        }
        isBindContext = false;
        return this;
    }

    public static <T> TaskBuilder create(T param) {
        TaskBuilder tl = new TaskBuilder();
        tl.param = param;
        return tl;
    }

    @Override
    public TaskBuilder init(Action init) {
        this.initAction = init;
        return this;
    }

    @Override
    public TaskBuilder task(Task task) {
        this.task = task;
        return this;
    }

    @Override
    public TaskBuilder progress(Action progress) {
        this.progressAction = progress;
        return this;
    }


    @Override
    public <T> void updateProgress(T value) {
        Message msg = handler.obtainMessage(PROGRESS);
        this.progress = value;
        msg.obj = this;
        msg.sendToTarget();
    }

    public TaskBuilder priority(int level) {
        this.priority = level;
        return this;
    }

    public TaskBuilder error(Action<Exception> errorAction) {
        this.errorAction = errorAction;
        return this;
    }

    @Override
    public void execute(Action finish) {

        if (task == null) {
            throw new IllegalArgumentException("Task can not be null");
        }

        this.finishAction = finish;

        if (initAction != null) {
            initAction.call(param);
        }

        int threadCount = ((ThreadPoolExecutor) executor).getActiveCount();
        if (priority == HIGH_LEVEL || threadCount == POOL_SIZE) {

            // 遇到高优先级的任务，直接开启新的线程
            new Thread(new Runnable() {
                @Override
                public void run() {

                    Log.d(TAG, "Start a new thread to execute this task, " + Thread.currentThread().getName());
                    // Log.v(TAG, "thread = " + Thread.currentThread().getName());
                    Message msg = handler.obtainMessage();

                    try {
                        result = task.call(TaskBuilder.this, param);
                    } catch (Exception e) {
                        if (errorAction != null) {
                            msg.what = ERROR;
                            msg.obj = TaskBuilder.this;
                            TaskBuilder.this.result = e;

                            msg.sendToTarget();

                            return;

                        } else {
                            throw e;
                        }
                    }

                    msg.what = FINISH;
                    msg.obj = TaskBuilder.this;
                    msg.sendToTarget();
                }
            }).start();

        } else {
            executor.execute(new Runnable() {

                @Override
                public void run() {
                    // Log.v(TAG, "thread = " + Thread.currentThread().getName());
                    Message msg = handler.obtainMessage();

                    try {
                        result = task.call(TaskBuilder.this, param);
                    } catch (Exception e) {
                        if (errorAction != null) {
                            msg.what = ERROR;
                            msg.obj = TaskBuilder.this;

                            TaskBuilder.this.result = e;

                            msg.sendToTarget();

                            return;
                        } else {
                            throw e;
                        }
                    }

                    msg.what = FINISH;
                    msg.obj = TaskBuilder.this;
                    msg.sendToTarget();
                }
            });

            threadCount = ((ThreadPoolExecutor) executor).getActiveCount();

            if (POOL_SIZE - threadCount < 5) {
                Log.w(TAG, "TaskBuilder idle threads count less than 5, be careful.");
            }

        }
    }

    public void execute() {
        execute(null);
    }
}