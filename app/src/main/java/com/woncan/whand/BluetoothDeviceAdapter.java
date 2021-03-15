package com.woncan.whand;

import android.bluetooth.BluetoothDevice;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

public class BluetoothDeviceAdapter extends BaseQuickAdapter<BluetoothDevice, BaseViewHolder> {

    private BluetoothDeviceAdapter(int layoutResId) {
        super(layoutResId);
    }

    public BluetoothDeviceAdapter() {
        this(R.layout.item_bluetooth_device);
    }

    @Override
    protected void convert(BaseViewHolder helper, BluetoothDevice item) {
        helper.setText(R.id.item_bluetooth_name, item.getName());

    }


}