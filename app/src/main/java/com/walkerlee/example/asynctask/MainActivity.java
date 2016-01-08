package com.walkerlee.example.asynctask;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mBtnOperation;
    private TextView mTvProgress;
    private TimeConsumingTask mTimeConsumingTask;
    private Integer[] sleepingTime = {1,2,3};



    private Handler mHandlerProgress = new Handler(){
        @Override
        public void handleMessage(Message msg) {
             if (msg.what!=0){
                 mTvProgress.setText("Progress:"+msg.what);
             }else {
                 mTvProgress.setText("Result: finished success? "+msg.getData().getBoolean(TimeConsumingTask.TAG_RESULT));
                 mTimeConsumingTask = new TimeConsumingTask(mHandlerProgress);
                 mBtnOperation.setText(getString(R.string.btn_start));
             }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnOperation= (Button) findViewById(R.id.btn_operation);
        mTvProgress= (TextView) findViewById(R.id.tv_result);
        mBtnOperation.setOnClickListener(this);
        mTimeConsumingTask = new TimeConsumingTask(mHandlerProgress);
    }

    @Override
    public void onClick(View v) {
        if (!mTimeConsumingTask.getStatus().equals(AsyncTask.Status.PENDING)) {
            mTimeConsumingTask.cancel(true);
            mTimeConsumingTask = new TimeConsumingTask(mHandlerProgress);
            mBtnOperation.setText(getString(R.string.btn_start));
        } else {
            mBtnOperation.setText(getString(R.string.btn_stop));
            mTimeConsumingTask.execute(sleepingTime);
        }
    }
}
