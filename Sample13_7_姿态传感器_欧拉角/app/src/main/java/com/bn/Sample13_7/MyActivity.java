package com.bn.Sample13_7;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;
public class MyActivity extends Activity {

	SensorManager mySensorManager;

	Sensor myAccelerometer;
	Sensor myMagnetic;
	Sensor myOrientation;
	TextView tYaw;
	TextView tPitch;
	TextView tRoll;

	TextView tOriYaw;
	TextView tOriPitch;
	TextView tOriRoll;


	float []vlAccelerometer=new float[3];
	float []vlManager=new float[3];

    @Override
    public void onCreate(Bundle savedInstanceState)
	{

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        tYaw = (TextView)findViewById(R.id.tYaw);	//用于显示Yaw旋转角度
        tPitch = (TextView)findViewById(R.id.tPicth);	//用于显示Pitch旋转角度
        tRoll = (TextView)findViewById(R.id.tRoll); //用于显示Roll旋转角度

		tOriYaw = (TextView)findViewById(R.id.tOriYaw);
		tOriPitch = (TextView)findViewById(R.id.tOriPicth);
		tOriRoll = (TextView)findViewById(R.id.tOriRoll);


        //获得SensorManager对象
        mySensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);	
        //传感器的类型
        myAccelerometer=mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        myMagnetic=mySensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		myOrientation=mySensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        }
    @Override
	protected void onResume(){ //重写onResume方法
		super.onResume();
		mySensorManager.registerListener(
				myAccelerometerListener, 		//为重力传感器添加监听
				myAccelerometer, 		//传感器类型
				SensorManager.SENSOR_DELAY_NORMAL	//传感器事件传递的频度
		);
		mySensorManager.registerListener(
				myMagneticListener, 		//为磁场传感器添加监听
				myMagnetic, 		//传感器类型
				SensorManager.SENSOR_DELAY_NORMAL	//传感器事件传递的频度
		);
		mySensorManager.registerListener(
				myOrientationListener, 	 // 方向传感器
				myOrientation,
				SensorManager.SENSOR_DELAY_NORMAL	//传感器事件传递的频度
		);
	}	
	@Override
	protected void onPause(){//重写onPause方法	
		super.onPause();
		mySensorManager.unregisterListener(myAccelerometerListener);//取消注册监听器
		mySensorManager.unregisterListener(myMagneticListener);//取消注册监听器
		
	}
	private SensorEventListener myAccelerometerListener = 
		new SensorEventListener(){//开发实现了SensorEventListener接口的传感器监听器
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy){}
		@Override
		public void onSensorChanged(SensorEvent event){
			vlAccelerometer=event.values;//获取三个轴方向上的加速度值
            //声明旋转矩阵
		  float[] R=new float[9];
		  //获取旋转矩阵的各项值
		  SensorManager.getRotationMatrix
		  (
			R, 
			null, 
			vlAccelerometer, 
			vlManager
	      );
		//姿态值数组
		float[] Values=new float[3];
		//获取姿态值
		SensorManager.getOrientation(R, Values);
			tYaw.setText(  "Yaw轴的旋转角度："		+ (Values[0]*180.0/3.14));
			tPitch.setText("Pitch轴的旋转角度："	+ (Values[1]*180.0/3.14));
			tRoll.setText( "Roll轴的旋转角度："	+ (Values[2]*180.0/3.14));
		}
	};
	private SensorEventListener myMagneticListener=new SensorEventListener() {
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			vlManager=event.values;//获取三个轴方向上的磁场值
            //声明旋转矩阵
		  float[] R=new float[9];
		//获取旋转矩阵的各项值
		  SensorManager.getRotationMatrix
		  (
			R, 
			null, 
			vlAccelerometer, 
			vlManager
	      );
		//姿态值数组
		float[] Values=new float[3];
		//获取姿态值
		SensorManager.getOrientation(R, Values);
		  tYaw.setText(  "Yaw轴的旋转角度："	+ (Values[0]*180.0/3.14));
		  tPitch.setText("Pitch轴的旋转角度："	+ (Values[1]*180.0/3.14));
		  tRoll.setText( "Roll轴的旋转角度："	+ (Values[2]*180.0/3.14));

			// The quaternion is stored as [w, x, y, z]
			// SensorManager.getQuaternionFromVector(); 只是归一化旋转向量 比如旋转向量没有w的话，就加上，并且开头一个是w w,x,y,z
		}




		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			
		}
	};

	private SensorEventListener myOrientationListener
			= new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {

			/*
				sensors_event_t.orientation.x：
					方位角，磁北方向与 Y 轴之间的夹角，绕 Z 轴转动 (0<=azimuth<360)。0 = 北，90 = 东，180 = 南，270 = 西
				sensors_event_t.orientation.y：
					俯仰，绕 X 轴旋转 (-180<=pitch<=180)，当 z 轴向 y 轴移动时为正值。
				sensors_event_t.orientation.z：
					滚动，绕 Y 轴旋转 (-90<=roll<=90)，当 x 轴向 z 轴移动时为正值。
			* */

			tOriYaw.setText("磁北方向与 Y 轴之间的夹角，绕 Z 轴转动 (0<=azimuth<360) 0 = 北，90 = 东 \n"
					+ event.values[0]);
			tOriPitch.setText("俯仰 绕 X 轴旋转 (-180<=pitch<=180)，当 z 轴向 y 轴移动时为正值 \n"
					+ event.values[1]);
			tOriRoll.setText("滚动 绕 Y 轴旋转 (-90<=roll<=90)，当 x 轴向 z 轴移动时为正值 \n"
					+ event.values[2]);
		 }

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}
	};
	
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