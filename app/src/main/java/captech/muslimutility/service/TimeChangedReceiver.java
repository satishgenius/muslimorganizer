package captech.muslimutility.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import captech.muslimutility.utility.MindtrackLog;


public class TimeChangedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        MindtrackLog.add("Time Change");
        context.startService(new Intent(context, PrayingDayCalculateHandler.class));
    }

}