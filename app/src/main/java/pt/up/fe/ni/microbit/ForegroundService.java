package pt.up.fe.ni.microbit;

import android.app.Notification;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.IntDef;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by tiagocv on 19/02/18.
 */

public class ForegroundService extends NotificationListenerService {

    private String bluetooth_device_MAC;
    private BluetoothDevice remoteDevice;
    private UUID uuid;
    private BluetoothSocket socket;
    private BluetoothAdapter adapter;
    private OutputStream outStream;
    private InputStream inStream;
    private static final String TAG = "Microbit_Service";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        bluetooth_device_MAC = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE).getString("bluetooth_device", "");

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        adapter = BluetoothAdapter.getDefaultAdapter();
        if ((adapter == null) || (!adapter.isEnabled())) {
            Toast.makeText(getApplicationContext(), "Microbit: Bluetooth not enabled", Toast.LENGTH_SHORT).show();
            return super.onStartCommand(intent, flags, startId);
        }

        Toast.makeText(getApplicationContext(), "Microbit: Trying to connect to " + bluetooth_device_MAC, Toast.LENGTH_SHORT).show();

        try {
            // Find the remote device
            remoteDevice = adapter.getRemoteDevice(bluetooth_device_MAC);

            uuid = UUID.fromString("ae5a9be0-1972-11e8-b566-0800200c9a66");

            socket = remoteDevice.createRfcommSocketToServiceRecord(uuid);
            Log.d(TAG, "Socket Created.");

            adapter.cancelDiscovery();

            socket.connect();

            // Get input and output streams from the socket
            outStream = socket.getOutputStream();
            inStream = socket.getInputStream();

            Toast.makeText(getApplicationContext(), "Microbit: Successfully connected to " + bluetooth_device_MAC, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Connected successfully to " + bluetooth_device_MAC + ".");

        } catch (Exception e){
            Toast.makeText(getApplicationContext(), "Microbit: Failed to connect to " + bluetooth_device_MAC, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Connection creation failed ");
            socket = null;
        }
        // Make sure Bluetooth adapter is not in discovery mode
        adapter.cancelDiscovery();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d(TAG, "Received Notification");
        if (socket != null) {
            if (getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE).getBoolean(sbn.getPackageName(), false)) {
                Bundle extras = sbn.getNotification().extras;
                Log.d(TAG, "Package: " + sbn.getPackageName());

                try {

                    String s = sbn.getPackageName();
                    // Add the delimiter
                    s += '\n';

                    // Convert to bytes and write
                    outStream.write(s.getBytes());
                    Log.d(TAG, "[SENT] " + s);

                } catch (Exception e) {
                    Log.e(TAG, "Write failed!");
                }
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.d(TAG, "Deleted Notification");
        if (socket != null) {
            Bundle extras = sbn.getNotification().extras;
            Log.d(TAG, "Package: " + sbn.getPackageName());
        }
    }


}
