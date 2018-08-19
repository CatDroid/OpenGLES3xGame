package com.bn.view;

import javax.microedition.khronos.opengles.GL10;

import android.view.MotionEvent;

public abstract class BNAbstractView 
{
	public abstract void initView(); // GL-Thread
	public abstract void drawView(GL10 gl); // GL-Thread
	public abstract void lostContextOnGLThread();// GL-Thread

	public abstract boolean onTouchEvent(MotionEvent e); // Main-Thread
}