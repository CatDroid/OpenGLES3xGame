package com.bn.Sample3_13;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

public class Sample3_13Activity extends Activity {

    MySurfaceView mview;
    static float WIDTH;
    static float HEIGHT;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        if (dm.widthPixels > dm.heightPixels) {
            WIDTH = dm.widthPixels;
            HEIGHT = dm.heightPixels;
        } else {
            //noinspection SuspiciousNameCombination
            WIDTH = dm.heightPixels;
            //noinspection SuspiciousNameCombination
            HEIGHT = dm.widthPixels;
        }


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mview = new MySurfaceView(this);
        mview.requestFocus();
        mview.setFocusableInTouchMode(true);
        setContentView(mview);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mview.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mview.onPause();
    }
}