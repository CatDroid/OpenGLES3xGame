package com.bn.Sample2_3;
import static com.bn.Sample2_3.Constant.SCREEN_HEIGHT;
import static com.bn.Sample2_3.Constant.SCREEN_WIDTH;
import static com.bn.Sample2_3.Constant.flag_go;
import java.util.HashMap;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
public class Sample2_3_Activity extends Activity 
{
	private GameSurfaceView mGLSurfaceView;
	AudioManager mgr;// 音频管理者
	SoundPool soundPool;// 声音池
	MediaPlayer bgMusic;// 游戏背景音乐播放器
	HashMap<Integer,Integer> soundMap;//存放声音池中的声音ID的Map
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);   
        initScreen();
        initSound();//在activity中的onCreate方法中调用
		//初始化GLSurfaceView
        mGLSurfaceView = new GameSurfaceView(this);
        setContentView(mGLSurfaceView);	
        mGLSurfaceView.requestFocus();//获取焦点
        mGLSurfaceView.setFocusableInTouchMode(true);//设置为可触控  
        bgMusic.start();
    }
    //初始化屏幕
    @SuppressWarnings("deprecation")
	public void initScreen()
    {
    	requestWindowFeature(Window.FEATURE_NO_TITLE); 
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,  
		              WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//设置为横屏模式
		SCREEN_WIDTH=getWindowManager().getDefaultDisplay().getWidth();
		SCREEN_HEIGHT=getWindowManager().getDefaultDisplay().getHeight();
    }
    @Override
    protected void onResume() 
    {
        super.onResume();
        mGLSurfaceView.onResume();
        flag_go=true;
    }
    @Override
    protected void onPause()
    {
        super.onPause();
        mGLSurfaceView.onPause();
        flag_go=false;
    }  
    //创建选项菜单
    public boolean onCreateOptionsMenu(Menu menu) 
    {
    	  menu.add(0, 0, 0, "风向")
    	   .setIcon(R.drawable.icon);
    	  menu.add(0, 1, 0, "风力")
    	   .setIcon(R.drawable.icon);
    	  return super.onCreateOptionsMenu(menu);
    }
	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
	  switch(item.getItemId())
	  {
	  case 0: 
		  showDialog(0);
	   break;
	  case 1:   
		  showDialog(1);
	   break;
	  }
	  return super.onOptionsItemSelected(item);
	}
    @Override
    public Dialog onCreateDialog(int id)
    {
    	Dialog dialog=null;
    	switch(id)
    	{
    	  case 0://生成普通对话框的代码
    		  String msg="当前的风向为: "+Constant.wind_direction+" 度";
    		  LayoutInflater factory = LayoutInflater.from(this);  
    		  View view = factory.inflate(R.layout.seekbar, null);  
    		  final TextView tv=(TextView)view.findViewById(R.id.seekbar_tv);
    		  tv.setText(msg);
    		  final SeekBar sb=(SeekBar)view.findViewById(R.id.seekbar_sb);
    		  sb.setMax(359);
    		  sb.setProgress((int)Constant.wind_direction);
    		  sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() 
    		  {
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
				}
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) 
				{
					Constant.wind_direction=sb.getProgress();
					tv.setText("当前的风向为: "+(float)sb.getProgress()+" 度");
				}
			});
    		  Builder b=new AlertDialog.Builder(this);  
    		  b.setIcon(R.drawable.icon);//设置图标
    		  b.setTitle("设置风向");//设置标题
    		  b.setView(view);
    		  b.setPositiveButton//为对话框设置按钮
    		  (
    				"确定", 
    				new DialogInterface.OnClickListener()
	        		{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							Constant.wind_direction=sb.getProgress();
						}      			
	        		}
    		  );
    		  dialog=b.create();
    	  break;
    	  case 1://生成普通对话框的代码
    		  msg="当前的风力为: "+Constant.wind+" 级";
    		  factory = LayoutInflater.from(this);
    		  view = factory.inflate(R.layout.seekbar, null);  
    		  final TextView tv1=(TextView)view.findViewById(R.id.seekbar_tv);
    		  tv1.setText(msg);
    		  final SeekBar sb1=(SeekBar)view.findViewById(R.id.seekbar_sb);
    		  sb1.setMax(12);
    		  sb1.setProgress(Constant.wind);
    		  sb1.setOnSeekBarChangeListener(new OnSeekBarChangeListener() 
    		  {
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
				}
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) 
				{
					Constant.setWindForce(sb1.getProgress());
					tv1.setText("当前的风力为: "+sb1.getProgress()+" 级");
				}
			});
    		  b=new AlertDialog.Builder(this);  
    		  b.setIcon(R.drawable.icon);//设置图标
    		  b.setTitle("设置风力");//设置标题
    		  b.setView(view);
    		  b.setPositiveButton//为对话框设置按钮
    		  (
    				"确定", 
    				new DialogInterface.OnClickListener()
	        		{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							Constant.setWindForce(sb1.getProgress());
						}      			
	        		}
    		  );
    		  dialog=b.create();
    	  break;
    	}
    	return dialog;
    }
   //加载声音资源
	public void initSound() 
	{
		// 获取音频管理者
		mgr = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		// 初始化媒体播放器
		bgMusic = MediaPlayer.create(this, R.raw.gamebg_music);
		bgMusic.setLooping(true);// 是否循环
		// 初始化声音池
		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 5);// 创建声音池
		soundMap = new HashMap<Integer, Integer>();// 创建map
		soundMap.put(0, soundPool.load(this, R.raw.wind, 1));
	}
    //播放声音池的方法
    public void playSound(int sound,int loop)
    {
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);   
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);       
        float volume = streamVolumeCurrent / streamVolumeMax;   
        soundPool.play
        (
         soundMap.get(sound), //声音资源id
         volume,      //左声道音量
         volume,      //右声道音量
         1,        //优先级     
         loop,       //循环次数 -1带表永远循环
         0.5f      //回放速度0.5f～2.0f之间
        );
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e)  
    { 
    	 //控制音量键只能控制媒体音量的大小
        if(keyCode==KeyEvent.KEYCODE_VOLUME_DOWN||keyCode==KeyEvent.KEYCODE_VOLUME_UP)
        {
              setVolumeControlStream(AudioManager.STREAM_MUSIC);
              return super.onKeyDown(keyCode, e);
        }
    	if(keyCode==4)
    	{
    		System.exit(0);
    		return true;
    	}
		return super.onKeyDown(keyCode, e);
    }
}