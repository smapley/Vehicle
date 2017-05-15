package com.smapley.vehicle.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.smapley.vehicle.R;

/**
 * Created by wuzhixiong on 2017/5/2.
 */
public class BluetoothUtils {

    private BluetoothLeScanner bluetoothLeScanner;
    private boolean mScanning = false;
    private Handler mHandler = new Handler();
    // 5秒后停止寻找.
    private static final long SCAN_PERIOD = 10000;

    private Context context;

    private BlueToothCallback blueToothCallback;

    public BluetoothUtils(Context context, BlueToothCallback blueToothCallback) {
        this.context = context;
        this.blueToothCallback = blueToothCallback;
        init();
    }

    private void init() {
        try {
            final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
            if (bluetoothAdapter == null) {
                Toast.makeText(context, R.string.permission_bluetooth, Toast.LENGTH_SHORT).show();
            } else {
                //开启蓝牙
                bluetoothAdapter.enable();
                bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        scanLeDevice(true);
    }

    private void scanLeDevice(final boolean enable) {
        try {
            if (bluetoothLeScanner == null) {
                init();
                Toast.makeText(context, R.string.permission_bluetooth, Toast.LENGTH_SHORT).show();
                return;
            }

            if (enable && !mScanning) {
                mHandler.postDelayed(() -> {
                    mScanning = false;
                    bluetoothLeScanner.stopScan(scanCallback);
                    blueToothCallback.onStop();
                }, SCAN_PERIOD);
                blueToothCallback.before();
                mScanning = true;
                bluetoothLeScanner.startScan(scanCallback);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private ScanCallback scanCallback = new ScanCallback() {
        static final String TAG = "-------------->>";

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            try {
                super.onScanResult(callbackType, result);
                BluetoothDevice device = result.getDevice();
                if (device != null) {
                    ScanRecord record = result.getScanRecord();
                    StringBuilder sb = new StringBuilder("");
                    char[] chars = "0123456789ABCDEF".toCharArray();
                    byte[] bs;
                    if (record != null) {
                        bs = record.getBytes();
                        int bit;
                        for (int i = 0; i < record.getBytes().length; i++) {
                            bit = (bs[i] & 0x0f0) >> 4;
                            sb.append(chars[bit]);
                            bit = bs[i] & 0x0f;
                            sb.append(chars[bit]);
                        }
                        blueToothCallback.onSearch(device.getAddress(), result.getRssi(), sb.toString());
                        Log.d(TAG, "------------------------------------------------" + device.getAddress() + "---" + record.getTxPowerLevel() + "---" + sb.toString());
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public abstract static class BlueToothCallback {
        public abstract void before();

        public abstract void onSearch(String mac, int rssi, String data);

        public abstract void onStop();
    }

}
