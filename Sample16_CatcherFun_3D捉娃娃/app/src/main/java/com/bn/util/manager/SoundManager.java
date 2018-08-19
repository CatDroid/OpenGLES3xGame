package com.bn.util.manager;

import static com.bn.constant.SourceConstant.*;
import java.util.HashMap;

import com.bn.catcherFun.MainActivity;
import com.bn.hand.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

@SuppressLint("UseSparseArrays")
public class SoundManager
{
	SoundPool sp ;
	HashMap<Integer	,Integer> hm ;
	com.bn.catcherFun.MainActivity activity ;
	
	public MediaPlayer mp  ;
	public SoundManager(MainActivity activity)
	{
		this.activity = activity  ;
		initSound();
	}
	
	//声音 初始化
	
	public void initSound()
	{
		sp = new SoundPool
		(4, 
		AudioManager.STREAM_MUSIC, 
		100
		);
		hm = new HashMap<Integer, Integer>();  
		hm.put(SOUND_Click, sp.load(activity,R.raw.click, 1));//点击按钮
		hm.put(SOUND_Back, sp.load(activity, R.raw.back, 1));//点击返回按钮
		hm.put(SOUND_DropMoney, sp.load(activity, R.raw.dropmoney, 1));//金币掉落音效
	}
	public void playBackGroundMusic(Activity ac,int Id)
	{
		if(MainActivity.sound.mp!=null){
			MainActivity.sound.mp.pause();
		    MainActivity.sound.mp=null;
		}
	 	 if(MainActivity.sound.mp==null)
	 	 {
 			 MainActivity.sound.mp =  MediaPlayer.create(ac, Id);
 			 MainActivity.sound.mp.setVolume(0.2f, 0.2f);//设置左右声道音量
 			 MainActivity.sound.mp.setLooping(true);//循环播放
 			 MainActivity.sound.mp.start();
	 	 }
	}
	public void playMusic(int sound,int loop)
	{
		@SuppressWarnings("static-access")
		AudioManager am = (AudioManager)activity.getSystemService(activity.AUDIO_SERVICE);
		float steamVolumCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC)  ;
		float steamVolumMax = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)  ;
		float volum = steamVolumCurrent/steamVolumMax  ;
		sp.play(hm.get(sound), volum, volum, 1	, loop, 1)  ;//播放
	}
	

	long preTimeStamp=0;
	public void playGameMusic(int sound,int loop)
	{
		long currTimeStamp=System.nanoTime();
		if(currTimeStamp-preTimeStamp<500000000L)
		{
			return;
		}
		preTimeStamp=currTimeStamp;
		@SuppressWarnings("static-access")
		AudioManager am = (AudioManager)activity.getSystemService(activity.AUDIO_SERVICE);
		float steamVolumCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC)  ;//获得当前音量
		float steamVolumMax = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)  ;//获得最大音量
		float volum = steamVolumCurrent/steamVolumMax  ;//计算声音播放的音量
		sp.play(
				hm.get(sound), //声音资源id
				volum, //左声道音量
				volum, //右声道音量
				1	, //优先级
				loop, //循环次数 -1代表永远循环
				1//回放速度0.5f～2.0f之间
				);//播放
	}	
	
	public void stopGameMusic(int sound)
	{
		sp.pause(sound);
		sp.stop(sound);
		sp.setVolume(sound, 0, 0);
	}
}

