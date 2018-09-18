package com.google.jimlongja.teststub;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.lang.reflect.Method;

import java.util.Properties;

import static android.text.TextUtils.split;

public class MainActivity extends Activity {

    Button mBtnCallAPI;
    TextView mTextWidth;
    TextView mTextHeight;
    TextView mTextIsUHD;
    TextView mTextRefreshRate;
    private static final String TAG = "TestStub";
    private static final String ANDROID_SYSTEM_PROPERTIES_CLASS = "android.os.SystemProperties";

    private class DisplayMode {
        int width;
        int height;
        float refreshRate;

        DisplayMode() {
            this.width = 0;
            this.height = 0;
            this.refreshRate = 0.0f;
        }

        DisplayMode(Display.Mode mode) {
            this.width = mode.getPhysicalWidth();
            this.height = mode.getPhysicalHeight();
            this.refreshRate = mode.getRefreshRate();
        }

        @Override
        public String toString() {
            return new StringBuilder("{")
                    .append(", width=").append(width)
                    .append(", height=").append(height)
                    .append(", fps=").append(refreshRate)
                    .append("}")
                    .toString();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnCallAPI = (Button) findViewById(R.id.button_callAPI);
        mTextWidth = (TextView) findViewById(R.id.text_Width);
        mTextHeight = (TextView) findViewById(R.id.text_Height);
        mTextRefreshRate = (TextView) findViewById(R.id.text_RefreshRate);
        mTextIsUHD = (TextView) findViewById(R.id.text_isUHD);

        mBtnCallAPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisplayMode mode = getMaxDisplayMode();
                mTextWidth.setText("Width\n" + Integer.toString(mode.width));
                mTextHeight.setText("Height\n" + Integer.toString(mode.height));
                mTextRefreshRate.setText("Refresh Rate\n" + Float.toString(mode.refreshRate));
                mTextIsUHD.setText("UHD\n" + (isUHD(mode) ? "Yes" : "No"));
            }
        });

        Point displaySize = getSystemDisplaySize();
        Log.i(TAG, "sys.display-size: width=" + Integer.toString(displaySize.x) + " height=" + Integer.toString(displaySize.y) + "\n");

//        displaySize = getVendorDisplaySize();
//        Log.i(TAG, "vendor.display-size: width=" + Integer.toString(displaySize.x) + " height=" + Integer.toString(displaySize.y) + "\n");

    }

    public boolean isUHD(){
        return isUHD(getMaxDisplayMode());
    }

    public boolean isUHD(DisplayMode mode) {
        boolean result = (mode.width >= 3840 && mode.height >= 2160);
        Log.i(TAG, "UHD = " + (result ? "Yes" : "No"));
        return result;
    }

    public DisplayMode getMaxDisplayMode() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMode max = new DisplayMode();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        Display.Mode[] modes = display.getSupportedModes();

        for (Display.Mode mode : modes) {
            int width = mode.getPhysicalWidth();
            if (max.width < width) {
                max = new DisplayMode(mode);
            }
        }

        Log.i(TAG, max.toString());

        return max;
    }

    public Point getSystemDisplaySize() {

        String displaySize = null;

        try {
            Class<?> systemProperties = Class.forName(ANDROID_SYSTEM_PROPERTIES_CLASS);
            Method getMethod = systemProperties.getMethod("get", String.class);
            displaySize = (String) getMethod.invoke(systemProperties, "sys.display-size");
        } catch (Exception e) {
            Log.e(TAG, "Failed to read sys.display-size", e);
            return new Point(0, 0);
        }

        // If we managed to read sys.display-size, attempt to parse it.
        return parseDisplaySizeProperty(displaySize);


    }

    public Point getVendorDisplaySize() {

        String displaySize = null;

        try {
            Class<?> vendorProperties = Class.forName("android.os.VendorProperties");
            Method getMethod = vendorProperties.getMethod("get", String.class);
            displaySize = (String) getMethod.invoke(vendorProperties, "vendor.display-size");
        } catch (Exception e) {
            Log.e(TAG, "Failed to read vendor.display-size", e);
            return new Point(0, 0);
        }

        // If we managed to read sys.display-size, attempt to parse it.
        return parseDisplaySizeProperty(displaySize);


    }

    private Point parseDisplaySizeProperty(String prop) {
        if (!TextUtils.isEmpty(prop)) {
            try {
                String[] propParts = split(prop.trim(), "x");
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
            Log.e(TAG, "Invalid property: " + prop);
        }

        return new Point(0, 0);
    }

}
