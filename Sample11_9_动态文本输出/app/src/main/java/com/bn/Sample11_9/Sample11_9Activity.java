package com.bn.Sample11_9;

import android.app.Activity;
import android.os.Bundle;


public class Sample11_9Activity extends Activity
{
	MySurfaceView mview;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        mview = new MySurfaceView(this);
        mview.requestFocus();//获取焦点
        mview.setFocusableInTouchMode(true);//设置为可触控  
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