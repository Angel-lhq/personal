package com.example.utilTest.task;

import android.content.Context;

public interface Builder {
    public Builder bind(Context context);
    public Builder unbind();
    public Builder init(Action init);
    public Builder task(Task task);
    public Builder progress(Action progress);
    public <T> void updateProgress(T value);
    public void execute(Action finish);
}
