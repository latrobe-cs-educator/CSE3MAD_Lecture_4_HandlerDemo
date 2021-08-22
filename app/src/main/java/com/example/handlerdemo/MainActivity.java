package com.example.handlerdemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    //debug variable
    String TAG = "MActivity";

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String string = bundle.getString("myKey");
            TextView myTextView =
                    (TextView) findViewById(R.id.myTextView);
            myTextView.setText(string);
            Log.d(TAG, "In handleMessage, string from bundle is " + string);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void blockUiBtnClick(View view) {
        //long task to run in main UI thread
        Log.d(TAG, "Block UI button is clicked, beginning long task");
        long endTime = System.currentTimeMillis() + 20 * 2000;

        while (System.currentTimeMillis() < endTime) {
            synchronized (this) {
                try {
                    wait(endTime - System.currentTimeMillis());
                } catch (Exception e) {
                    Log.d("TAG", "error" + e.toString());
                }
            }
        }
        TextView myTextView = (TextView) findViewById(R.id.myTextView);
        myTextView.setText(R.string.btn_msg);
        Log.d(TAG, "Block UI button is clicked, end long task");
    }

    public void threadBtnClick(View view) {
        Runnable runnable = new Runnable() {
            public void run() {
                //long task to run in new thread
                Log.d(TAG, "In threadBtnClick, in run() starting long task");
                long endTime = System.currentTimeMillis() + 20 * 2000;

                while (System.currentTimeMillis() < endTime) {
                    synchronized (this) {
                        try {
                            wait(endTime - System.currentTimeMillis());
                        } catch (Exception e) {
                            Log.d("TAG", "error" + e.toString());
                        }
                    }
                }
                //message to send after long task
                Message msg = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("myKey", "Hi from another Thread");
                msg.setData(bundle);
                Log.d(TAG, "Sending message to main UI thread after long task");
                handler.sendMessage(msg);
            }
        };

        //create new thread to run long task
        Thread mythread = new Thread(runnable);
        Log.d(TAG, "Starting new thread");
        mythread.start();

    }
}
