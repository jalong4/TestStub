package com.google.jimlongja.teststub;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import java.lang.reflect.Method;

import java.util.Properties;
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

    private static final String TAG = "TestStub";
    private static final String ANDROID_SYSTEM_PROPERTIES_CLASS = "android.os.SystemProperties";
    private static final String SYS_DISPLAY_SIZE = "sys.display-size";
    private static final String VENDOR_DISPLAY_SIZE = "vendor.display-size";
    private static final UUID WIDEVINE_UUID = new UUID(0xEDEF8BA979D64ACEL, 0xA3C827DCD51D21EDL);

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

        mBtnCallAPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callDisplayModeAPI();
            }
        });


        mBtnReadSysProp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSystemProperties();
            }
        });


        mBtnReadVendorProp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        callDisplayModeAPI();
        updateSystemProperties();
        updateVendorProperties();

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

}
