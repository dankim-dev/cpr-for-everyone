package com.example.meitapp2;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;


import java.util.Set;

public class BluetoothActivity<Private> extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;

    TextView mStatusBluetv, mPairedTv;
    ImageView mBlueIv;
    Button mOnBtn, mOffBtn, mDiscoverBtn, mPairedBtn;

    BluetoothAdapter mBlueAdapter;

    // !-- 이 액티비티는 블루투스 on/off 및 찾기 기능, 실질적으로 아두이노와 연결하기 위한 액티비티로 가는 버튼이 구현되어 있음 --!

    //StartActivityForResult 대체
    ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult activityResult) {
                            int resultCode = activityResult.getResultCode();
                            Intent data = activityResult.getData();
                            int requestCode = REQUEST_ENABLE_BT;

                            switch (requestCode) {
                                case REQUEST_ENABLE_BT:
                                    if (resultCode == RESULT_OK) {
                                        // bluetooth is on
                                        mBlueIv.setImageResource(R.drawable.ic_action_on);
                                        Toast.makeText(getApplicationContext(), "블루투스를 켰습니다.", Toast.LENGTH_SHORT).show();
                                        //showToast("Bluetooth is on");
                                    }
                                    else {
                                        //user denied to turn bluetooth on
                                        //showToast("couldn't on bluetooth");
                                        Toast.makeText(getApplicationContext(), "블루투스 연결을 거부했습니다. 권한을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                            }
                        }
                    }
            );

    /*ActivityResultLauncher<Intent> activityResultLauncher2 =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult activityResult) {
                            int resultCode = activityResult.getResultCode();
                            Intent data = activityResult.getData();
                            int requestCode = REQUEST_DISCOVER_BT;

                            switch (requestCode) {
                                case REQUEST_ENABLE_BT:
                                    if (resultCode == RESULT_OK) {
                                        // bluetooth is on
                                        mBlueIv.setImageResource(R.drawable.ic_action_on);
                                        showToast("Bluetooth is on");
                                    }
                                    else {
                                        //user denied to turn bluetooth on
                                        showToast("couldn't on bluetooth");
                                    }
                                    break;
                            }
                        }
                    }
            );*/



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        mStatusBluetv = findViewById(R.id.statusBluetoothTv);
        mPairedTv = findViewById(R.id.pairedTv);
        mBlueIv = findViewById(R.id.bluetoothIv);
        mOnBtn = findViewById(R.id.onBtn);
        mOffBtn = findViewById(R.id.offBtn);
        mDiscoverBtn = findViewById(R.id.discoverableBtn);
        //mPairedBtn = findViewById(R.id.pairedBtn);

        //adapter
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();

        //check if bluetooth is available or not
        if (mBlueAdapter == null) {
            mStatusBluetv.setText("Bluetooth is not available");
        } else {
            mStatusBluetv.setText("Bluetooth 작동 상태");
        } //else로 계속 됨


        //블루투스 상태 (On/Off) 알기 위해 image asset 설정
        if (mBlueAdapter.isEnabled()) {
            mBlueIv.setImageResource(R.drawable.ic_action_on);
        } else {
            mBlueIv.setImageResource(R.drawable.ic_action_off);
        } //블루투스 끄고 키는 데에 작동하는 것 확인됨

        // on btn click
        // ShowToast 함수가 작동하지 않는 관계로 직접 Toast Message 정의
        mOnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBlueAdapter.isEnabled()) {
                    Toast.makeText(getApplicationContext(), "블루투스를 켭니다.", Toast.LENGTH_SHORT).show();
                    //showToast("Turning On Bluetooth...");
                    // intent to on bluetooth
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    //startActivityForResult(intent, REQUEST_ENABLE_BT);
                    activityResultLauncher.launch(intent);

                } else {
                    Toast.makeText(getApplicationContext(), "블루투스가 이미 연결되어 있습니다.", Toast.LENGTH_SHORT).show();
                    //showToast("Bluetooth is already on");
                }
            }
        });

        //discover bluetooth btn 구현
        mDiscoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    if (!mBlueAdapter.isDiscovering()) {
                        Toast.makeText(getApplicationContext(), "주변에서 이 기기를 검색할 수 있게 합니다.", Toast.LENGTH_SHORT).show();
                        //showToast("Making your Device discoverable");
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                        startActivityForResult(intent, REQUEST_DISCOVER_BT);
                        //activityResultLauncher2.launch(intent);
                }
                } catch (SecurityException e) {
                    e.printStackTrace();
                } // 안드로이드 12를 위해 권한 부여를 해 줘야 함
            }
        });

        //off btn click 구현
        mOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                if (mBlueAdapter.isEnabled()) {
                    mBlueAdapter.disable();
                    Toast.makeText(getApplicationContext(), "블루투스를 끕니다.", Toast.LENGTH_SHORT).show();
                    //showToast("Turning Bluetooth Off");
                    mBlueIv.setImageResource(R.drawable.ic_action_off);
                } else {
                    Toast.makeText(getApplicationContext(), "블루투스가 이미 꺼져 있습니다.", Toast.LENGTH_SHORT).show();
                    //showToast("Bluetooth is already Off");
                }
            } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        });

        //get paired devices btn click
 /*       mPairedTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {


                    if (mBlueAdapter.isEnabled()) {
                        mPairedTv.setText("Paired Devices");
                        Set<BluetoothDevice> devices = mBlueAdapter.getBondedDevices();
                        for (BluetoothDevice device : devices) {
                            mPairedTv.append("\nDevice: " + device.getName() + "," + device);
                        }
                    } else {
                        //bluetooth is off so cannot get paired devices
                        Toast.makeText(getApplicationContext(), "Turn on bluetooth to get paired devices", Toast.LENGTH_SHORT).show();
                        //showToast("Turn on bluetooth to get paired devices");
                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        });*/

    }

 /*   @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    // bluetooth is on
                    mBlueIv.setImageResource(R.drawable.ic_action_on);
                    showToast("Bluetooth is on");
                }
                else {
                    //user denied to turn bluetooth on
                    showToast("couldn't on bluetooth");
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }*/


    //toast message function
    /*private void showToast(String msg) {
        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
    }*/

    public void onConnectbtnClicked(View v) {
        Intent intent = new Intent(BluetoothActivity.this, BluetoothActivity2.class);
        startActivity(intent);
    }

}