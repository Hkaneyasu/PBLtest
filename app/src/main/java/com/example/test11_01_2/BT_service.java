package com.example.test11_01_2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


//バックグラウンドでbluetooth接続と待機
//ONリクエストはフォアグラウンドで実行する
public class BT_service extends Service {

    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String DeviceName2 ="arduino_test";
    private static final String DeviceName1 = "Galaxy S7 edge";
    private static final String PCName = "DESKTOP-39D7KJ3";
    private final String mNUMBER = "17108039";
    public final String AppFileName = "PBLservice";
    public BluetoothServerSocket bServerSocket = null;
    public BluetoothAdapter bAdapter = null;
    public BluetoothSocket bSocket = null;
    public BluetoothDevice bDevice = null;
    public String check;
    public InputStream bInStream;
    public OutputStream bOStream;

    public boolean streamRunning = false;
    public boolean flag;    //フラグ管理はバックグラウンドのみで行う
    public int minRSSI;

    public Timer btTimer = null;
    Handler btHandler = new Handler();

    private Handler h;
    private Runnable r;

    public String ServiceState = "接続待機中";

    public int lastUpdate[] = new int[25];      //最終更新日
    public int tf[] = new int[25];      //来たか来なかったか true=>1 flase=>0
    public int lastDate;
    static String KomaNames[] = new String[25];     //月0~4 火5~9 水10~14 木15~19 金20~24

    private int CYCLE = 1/3;     //何分
    public int HANPUKU = 5*1;      //何秒


    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this,"BTservice was created",Toast.LENGTH_LONG).show();

        bAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //startService(new Intent(BT_service.this,BackGroundService.class));

        Toast.makeText(this,"BTservice started",Toast.LENGTH_LONG).show();
        startForeground(101, updateNotification());

        SharedPreferences pref = getSharedPreferences(AppFileName,MODE_PRIVATE);
        flag = pref.getBoolean("FLAG",false);   //キー,デフォルト
        array_get();
        tfReset();

        //RSSI scan　
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        BTstart();

            h = new Handler();
            r = new Runnable() {
                @Override
                public void run() {
                    startForeground(101, updateNotification());
                    h.postDelayed(this, 1000);
                }
            };
            h.post(r);

        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        pref_set();
        disconnect();

        //stopService(new Intent(BT_service.this,BackGroundService.class));
        Toast.makeText(this,"BTservice finished",Toast.LENGTH_LONG).show();

        stopForeground(true);
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    public void BTstart(){
        bAdapter.cancelDiscovery();
        if (bAdapter.isEnabled()) {
            if (device_is_find(bAdapter)) {
                Open_BTserver btserver = new Open_BTserver();
                btserver.start();
            }
        }
    }

    //マイコンに接続時にデバイス取得
    public boolean device_is_find( BluetoothAdapter bAdapter){
        Set<BluetoothDevice> devices = bAdapter.getBondedDevices();
        for(BluetoothDevice device : devices){
            if(device.getName().equals(DeviceName1)){
                bDevice = device;
                return true;
            }
        }
        return false;
    }

    public class Open_BTserver extends Thread{
        public void run(){
            streamRunning = false;
            if(MainActivity.escape) return;         //escape信号はメイン起動時しか発信できない
            check = "start";
            MainActivity.scanedRSSI = -100;

            ServiceState = "Serverstart";
            while (!scanRSSI(-60)) {    //RSSIが規定値以上ならtrue
                if(check.equals("disconnect")) break;
                if(MainActivity.escape) return;
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            minRSSI = MainActivity.scanedRSSI;
            ServiceState = "phase1 OK";

/*
            while (!RSSIunder(minRSSI - 10)){   //RSSIが規定値以下に落ちたら
                if(check == "disconnect") break;
                if(MainActivity.escape) return;
                try{
                    Thread.sleep(300);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            ServiceState = "phase2 OK";

 */


            //↓↓デバイス通過を確認↓↓
            while (true){               //2度目のconnect
                if(check.equals("disconnect") ) break;
                if(MainActivity.escape) return;
                try {
                    bSocket = bDevice.createRfcommSocketToServiceRecord(uuid);
                    bSocket.connect();
                    check = "connectionOK";

                    if (bSocket != null && check.equals("connectionOK")) {    //ソケットが受け取れたら
                        //ストリーム処理開始
                        BT_stream bStream = new BT_stream();
                        bStream.start();
                    }
                    break;
                }catch (IOException e){
                    e.printStackTrace();
                    try {
                        Thread.sleep(1000);     //connectが失敗したら1秒後にもう一度connect
                    }catch (InterruptedException w){
                        w.printStackTrace();
                    }
                }
            }

            while (true){
                if(check.equals("finish")) break;
                try {
                    Thread.sleep(300);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }

            disconnect();
            BTstart();
        }
    }

    public class  BT_stream extends Thread{
        public void run(){
            byte[] inBuff = new byte[64];
            streamRunning = true;
            check = "streamStart";
            ServiceState = "StreamStart";

            try {
                bInStream = bSocket.getInputStream();
                bOStream = bSocket.getOutputStream();

                int inBytes;
                byte[] buff;
                String cmd;

                bOStream.write("012".getBytes());

                while (true){   //割り込みがかかるまで通信
                    if(Thread.interrupted()) break;
                    //bOStream.write("012".getBytes());
                    //bOStream.write(011);

                    inBytes = bInStream.read (inBuff);
                    buff = new byte[inBytes];
                    System.arraycopy(inBuff,0,buff,0,inBytes);
                    cmd = new String(buff);

                    if(cmd.length() > 0){
                        Calendar cal = Calendar.getInstance();       //カレンダーを取得
                        int iHour = cal.get(Calendar.HOUR);         //時を取得(0~11)
                        int iMinute = cal.get(Calendar.MINUTE);    //分を取得
                        int iSecond = cal.get(Calendar.SECOND);    //分を取得
                        int nowTime = iHour * 3600 + iMinute * 60 + iSecond;  //時間を秒に変換(12時間まで)

                        Log.d("BluetoothService","メッセージを受信");
                        if(flag){   //退室時処理
                            flag = false;
                            saveFlag();
                            ExitTime();
                            int jikan = RecordAttendance2();
                            hanntei imaitu = new hanntei();
                            int koma = imaitu.set();

                            String sendMessage = koma+ "," +mNUMBER + "," + jikan;
                            try {
                                bOStream.write(sendMessage.getBytes());
                            }catch (IOException e){
                                e.printStackTrace();
                            }
                        }else {      // 入室時処理
                            flag = true;
                            saveFlag();
                            EnterTime(nowTime);
                            String sendMessage = "入室しました";
                            try {
                                bOStream.write(sendMessage.getBytes());
                            }catch (IOException e){
                                e.printStackTrace();
                            }
                        }
                        try {
                            Thread.sleep(1000*60* CYCLE);    //正常に出席登録できたらCYCLE分間通信禁止
                            break;
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                }
                check = "finish";
                ServiceState = "finish";

            } catch (IOException e) {
                e.printStackTrace();
            }
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
        if(MainActivity.scanedRSSI < n){
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
        if(MainActivity.scanedRSSI < n){
            bAdapter.cancelDiscovery();
            return true;
        }else {
            bAdapter.cancelDiscovery();
            return false;
        }
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
                        MainActivity.scanedRSSI = rssi;
                    }
                }
            }
        }
    };

    public void EnterTime(int nowTime) {
        //入室時間を端末に保存
        SharedPreferences pref = getSharedPreferences(AppFileName,MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("EnterTime", nowTime);
        editor.apply();

    }

    public void ExitTime() {
        Calendar cal = Calendar.getInstance();       //カレンダーを取得
        int iHour = cal.get(Calendar.HOUR);         //時を取得(0~11)
        int iMinute = cal.get(Calendar.MINUTE);    //分を取得
        int iSecond = cal.get(Calendar.SECOND);    //分を取得
        int iSum_Time = iHour * 3600 + iMinute * 60 + iSecond;  //時間を秒に変換(12時間まで)

        //退室時間を端末に保存
        SharedPreferences pref = getSharedPreferences(AppFileName,MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("ExitTime", iSum_Time);
        editor.apply();
    }

    //実際に出席を記録するのはここだけ　滞在時間で判定  反復横跳びの人は無反応（周期）こっちは変更したら保存
    //科目名未登録＝＞記録されない
    public int RecordAttendance2(){
        //SharedPreferences pref = getPreferences(MODE_PRIVATE);
        SharedPreferences pref = getSharedPreferences(AppFileName,MODE_PRIVATE);
        int inT = pref.getInt("EnterTime",-1);
        int outT = pref.getInt("ExitTime",-1);
        Calendar cal = Calendar.getInstance();       //カレンダーを取得

        int FullTime = 90*60;
        int TikokuTime = FullTime - 10*60;
        int KessekiTime = FullTime - 30*60;
        int taizai = outT - inT;        //滞在時間
        if(taizai < HANPUKU) return 0;                              //HANPUKU秒未満の人は無視
        if(taizai > FullTime ) taizai = FullTime;

        hanntei imaitu = new hanntei();
        int koma = imaitu.TimeToKoma((outT + inT)/2);        //何曜何限か

        int jikann = 2;
        if( taizai > TikokuTime) jikann = 0;
        else if(taizai > KessekiTime ) jikann = 1;

        //if(!is_allowedUpdate()) { Log.d("BluetooethService","更新が許可されていません"); koma = -1;}       //更新許可が出ない＝＞プロセス終了
        else Log.d("BluetoothService","更新が許可されました");

        int cKoma = imaitu.convKoma(koma);          //ちょっと時間すぎて出た人の対応
        if(cKoma == 25){
            int iHour = cal.get(Calendar.HOUR);         //時を取得(0~11)
            int iMinute = cal.get(Calendar.MINUTE);    //分を取得
            int iSecond = cal.get(Calendar.SECOND);    //分を取得
            int iSum_Time = iHour * 3600 + iMinute * 60 + iSecond;  //時間を秒に変換(12時間まで)
            int nowT = imaitu.TimeToKoma(iSum_Time + 10*60);
            cKoma = imaitu.convKoma(nowT) -1 ;
        }

        KomaNames = getArray("KomaNames");  //メインのKomaNamesを取得
        if(KomaNames[cKoma].equals("未登録")){  Log.d("BluetoothService","科目名未登録で記録されません"); koma = -1;}     //科目名未登録なら記録しない
        else Log.d("BluetoothService","科目名登録済み　データ記録");

        tf[cKoma] = 1;                              //この時間のtfをtrueに
        tfKesseki(cKoma);                                //必要なところは欠席として記録

        switch (koma){      //koma=-1に設定すれば記録無し
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

            default: break;
        }

        pref_set();
        return taizai;

    }

    //trueの前にfalseがあったら欠席にしてtrueに
    public void tfKesseki(int cKoma){
        int i,j;
        int d[] = new int[5];                         //forで入ってほしくないところリスト
        if(0<=cKoma && cKoma<=4) for(i=0; i<=4 ; i++) d[i] = i;             //何曜日か
        if(5<=cKoma && cKoma<=9) for(i=5; i<=9 ; i++) d[i-5] = i;
        if(10<=cKoma && cKoma<=14) for(i=10; i<=14 ; i++) d[i-10] = i;
        if(15<=cKoma && cKoma<=19) for(i=15; i<=19 ; i++) d[i-15] = i;
        if(20<=cKoma && cKoma<=24) for(i=20; i<=24 ; i++) d[i-20] = i;


        for(i=0;i<25;i++){
            for(j=0;j<5;j++){
                if(d[j] == i) i++;
            }
            if(i > 24 ) break;
            if(tf[i] == 0){
                incrementKesseki(i);        //欠席++
                tf[i] = 1;
            }
        }
    }

    public void incrementKesseki(int cKoma){
        switch (cKoma){
            case 0:  Monday1[2]++;  break;
            case 1:  Monday2[2]++;  break;
            case 2:  Monday3[2]++;  break;
            case 3:  Monday4[2]++;  break;
            case 4:  Monday5[2]++;  break;

            case 5:  Tuesday1[2]++;  break;
            case 6:  Tuesday2[2]++;  break;
            case 7:  Tuesday3[2]++;  break;
            case 8:  Tuesday4[2]++;  break;
            case 9:  Tuesday5[2]++;  break;

            case 10:  Wednesday1[2]++;  break;
            case 11:  Wednesday2[2]++;  break;
            case 12:  Wednesday3[2]++;  break;
            case 13:  Wednesday4[2]++;  break;
            case 14:  Wednesday5[2]++;  break;

            case 15:  Thursday1[2]++;  break;
            case 16:  Thursday2[2]++;  break;
            case 17:  Thursday3[2]++;  break;
            case 18:  Thursday4[2]++;  break;
            case 19:  Thursday5[2]++;  break;

            case 20:  Friday1[2]++;  break;
            case 21:  Friday2[2]++;  break;
            case 22:  Friday3[2]++;  break;
            case 23:  Friday4[2]++;  break;
            case 24:  Friday5[2]++;  break;
        }
    }

    //曜日変更後最初の起動日に実行される　その日のｔｆを全部falseに変更
    public void tfReset(){
        int i;
        Calendar cal = Calendar.getInstance();       //カレンダーを取得
        int iDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);   //(1~7  sun~sat)
        if(iDayOfWeek == lastDate) return;
        else{
            lastDate = iDayOfWeek;          //エラー回避 lastDateを変更しておく
            switch (iDayOfWeek){
                case 2: for(i=0;i<5;i++) tf[i] = 0; break;
                case 3: for(i=5;i<10;i++) tf[i] = 0; break;
                case 4: for(i=10;i<15;i++) tf[i] = 0; break;
                case 5: for(i=15;i<20;i++) tf[i] = 0; break;
                case 6: for(i=20;i<25;i++) tf[i] = 0; break;
            }
        }
    }

    //Monday1~Friday5まで保存 int[] a,b,c -> string "a,b,c"  lastUpdateも保存
    public void pref_set(){
        Calendar cal = Calendar.getInstance();       //カレンダーを取得
        int iDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);   //(1~7  sun~sat)

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


        //updateArray
        String LU = String.valueOf(lastUpdate[0]);
        for(i=1;i<25;i++){ LU += "," + lastUpdate[i]; }
        editor.putString("lu",LU);

        //tf
        String TF = String.valueOf(tf[0]);
        for(i=1;i<25;i++){ TF += ","+tf[i]; }
        editor.putString("tf",TF);

        //lastDate
        editor.putInt("lastD",iDayOfWeek);


        //Monday
        for(i=1;i<3;i++){ Mo1 += ","+Monday1[i]; }
        editor.putString("mo1",Mo1);
        for(i=1;i<3;i++){ Mo2 += ","+Monday2[i]; }
        editor.putString("mo2",Mo2);
        for(i=1;i<3;i++){ Mo3 += ","+Monday3[i]; }
        editor.putString("mo3",Mo3);
        for(i=1;i<3;i++){ Mo4 += ","+Monday4[i]; }
        editor.putString("mo4",Mo4);
        for(i=1;i<3;i++){ Mo5 += ","+Monday5[i]; }
        editor.putString("mo5",Mo5);

        //Tuesday
        for(i=1;i<3;i++){ Tu1 += ","+Tuesday1[i]; }
        editor.putString("tu1",Tu1);
        for(i=1;i<3;i++){ Tu2 += ","+Tuesday2[i]; }
        editor.putString("tu2",Tu2);
        for(i=1;i<3;i++){ Tu3 += ","+Tuesday3[i]; }
        editor.putString("tu3",Tu3);
        for(i=1;i<3;i++){ Tu4 += ","+Tuesday4[i]; }
        editor.putString("tu4",Tu4);
        for(i=1;i<3;i++){ Tu5 += ","+Tuesday5[i]; }
        editor.putString("tu5",Tu5);

        //Wednesday
        for(i=1;i<3;i++){ We1 += ","+Wednesday1[i]; }
        editor.putString("we1",We1);
        for(i=1;i<3;i++){ We2 += ","+Wednesday2[i]; }
        editor.putString("we2",We2);
        for(i=1;i<3;i++){ We3 += ","+Wednesday3[i]; }
        editor.putString("we3",We3);
        for(i=1;i<3;i++){ We4 += ","+Wednesday4[i]; }
        editor.putString("we4",We4);
        for(i=1;i<3;i++){ We5 += ","+Wednesday5[i]; }
        editor.putString("we5",We5);

        //Thursday
        for(i=1;i<3;i++){ Th1 += ","+Thursday1[i]; }
        editor.putString("th1",Th1);
        for(i=1;i<3;i++){ Th2 += ","+Thursday2[i]; }
        editor.putString("th2",Th2);
        for(i=1;i<3;i++){ Th3 += ","+Thursday3[i]; }
        editor.putString("th3",Th3);
        for(i=1;i<3;i++){ Th4 += ","+Thursday4[i]; }
        editor.putString("th4",Th4);
        for(i=1;i<3;i++){ Th5 += ","+Thursday5[i]; }
        editor.putString("th5",Th5);

        //Friday
        for(i=1;i<3;i++){ Fr1 += ","+Friday1[i]; }
        editor.putString("fr1",Fr1);
        for(i=1;i<3;i++){ Fr2 += ","+Friday2[i]; }
        editor.putString("fr2",Fr2);
        for(i=1;i<3;i++){ Fr3 += ","+Friday3[i]; }
        editor.putString("fr3",Fr3);
        for(i=1;i<3;i++){ Fr4 += ","+Friday4[i]; }
        editor.putString("fr4",Fr4);
        for(i=1;i<3;i++){ Fr5 += ","+Friday5[i]; }
        editor.putString("fr5",Fr5);

        editor.apply();

    }

    public void array_get(){

        SharedPreferences pref = getSharedPreferences(AppFileName,MODE_PRIVATE);

        int i;
        String[] buff;
        String luSet = "0";                                                     //lu
        for(i=1;i<25;i++) luSet += ",0";

        buff = pref.getString("lu",luSet).split(",",0);
        for(i=0;i<25;i++)  lastUpdate[i] = Integer.parseInt(buff[i]);

        String tfSet = "1";                                                     //tf
        for(i=1;i<25;i++) tfSet += ",1";
        buff = pref.getString("tf",tfSet).split(",",0);
        for(i=0;i<25;i++) tf[i] = Integer.parseInt(buff[i]);

        lastDate = pref.getInt("lastD",0);                              //lastD
    }

    //接続終了 初期化
    public void disconnect(){
        bAdapter.cancelDiscovery();
        streamRunning = false;
        check = "disconnect";
        ServiceState = "disconnected";
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

    public void saveFlag(){     //mainでも反映されるようにflag変更したら保存しておく
        SharedPreferences pref = getSharedPreferences(AppFileName,MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("FLAG", flag);
        editor.apply();
    }

    //本日既に変更済みかどうか　変更しても良い＝＞true /  変更済みだから通信ダメ＝＞false
    public boolean is_allowedUpdate(){

        //時刻表示するコードを追加
        Calendar cal = Calendar.getInstance();       //カレンダーを取得
        int iDate = cal.get(Calendar.DATE);         //日を取得
        int iHour = cal.get(Calendar.HOUR);         //時を取得(0~11)
        int iMinute = cal.get(Calendar.MINUTE);    //分を取得
        int iSecond = cal.get(Calendar.SECOND);    //分を取得
        int iSum_Time = iHour * 3600 + iMinute * 60 + iSecond;  //時間を秒に変換(12時間まで)


        hanntei imaitu = new hanntei();
        int nowKoma = imaitu.set();
        int ckoma = imaitu.convKoma(nowKoma);
        if(ckoma == 25) {
            nowKoma = imaitu.TimeToKoma(iSum_Time + 10 * 60);
            ckoma = imaitu.convKoma(nowKoma) - 1;               //退出時に呼ぶ＝＞ちょっと時間すぎてるかも
        }
        if(lastUpdate[ckoma] == iDate) return false;    //本日既に変更済み
        lastUpdate[ckoma] = iDate;
        return true;
    }


    //preferenceからkomamaneを読みだすメソッド
    public String[] getArray(String PrefKey){
        //SharedPreferences prefs2 = getSharedPreferences("Array", Context.MODE_PRIVATE);
        SharedPreferences pref = getSharedPreferences(AppFileName,MODE_PRIVATE);

        String[] defArray = new String[25];         //初期起動用　初期値全要素を未登録に
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

    //ステータスバーに表示
    private Notification updateNotification(){

        Context context = getApplicationContext();
        PendingIntent action = PendingIntent.getActivity(context,
                0, new Intent(context, MainActivity.class),
                PendingIntent.FLAG_CANCEL_CURRENT);


        String CHANNEL_ID = "PBL_channel";
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "PBLChannel",
                NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("PBL channel description");
        manager.createNotificationChannel(channel);
        builder = new NotificationCompat.Builder(this, CHANNEL_ID);

        String info = "接続待機中";

        return builder.setContentIntent(action).
                setContentTitle("PBLservice").
                setTicker("なんぞこれ").
                setContentText(ServiceState + "_flag=" + flag + "_RSSI="+MainActivity.scanedRSSI + "(min;"+ minRSSI +")").
                setSmallIcon(R.drawable.ic_launcher_background).
                setContentIntent(action).setOngoing(true).build();

    }


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


}
