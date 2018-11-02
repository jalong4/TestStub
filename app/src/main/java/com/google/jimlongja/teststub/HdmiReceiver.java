// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.jimlongja.teststub;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;


public final class HdmiReceiver extends BroadcastReceiver {

    /**
     * Hdmi listener.
     */
    public interface HdmiListener {
        void onHdmiPluggedState(boolean plugged, Intent intent);
    }


    public static final String ACTION_HDMI_PLUGGED = "android.intent.action.HDMI_PLUGGED";
    public static final String EXTRA_HDMI_PLUGGED_STATE = "state";


    private final IntentFilter intentFilter = new IntentFilter();

    private final Context context;
    private final HdmiListener listener;

    private boolean registered = false;
    private boolean hdmiPlugged = false;

    public HdmiReceiver(Context context, HdmiListener listener) {
        this.context = context;
        this.listener = listener;
        this.intentFilter.addAction(ACTION_HDMI_PLUGGED);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (ACTION_HDMI_PLUGGED.equals(action)) {
            hdmiPlugged = intent.getBooleanExtra(EXTRA_HDMI_PLUGGED_STATE, false);
            listener.onHdmiPluggedState(hdmiPlugged, intent);
        }
    }

    public void register() {
        if (!registered) {
            registered = true;
            context.registerReceiver(this, intentFilter);
        }
    }

    public void unregister() {
        if (registered) {
            registered = false;
            hdmiPlugged = false;
            context.unregisterReceiver(this);
        }
    }

    public boolean isHdmiPlugged() {
        return hdmiPlugged;
    }

    static public String dumpIntent(Intent i){

        Bundle bundle = i.getExtras();
        String extras = "";
        if (bundle != null) {
            extras += "Dumping Intent start\n";
            for (String key : bundle.keySet()) {
                extras += "[" + key + "=" + bundle.get(key)+"]\n";
            }
            extras +="Dumping Intent end\n";
        }
        return extras;
    }

}