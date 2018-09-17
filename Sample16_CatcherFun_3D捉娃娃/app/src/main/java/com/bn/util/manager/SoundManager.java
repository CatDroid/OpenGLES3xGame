package com.bn.util.manager;

import static com.bn.constant.SourceConstant.*;

import java.util.HashMap;


import com.bn.hand.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

@SuppressLint("UseSparseArrays")
public class SoundManager {

    private final Object mSoundPoolLock = new Object();
    private final Object mMediaPlayerLock = new Object();
    private SoundPool sp;
    private MediaPlayer mp;
    private HashMap<Integer, Integer> hm;




    private SoundManager() {

    }

    private boolean mInitDone = false;
    private boolean mDestroyDone = false;

    public void init(Context ctx){
        synchronized (this){
            if(mDestroyDone){
                return ;
            }
            if(!mInitDone) {
                mInitDone = true;
                initSound(ctx );
                this.notifyAll();
            }
        }
    }

    public synchronized void waitForInit(){
        while(!mInitDone && !mDestroyDone){
            try {
                this.wait();// init() or destory()
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



    volatile private static SoundManager sSoundManager = null;
    private static final Object sInstanceLock = new Object();
    public static SoundManager instance(){
        return getInstance(true);
    }
    public static SoundManager getInstance(boolean waitForInit){
        if( sSoundManager == null){
            synchronized (sInstanceLock){
                if(sSoundManager == null){
                    sSoundManager = new SoundManager();
                }
            }
        }

        if(waitForInit){
            sSoundManager.waitForInit();
        }
        return sSoundManager; // 单例 只要调用了destroy就不会再有作用 正常单例在应用程序不退出,应该不析构的

    }



    //声音 初始化

    private synchronized void initSound(Context ctx ) {
        if(mDestroyDone){
            return ;
        }
        sp = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        hm = new HashMap<Integer, Integer>();
        hm.put(SOUND_Click, sp.load(ctx, R.raw.click, 1));//点击按钮
        hm.put(SOUND_Back, sp.load(ctx, R.raw.back, 1));//点击返回按钮
        hm.put(SOUND_DropMoney, sp.load(ctx, R.raw.dropmoney, 1));//金币掉落音效
    }

    public void playBackGroundMusic(Context ctx , int Id) {

        synchronized (this){
            if(!mInitDone){
                return ;
            }
        }
        synchronized (mMediaPlayerLock){
            if (mp != null) {
                mp.pause();
                mp.release();
                mp = null;
            }
            mp = MediaPlayer.create(ctx, Id);
            mp.setVolume(0.2f, 0.2f);//设置左右声道音量
            mp.setLooping(true);//循环播放
            mp.start();
        }

    }

    public void pauseBackGroundMusic(){
        synchronized (this){
            if(!mInitDone){
                return ;
            }
        }
        synchronized (mMediaPlayerLock){
            if (mp != null) {
                mp.pause();
            }
        }
    }

    public void resumeBackGroundMusic(){
        synchronized (this){
            if(!mInitDone){
                return ;
            }
        }
        if (mp != null) {
            mp.start();
        }
    }


    public void playMusic(Context ctx, int sound, int loop) {
        synchronized (this){
            if(!mInitDone){
                return ;
            }
        }
        @SuppressWarnings("static-access")
        AudioManager am = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        float steamVolumCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        float steamVolumMax = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volum = steamVolumCurrent / steamVolumMax;
        synchronized (mSoundPoolLock){
            if(sp!=null){
                sp.play(hm.get(sound), volum, volum, 1, loop, 1);//播放
            }
        }
    }


    private long preTimeStamp = 0;
    public void playGameMusic(Context ctx, int sound, int loop) { // playMusic 区别只是限制两次播放的时间间隔

        synchronized (this){
            if(!mInitDone){
                return ;
            }
        }

        long currTimeStamp = System.nanoTime();
        if (currTimeStamp - preTimeStamp < 500000000L) {
            return;
        }
        preTimeStamp = currTimeStamp;
        @SuppressWarnings("static-access")
        AudioManager am = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        float steamVolumCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC);//获得当前音量
        float steamVolumMax = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);//获得最大音量
        float volum = steamVolumCurrent / steamVolumMax;//计算声音播放的音量
        synchronized (mSoundPoolLock) {
            if (sp != null) {
                sp.play(                // 播放
                        hm.get(sound),  // 声音资源id
                        volum,          // 左声道音量
                        volum,          // 右声道音量
                        1,              // 优先级
                        loop,           // 循环次数 -1代表永远循环
                        1               // 回放速度0.5f～2.0f之间
                );
            }
        }

    }

    public void stopGameMusic(int sound) {
        synchronized (this){
            if(!mInitDone){
                return ;
            }
        }
        synchronized (mSoundPoolLock){
            if(sp!=null){
                sp.pause(sound);
                sp.stop(sound);
                sp.setVolume(sound, 0, 0);
            }
        }

    }

    public void destroy() {
        synchronized (this){
            if(!mInitDone){
                return ;
            }
            mDestroyDone = true ;
            mInitDone = false;
            this.notifyAll();
        }
        synchronized (mSoundPoolLock){
            if(sp!=null){
                sp.release();
                sp = null;
            }
        }
        synchronized (mMediaPlayerLock){
            if(mp!=null){
                mp.pause();
                mp.release();
                mp = null;
            }
        }

    }
}

