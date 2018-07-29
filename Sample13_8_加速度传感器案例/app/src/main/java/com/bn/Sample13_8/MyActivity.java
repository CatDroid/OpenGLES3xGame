package com.bn.Sample13_8;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;

import static com.bn.Sample13_8.Constant.SPAN_LOCK;
import static com.bn.Sample13_8.Constant.TAG;

public class MyActivity extends Activity {
	//SensorManager对象引用
	SensorManager mySensorManager;		
	Sensor sensorAccelerometer;//加速度传感器的引用
	MySurfaceView mySurfaceView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		try {
			Class GLSurfaceViewClass = Class.forName("android.opengl.GLSurfaceView");


			Field LOG_SURFACE_FIELD = GLSurfaceViewClass.getDeclaredField("LOG_SURFACE");
			LOG_SURFACE_FIELD.setAccessible(true);
			LOG_SURFACE_FIELD.set(null,true);
			Boolean LOG_SURFACE = LOG_SURFACE_FIELD.getBoolean(null);


			Field LOG_THREADS_FIELD = GLSurfaceViewClass.getDeclaredField("LOG_THREADS");
			LOG_THREADS_FIELD.setAccessible(true);
			LOG_THREADS_FIELD.set(null,true);
			Boolean LOG_THREADS = LOG_THREADS_FIELD.getBoolean(null);

			// hhl 除非定义成这样 否则已经被编译器内联到使用的地方!!!!
			// private static final String name = new String("Beijing");

//			Field modifiersField = Field.class.getDeclaredField("modifiers"); //①
//			modifiersField.setAccessible(true);
//			modifiersField.setInt(nameField, nameField.getModifiers() & ~Modifier.FINAL);

			Log.e(TAG,"result " + LOG_SURFACE + " " +LOG_THREADS );

			// 想要修改某个类或对象的私有变量的值的话, 在调用 set 设置新值之前执行一下 setAccessible(true) 即可
			// 这样利用的 Java 的反射就能绕过 private 的限制 ，不再有 IllegalAccessException 异常
			// 在修改 final 型值时，要特别留意它的常量值本身是否被编译器优化内联到某处，否则你会看到虽然没什么异常，但取出的还是原来的值
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		//全屏
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,  
		              WindowManager.LayoutParams.FLAG_FULLSCREEN);		
		//设置为屏模式
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		//获得SensorManager对象
        mySensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensorAccelerometer=mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);       
        
        mySurfaceView = new MySurfaceView(this);
        this.setContentView(mySurfaceView);       
        //获取焦点
        mySurfaceView.requestFocus();
        //设置为可触控
        mySurfaceView.setFocusableInTouchMode(true);
    }
    

  //重力传感器的监听器
  	private SensorEventListener mek=new SensorEventListener()
  	{
  		@Override
  		public void onAccuracyChanged(Sensor sensor, int accuracy) {}

  		@Override
  		public void onSensorChanged(SensorEvent event) 
  		{			
  			//获取重力加速度在屏幕上的XY分量
  			float gx=event.values[0];
  			float gy=event.values[1];

  			//求出屏幕上重力加速度向量的分量长度
  			double mLength=gx*gx+gy*gy;
  			mLength=Math.sqrt(mLength); // 只是求x和y方向的分量，也就是X0Y平面 手机屏幕方向上的分量
  			//若分量为0则返回
  			if(mLength==0) // hhl 水平的情况下的确会出现,只有z分量有值，其他x和y都是0.0f
  			{
				Log.d(TAG,"mLength==0");
  				return;
  			}
			// hhl 由于是横屏状态，由于加速度传感器总是认为手机短边是X轴，所以需要调换一下
  			double direction[] = new double[]{(gy/mLength),(gx/mLength)};
  			// 若分量不为0则设置球滚动的步进
			synchronized (SPAN_LOCK) {
				Constant.SPANX = (float) (direction[0] * Constant.MOVE_STEP);
				Constant.SPANZ = (float) (direction[1] * Constant.MOVE_STEP);
			}
			// gx/mLength,gy/mLength  是单位向量 方向向量

  		}		
  	};	

	@Override
	protected void onResume() {						//重写onResume方法
		mySensorManager.registerListener
		(mek, sensorAccelerometer, SensorManager.SENSOR_DELAY_UI);
		super.onResume();
	}
	@Override
	protected void onPause() {									//重写onPause方法
		mySensorManager.unregisterListener(mek);
		super.onPause();
	}
	@Override
	public boolean onKeyDown(int keyCode,KeyEvent e)
	{
		switch(keyCode)
	    	{
		case 4:
			System.exit(0);
			break;
	    	}
		return true;
	}
}