package com.example.meitapp2;

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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;


public class BluetoothActivity2 extends AppCompatActivity {

    private BluetoothSPP bt;

    // !-- 이 액티비티는 블루투스로 안드로이드와 아두이노를 연결하고, 테스트해볼 수 있게 설계되었으며, 페어링을 위해 기기 설정 내 블루투스 탭으로 전환되는 버튼이 있음 --!
    //블루투스로 아두이노와 안드로이드 연결하기 부분 구현
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth2);

        Intent intent = getIntent(); //변수 받아오기 추가 부분


        bt = new BluetoothSPP(this); //Initializing

        /*ActivityResultLauncher<Intent> activityResultLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult activityResult) {
                                int resultCode = activityResult.getResultCode();
                                Intent data = activityResult.getData();
                                int requestCode = BluetoothState.REQUEST_CONNECT_DEVICE;

                                switch (requestCode) {
                                    case BluetoothState.REQUEST_CONNECT_DEVICE:
                                        if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                                            bt.disconnect();
                                        }
                                        else {
                                            Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                                        }
                                        break;
                                }
                            }
                        }
                );*/

        if (!bt.isBluetoothAvailable()) { //블루투스 사용 불가
            Toast.makeText(getApplicationContext()
                    , "블루투스를 사용할 수 없습니다."
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신받기
            TextView pressure1 = findViewById(R.id.pressure1);
            TextView success1 = findViewById(R.id.success1);
            TextView fail1 = findViewById(R.id.fail1);

            public void onDataReceived(byte[] data, String message) {
                pressure1.setText("압력은 " + message + " 입니다");

                double receivedmsg = Double.parseDouble(message); //string형으로 메세지 받아왔으니 double형으로 변환

                if (receivedmsg > 800) {
                    success1.setTextColor(Color.parseColor("#4CAF50"));
                    fail1.setTextColor(Color.parseColor("#4B5487"));
                }
                else if (receivedmsg == 0) {
                    success1.setTextColor(Color.parseColor("#4B5487"));
                    fail1.setTextColor(Color.parseColor("#4B5487"));
                }
                else {
                    fail1.setTextColor(Color.parseColor("#F44336"));
                    success1.setTextColor(Color.parseColor("#4B5487"));
                }

            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때 토스트메세지
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "연결된 기기 이름 : " + name + "\n" + address
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

        Button btnConnect1 = findViewById(R.id.btnConnect1); //연결시도
        btnConnect1.setOnClickListener(new View.OnClickListener() {
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

    }

    public void onDestroy() {
        super.onDestroy();
        bt.stopService(); //블루투스 중지
    }

    public void onStart() {
        super.onStart();
     /* ActivityResultLauncher<Intent> activityResultLauncher2 =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult activityResult) {
                                int resultCode = activityResult.getResultCode();
                                Intent data = activityResult.getData();
                                int requestCode = BluetoothState.REQUEST_ENABLE_BT;

                                switch (requestCode) {
                                    case BluetoothState.REQUEST_ENABLE_BT:
                                        if (!bt.isBluetoothEnabled()) {
                                            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                        }
                                        break;
                                }
                            }
                        }
                );*/

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
        Button btnSend1 = findViewById(R.id.btnSend1); //데이터 전송 (시리얼모니터)
        btnSend1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bt.send("meit 연결 상태 확인용 메세지", true);
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

    // 기기 설정 창의 블루투스 연결 부분으로 이동(페어링을 쉽게 할 수 있다는 장점)
    public void onBtnsetting1Clicked(View v) {
        Intent intent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(intent);
    }

}