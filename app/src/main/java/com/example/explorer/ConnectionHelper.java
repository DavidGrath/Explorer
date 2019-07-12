package com.example.explorer;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;

import java.io.File;
import java.util.List;

public class ConnectionHelper {

    static BluetoothAdapter bluetoothAdapter;
    public static Activity activity;
    public static Context context = activity;

    public static void share(File file, final int CONST) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!bluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(intent, CONST);
        }
    }
}
