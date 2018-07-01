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
	SensorManager mySensorManager;	//SensorManager对象引用	
	Sensor myAccelerometer; 	//传感器类型
	Sensor myMagnetic; 	//传感器类型
	TextView tYaw;	 //TextView对象引用	
	TextView tPitch; //TextView对象引用	
	TextView tRoll;	 //TextView对象引用
	float []vlAccelerometer=new float[3];
	float []vlManager=new float[3];
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tYaw = (TextView)findViewById(R.id.tYaw);	//用于显示Yaw旋转角度
        tPitch = (TextView)findViewById(R.id.tPicth);	//用于显示Pitch旋转角度
        tRoll = (TextView)findViewById(R.id.tRoll); //用于显示Roll旋转角度
        //获得SensorManager对象
        mySensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);	
        //传感器的类型
        myAccelerometer=mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        myMagnetic=mySensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
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
		  tYaw.setText(  "Yaw轴的旋转角度："+Values[0]);		
		  tPitch.setText("Pitch轴的旋转角度："+Values[1]);
		  tRoll.setText( "Roll轴的旋转角度："+Values[2]);		
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
		  tYaw.setText(  "Yaw轴的旋转角度："+Values[0]);		
		  tPitch.setText("Pitch轴的旋转角度："+Values[1]);		
		  tRoll.setText( "Roll轴的旋转角度："+Values[2]);	
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