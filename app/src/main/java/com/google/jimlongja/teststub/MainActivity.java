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
import android.media.MediaDrm.OnKeyStatusChangeListener;
import android.media.MediaDrm.KeyStatus;

import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore.Audio.Media;
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


import static android.content.pm.PackageManager.FEATURE_VERIFIED_BOOT;
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
  TextView mTextDerivedModelGroup;

  Button mBtnGetDrmInfo;
  TextView mTextMaxHdcpLevel;
  TextView mTextConnectedHdcpLevel;
  TextView mTextMaxSessions;
  TextView mTextConnectedSessions;
  TextView mTextWidevineSystemId;

  Button mBtnHasVerifiedBoot;
  TextView mTextHasVerifiedBoot;
  Button mBtnDeepLinkToAddAccessories;
  Button mBtnLaunchNetflix;

  private HdmiReceiver mHdmiReceiver;

  private AudioManager mAudioManager;
  private AudioDeviceCallback mAudioDeviceCallback;

  private MediaDrm mMediaDrm = null;

  Intent mNetflixIntent;

  private static final String TAG = "TestStub";
  private static final String ANDROID_SYSTEM_PROPERTIES_CLASS = "android.os.SystemProperties";
  private static final String SYS_DISPLAY_SIZE = "sys.display-size";
  private static final String VENDOR_DISPLAY_SIZE = "vendor.display-size";
  private static final UUID WIDEVINE_UUID = new UUID(0xEDEF8BA979D64ACEL, 0xA3C827DCD51D21EDL);
  private List<String> hdcpLevels = Arrays
      .asList("UNKNOWN", "HDCP_NONE", "HDCP_V1", "HDCP_V2", "HDCP_V2_1", "HDCP_V2_2");


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
    mTextDerivedModelGroup = (TextView) findViewById(R.id.text_derived_model_group);

    mBtnGetDrmInfo = (Button) findViewById(R.id.button_getDrmInfo);
    mTextMaxHdcpLevel = (TextView) findViewById(R.id.text_maxHdcpLevel);
    mTextConnectedHdcpLevel = (TextView) findViewById(R.id.text_connectedHdcpLevel);
    mTextMaxSessions = (TextView) findViewById(R.id.text_maxSessions);
    mTextConnectedSessions = (TextView) findViewById(R.id.text_connectedSessions);
    mTextWidevineSystemId = (TextView) findViewById(R.id.text_widevine_systemid);

    mBtnHasVerifiedBoot = (Button) findViewById(R.id.button_has_verified_boot);
    mTextHasVerifiedBoot = (TextView) findViewById(R.id.text_has_verified_boot);
    mBtnDeepLinkToAddAccessories = (Button) findViewById(R.id.button_deepLinkToAddAccessory);
    mBtnLaunchNetflix = (Button) findViewById(R.id.button_launchNetflix);
    mNetflixIntent = getPackageManager().getLaunchIntentForPackage("com.netflix.ninja");

    try {
      Drawable logo = getPackageManager().getActivityLogo(mNetflixIntent);
      mBtnLaunchNetflix.setBackground(logo);
      Log.i(TAG, "Got Netflix logo\n");

    } catch (Exception e) {
      Log.i(TAG, "Netflix not installed");
    }

    HdmiReceiver.HdmiListener listener = new HdmiReceiver.HdmiListener() {
      @Override
      public void onHdmiPluggedState(boolean plugged, Intent intent) {
        Log.i(TAG, "Received Action: " + intent.getAction() + "\n");
        Log.i(TAG, plugged ? "HDMI connected" : "HDMI disconnected");
        Log.i(TAG, HdmiReceiver.dumpIntent(intent));

      }
    };
    mHdmiReceiver = new HdmiReceiver(this, listener);

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
              setHdcpLabels(getDrmInfo());
            }
          }
        });
      }
    });

    setVerifiedBootLabel();
    mBtnHasVerifiedBoot.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            setVerifiedBootLabel();
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

    mMediaDrm = getDrmInfo();
    setHdcpLabels(mMediaDrm);

  }

  private void setVerifiedBootLabel() {
    mTextHasVerifiedBoot.setText("\n" + (hasVerifiedBoot() ? "Yes"
        : "No") + "\nstate: [" + verifiedBootState() + "]");
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

  Boolean hasVerifiedBoot() {
    Boolean hasVerifiedBoot = getPackageManager().hasSystemFeature(FEATURE_VERIFIED_BOOT);
    Log.i(TAG, "hasVerifiedBoot=[" + Boolean.toString(hasVerifiedBoot) + "]\n");
    return hasVerifiedBoot;
  }

  String verifiedBootState() {
    String verifiedBootState = getSystemProperty("ro.boot.verifiedbootstate");
    Log.i(TAG, "VerifiedBootState=[" + verifiedBootState + "]\n");
    return verifiedBootState;
  }

  public void callDisplayModeAPI() {
    Display.Mode mode = getMaxDisplayMode();
    mTextWidth.setText("Width\n" + Integer.toString(mode.getPhysicalWidth()));
    mTextHeight.setText("Height\n" + Integer.toString(mode.getPhysicalHeight()));
    mTextRefreshRate.setText("Refresh Rate\n" + Float.toString(mode.getRefreshRate()));
    mTextIsUHD.setText("UHD\n" + (isUHD(mode) ? "Yes" : "No"));
    Log.i(TAG, "Display.Mode API: width=" + Integer.toString(mode.getPhysicalWidth()) + " height="
        + Integer.toString(mode.getPhysicalHeight()) + " refresh Rate=" + Float
        .toString(mode.getRefreshRate()) + "\n");
  }

  public void updateSystemProperties() {
    Point displaySize = getDisplaySizeFromProperties(SYS_DISPLAY_SIZE);
    mTextSysPropWidth.setText("Width\n" + Integer.toString(displaySize.x));
    mTextSysPropHeight.setText("Height\n" + Integer.toString(displaySize.y));
    Log.i(TAG,
        SYS_DISPLAY_SIZE + ": width=" + Integer.toString(displaySize.x) + " height=" + Integer
            .toString(displaySize.y) + "\n");
  }

  public void updateVendorProperties() {
    Point displaySize = getDisplaySizeFromProperties(VENDOR_DISPLAY_SIZE);
    mTextVendorPropWidth.setText("Width\n" + Integer.toString(displaySize.x));
    mTextVendorPropHeight.setText("Height\n" + Integer.toString(displaySize.y));

    setDerivedModelGroup();

    Log.i(TAG,
        VENDOR_DISPLAY_SIZE + ": width=" + Integer.toString(displaySize.x) + " height=" + Integer
            .toString(displaySize.y) + "\n");
  }

  public String getDerivedModelGroup() {

    return getSystemProperty("ro.product.brand", "brand") + "-" +
            getSystemProperty("ro.product.name", "name") + "-" +
            getSystemProperty("ro.product.device", "device") + "-" +
            getDrmInfo().getPropertyString("systemId") + "-" +
            getSystemProperty("key-oem1", "operator");  // replace this with property that we set on first run.
  }

  public void setDerivedModelGroup() {
    String derivedModelGroup = getDerivedModelGroup();
    mTextDerivedModelGroup.setText("modelGroup\n" + derivedModelGroup);
  }

  public boolean isUHD() {
    return isUHD(getMaxDisplayMode());
  }

  public boolean isUHD(Display.Mode mode) {
    if (Build.VERSION.SDK_INT < 28) {  // can't determine hdcplevel
      return false;
    }

    Point displaySize = getDisplaySizeFromProperties(VENDOR_DISPLAY_SIZE);
    int hdcpLevel = getDrmInfo().getConnectedHdcpLevel();

    boolean hasHDCPV2_2 = "HDCP_V2_2".equals(hdcpLevels.get(hdcpLevel));

    return(displaySize.y >= 2160) && (displaySize.x >=3840) && (mode.getRefreshRate() >= 60.0) && hasHDCPV2_2;
  }

  public Display.Mode getMaxDisplayMode() {
    return getWindowManager().getDefaultDisplay().getMode();
  }


  private MediaDrm getDrmInfo() {

    MediaDrm mediaDrm = null;
    try {
      mediaDrm = new MediaDrm(WIDEVINE_UUID);
    } catch (Exception e) {
      throw new Error("Unexpected exception ", e);
    } finally {
      if (mMediaDrm != null) {
        mMediaDrm.close();
      }
    }

    return mediaDrm;

  }

  private void setHdcpLabels(MediaDrm mediaDrm) {

    // to find list of properties, see //vendor/widevine/libwvdrmengine/mediadrm/src_hidl/WVDrmPlugin.cpp

    String systemid = mediaDrm.getPropertyString("systemId");
    String maxHdcpLevel = mediaDrm.getPropertyString("maxHdcpLevel");
    String connectedHdcpLevel = mediaDrm.getPropertyString("hdcpLevel");
    String maxSessions = mediaDrm.getPropertyString("maxNumberOfSessions");
    String connectedSessions = mediaDrm.getPropertyString("numberOfOpenSessions");

    mTextMaxHdcpLevel.setText("Max HDCP Level\n" + maxHdcpLevel);
    mTextConnectedHdcpLevel.setText("Connected HDCP Level\n" + connectedHdcpLevel);
    mTextMaxSessions.setText("Max Sessions\n" + maxSessions);
    mTextConnectedSessions.setText("Open Sessions\n" + connectedSessions);
    mTextWidevineSystemId.setText("System ID\n" + systemid);

  }


  private void logDrmInfo(MediaDrm mediaDrm) {
    String systemid = mediaDrm.getPropertyString("systemId");
    String maxHdcpLevel = mediaDrm.getPropertyString("maxHdcpLevel");
    String connectedHdcpLevel = mediaDrm.getPropertyString("hdcpLevel");
    String maxSessions = mediaDrm.getPropertyString("maxNumberOfSessions");
    String connectedSessions = mediaDrm.getPropertyString("numberOfOpenSessions");
    Log.i(TAG, "Max HDCP Level: " + maxHdcpLevel + " Connected HDCP Level: " + connectedHdcpLevel + "\n");
    Log.i(TAG, "Max Sessions: " + maxSessions + " Connected Sessions: " + connectedSessions + "\n");
    Log.i(TAG, "System Id: " + systemid + "\n");
  }

  private String getSystemProperty(String prop) {
    return getSystemProperty(prop, "");
  }

  private String getSystemProperty(String prop, String defaultValue) {
    try {
      Class<?> systemProperties = Class.forName(ANDROID_SYSTEM_PROPERTIES_CLASS);
      Method getMethod = systemProperties.getMethod("get", String.class);
      String value = (String) getMethod.invoke(systemProperties, prop);
      return "".equals(value) ? defaultValue : value;
    } catch (Exception e) {
      Log.e(TAG, "Failed to read " + prop, e);
      return defaultValue;
    }
  }

  public Point getDisplaySizeFromProperties(String prop) {

    String displaySize = getSystemProperty(prop);

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
