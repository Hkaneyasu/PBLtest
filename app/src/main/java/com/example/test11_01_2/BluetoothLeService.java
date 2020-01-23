package com.example.test11_01_2;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.UUID;

public class BluetoothLeService extends Service {

    private final static String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothManager btManager;
    private BluetoothAdapter btAdapter;
    private String btDeviceAddress;
    private BluetoothGatt btGatt;
    public static String UUID_BLESERiAL_SERVICE = "bd011f22-7d3c-0db6-e441-55873d44ef40";
    public static String UUID_BLESERIAL_RX = "2a750d7d-bd9a-928f-b744-7d5a70cef1f9";
    public static String UUID_BLESERIAL_TX = "0503b819-c75b-ba9b-3641-6a7f338dd9bd";

    public static String CLIENT_CHARACTERISTIC_CONFIG = "000029002-0000-1000-8000-00805f9b34fb";

    public final static String ACTION_GATT_CONNECTED = "com.example.text11_01_2.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.example.text11_01_2.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.text11_01_2.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAIlABLE = "com.example.text11_01_2.ACTION_DATA_AVAIlABLE";
    public final static String EXTRA_DATA = "com.example.text11_01_2.EXTRA_DATA";



    private final BluetoothGattCallback btGattCallback = new BluetoothGattCallback() {

        @Override   //状態変化時のイベント
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            String intentAction;
            if(newState == BluetoothProfile.STATE_CONNECTED){
                intentAction = ACTION_GATT_CONNECTED;
                broadcastUpdata(intentAction);
                Log.i(TAG, "*********Connected to GATT server");
                Log.i(TAG, "********Attempting to start setvice discovery :"+btGatt.discoverServices());
            }else if(newState == BluetoothProfile.STATE_DISCONNECTED){
                intentAction = ACTION_GATT_DISCONNECTED;
                Log.i(TAG, "*********Disconnected from GATT server");
                broadcastUpdata(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            try {
                if(status == BluetoothGatt.GATT_SUCCESS){
                    broadcastUpdata(ACTION_GATT_SERVICES_DISCOVERED);
                    BluetoothGattService btservice = btGatt.getService(UUID.fromString(UUID_BLESERiAL_SERVICE));
                    BluetoothGattCharacteristic characteristic = btservice.getCharacteristic(UUID.fromString(UUID_BLESERIAL_RX));
                    btGatt.setCharacteristicNotification(characteristic, true);
                    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    btGatt.writeDescriptor(descriptor);
                }else{
                    Log.w(TAG, "********onServiceDiscovered received :"+status);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if(status == BluetoothGatt.GATT_SUCCESS){
                broadcastUpdata(ACTION_DATA_AVAIlABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            broadcastUpdata(ACTION_DATA_AVAIlABLE, characteristic);
        }
    };

    private void broadcastUpdata(final String action){
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdata(final String action, final BluetoothGattCharacteristic characteristic){
        final Intent intent = new Intent(action);
        if (UUID.fromString(UUID_BLESERIAL_RX).equals((characteristic.getUuid()))) {
            final byte[] data = characteristic.getValue();
            if(data != null && data.length > 0){
                intent.putExtra(EXTRA_DATA, data);
            }
        }
        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder{
        BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent){
        if(btGatt != null){
            btGatt.close();
            btGatt = null;
        }
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    public boolean initialize(){
        if(btManager == null){
            btManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
            if(btManager == null){
                Log.e(TAG, "********Unable to initialize BluetoothManager");
                return false;
            }
        }

        btAdapter = btManager.getAdapter();
        if(btAdapter == null){
            Log.e(TAG, "*********Unable to obtain a BluetoothAdapter");
            return false;
        }
        return true;
    }

    public boolean connect(final String address){
        if(btAdapter == null || address == null){
            Log.w(TAG, "*******BluetoothAdapter not initialized or unspecified address");
            return false;
        }

        if(btDeviceAddress != null && address.equals(btDeviceAddress) && btGatt != null){
            Log.d(TAG, "******Trying to use an existing btGatt for connection");
            if(btGatt.connect()){
                return true;
            }else{
                return false;
            }
        }

        final BluetoothDevice device = btAdapter.getRemoteDevice(address);
        if(device == null){
            Log.w(TAG, "******Device not found. Unable to connect");
            return false;
        }
        btGatt = device.connectGatt(this, false, btGattCallback);
        Log.d(TAG, "*******Trying to create a new connection");
        btDeviceAddress = address;
        return true;
    }

    public void disconnect(){
        if(btAdapter == null || btGatt == null){
            Log.w(TAG, "******BluetoothAdapter not initialized");
            return;
        }
        btGatt.disconnect();
    }

    public void readCharacteristic(BluetoothGattCharacteristic characteristic){
        if(btAdapter == null || btGatt == null){
            Log.w(TAG, "*******BluetoothAdapter not initialized");
        }
        btGatt.readCharacteristic(characteristic);
    }

    public void sendData(byte[] bytes){
        BluetoothGattService btService = btGatt.getService(UUID.fromString(UUID_BLESERiAL_SERVICE));
        BluetoothGattCharacteristic characteristic = btService.getCharacteristic(UUID.fromString(UUID_BLESERIAL_TX));
        characteristic.setValue(bytes);
        btGatt.writeCharacteristic(characteristic);
    }

}
