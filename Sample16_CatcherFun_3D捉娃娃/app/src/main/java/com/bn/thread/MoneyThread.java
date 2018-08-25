package com.bn.thread;

import com.bn.view.GameView;

import static com.bn.constant.SourceConstant.*;

public class MoneyThread extends Thread //监听键盘状态的线程
{
    GameView gv;
    long currtime;
    long pretime;

    public MoneyThread(GameView mv) {
        super("MoneyThread");
        this.gv = mv;
        pretime = System.currentTimeMillis();

    }

    private boolean mIsStop = false;
    public void quitSync(){
        mIsStop = true ;
        try {
            interrupt(); // hhl: wake up from sleep if sleep
            join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {

        try {
            while (!mIsStop) {
                currtime = System.currentTimeMillis();
                if (moneycount == 20) { // 最高20个金币
                    pretime = currtime;
                } else if (moneycount < 20 && (currtime - pretime > 3 * 60  *1000 )) { // 每三分钟增加一个金币
                    moneycount++;
                    pretime = currtime;
                }
                if (moneycount >= 1) {
                    gv.ismoneyout = false;
                }
                Thread.sleep(20);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

	
