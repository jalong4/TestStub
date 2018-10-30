package com.google.jimlongja.teststub;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.AudioDeviceCallback;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.MediaDrm;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static android.text.TextUtils.split;

public class MainActivity extends Activity {

    Button mBtnCallAPI;
    TextView mTextWidth;
    TextView mTextHeight;
    TextView mTextIsUHD;
    TextView mTextRefreshRate;

    Button mBtnReadSysProp;
    TextView mTextSysPropWidth;
    TextView mTextSysPropHeight;

    Button mBtnReadVendorProp;
    TextView mTextVendorPropWidth;
    TextView mTextVendorPropHeight;

    Button mBtnGetDrmInfo;
    TextView mTextMaxHdcpLevel;
    TextView mTextConnectedHdcpLevel;
    TextView mTextMaxSessions;
    TextView mTextConnectedSessions;

    Button mBtnDeepLinkToStorageSettings;
    Button mBtnDeepLinkToAddAccessories;
    Button mBtnLaunchNetflix;

    private HdmiReceiver mHdmiReceiver;

    private AudioManager mAudioManager;
    private AudioDeviceCallback mAudioDeviceCallback;

    Intent mNetflixIntent;

    private static final String TAG = "TestStub";
    private static final String ANDROID_SYSTEM_PROPERTIES_CLASS = "android.os.SystemProperties";
    private static final String SYS_DISPLAY_SIZE = "sys.display-size";
    private static final String VENDOR_DISPLAY_SIZE = "vendor.display-size";
    private static final UUID WIDEVINE_UUID = new UUID(0xEDEF8BA979D64ACEL, 0xA3C827DCD51D21EDL);
    private List<String> hdcpLevels = Arrays.asList("UNKNOWN", "HDCP_NONE", "HDCP_V1", "HDCP_V2", "HDCP_V2_1", "HDCP_V2_2");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnCallAPI = (Button) findViewById(R.id.button_callAPI);
        mTextWidth = (TextView) findViewById(R.id.text_Width);
        mTextHeight = (TextView) findViewById(R.id.text_Height);
        mTextRefreshRate = (TextView) findViewById(R.id.text_RefreshRate);
        mTextIsUHD = (TextView) findViewById(R.id.text_isUHD);

        mBtnReadSysProp = (Button) findViewById(R.id.button_getSysProp);
        mTextSysPropWidth = (TextView) findViewById(R.id.text_sysPropWidth);
        mTextSysPropHeight = (TextView) findViewById(R.id.text_sysPropHeight);

        mBtnReadVendorProp = (Button) findViewById(R.id.button_getVendorProp);
        mTextVendorPropWidth = (TextView) findViewById(R.id.text_vendorPropWidth);
        mTextVendorPropHeight = (TextView) findViewById(R.id.text_vendorPropHeight);

        mBtnGetDrmInfo = (Button) findViewById(R.id.button_getDrmInfo);
        mTextMaxHdcpLevel = (TextView) findViewById(R.id.text_maxHdcpLevel);
        mTextConnectedHdcpLevel = (TextView) findViewById(R.id.text_connectedHdcpLevel);
        mTextMaxSessions = (TextView) findViewById(R.id.text_maxSessions);
        mTextConnectedSessions = (TextView) findViewById(R.id.text_connectedSessions);

        mBtnDeepLinkToStorageSettings = (Button) findViewById(R.id.button_deepLinkToStorageSettings);
        mBtnDeepLinkToAddAccessories = (Button) findViewById(R.id.button_deepLinkToAddAccessory);
        mBtnLaunchNetflix = (Button) findViewById(R.id.button_launchNetflix);
        mNetflixIntent = getPackageManager().getLaunchIntentForPackage("com.netflix.ninja");

        try {
            Drawable logo = getPackageManager().getActivityLogo(mNetflixIntent);
            mBtnLaunchNetflix.setBackground(logo);
            Log.i(TAG, "Got Netflix logo\n");

        } catch(Exception e) {
            throw new Error("Unexpected exception ", e);
        }

        HdmiReceiver.HdmiListener listener = new HdmiReceiver.HdmiListener() {
            @Override
            public void onHdmiPluggedState(boolean plugged, String action, String extras) {
                Log.i(TAG, "Received Action: " + action + "\n");
                Log.i(TAG, plugged ? "HDMI connected" : "HDMI disconnected");
                Log.i(TAG, extras);

            }
        };
        mHdmiReceiver = new HdmiReceiver(this, listener) ;

//        HdmiControlManager hdmiControlManager =
//                (HdmiControlManager) getSystemService(Context.HDMI_CONTROL_SERVICE);
//        if (hdmiControlManager != null) {
//            hdmiControlManager.addHotplugEventListener(new HotplugEventListener() {
//                @Override
//                public void onReceived(HdmiHotplugEvent hdmiHotplugEvent) {
//                    // do something
//                }
//            });
//        }

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        mAudioDeviceCallback = new AudioDeviceCallback() {
            @Override
            public void onAudioDevicesAdded(AudioDeviceInfo[] addedDevices) {
                super.onAudioDevicesAdded(addedDevices);
                Log.i(TAG, "onAudioDevicesAdded hit");
            }

            @Override
            public void onAudioDevicesRemoved(AudioDeviceInfo[] removedDevices) {
                super.onAudioDevicesRemoved(removedDevices);
                Log.i(TAG, "onAudioDevicesRemoved hit");
            }
        };



        mBtnCallAPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callDisplayModeAPI();
                    }
                });
            }
        });


        mBtnReadSysProp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateSystemProperties();
                    }
                });
            }
        });


        mBtnReadVendorProp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateVendorProperties();
                    }
                });
            }
        });

        mBtnGetDrmInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= 28) {
                            getDrmInfo();
                        }
                    }
                });
            }
        });

        mBtnDeepLinkToStorageSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_INTERNAL_STORAGE_SETTINGS), 0);
                    }
                });
            }
        });

        mBtnLaunchNetflix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(mNetflixIntent);
                    }
                });
            }
        });


        callDisplayModeAPI();
        updateSystemProperties();
        updateVendorProperties();

        if (Build.VERSION.SDK_INT >= 28) {
            getDrmInfo();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mHdmiReceiver.register();
    }

    @Override
    protected void onStop() {
        mHdmiReceiver.unregister();
        super.onStop();
    }

    public void callDisplayModeAPI() {
        Display.Mode mode = getMaxDisplayMode();
        mTextWidth.setText("Width\n" + Integer.toString(mode.getPhysicalWidth()));
        mTextHeight.setText("Height\n" + Integer.toString(mode.getPhysicalHeight()));
        mTextRefreshRate.setText("Refresh Rate\n" + Float.toString(mode.getRefreshRate()));
        mTextIsUHD.setText("UHD\n" + (isUHD(mode) ? "Yes" : "No"));
        Log.i(TAG, "Display.Mode API: width=" + Integer.toString(mode.getPhysicalWidth()) + " height=" + Integer.toString(mode.getPhysicalHeight()) + " refresh Rate=" + Float.toString(mode.getRefreshRate()) + "\n");
    }

    public void updateSystemProperties() {
        Point displaySize = getDisplaySizeFromProperties(SYS_DISPLAY_SIZE);
        mTextSysPropWidth.setText("Width\n" + Integer.toString(displaySize.x));
        mTextSysPropHeight.setText("Height\n" + Integer.toString(displaySize.y));
        Log.i(TAG, SYS_DISPLAY_SIZE + ": width=" + Integer.toString(displaySize.x) + " height=" + Integer.toString(displaySize.y) + "\n");
    }

    public void updateVendorProperties() {
        Point displaySize = getDisplaySizeFromProperties(VENDOR_DISPLAY_SIZE);
        mTextVendorPropWidth.setText("Width\n" + Integer.toString(displaySize.x));
        mTextVendorPropHeight.setText("Height\n" + Integer.toString(displaySize.y));
        Log.i(TAG, VENDOR_DISPLAY_SIZE + ": width=" + Integer.toString(displaySize.x) + " height=" + Integer.toString(displaySize.y) + "\n");
    }

    public boolean isUHD(){
        return isUHD(getMaxDisplayMode());
    }

    public boolean isUHD(Display.Mode mode) {
        boolean result = (mode.getPhysicalWidth() >= 3840 && mode.getPhysicalHeight() >= 2160);
        return result;
    }

    public Display.Mode getMaxDisplayMode() {
        return getWindowManager().getDefaultDisplay().getMode();
    }


    public void getDrmInfo() {
        MediaDrm drm = null;

        try {
            drm = new MediaDrm(WIDEVINE_UUID);

            int connectedLevel = drm.getConnectedHdcpLevel();
            int maxLevel = drm.getMaxHdcpLevel();
            int maxSessionCount = drm.getMaxSessionCount();
            int openSessionCount = drm.getOpenSessionCount();

            mTextMaxHdcpLevel.setText("Max HDCP Level\n" + hdcpLevels.get(maxLevel));
            mTextConnectedHdcpLevel.setText("Connected HDCP Level\n" + hdcpLevels.get(connectedLevel));
            mTextMaxSessions.setText("Max Sessions\n" + Integer.toString(maxSessionCount));
            mTextConnectedSessions.setText("Connected Sessions\n" + Integer.toString(openSessionCount));
            Log.i(TAG, "Max HDCP Level: " + hdcpLevels.get(maxLevel) + " Connected HDCP Level: " + hdcpLevels.get(connectedLevel) + "\n");
            Log.i(TAG, "Max Sessions: " + Integer.toString(maxSessionCount) + " Connected Sessions: " + Integer.toString(openSessionCount) + "\n");

        } catch(Exception e) {
            throw new Error("Unexpected exception ", e);
        } finally {
            if (drm != null) {
                drm.close();
            }
        }

    }

    public Point getDisplaySizeFromProperties(String prop) {

        String displaySize = null;

        try {
            Class<?> systemProperties = Class.forName(ANDROID_SYSTEM_PROPERTIES_CLASS);
            Method getMethod = systemProperties.getMethod("get", String.class);
            displaySize = (String) getMethod.invoke(systemProperties, prop);
        } catch (Exception e) {
            Log.e(TAG, "Failed to read " + prop, e);
            return new Point(0, 0);
        }

        // If we managed to read sys.display-size, attempt to parse it.
        if (!TextUtils.isEmpty(displaySize)) {
            try {
                String[] propParts = split(displaySize.trim(), "x");
                if (propParts.length == 2) {
                    int width = Integer.parseInt(propParts[0]);
                    int height = Integer.parseInt(propParts[1]);
                    if (width > 0 && height > 0) {
                        return new Point(width, height);
                    }
                }
            } catch (NumberFormatException e) {
                return new Point(0, 0);
            }
            Log.e(TAG, "Invalid property: " + displaySize);
        }

        return new Point(0, 0);


    }

    private void linkToIntent(String intentName) {
        startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
    }



}
