package com.example.meitapp2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.widget.Toast;
import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;


public class SubActivity<mBluetoothHandler, protectd> extends AppCompatActivity {

    // !-- 이 액티비티에는 스톱워치(4분 지나면 색 변함)와 성인과 아동 심폐소생술 모드 액티비티로 들어갈 수 있는 버튼이 구현되어 있음 --!

    private BluetoothSPP bt;

    TextView textView;

    Button start, pause, reset;

    long MsTime = 0L;
    long StartTime = 0L;
    long TimeBuff = 0L;
    long UpdateTime = 0L;

    Handler handler;

    int Seconds, Minutes, MilliSeconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        textView = (TextView) findViewById(R.id.text_time);
        start = (Button)findViewById(R.id.start);
        pause = (Button)findViewById(R.id.pause);
        reset = (Button)findViewById(R.id.reset);

        handler = new Handler();

        //시작버튼
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartTime = SystemClock.uptimeMillis();
                handler.postDelayed(runnable, 0);

                reset.setEnabled(false);
                start.setEnabled(false);
            }
        });

        //일시정지 버튼
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimeBuff += MsTime;

                handler.removeCallbacks(runnable);

                reset.setEnabled(true);
                start.setEnabled(true);

            }
        });

        //reset 버튼
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MsTime =0L;
                StartTime = 0L;
                TimeBuff = 0L;
                UpdateTime = 0L;
                Seconds = 0;
                Minutes = 0;
                MilliSeconds = 0;

                textView.setText("00:00:00");
                textView.setTextColor(Color.parseColor("#3F51B5"));
            }
        });

    }

    //runnable 객체 정의 (스톱워치 관련)
    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //현재까지 시간 - 시작 버튼 누른 시간
            MsTime  = SystemClock.uptimeMillis() - StartTime;

            //일시정지 눌렀을 때 총 시간  + 시작버튼 누른 이후부터의 시간 = 총시간
            UpdateTime = TimeBuff + MsTime;
            Seconds = (int) (UpdateTime/1000);
            Minutes = Seconds /60;
            Seconds = Seconds % 60;
            MilliSeconds = (int) (UpdateTime % 1000);

            //TextView 에 UpdateTime 갱신
            textView.setText(""+Minutes + ":"
                    + String.format("%02d", Seconds) + ":"
                    + String.format("%03d", MilliSeconds));

            handler.postDelayed(this, 0);

            //시간 지나면 TextView 색상 바꾸기
            //if문 이용해서 시간 얼마 지나면 4분정도 바뀌기
            if(Minutes>=4){
                textView.setTextColor(Color.parseColor("#FFF44336"));
            }
            else{
                textView.setTextColor(Color.parseColor("#3F51B5"));
            }
        }
    };

    // 버튼 클릭 시 성인의 CPR을 하는 BluetoothActivityForAdult로 연결
    public void onBtnadultClicked(View v) {
        Intent intent = new Intent(SubActivity.this, BluetoothActivityForAdult.class);
        startActivity(intent);
    }

    // 버튼 클릭 시 아동의 CPR을 하는 BluetoothActivityForChild로 연결
    public void onBtnchildClicked(View v) {
        Intent intent = new Intent(SubActivity.this, BluetoothActivityForChild.class);
        startActivity(intent);
    }




}