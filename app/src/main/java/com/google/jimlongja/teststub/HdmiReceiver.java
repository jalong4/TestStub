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
        void onHdmiPluggedState(boolean plugged, String action, String extras);
    }

    public static final String ACTION_HDMI_MODE_CHANGED     = "droidlogic.intent.action.HDMI_MODE_CHANGED";
    public static final String EXTRA_HDMI_MODE              = "mode";


    private final IntentFilter intentFilter = new IntentFilter(ACTION_HDMI_MODE_CHANGED);

    private final Context context;
    private final HdmiListener listener;

    private boolean registered = false;
    private boolean hdmiPlugged = false;

    public HdmiReceiver(Context context, HdmiListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (ACTION_HDMI_MODE_CHANGED.equals(action)) {
            String newMode = intent.getStringExtra(EXTRA_HDMI_MODE);
            if (newMode.isEmpty()) {
                return;
            }
            hdmiPlugged = newMode.equals("connected");
            hdmiPlugged = false;
            listener.onHdmiPluggedState(hdmiPlugged, action, dumpIntent(intent));
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

    public String dumpIntent(Intent i){

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