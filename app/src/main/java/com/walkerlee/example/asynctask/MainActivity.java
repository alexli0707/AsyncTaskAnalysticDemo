package com.walkerlee.example.asynctask;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.concurrent.ThreadPoolExecutor;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mBtnOperation;
    private TextView mTvProgress;
    private TextView mTvStatus;
    private TimeConsumingTask mTimeConsumingTask;
    private Integer[] sleepingTime = {3};


    private Handler mHandlerProgress = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what != 0) {
                mTvProgress.setText("Progress:" + msg.what);
            } else {
                mTvProgress.setText("Result: finished success? " + msg.getData().getBoolean(TimeConsumingTask.TAG_RESULT));
                mTimeConsumingTask = new TimeConsumingTask(mHandlerProgress);
                mBtnOperation.setText(getString(R.string.start));
                logAsyncTaskStatus();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnOperation = (Button) findViewById(R.id.btn_operation);
        mTvProgress = (TextView) findViewById(R.id.tv_result);
        mTvStatus = (TextView) findViewById(R.id.tv_status);
        mBtnOperation.setOnClickListener(this);
        mTimeConsumingTask = new TimeConsumingTask(mHandlerProgress);
        logAsyncTaskStatus();
    }

    private void logAsyncTaskStatus() {
        mTvStatus.setText(getString(R.string.status, Runtime.getRuntime().availableProcessors(), getPoolSize(mTimeConsumingTask)));
    }

    @Override
    public void onClick(View v) {
        if (!mTimeConsumingTask.getStatus().equals(AsyncTask.Status.PENDING)) {
            mTimeConsumingTask.cancel(true);
            mTimeConsumingTask = new TimeConsumingTask(mHandlerProgress);
            mBtnOperation.setText(getString(R.string.start));
        } else {
            mBtnOperation.setText(getString(R.string.stop));
            // execute a task will last 3 seconds.
            mTimeConsumingTask.execute(sleepingTime);
//            for (int i = 0; i < 5; i++) {
//                new TimeConsumingTask(mHandlerProgress).execute(sleepingTime);
//            }
        }
    }
    //根据反射获取AsyncTask 线程池大小
    private int getPoolSize(AsyncTask asyncTask) {
        if (null == asyncTask) {
            return 0;
        }
        Field field = null;
        try {
            field = asyncTask.getClass().getSuperclass().getField("THREAD_POOL_EXECUTOR");
            field.setAccessible(true);
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) field.get(asyncTask);
            return threadPoolExecutor.getPoolSize();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
