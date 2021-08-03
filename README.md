# WHand 项目配置

下面2种方式任选其一

方式一：Gradle集成SDK (推荐)

在Project的build.gradle文件中配置repositories，添加maven或jcenter仓库地址

	allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
在主工程的build.gradle文件配置dependencies

	android {
		compileOptions {
			sourceCompatibility JavaVersion.VERSION_1_8
			targetCompatibility JavaVersion.VERSION_1_8
		}
	}
	dependencies {
	        implementation 'com.github.woncan:WHand:latest.release' //其中latest.release指代最新SDK版本号
	}

方式二：aar导入

[下载aar](http://survey-file.woncan.cn/firmware/20210621-181848/whand-release.aar)
将下载的aar包复制到工程的 libs 目录下，如果有老版本aar包在其中,请删除。

构建aar编译路径

	dependencies {
		implementation fileTree(dir: 'libs', include: ['*.jar'])
		implementation(name: 'whand-release', ext: 'aar')
		}

	android {
		repositories {
			flatDir {
				dirs 'libs'
			}
	}


# 使用方式
初始化WHandManager

	WHandManager.getInstance().init(BuildConfig.DEBUG);
参数为SDK日志开关，debug模式下会输出log

开启扫描，发现蓝牙设备
需要蓝牙权限和定位权限

	WHandManager.getInstance().startScan(new ScanCallback() {

		@Override
		public void onError(int errorCode, String message) {
			 //返回错误码 和 错误信息
		}

		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			//扫描到蓝牙就会回调(可能一个设备会被发现多次，需要去重)
		}
	});

扫描会默认在Options.scanPeriod (毫秒)后停止，也可以手动停止

	WHandManager.getInstance().stopScan();

连接设备

	//连接扫描到的蓝牙设备
	device = WHandManager.getInstance().connect(MainActivity.this, bluetoothDevice);
	//直接设置账号密码时采用千寻差分
	//ip:rtk.ntrip.qxwz.com     port:8003     mountPoint:RTCM32_GGB
	device.setAccount("account","password");
	//修改差分站调用
	//device.setNtripConfig(ip,port,mountPoint,account,password);
	//以上选取一种方式配置

	设置连接状态监听
	device.setOnConnectionStateChangeListener(new OnConnectionStateChangeListener() {
			@Override
			public void onConnectionStateChange(int status, int newState) {
			 //status表示之前的连接状态，newState现在的连接状态
			 //全部有4种连接状态
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
			//返回设备信息 wHandInfo里面包含 位置信息 和 陀螺仪信息
			}
			@Override
			public void onAccountChanged(String name) {
			//设备内账号改变会调用
			}

			@Override
			public void onError(Exception e) {
			//连接出错时调用
			}
		});

### WHandInfo
| WHandInfo|    说明| 数据类型|
| :-------- | :--------| :--: |
| latitude   | 纬度     |  double|
| longitude  | 经度     |  double|
| altitude   |海拔高度|  double|
| altitudeErr|大地水准面高度异常差值|  double|
| gpsNum     | 卫星颗数|  int|
| rtkType	 | 解算精度|  int|
| accuracyFlat| 水平精度|  int|
| accuracyAlt| 高程精度|  int|
| accelerationX| X轴加速度|  long|
| accelerationY| Y轴加速度|  long|
| accelerationZ| Z轴加速度|  long|
| spinX| X轴角速度|  long|
| spinY| Y轴角速度|  long|
| spinZ|Z轴角速度|  long|
| laserDistance|激光测距|  int|
| accelerationLaser|激光精度|  int|
| power| 电量|  int|

备注:解算精度  -1 没收到星  1单点  2码差分   5浮点    4固定

	大地高=海拔高+大地水准面高度异常差值

### 更多设置

	//需要提前配置
	//设置debug模式
	Options.isDebug = BuildConfig.DEBUG;
	//设置蓝牙是否自动连接
	Options.isAutoConnect = false;
	//设置蓝牙扫描时长  默认10秒后关闭扫描
	Options.scanPeriod = 10 * 1000;

| device API|  说明
| :-----  | :----:  |
| setAngle(int angle)|  设置高度角   |
|   setInterval(int interval)|   设置传输间隔   |
|    disconnect()   |  断开连接  |
|   getAccount()   |  获取账号|
|   setAccount(String account,String password)  |  设置账号
|   reStartSocket()  |  重启socket|
|showLaser(boolean isShow)|设置激光开关|


