package com.example.meitapp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    // !-- 이 액티비티는 메인 화면 UI가 구현되어 있으며, 각 버튼을 누름에 따라 다른 기능이 있는 액티비티로 넘어감 --!

    //main ui 코드
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //119 전화 연결
    public void onCallClicked(View v){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:119"));
        startActivity(intent);
    }

    //subactivity로 연결 -> 스톱워치,성인과 아동모드
    public void onHeartbtnClicked(View v) {
        Intent intent = new Intent(MainActivity.this, SubActivity.class);
        startActivity(intent);
    }

    //AED 위치 연결
    public void onMapClicked(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.e-gen.or.kr/egen/search_aed.do?searchType=general&lat=&lon=&sidoCode=11&gugunCode=680&dongCode=&loca=11&emogdstr=1101&addraed="));
        startActivity(intent);
    }

    // 유튜브 심폐소생술 연결
    public void onVideoClicked(View v) {
        startActivity(new Intent(Intent.ACTION_VIEW)
                .setData(Uri.parse("https://youtu.be/q7J2T6MFA9g")) // edit this url
                .setPackage("com.google.android.youtube"));	// do not edit
    }

    // BluetoothActivity로 연결
    public void onBtbuttonClicked(View v) {
        Intent intent = new Intent(MainActivity.this, BluetoothActivity.class);
        startActivity(intent);
    }
}
