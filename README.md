# WHand 导入项目


项目中添加

   libs中添加aar文件
    
   添加
   
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8 
        targetCompatibility JavaVersion.VERSION_1_8
    }
    
     Android 4.3之后，开始支持BLE低功耗蓝牙
    
# 使用方式
    初始化
    WHandManager.getInstance().init(BuildConfig.DEBUG);
    只有DEBUG模式下会输出log
    
    配置Options
    
    开启扫描
    需要ACCESS_COARSE_LOCATION或者ACCESS_FINE_LOCATION权限
    
	WHandManager.getInstance().startScan(new ScanCallback() {

    	@Override
        public void onError(int errorCode, String message) {
                 //错误回调
	    }

    	@Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
               //扫描到蓝牙就会回调，可能一个设备会被发现多次
    	}
	});
    
    扫描会默认在Options.scanPeriod (毫秒)后停止，也可以手动停止
    WHandManager.getInstance().stopScan();
	
    蓝牙连接
    device = WHandManager.getInstance().connect(MainActivity.this, bluetoothDevice);
    返回连接设备，可以直接设置设备
    
    设置连接状态监听
    device.setOnConnectionStateChangeListener(new OnConnectionStateChangeListener() {
                @Override
                public void onConnectionStateChange(int status, int newState) {
                //四种状态
                 //BluetoothProfile.STATE_CONNECTED
                 //BluetoothProfile.STATE_DISCONNECTED
                 //BluetoothProfile.STATE_CONNECTING
                 //BluetoothProfile.STATE_DISCONNECTING
                }
    });
    设置返回数据监听
       device.setOnConnectListener(new OnConnectListener() {
                @Override
                public void onDeviceChanged(WHandInfo wHandInfo) {
                    //返回设备信息
                }

                @Override
                public void onAccountChanged(String name) {
                   //设备内账号
                }

                @Override
                public void onError(Exception e) {
                    //连接出错时回调
                }
            });
    
    
| device API|  说明
| :-----  | :----:  |
| setAngle(int angle)|  设置高度角   |
|   setInterval(int interval)|   设置传输间隔   |
|    disconnect()   |  断开连接  |
|   getAccount()   |  获取账号|
|   setAccount(String account,String password)  |  设置账号
|   reStartSocket()  |  重启socket|
	
            
    

    
