package com.bn.Sample2_5;

import android.app.Activity;
import android.os.Bundle;

public class Sample2_5_Activity extends Activity 
{
	GameSurfaceView gameView;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        gameView = new GameSurfaceView(this);
        setContentView(gameView);	
        gameView.requestFocus();//获取焦点
        gameView.setFocusableInTouchMode(true);//设置为可触控  
    }


    @Override
    protected void onResume() {
        super.onResume();
        gameView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.onPause();
    }    
}