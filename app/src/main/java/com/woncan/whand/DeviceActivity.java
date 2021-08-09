package com.woncan.whand;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.woncan.whand.databinding.ActivityDeviceBinding;
import com.woncan.whand.device.IDevice;
import com.woncan.whand.listener.OnConnectListener;

import java.util.Locale;

public class DeviceActivity extends AppCompatActivity {

    private ActivityDeviceBinding dataBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_device);
        dataBinding.tvLog.setMovementMethod(ScrollingMovementMethod.getInstance());
        BluetoothDevice bluetoothDevice = getIntent().getParcelableExtra("device");
        if (bluetoothDevice == null) {
            showLog("设备为空");
        }

        IDevice device = WHandManager.getInstance().connect(this, bluetoothDevice);
//        device.setNtripConfig("rtk.ntrip.qxwz.com", 8003, "RTCM32_GGB", "account", "password");
        device.setAccount("account", "password");
        device.setOnConnectionStateChangeListener((status, newStatus) -> {
            showLog(String.format(Locale.CHINA, "newStatus：%d   oldStatus：%d", newStatus, status));
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
                    showLog("蓝牙断开");
                    break;
            }
        });
        device.setOnConnectListener(new OnConnectListener() {
            @Override
            public void onDeviceChanged(WHandInfo wHandInfo) {
                showLog(wHandInfo.toString());
            }

            @Override
            public void onNameChanged(String name) {
                showLog(name);
            }

            @Override
            public void onNMEAReceive(String nmea) {
                Log.i("TAG", "onNMEAReceive: " + nmea);
            }

            @Override
            public void onAccountChanged(String name) {
                showLog(name);
            }

            @Override
            public void onError(Exception e) {
                showLog(e.getMessage());
            }
        });
    }


    private void showLog(@Nullable String log) {
        if (!TextUtils.isEmpty(log))
            runOnUiThread(() -> dataBinding.tvLog.append(log + "\n"));

    }

}