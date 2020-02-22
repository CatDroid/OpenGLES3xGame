package com.bn.Sample6_6;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.ToggleButton;

public class Sample6_6Activity extends Activity {
	MySurfaceView msv;
    @Override
    public void onCreate(Bundle savedInstanceState)
	{

        super.onCreate(savedInstanceState);
		// 设置为全屏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// 初始化GLSurfaceView
        msv = new MySurfaceView(this);
        setContentView(R.layout.activity_flag);
        LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
        ll.addView(msv);

        msv.requestFocus();					// 获取焦点
        msv.setFocusableInTouchMode(true);	// 设置为可触控

        SeekBar sb = (SeekBar) findViewById(R.id.seekBar1);
        sb.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener()
                {
    				@Override
    				public void onProgressChanged(SeekBar seekBar, int progress,
    						boolean fromUser)
					{
    					Constant.WindForce = 4.0f*progress/seekBar.getMax();//设置风力
    				}

    				@Override
    				public void onStartTrackingTouch(SeekBar seekBar)
					{

					}

    				@Override
    				public void onStopTrackingTouch(SeekBar seekBar)
					{

					}
                }
            );


        Button bt1 = (Button) findViewById(R.id.button1);//取消固定粒子按钮
        bt1.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {
				//取消固定粒子
				Sample6_6Activity.this.msv.mRenderer.pc.particles[0][0].bLocked = false;
				Sample6_6Activity.this.msv.mRenderer.pc.particles[Constant.NUMROWS][0].bLocked = false;
			}
		});


        Button bt2 = (Button) findViewById(R.id.button2);//重置按钮
        bt2.setOnClickListener(new OnClickListener()
		{
        	@Override
        	public void onClick(View v) {
        		synchronized(Constant.lockB)
        		{
        			Sample6_6Activity.this.msv.mRenderer.pc.initalize();//重置粒子
        		}
        	}
        });


        Button bt3 = (Button) findViewById(R.id.button3);//切换旗面按钮
        bt3.setOnClickListener(new OnClickListener()
		{
        	@Override
        	public void onClick(View v) {
        		Sample6_6Activity.this.msv.nowId = (Sample6_6Activity.this.msv.nowId+1)%3;//切换id
        	}
        });


        ToggleButton tb = (ToggleButton) findViewById(R.id.toggleButton1);
        tb.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				Constant.isC = isChecked;//是否开启碰撞检测
			}
		});

    }


	@Override
	protected void onPause() {
		super.onPause();
		msv.onPause();
	}
    
}
