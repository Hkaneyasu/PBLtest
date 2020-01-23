package com.example.test11_01_2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.ComponentName;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test11_01_2.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    private final String mNUMBER = "17108039";

    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String DeviceName2 = "arduino_test";
    private static final String DeviceName1 = "Galaxy A30";
    private static final String PCName = "DESKTOP-39D7KJ3";
    public int valF = 0;
    public int valS = 0;
    static String state = "start";
    public boolean Sym_yet_pushed = true;
    public String Text;
    static BluetoothAdapter bAdapter = null;
    public BluetoothServerSocket bServerSocket = null;
    public BluetoothSocket bSocket = null;
    public byte[] Buffer = new byte[1024];
    public ArrayAdapter<String> deviceList;
    public BluetoothDevice bDevice = null;
    static int readNum = 23;        //２文字以上遅れるか
    static String readS = "start";
    public int DEfAULT_NUM = 23;
    public TextView readText;
    public String check;
    static boolean escape;
    public boolean streamRunning = false;
    public String connectionPhase = "start";

    public Handler handler = new Handler();

    private String mResult = "";
    static int scanedRSSI = -100;

    public InputStream bInStream;   //同一のIOException内じゃないと参照できないっぽい
    public OutputStream bOStream;

    //<BLEservice----------------------------------------------
    private final static String TAG = "MainActivity";
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public String bleDeviceName;
    public String bleDeviceAddress;
    public BluetoothLeService bleService;
    public Intent gattServiceIntent;
    public final String AppFileName = "PBLservice";
    //----------------------------------------BLEservice>

    //<BLEscan------------------------------------------------
    private static final long SCAN_PERIOD = 10000;
    public Handler bleHandler;
    private BluetoothLeScanner bleScanner;
    public String DeviceStatus;
    public BluetoothGatt btGatt;
    //----------------------------------------BLEscan>

    //<timing--------------------------------------------
    public boolean currentState = false;
    public boolean nextState = false;
    static  boolean flag;
    public int[] lastUpdate = new int[25];
    //-------------------------------------------/timing>

    //<cal-----------------------------------------------
    public static int iYear;
    public static int iMonth;
    public static int iDate;
    public static int iHour;
    public static int iMinute;
    public static int iSecond;
    public static int iDayOfWeek;
    public static int iAM_PM;
    public static int iSum_Time;
    public static String strKoma = null;//今が何曜日の何コマ目か
    public TextView tv;
    private Button Rireki;
    private Button Itirann;
    static int Nowtime[] = new int[3];   //現在のコマを代入する用  staticでActivity間のデータ移動
    public static String strKoma_Name = "授業時間外"; //科目名表示用
    public hanntei imaitu;
    public boolean imakita = false;
    //---------------------------------------------------cal>

    public int CYCLE = 1/60;   //再接続まで何秒か
    public int HANPUKU = 5*60;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //プリファレンス初期化
        //SharedPreferences.Editor editor = pref.edit();
        //editor.clear();

        //RSSI scan　
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        BT_request();
        ExMethod();

        //gattServiceIntent = new Intent(this, BluetoothLeService.class);


        //ページ遷移用
        Rireki = (Button)findViewById(R.id.rireki);
        Itirann = (Button)findViewById(R.id.itirann);

        //履歴ボタン
        Rireki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), Rireki.class);
                startActivity(intent);
            }
        });

        //一覧ボタン
        Itirann.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), Itirann.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();

        //flag状態を端末から読み込んで開始
        //SharedPreferences pref = getPreferences(MODE_PRIVATE);
        SharedPreferences pref = getSharedPreferences(AppFileName,MODE_PRIVATE);
        flag = pref.getBoolean("FLAG",false);   //キー,デフォルト
        pref_get();
        KomaNames = getArray("KomaNames");  //メインに戻るたびにKomaNames更新

        //時刻表示するコードを追加
        Calendar cal = Calendar.getInstance();       //カレンダーを取得

        iYear = cal.get(Calendar.YEAR);         //年を取得
        iMonth = cal.get(Calendar.MONTH) + 1;       //月を取得  0~11に+1して1~12
        iDate = cal.get(Calendar.DATE);         //日を取得
        iHour = cal.get(Calendar.HOUR);         //時を取得(0~11)
        iMinute = cal.get(Calendar.MINUTE);    //分を取得
        iSecond = cal.get(Calendar.SECOND);    //分を取得
        iDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);   //(1~7  sun~sat)
        iAM_PM = cal.get(Calendar.AM_PM);       //曜日を取得
        iSum_Time = iHour*3600 + iMinute*60 + iSecond;  //時間を秒に変換(12時間まで)

        imaitu = new hanntei();
        imaitu.set();

        //時刻表示
        String strDay = iYear + "年" + iMonth + "月" + iDate + "日";     //日付を表示形式で設定
        String strTime = iHour + "時" + iMinute + "分" + iSecond + "秒"; //時刻を表示形式で設
        String set = "起動日時：" + strDay  + strTime + strKoma +"\n　　　　　  　"  +  "科目名:" + strKoma_Name;
        tv = (TextView) findViewById(R.id.textView);
        tv.setText(set);

        buttonStart();
    }

    @Override
    protected void onPause(){
        super.onPause();
        if (!isMyServiceRunning(BT_service.class)) return;
        Intent stopIntent = new Intent(this, BT_service.class);
        stopIntent.setAction("stop");
        startService(stopIntent);

        //bleStopScan();

        //flag状態を端末に保存して終了
        //SharedPreferences pref = getPreferences(MODE_PRIVATE);
        SharedPreferences pref = getSharedPreferences(AppFileName,MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("FLAG", flag);
        editor.commit();
        pref_set();

        check = "disconnect";
        escape = false;

       //stopCommand();
    }


    /*
    //<BLE---------------------------------------------------------------------------------
    //onClickとかで実行するやつ
    //byte[] bytes = new byte[8];
    //bleService.sendData(bytes);     //送信


    private final ServiceConnection bleServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            bleService = ((BluetoothLeService.LocalBinder) service).getService();
            if(!bleService.initialize()){
                Log.e(TAG, "*****Unalbe to initialize Bluetooth");
                finish();
            }
            bleService.connect(bleDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bleService = null;
        }
    };

    private final BroadcastReceiver bleGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)){
                //GATT接続時の処理
            }else if(BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)){
                //GATT切断時の処理
            }else if(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                //GATTサービス取得時の処理
            } else if (BluetoothLeService.ACTION_DATA_AVAIlABLE.equals(action)) {
                //データ受信時の処理
                byte[] receiveData = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                if(receiveData != null){
                    readNum = convByteToInt(receiveData[0]);  //  byte[] => int
                }
            }
        }
    };

    private static int convByteToInt(byte b){
        int i = (int)b;
        i &= 0x000000FF;
        return i;
    }

    private static IntentFilter makeGattUpdateIntentFilter(){
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAIlABLE);
        return intentFilter;
    }

 */
    //--------------------------------------------------------------------------BLE/>

    //端末btをONにする許可インテント表示
    public void BT_request(){
        bAdapter = BluetoothAdapter.getDefaultAdapter();   // 端末のアダプタ取得
        state += "\n get adapter";
        //T_ST.setText(state);

        final int REQUEST_ENABLE_BT = 1;    //任意のリクエストコード

        if(!bAdapter.isEnabled()){     //BTがOFFのとき　ON許可インテント
            Intent BT_allow = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(BT_allow, REQUEST_ENABLE_BT);
        }
    }

    //実験用処理メソッド
    public void ExMethod(){
        final Button B_start = findViewById(R.id.ButtonConnect);
        final Button B_scan = findViewById(R.id.ButtonState);
        final TextView T_ST =findViewById(R.id.state);
        final Button B_stop = findViewById(R.id.ButtonStop);
        final TextView T_reg = findViewById(R.id.registText);
        readText = findViewById(R.id.ReadText);
        final BT_stream stream = new BT_stream();


        B_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //scanloop scanThread = new scanloop();     //scanはスレッドで実行すると上手くいかない
                //scanThread.run();                         //->アクティビティを占領する

                //BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                //BluetoothAdapter bleAdapter = manager.getAdapter();
                //bleAdapter.startLeScan(scanCallback);

                //bAdapter.startDiscovery();
            }
        });

        B_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // BT開始
                escape = false; //UI無限ループ許可
                state = null;
                readS = null;
                check  = null;
                readNum = 0;
                bAdapter.cancelDiscovery();
                BT_request();
                UIroop uIroop = new UIroop();
                uIroop.start();
                //Intent BTintent= new Intent(MainActivity.this, BT_service.class);
                //startForegroundService(BTintent);
                stopService(new Intent(MainActivity.this, BT_service.class));
                startService(new Intent(MainActivity.this, BT_service.class));

                /*
                if(bAdapter.isEnabled()){
                    if(device_is_find(bAdapter)){
                        //Open_BTserver btserver = new Open_BTserver();
                        //btserver.start();

                        //BLE通信のservice--------------------------------
                        //final Intent intent = getIntent();
                        //bleDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
                        //bleDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
                        //bindService(gattServiceIntent, bleServiceConnection, BIND_AUTO_CREATE);

                    }
                }

                 */
                //startCommand();
            }

        });

        B_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //BT停止
                //bleStopScan();
                //bleScanner = null;
                bAdapter.cancelDiscovery();
                escape = true;  //無限ループ禁止
                readNum = 0;
                stopService(new Intent(MainActivity.this, BT_service.class));

                state = null;
                readS = null;

                disconnect();

                //BLE^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
                //unregisterReceiver(bleGattUpdateReceiver);
                //unbindService(bleServiceConnection);
                //bleService = null;

                //BLEscan-------------------------------------
                //bAdapter.stopLeScan(bleScanCallback);



                //stopCommand();
            }
        });

    }

    public void buttonStart(){
            if(escape) return;
            // BT開始
            escape = false; //UI無限ループ許可
            state = null;
            readS = null;
            check = null;
            readNum = 0;
            //bAdapter.cancelDiscovery();
            BT_request();
            UIroop uIroop = new UIroop();
            uIroop.start();
            //Intent BTintent= new Intent(MainActivity.this, BT_service.class);
            //startForegroundService(BTintent);
            //startService(new Intent(MainActivity.this, BT_service.class));

        /*
            if (bAdapter.isEnabled()) {
                if (device_is_find(bAdapter)) {
                    Open_BTserver btserver = new Open_BTserver();
                    btserver.start();

                    //BLE通信のservice--------------------------------
                    //final Intent intent = getIntent();
                    //bleDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
                    //bleDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
                    //bindService(gattServiceIntent, bleServiceConnection, BIND_AUTO_CREATE);

                }
            }

         */
            //startCommand();
    }

    private class UIroop extends Thread{
        public void run(){

            while (true) {

                if(escape) break;   //更新許可が出てるときだけ実行

                SharedPreferences pref = getSharedPreferences(AppFileName,MODE_PRIVATE);
                flag = pref.getBoolean("FLAG",false);   //キー,デフォルト
                pref_get();

                try {   //０．３秒毎に自動更新
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        ChangeRead(readS);
                        //ChangeRead(String.valueOf(readNum));
                        ChangeState(String.valueOf(scanedRSSI));
                        ChangeReg(connectionPhase);

                        if(flag) ChangeSyusseki("入室しました");
                        else ChangeSyusseki("退室しました");

                        if(imakita) {   //入室の瞬間の時間をセット
                            imaitu.set();
                            imakita = false;

                            //時刻表示
                            imaitu.set();
                            String strDay = iYear + "年" + iMonth + "月" + iDate + "日";     //日付を表示形式で設定
                            String strTime = iHour + "時" + iMinute + "分" + iSecond + "秒"; //時刻を表示形式で設
                            String set = "記録した日時：" + strDay  + strTime + strKoma +"\n　　　　　  　"  +  "科目名:" + strKoma_Name;
                            tv.setText(set);
                        }
                    }
                });
            }
        }
    }

    private class scanloop extends Thread{
        public void run(){
            while(true){
                if(escape) break;
                bAdapter.startDiscovery();
                //bleScan();
                try{
                    Thread.sleep(10000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                bAdapter.cancelDiscovery();
                //bleStopScan();
                try {
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            return;
        }
    }

    //scanの結果RSSIが規定値以上だったらtrueを返す
    public boolean scanRSSI(int n){
        bAdapter.startDiscovery();
        try {
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        if(scanedRSSI < n){
            bAdapter.cancelDiscovery();
            return false;
        }else {
            bAdapter.cancelDiscovery();
            return true;
        }
    }

    //scanの結果RSSIが規定値以下だったらtrueを返す
    public boolean RSSIunder(int n){
        bAdapter.startDiscovery();
        try {
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        if(scanedRSSI < n){
            bAdapter.cancelDiscovery();
            return true;
        }else {
            bAdapter.cancelDiscovery();
            return false;
        }
    }

    //メソッドにまとめるとうまくいかない
    public void startCommand(){
        // BT開始
        escape = false; //UI無限ループ許可
        state = null;
        readS = null;
        check  = "start";
        readNum = 0;
        bAdapter.cancelDiscovery();
        BT_request();
        UIroop uIroop = new UIroop();
        uIroop.start();

        if(bAdapter.isEnabled()){
            if(device_is_find(bAdapter)){
                Open_BTserver btserver = new Open_BTserver();
                btserver.start();
            }
        }


        while (check != "finish") {     //ストリーム処理が終わるまで待機
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {                           //CLYCLE:接続禁止期間
            Thread.sleep(1000*60* CYCLE);
        }catch (InterruptedException e){
            e.printStackTrace();
        }



        startCommand();         //再起呼び出しで無限ループ
    }

    //メソッドにまとめるとうまくいかない
    public void stopCommand(){
        bAdapter.cancelDiscovery();
        escape = true;  //無限ループ禁止
        readNum = 0;
        state = null;
        readS = null;
        disconnect();
    }

    //マイコンに接続時にデバイス取得
    public boolean device_is_find( BluetoothAdapter bAdapter){
        Set<BluetoothDevice> devices = bAdapter.getBondedDevices();
        for(BluetoothDevice device : devices){
            if(device.getName().equals(DeviceName1)){
                bDevice = device;
                state += "\ndevice="+bDevice;
                bleDeviceAddress = bDevice.getAddress();
                bleDeviceName = bDevice.getName();
                return true;
            }
        }
        return false;
    }


    //<BLEscan-callback-----------------------------------------------------
    /*
    private BluetoothAdapter.LeScanCallback bleScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            // スキャンできた端末の情報をログ出力
            ParcelUuid[] uuids = device.getUuids();
            String uuid = "";
            if (uuids != null) {
                for (ParcelUuid puuid : uuids) {
                    uuid += puuid.toString() + " ";
                }
            }
            String msg = "name=" + device.getName() + ", bondStatus="
                    + device.getBondState() + ", address="
                    + device.getAddress() + ", type" + device.getType()
                    + ", uuids=" + uuid;
            Log.d("BLEActivity", msg);

        }
    };

    private void bleScan(){
                bleScanner = bAdapter.getBluetoothLeScanner();
                bleScanner.startScan(new ScanCallback() {
                    @Override
                    public void onScanResult(int callbackType, ScanResult result) {
                        super.onScanResult(callbackType, result);
                        if(result != null) {
                            BluetoothDevice myDevice = result.getDevice();
                            if(myDevice.getName() != null) {
                                Log.d(TAG, "\n*********************************\n"
                                        + myDevice.getName() + myDevice.getAddress() +
                                        "\n" + result.getRssi() + "*************************************\n");
                                DeviceStatus = myDevice.getName() + myDevice.getAddress() + result.getRssi() + "\n";
                            }
                        }
                    }

                    @Override
                    public void onScanFailed(int errorCode) {
                        super.onScanFailed(errorCode);
                        Log.d(TAG, "scanFailed ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    }
                });
    }

    private void bleStopScan(){
        if(bleScanner != null){
            bleScanner.stopScan(new ScanCallback() {
                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                }
            });
            bleScanner = null;
        }
    }

     */

    //-------------------------------------------------------------BLEscan>


    //<bluetoothGATTを使ったrssi取得----------------------------------------------
    /*
    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            if(status == BluetoothGatt.GATT_SUCCESS){
                Log.d(TAG, "********************************************rssi="+rssi);
            }
        }
    };

    private BluetoothAdapter.LeScanCallback scanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord) {
            if(bluetoothDevice.getName() != null){
                if(btGatt == null){
                    btGatt = bluetoothDevice.connectGatt(getApplicationContext(), false, gattCallback);
                    btGatt.readRemoteRssi();

                    String msg = "Name = " + bluetoothDevice.getName() + "rssi =" + rssi;
                    Log.d(TAG,"FIND!!*********************************************\n"+msg+
                            "\n*************************************************");

                }
            }
        }
    };

     */

    //-----------------------------------------------------------------------GATT>



    //サーバーソケットを開く→BTソケットをオープン＆保持→ストリーム処理
    //connect成功->近くにデバイスがある-> disconnect&scan -> rssiが-50dB以上-> canceldiscovery&再度connect
    public class Open_BTserver extends Thread{

        public void BTserver(){
            try {
                bServerSocket = bAdapter.listenUsingInsecureRfcommWithServiceRecord("NAME", uuid);

            }catch (IOException e){
                e.printStackTrace();
            }
            state += "\nopen ServerSocket";
            //T_ST.setText(state);
        }

        public void run() {
            streamRunning = false;
            if(escape) return;
            check = "start";
            scanedRSSI = -100;

            /*
            while (true){                               //最初のconnect
                if(check == "disconnect") break;
                try {
                    bSocket = bDevice.createRfcommSocketToServiceRecord(uuid);
                    bSocket.connect();
                    break;
                }catch (IOException e){
                    e.printStackTrace();
                    Log.d(TAG, "phase1 再接続");
                    try {
                        Thread.sleep(1000);     //connectが失敗したら1秒後にもう一度connect
                    }catch (InterruptedException w){
                        w.printStackTrace();
                    }
                }
            }

            connectionPhase = "phase1 OK";

            //↓↓connect成功↓↓
                try{
                    if(bSocket != null) {
                        bSocket.close();       //いったん切断してscan開始
                        bSocket = null;
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }

             */

            while (!scanRSSI(-60)) {    //RSSIが規定値以上ならtrue
                if(check == "disconnect") break;
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            connectionPhase = "phase1 OK RSSI = " +scanedRSSI+ "\n";
            int minRSSI = scanedRSSI;

            while (!RSSIunder(minRSSI - 10)){   //RSSIが規定値以下に落ちたら
                if(check == "disconnect") break;
                try{
                    Thread.sleep(300);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            connectionPhase += "phase2 OK RSSI = " +scanedRSSI+ "\n";

            //↓↓デバイス通過を確認↓↓
            while (true){               //2度目のconnect
                if(check == "disconnect") break;
                try {
                    bSocket = bDevice.createRfcommSocketToServiceRecord(uuid);
                    bSocket.connect();
                    check = "connectionOK";
                    state += "\nconnectOK socket=" + bSocket;

                    if (bSocket != null && check=="connectionOK") {    //ソケットが受け取れたら
                        //ストリーム処理開始
                        BT_stream bStream = new BT_stream();
                        bStream.start();
                    }
                    connectionPhase += "phase3 OK\n";
                    break;
                }catch (IOException e){
                    e.printStackTrace();
                    Log.d(TAG, "phase1 再接続");
                    try {
                        Thread.sleep(1000);     //connectが失敗したら1秒後にもう一度connect
                    }catch (InterruptedException w){
                        w.printStackTrace();
                    }
                }
            }

            while (true){
                if(check == "finish") break;
                try {
                    Thread.sleep(300);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }

            /*
            check = "connectionOK";
            state += "\nconnectOK socket=" + bSocket;

            while (check != "finish"){      //ストリーム処理が終わってdisconnectされるまで待機
                try {
                    Thread.sleep(500);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            this.run();

             */
                /*
            while (true) {
                if(check == "disconnect") break;

                try {   //クライアントからの接続要求待ち　ソケットが返される
                    // サーバーソケットから取得する場合


                    //BTserver();
                    //bSocket = bServerSocket.accept();
                    //bServerSocket.close();
                    //bServerSocket = null;


                    //デバイス名からRFCOMMソケットを取得する場合
                    bSocket = bDevice.createRfcommSocketToServiceRecord(uuid);
                    bSocket.connect();

                    //↓↓connect成功↓↓
                    bSocket.close();       //いったん切断してscan開始
                    bSocket = null;

                    while (!scanRSSI()) {    //RSSIが規定値以上ならtrue
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    //↓↓デバイス通過を確認↓↓
                    bSocket = bDevice.createRfcommSocketToServiceRecord(uuid);
                    bSocket.connect();              //再度接続

                    check = "connectionOK";
                    state += "\nconnectOK socket=" + bSocket;

                    if (bSocket != null && check=="connectionOK") {    //ソケットが受け取れたら
                        //ストリーム処理開始
                        BT_stream bStream = new BT_stream();
                        bStream.start();
                    }

                    return;      //正常にconnectできたらストリーム処理開始
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        Thread.sleep(1000);     //connectが失敗したら1秒後にもう一度connect
                    }catch (InterruptedException w){
                        w.printStackTrace();
                    }
                }
            }
            */

        }
    }

    //IN/OUTputストリーム取得→ストリームへの書き込み＆読み出し
    public class  BT_stream extends Thread{


        public void run() {   //全部こっちにまとめる

            byte[] inBuff = new byte[64];
            streamRunning = true;
            check = "streamStart";

            try {

                bInStream = bSocket.getInputStream();
                bOStream = bSocket.getOutputStream();
                state += "\nget Stream \n" + bInStream + "\n" + bOStream;   //streamがnullじゃないか確認

                int inBytes = 0;
                byte[] buff = new byte[inBytes];
                String cmd = new String(buff);

                bOStream.write("012".getBytes());

                while (true){   //割り込みがかかるまで通信
                    if(Thread.interrupted()) break;

                    nextState = false;
                    currentState = false;

                    //bOStream.write("012".getBytes());
                    //bOStream.write(011);

                    inBytes = bInStream.read (inBuff);
                    buff = new byte[inBytes];
                    System.arraycopy(inBuff,0,buff,0,inBytes);
                    cmd = new String(buff);

                    if(cmd != null){
                        Calendar cal = Calendar.getInstance();       //カレンダーを取得
                        int iHour = cal.get(Calendar.HOUR);         //時を取得(0~11)
                        int iMinute = cal.get(Calendar.MINUTE);    //分を取得
                        int iSecond = cal.get(Calendar.SECOND);    //分を取得
                        int iAM_PM = cal.get(Calendar.AM_PM);       //曜日を取得
                        int nowTime = iHour * 3600 + iMinute * 60 + iSecond;  //時間を秒に変換(12時間まで)
                        readS = cmd;

                        if(flag){   //退室時処理
                            flag = false;
                            ExitTime();
                            //RecordAttendance();
                            int jikan = RecordAttendance2();

                            hanntei imaitu = new hanntei();
                            int koma = imaitu.set();


                            String sendMessage = koma+ "," +mNUMBER + "," + jikan;
                            try {
                                bOStream.write(sendMessage.getBytes());
                            }catch (IOException e){
                                e.printStackTrace();
                            }
                        }else{      // 入室時処理
                            flag = true;
                            imakita = true;
                            //hanntei Hantei = new hanntei();
                            //int tikokuTime = Hantei.tikoku();
                            //if(tikokuTime == -1) EnterTime(tikokuTime);     //遅刻ならtikokutimeにして記録
                            /*else*/  EnterTime(nowTime);                        //ちゃんと来た人は今の時間を記録

                            /*
                            String sendMessage = "enter"+ mNUMBER +","+ iAM_PM +","+ nowTime;
                            try {
                                bOStream.write(sendMessage.getBytes());
                            }catch (IOException e){
                                e.printStackTrace();
                            }

                             */
                        }

                        try {
                            Thread.sleep(1000*60* CYCLE);    //正常に出席登録できたらCYCLE分間通信禁止
                            break;
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                    }


                    currentState = nextState;
                }

                state += "\n send to target";
                check = "finish";


            } catch (IOException e) {
                e.printStackTrace();
                state += "\nsending failed";
            }

            //if(check=="finish") disconnect();

        }

        //ストリームの値を読み出して　Buffer　に格納
        public int read_loop(){
            int getNum = 0;
            try {
                getNum = bInStream.read(Buffer);
            }catch (IOException e){
                e.printStackTrace();
            }
            return getNum;
        }

        //numをByte[]に変換して　ストリームに書き込む
        public void write_loop(int num){
            try {
                bOStream.write((byte)num);
            }catch (IOException e){
                e.printStackTrace();
            }
        }

    }

    //接続終了 初期化
    public void disconnect(){
        bAdapter.cancelDiscovery();
        streamRunning = false;
        state = null;
        check = "disconnect";
        readS = null;
        connectionPhase = "disconnect";
        try {
            //bServerSocket.close(); bServerSocket=null;
            if(bInStream!=null) {
                bInStream.close();
                bInStream = null;
            }
            if(bOStream!=null) {
                bOStream.close();
                bOStream = null;
            }
            if(bSocket!=null) {
                bSocket.close();
                bSocket = null;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void ChangeReg(String s){
        final TextView T_regist = findViewById(R.id.registText);
        T_regist.setText(s);
    }

    public void ChangeState(String s){
        final TextView T_state = findViewById(R.id.state);
        T_state.setText(s);
    }

    public void ChangeRead(String s){
        final TextView T_read = findViewById(R.id.ReadText);
        T_read.setText(s);
    }

    public void ChangeSyusseki(String s){
        TextView syusseki = findViewById(R.id.syusseki);
        syusseki.setText(s);
    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // RSSI値読み出し
                if(device.getName() != null) {
                    if(device.getName().equals(PCName)) {
                        int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                        mResult = "Device : " + device.getName() + " RSSI=" + rssi + "\n";
                        Log.d(TAG, "deviceFind!!!!!!!*****************************************************************\n" + mResult + "\n");
                        scanedRSSI = rssi;
                    }
                }
            }
        }
    };


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^




        public void EnterTime(int nowTime) {
            //入室時間を端末に保存
            //SharedPreferences pref = getPreferences(MODE_PRIVATE);
            SharedPreferences pref = getSharedPreferences(AppFileName,MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("EnterTime", nowTime);
            editor.commit();

        }


        public void ExitTime() {
            Calendar cal = Calendar.getInstance();       //カレンダーを取得
            int iHour = cal.get(Calendar.HOUR);         //時を取得(0~11)
            int iMinute = cal.get(Calendar.MINUTE);    //分を取得
            int iSecond = cal.get(Calendar.SECOND);    //分を取得
            int iSum_Time = iHour * 3600 + iMinute * 60 + iSecond;  //時間を秒に変換(12時間まで)

            //退室時間を端末に保存
            //SharedPreferences pref = getPreferences(MODE_PRIVATE);
            SharedPreferences pref = getSharedPreferences(AppFileName,MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("ExitTime", iSum_Time);
            editor.commit();

        }

        //実際に出席を記録するのはここだけ　滞在時間で判定  反復横跳びの人は無反応（周期）
        public int RecordAttendance2(){
            //SharedPreferences pref = getPreferences(MODE_PRIVATE);
            SharedPreferences pref = getSharedPreferences(AppFileName,MODE_PRIVATE);
            int inT = pref.getInt("EnterTime",-1);
            int outT = pref.getInt("ExitTime",-1);
            Calendar cal = Calendar.getInstance();       //カレンダーを取得
            int iDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);   //(1~7  sun~sat)
            int iAM_PM = cal.get(Calendar.AM_PM);       //曜日を取得

            int FullTime = 90*60;
            int TikokuTime = FullTime - 10*60;
            int KessekiTime = FullTime - 30*60;
            int taizai = outT - inT;        //滞在時間
            if(taizai < HANPUKU) return 0;
            if(taizai > FullTime ) taizai = FullTime;

            hanntei imaitu = new hanntei();
            int koma = imaitu.set();        //何曜何限か

            int jikann = 2;
            if( taizai > TikokuTime) jikann = 0;
            else if(taizai > KessekiTime ) jikann = 1;

            switch (koma){
                case 11:  Monday1[jikann]++;  break;
                case 12:  Monday2[jikann]++;  break;
                case 13:  Monday3[jikann]++;  break;
                case 14:  Monday4[jikann]++;  break;
                case 15:  Monday5[jikann]++;  break;

                case 21:  Tuesday1[jikann]++;  break;
                case 22:  Tuesday2[jikann]++;  break;
                case 23:  Tuesday3[jikann]++;  break;
                case 24:  Tuesday4[jikann]++;  break;
                case 25:  Tuesday5[jikann]++;  break;

                case 31:  Wednesday1[jikann]++;  break;
                case 32:  Wednesday2[jikann]++;  break;
                case 33:  Wednesday3[jikann]++;  break;
                case 34:  Wednesday4[jikann]++;  break;
                case 35:  Wednesday5[jikann]++;  break;

                case 41:  Thursday1[jikann]++;  break;
                case 42:  Thursday2[jikann]++;  break;
                case 43:  Thursday3[jikann]++;  break;
                case 44:  Thursday4[jikann]++;  break;
                case 45:  Thursday5[jikann]++;  break;

                case 51:  Friday1[jikann]++;  break;
                case 52:  Friday2[jikann]++;  break;
                case 53:  Friday3[jikann]++;  break;
                case 54:  Friday4[jikann]++;  break;
                case 55:  Friday5[jikann]++;  break;

            }

            return taizai;

        }

        //実際に出席を記録するのはここだけ 入退室の時間で判定
        public void RecordAttendance(){
            //SharedPreferences pref = getPreferences(MODE_PRIVATE);
            SharedPreferences pref = getSharedPreferences(AppFileName,MODE_PRIVATE);
            int inT = pref.getInt("EnterTime",-1);
            int outT = pref.getInt("ExitTime",-1);
            Calendar cal = Calendar.getInstance();       //カレンダーを取得
            int iDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);   //(1~7  sun~sat)
            int iAM_PM = cal.get(Calendar.AM_PM);       //曜日を取得

            if (iDayOfWeek == 2) {   //月曜
                if (iAM_PM == 0 && 37200 <= outT && inT <= 31800) {//8時50分~10時20分
                    Monday1[0]++; }
                if (iAM_PM == 0 && 43200 <= outT && inT <= 37800) {//10時30分~12時
                    Monday2[0]++; }
                if (iAM_PM == 1 && 9000 <= outT && inT <= 3600) {//13時~14時30分
                    Monday3[0]++; }
                if (iAM_PM == 1 && 15000 <= outT && inT <= 9600) {//14時40分~16時10分
                    Monday4[0]++; }
                if (iAM_PM == 1 && 21000 <= outT && inT <= 15600) {//16時20分~17時50分
                    Monday5[0]++; }

            } else if (iDayOfWeek == 3) {   //火曜

                if (iAM_PM == 0 && 37200 <= outT && inT <= 31800) {//8時50分~10時20分
                    Tuesday1[0]++; }
                if (iAM_PM == 0 && 43200 <= outT && inT <= 37800) {//10時30分~12時
                    Tuesday2[0]++; }
                if (iAM_PM == 1 && 9000 <= outT && inT <= 3600) {//13時~14時30分
                    Tuesday3[0]++; }
                if (iAM_PM == 1 && 15000 <= outT && inT <= 9600) {//14時40分~16時10分
                    Tuesday4[0]++; }
                if (iAM_PM == 1 && 21000 <= outT && inT <= 15600) {//16時20分~17時50分
                    Tuesday5[0]++; }

            } else if (iDayOfWeek == 4) {   //水曜

                if (iAM_PM == 0 && 37200 <= outT && inT <= 31800) {//8時50分~10時20分
                    Wednesday1[0]++; }
                if (iAM_PM == 0 && 43200 <= outT && inT <= 37800) {//10時30分~12時
                    Wednesday2[0]++; }
                if (iAM_PM == 1 && 9000 <= outT && inT <= 3600) {//13時~14時30分
                    Wednesday3[0]++; }
                if (iAM_PM == 1 && 15000 <= outT && inT <= 9600) {//14時40分~16時10分
                    Wednesday4[0]++; }
                if (iAM_PM == 1 && 21000 <= outT && inT <= 15600) {//16時20分~17時50分
                    Wednesday5[0]++; }

            } else if (iDayOfWeek == 5) {   //木曜

                if (iAM_PM == 0 && 37200 <= outT && inT <= 31800) {//8時50分~10時20分
                    Thursday1[0]++; }
                if (iAM_PM == 0 && 43200 <= outT && inT <= 37800) {//10時30分~12時
                    Thursday2[0]++; }
                if (iAM_PM == 1 && 9000 <= outT && inT <= 3600) {//13時~14時30分
                    Thursday3[0]++; }
                if (iAM_PM == 1 && 15000 <= outT && inT <= 9600) {//14時40分~16時10分
                    Thursday4[0]++; }
                if (iAM_PM == 1 && 21000 <= outT && inT <= 15600) {//16時20分~17時50分
                    Thursday5[0]++; }

            } else if (iDayOfWeek == 6) {   //金曜

                if (iAM_PM == 0 && 37200 <= outT && inT <= 31800) {//8時50分~10時20分
                    Friday1[0]++; }
                if (iAM_PM == 0 && 43200 <= outT && inT <= 37800) {//10時30分~12時
                    Friday2[0]++; }
                if (iAM_PM == 1 && 9000 <= outT && inT <= 3600) {//13時~14時30分
                    Friday3[0]++; }
                if (iAM_PM == 1 && 15000 <= outT && inT <= 9600) {//14時40分~16時10分
                    Friday4[0]++; }
                if (iAM_PM == 1 && 21000 <= outT && inT <= 15600) {//16時20分~17時50分
                    Friday5[0]++; }
            }
        }

        public void pref_set(){ //Monday1~Friday5まで保存 int[] a,b,c -> string "a,b,c"

            //SharedPreferences pref = getPreferences(MODE_PRIVATE);
            SharedPreferences pref = getSharedPreferences(AppFileName,MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();

            int i;

            String Mo1 = String.valueOf(Monday1[0]);
            String Mo2 = String.valueOf(Monday2[0]);
            String Mo3 = String.valueOf(Monday3[0]);
            String Mo4 = String.valueOf(Monday4[0]);
            String Mo5 = String.valueOf(Monday5[0]);
            String Tu1 = String.valueOf(Tuesday1[0]);
            String Tu2 = String.valueOf(Tuesday2[0]);
            String Tu3 = String.valueOf(Tuesday3[0]);
            String Tu4 = String.valueOf(Tuesday4[0]);
            String Tu5 = String.valueOf(Tuesday5[0]);
            String We1 = String.valueOf(Wednesday1[0]);
            String We2 = String.valueOf(Wednesday2[0]);
            String We3 = String.valueOf(Wednesday3[0]);
            String We4 = String.valueOf(Wednesday4[0]);
            String We5 = String.valueOf(Wednesday5[0]);
            String Th1 = String.valueOf(Thursday1[0]);
            String Th2 = String.valueOf(Thursday2[0]);
            String Th3 = String.valueOf(Thursday3[0]);
            String Th4 = String.valueOf(Thursday4[0]);
            String Th5 = String.valueOf(Thursday5[0]);
            String Fr1 = String.valueOf(Friday1[0]);
            String Fr2 = String.valueOf(Friday2[0]);
            String Fr3 = String.valueOf(Friday3[0]);
            String Fr4 = String.valueOf(Friday4[0]);
            String Fr5 = String.valueOf(Friday5[0]);



            //Monday
            for(i=1;i<3;i++){ Mo1 += ","+Monday1[i]; }
            editor.putString("mo1",Mo1); editor.commit();
            for(i=1;i<3;i++){ Mo2 += ","+Monday2[i]; }
            editor.putString("mo2",Mo2); editor.commit();
            for(i=1;i<3;i++){ Mo3 += ","+Monday3[i]; }
            editor.putString("mo3",Mo3); editor.commit();
            for(i=1;i<3;i++){ Mo4 += ","+Monday4[i]; }
            editor.putString("mo4",Mo4); editor.commit();
            for(i=1;i<3;i++){ Mo5 += ","+Monday5[i]; }
            editor.putString("mo5",Mo5); editor.commit();

            //Tuesday
            for(i=1;i<3;i++){ Tu1 += ","+Tuesday1[i]; }
            editor.putString("tu1",Tu1); editor.commit();
            for(i=1;i<3;i++){ Tu2 += ","+Tuesday2[i]; }
            editor.putString("tu2",Tu2); editor.commit();
            for(i=1;i<3;i++){ Tu3 += ","+Tuesday3[i]; }
            editor.putString("tu3",Tu3); editor.commit();
            for(i=1;i<3;i++){ Tu4 += ","+Tuesday4[i]; }
            editor.putString("tu4",Tu4); editor.commit();
            for(i=1;i<3;i++){ Tu5 += ","+Tuesday5[i]; }
            editor.putString("tu5",Tu5); editor.commit();

            //Wednesday
            for(i=1;i<3;i++){ We1 += ","+Wednesday1[i]; }
            editor.putString("we1",We1); editor.commit();
            for(i=1;i<3;i++){ We2 += ","+Wednesday2[i]; }
            editor.putString("we2",We2); editor.commit();
            for(i=1;i<3;i++){ We3 += ","+Wednesday3[i]; }
            editor.putString("we3",We3); editor.commit();
            for(i=1;i<3;i++){ We4 += ","+Wednesday4[i]; }
            editor.putString("we4",We4); editor.commit();
            for(i=1;i<3;i++){ We5 += ","+Wednesday5[i]; }
            editor.putString("we5",We5); editor.commit();

            //Thursday
            for(i=1;i<3;i++){ Th1 += ","+Thursday1[i]; }
            editor.putString("th1",Th1); editor.commit();
            for(i=1;i<3;i++){ Th2 += ","+Thursday2[i]; }
            editor.putString("th2",Th2); editor.commit();
            for(i=1;i<3;i++){ Th3 += ","+Thursday3[i]; }
            editor.putString("th3",Th3); editor.commit();
            for(i=1;i<3;i++){ Th4 += ","+Thursday4[i]; }
            editor.putString("th4",Th4); editor.commit();
            for(i=1;i<3;i++){ Th5 += ","+Thursday5[i]; }
            editor.putString("th5",Th5); editor.commit();

            //Friday
            for(i=1;i<3;i++){ Fr1 += ","+Friday1[i]; }
            editor.putString("fr1",Fr1); editor.commit();
            for(i=1;i<3;i++){ Fr2 += ","+Friday2[i]; }
            editor.putString("fr2",Fr2); editor.commit();
            for(i=1;i<3;i++){ Fr3 += ","+Friday3[i]; }
            editor.putString("fr3",Fr3); editor.commit();
            for(i=1;i<3;i++){ Fr4 += ","+Friday4[i]; }
            editor.putString("fr4",Fr4); editor.commit();
            for(i=1;i<3;i++){ Fr5 += ","+Friday5[i]; }
            editor.putString("fr5",Fr5); editor.commit();

        }

        public void pref_get(){ //保存されたstring "a,b,c"を分解->intに変換してmonday[0] ~[2]へ

            //SharedPreferences pref = getPreferences(MODE_PRIVATE);
            SharedPreferences pref = getSharedPreferences(AppFileName,MODE_PRIVATE);

            int i;
            String[] buff;

            String luSet = "0";                                                     //lu
            for(i=1;i<25;i++) luSet += ",0";
            buff = pref.getString("lu",luSet).split(",",0);
            for(i=0;i<25;i++)  lastUpdate[i] = Integer.parseInt(buff[i]);

            buff = pref.getString("mo1","0,0,0,").split(",", 0);    //mo1
            for(i=0;i<3;i++){ Monday1[i] = Integer.parseInt(buff[i]);}
            buff = pref.getString("mo2","0,0,0,").split(",", 0);    //mo2
            for(i=0;i<3;i++){ Monday2[i] = Integer.parseInt(buff[i]);}
            buff = pref.getString("mo3","0,0,0,").split(",", 0);    //mo3
            for(i=0;i<3;i++){ Monday3[i] = Integer.parseInt(buff[i]);}
            buff = pref.getString("mo4","0,0,0,").split(",", 0);    //mo4
            for(i=0;i<3;i++){ Monday4[i] = Integer.parseInt(buff[i]);}
            buff = pref.getString("mo5","0,0,0,").split(",", 0);    //mo5
            for(i=0;i<3;i++){ Monday5[i] = Integer.parseInt(buff[i]);}

            buff = pref.getString("tu1","0,0,0,").split(",", 0);    //tu1
            for(i=0;i<3;i++){ Tuesday1[i] = Integer.parseInt(buff[i]);}
            buff = pref.getString("tu2","0,0,0,").split(",", 0);    //tu2
            for(i=0;i<3;i++){ Tuesday2[i] = Integer.parseInt(buff[i]);}
            buff = pref.getString("tu3","0,0,0,").split(",", 0);    //tu3
            for(i=0;i<3;i++){ Tuesday3[i] = Integer.parseInt(buff[i]);}
            buff = pref.getString("tu4","0,0,0,").split(",", 0);    //tu4
            for(i=0;i<3;i++){ Tuesday4[i] = Integer.parseInt(buff[i]);}
            buff = pref.getString("tu5","0,0,0,").split(",", 0);    //tu5
            for(i=0;i<3;i++){ Tuesday5[i] = Integer.parseInt(buff[i]);}

            buff = pref.getString("we1","0,0,0,").split(",", 0);    //we1
            for(i=0;i<3;i++){ Wednesday1[i] = Integer.parseInt(buff[i]);}
            buff = pref.getString("we2","0,0,0,").split(",", 0);    //we2
            for(i=0;i<3;i++){ Wednesday2[i] = Integer.parseInt(buff[i]);}
            buff = pref.getString("we3","0,0,0,").split(",", 0);    //we3
            for(i=0;i<3;i++){ Wednesday3[i] = Integer.parseInt(buff[i]);}
            buff = pref.getString("we4","0,0,0,").split(",", 0);    //we4
            for(i=0;i<3;i++){ Wednesday4[i] = Integer.parseInt(buff[i]);}
            buff = pref.getString("we5","0,0,0,").split(",", 0);    //we5
            for(i=0;i<3;i++){ Wednesday5[i] = Integer.parseInt(buff[i]);}

            buff = pref.getString("th1","0,0,0,").split(",", 0);    //th1
            for(i=0;i<3;i++){ Thursday1[i] = Integer.parseInt(buff[i]);}
            buff = pref.getString("th2","0,0,0,").split(",", 0);    //th2
            for(i=0;i<3;i++){ Thursday2[i] = Integer.parseInt(buff[i]);}
            buff = pref.getString("th3","0,0,0,").split(",", 0);    //th3
            for(i=0;i<3;i++){ Thursday3[i] = Integer.parseInt(buff[i]);}
            buff = pref.getString("th4","0,0,0,").split(",", 0);    //th4
            for(i=0;i<3;i++){ Thursday4[i] = Integer.parseInt(buff[i]);}
            buff = pref.getString("th5","0,0,0,").split(",", 0);    //th5
            for(i=0;i<3;i++){ Thursday5[i] = Integer.parseInt(buff[i]);}

            buff = pref.getString("fr1","0,0,0,").split(",", 0);    //fr1
            for(i=0;i<3;i++){ Friday1[i] = Integer.parseInt(buff[i]);}
            buff = pref.getString("fr2","0,0,0,").split(",", 0);    //fr2
            for(i=0;i<3;i++){ Friday2[i] = Integer.parseInt(buff[i]);}
            buff = pref.getString("fr3","0,0,0,").split(",", 0);    //fr3
            for(i=0;i<3;i++){ Friday3[i] = Integer.parseInt(buff[i]);}
            buff = pref.getString("fr4","0,0,0,").split(",", 0);    //fr4
            for(i=0;i<3;i++){ Friday4[i] = Integer.parseInt(buff[i]);}
            buff = pref.getString("fr5","0,0,0,").split(",", 0);    //fr5
            for(i=0;i<3;i++){ Friday5[i] = Integer.parseInt(buff[i]);}

        }


    //preferenceからkomamaneを読みだすメソッド
    public String[] getArray(String PrefKey){
        //SharedPreferences prefs2 = getSharedPreferences("Array", Context.MODE_PRIVATE);
        SharedPreferences pref = getSharedPreferences(AppFileName,MODE_PRIVATE);

        String defArray[] = new String[25];         //初期起動用　初期値全要素を未登録に
        Arrays.fill(defArray,"未登録");
        String defVal = "未登録";
        int i;
        for(i=1;i<25;i++) {
            String str = "," + defArray[i];
            defVal += str;
        }

        String stringItem = pref.getString(PrefKey,defVal);
        if(stringItem != null && stringItem.length() != 0){
            return stringItem.split(",");
        }else{
            return null;
        }
    }


    //<static変数群---------------------------------------------------------------------

    //出席(=0)・欠席(=1)・遅刻(=2)カウント　　サーバーからデータ取得？
    static int Monday1[] = new int[3];
    static int Monday2[] = new int[3];
    static int Monday3[] = new int[3];
    static int Monday4[] = new int[3];
    static int Monday5[] = new int[3];

    static int Tuesday1[] = new int[3];
    static int Tuesday2[] = new int[3];
    static int Tuesday3[] = new int[3];
    static int Tuesday4[] = new int[3];
    static int Tuesday5[] = new int[3];

    static int Wednesday1[] = new int[3];
    static int Wednesday2[] = new int[3];
    static int Wednesday3[] = new int[3];
    static int Wednesday4[] = new int[3];
    static int Wednesday5[] = new int[3];

    static int Thursday1[] = new int[3];
    static int Thursday2[] = new int[3];
    static int Thursday3[] = new int[3];
    static int Thursday4[] = new int[3];
    static int Thursday5[] = new int[3];

    static int Friday1[] = new int[3];
    static int Friday2[] = new int[3];
    static int Friday3[] = new int[3];
    static int Friday4[] = new int[3];
    static int Friday5[] = new int[3];

    static String KomaNames[] = new String[25];     //月0~4 火5~9 水10~14 木15~19 金20~24

    //-----------------------------------------------------------------------static変数群>

}


