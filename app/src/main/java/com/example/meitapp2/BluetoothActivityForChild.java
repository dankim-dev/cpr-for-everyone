package com.example.meitapp2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;


public class BluetoothActivityForChild extends AppCompatActivity {

    // !-- 이 액티비티에는 아동용 심폐소생술 안내기를 구현했으며, 아두이노와 블루투스로 연결되고 송수신할 수 있음. 센서값에 따라 성공,실패 TextView의 색이 변함. --!

    private BluetoothSPP bt;

    // 메트로놈 mp3용
    Button btnmp3on3;
    Button btnmp3off3;

    MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_for_child);

        Intent intent = getIntent(); //변수 받아오기 추가 부분
        bt = new BluetoothSPP(this); //Initializing

        if (!bt.isBluetoothAvailable()) { //블루투스 사용 불가
            Toast.makeText(getApplicationContext()
                    , "블루투스를 사용할 수 없습니다."
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신받기
            TextView pressure3 = findViewById(R.id.pressure3);
            TextView success3 = findViewById(R.id.success3);
            TextView fail3 = findViewById(R.id.fail3);

            public void onDataReceived(byte[] data, String message) {
                pressure3.setText("압력은 " + message + " 입니다");

                double receivedmsg = Double.parseDouble(message); //string형으로 메세지 받아왔으니 double형으로 변환

                if (receivedmsg > 400) {
                    success3.setTextColor(Color.parseColor("#4CAF50"));
                    fail3.setTextColor(Color.parseColor("#4B5487"));
                }

                else if (receivedmsg == 0) {
                    success3.setTextColor(Color.parseColor("#4B5487"));
                    fail3.setTextColor(Color.parseColor("#4B5487"));
                }

                else {
                    fail3.setTextColor(Color.parseColor("#F44336"));
                    success3.setTextColor(Color.parseColor("#4B5487"));
                }

            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때 토스트메세지
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "연결된 기기 이름 :  " + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() { //연결해제 때 토스트메세지
                Toast.makeText(getApplicationContext()
                        , "연결이 해제되었습니다.", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() { //연결실패 때 토스트메세지
                Toast.makeText(getApplicationContext()
                        , "연결할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnConnect3 = findViewById(R.id.btnConnect3); //연결시도 때 토스트메세지
        btnConnect3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bt.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                    //activityResultLauncher.launch(intent);
                }
            }
        });

        // 메트로놈 mp3
        btnmp3on3 = findViewById(R.id.btnmp3on3);
        btnmp3off3 = findViewById(R.id.btnmp3off3);

        // 시작 버튼 눌렀을 때
        btnmp3on3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer = MediaPlayer.create(BluetoothActivityForChild.this, R.raw.cprbpm);
                mediaPlayer.start();
            }
        });

        // 중지 버튼 눌렀을 때
        btnmp3off3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
            }
        });
    }

    public void onDestroy() {
        super.onDestroy();
        bt.stopService(); //블루투스 중지

        if(mediaPlayer != null) { // 음원 중지
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void onStart() {
        super.onStart();

        if (!bt.isBluetoothEnabled()) { //
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
            //activityResultLauncher2.launch(intent);

        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                setup();
            }
        }
    }

    public void setup() {
        Button btnSend3 = findViewById(R.id.btnSend3); //데이터 전송 (시리얼 모니터)
        btnSend3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bt.send("아동 심폐소생술 모드입니다.", true);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                setup();
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }



}