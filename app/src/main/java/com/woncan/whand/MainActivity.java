package com.woncan.whand;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.woncan.whand.device.IDevice;
import com.woncan.whand.listener.OnConnectListener;
import com.woncan.whand.scan.ScanCallback;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WHandManager.getInstance().init(BuildConfig.DEBUG);
        Options.isDebug = BuildConfig.DEBUG;
        Options.isAutoConnect = false;
        Options.scanPeriod = 10 * 1000;
        WHandManager.getInstance().init(BuildConfig.DEBUG);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        WHandManager.getInstance().startScan(new ScanCallback() {

            @Override
            public void onError(int errorCode, String message) {
                Log.i("TAG", "onError: " + message);
            }

            @Override
            public void onLeScan(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord) {

                WHandManager.getInstance().stopScan();
                IDevice device = WHandManager.getInstance().connect(MainActivity.this, bluetoothDevice);

                device.setNtripConfig("rtk.ntrip.qxwz.com",8003,"RTCM32_GGB","account","password");

                device.setAccount("account","password");

                device.setOnConnectionStateChangeListener((status, newStatus) -> {
                    Log.i("TAG", "onConnectionStateChange: " + newStatus);
                    switch (newStatus) {//newState顾名思义，表示当前最新状态。status可以获取之前的状态。
                        case BluetoothProfile.STATE_CONNECTED:
                            //这里表示已经成功连接，如果成功连接，我们就会执行discoverServices()方法去发现设备所包含的服务
                            //设置数据传输间隔
                            device.setInterval(1000);
                            //设置激光开关
                            device.showLaser(false);
                            break;
                        case BluetoothProfile.STATE_DISCONNECTED:
                            //表示gatt连接已经断开。
                            break;
                    }
                });
                device.setOnConnectListener(new OnConnectListener() {
                    @Override
                    public void onDeviceChanged(WHandInfo wHandInfo) {
                        Log.i("TAG", "onDeviceChanged: " + wHandInfo.toString());
                    }

                    @Override
                    public void onNameChanged(String name) {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onAccountChanged(String name) {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this,
                                new String(Base64.decode(name, 0)), Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.i("TAG", "onError: " + e.getMessage());
                    }
                });
            }
        });

    }
}