package com.bn.Sample5_6;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Sample5_6_Activity extends Activity {
	private MySurfaceView mGLSurfaceView;
	RadioButton rb0;
	RadioButton rb1;
	RadioButton rb2;
	RadioButton rb3;
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置为全屏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 初始化GLSurfaceView
		mGLSurfaceView = new MySurfaceView(this);
		// 切换到主界面
		setContentView(R.layout.main);
		 //将自定义的SurfaceView添加到外层LinearLayout中
        LinearLayout ll=(LinearLayout)findViewById(R.id.main_liner); 
        ll.addView(mGLSurfaceView);
		mGLSurfaceView.requestFocus();// 获取焦点
		mGLSurfaceView.setFocusableInTouchMode(true);// 设置为可触控		
		
		rb0 = (RadioButton) findViewById(R.id.RadioButton00);
		rb1 = (RadioButton) findViewById(R.id.RadioButton01);
		rb2 = (RadioButton) findViewById(R.id.RadioButton02);
		rb3 = (RadioButton) findViewById(R.id.RadioButton03);
		// GL_POINTS RadioButton添加监听器
		rb0.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if(isChecked){
					Constant.CURR_DRAW_MODE = Constant.GL_POINTS;
					rb1.setChecked(false);
					rb2.setChecked(false);
					rb3.setChecked(false);
				}
			}
		});
		// GL_LINES RadioButton添加监听器
		rb1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if(isChecked){
					Constant.CURR_DRAW_MODE = Constant.GL_LINES;
					rb0.setChecked(false);
					rb2.setChecked(false);
					rb3.setChecked(false);
				}
			}
		});
		// GL_LINE_STRIP RadioButton添加监听器
		rb2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if(isChecked){
					Constant.CURR_DRAW_MODE = Constant.GL_LINE_STRIP;
					rb0.setChecked(false);
					rb1.setChecked(false);
					rb3.setChecked(false);
				}
			}
		});
		// GL_LINE_LOOP RadioButton添加监听器
		rb3.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if(isChecked){
					Constant.CURR_DRAW_MODE = Constant.GL_LINE_LOOP;
					rb0.setChecked(false);
					rb1.setChecked(false);
					rb2.setChecked(false);
				}
			}
		});

	}

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause(); 
    } 
}