package com.walkerlee.example.asynctask;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * TimeConsumingTask
 * Description:
 * Author:walker_lee
 */
public class TimeConsumingTask extends AsyncTask<Integer, Integer, Boolean> {
    private static String TAG = "TimeConsumingTask";
    private WeakReference<Handler> mHandler;
    public static final String TAG_RESULT = "result";

    public TimeConsumingTask(Handler handler) {
        mHandler = new WeakReference<Handler>(handler);
    }


    @Override
    protected void onPreExecute() {
        Log.d(TAG, "onPreExecute");
    }

    @Override
    protected Boolean doInBackground(Integer... params) {
        for (Integer num : params) {
            try {
                Thread.sleep(num * 1000);
                //will call onProgressUpdate()
                publishProgress(num);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        for (Integer value : values) {
            Log.d(TAG, "onProgressUpdate result: " + value);
            if (null != mHandler.get()) {
                Message message = new Message();
                message.what = value;
                mHandler.get().sendMessage(message);
            }
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        Log.d(TAG, "onPostExecute result: " + aBoolean);
        if (null != mHandler.get()) {
            Message message = new Message();
            message.getData().putBoolean(TAG_RESULT, aBoolean);
            mHandler.get().sendMessage(message);
        }
    }
}
