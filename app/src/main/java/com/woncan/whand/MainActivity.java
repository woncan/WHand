package com.woncan.whand;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.woncan.whand.databinding.ActivityMainBinding;
import com.woncan.whand.scan.ScanCallback;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding dataBinding;
    private BluetoothDeviceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        WHandManager.getInstance().init(BuildConfig.DEBUG);

        Options.isAutoConnect = false;
        Options.scanPeriod = 10 * 1000;

        dataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BluetoothDeviceAdapter();
        dataBinding.recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((baseQuickAdapter, view, position) -> {
            BluetoothDevice bluetoothDevice = adapter.getData().get(position);
            Intent intent = new Intent(MainActivity.this, DeviceActivity.class);
            intent.putExtra("device", bluetoothDevice);
            startActivity(intent);
        });


        dataBinding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Toast.makeText(MainActivity.this, "请开启定位权限", Toast.LENGTH_SHORT).show();
                    return;
                }
                WHandManager.getInstance().stopScan();
                WHandManager.getInstance().startScan(new ScanCallback() {

                    @Override
                    public void onError(int errorCode, String message) {
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        Log.i("TAG", "onError: " + message);
                    }

                    @Override
                    public void onLeScan(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord) {
                        Log.i("TAG", "onLeScan: " + bluetoothDevice.getAddress());
                        if (!adapter.getData().contains(bluetoothDevice)) {
                            adapter.addData(bluetoothDevice);
                        }
                    }
                });
            }
        });


    }
}