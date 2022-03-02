package com.example.gitcommitchecker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    private static PowerManager.WakeLock cpuWakeLock;
    private static WifiManager.WifiLock wifiLock;

    @Override
    public void onReceive(Context context, Intent intent) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiLock = wifiManager.createWifiLock("wifilock");
        wifiLock.setReferenceCounted(true);
        wifiLock.acquire();

        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        cpuWakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK |
                        PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.ON_AFTER_RELEASE, "app:alarm");

        cpuWakeLock.acquire(10*60*1000L /*10 minutes*/);

        Toast.makeText(context, "qwerqwre", Toast.LENGTH_SHORT).show();
        Log.d("qwer", "Alarm On");

        wifiLock.release();
        wifiLock = null;

        cpuWakeLock.release();
        cpuWakeLock = null;
    }
}
