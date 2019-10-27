package com.example.mydictionary.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.mydictionary.R;

/**
 *  启动页
 */

public class LunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lunch);
        new Thread(new Runnable() {
            @Override
            public void run() {

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    start();
                    LunchActivity.this.finish();

            }
        }).start();
    }
    private void start(){
        Intent intent = new Intent(LunchActivity.this,HistoryRecordsActivity.class);
        startActivity(intent);
    }
}
