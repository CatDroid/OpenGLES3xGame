package com.bn.thread;

import static com.bn.constant.SourceConstant.*;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3f;

import com.bn.object.BNAbstractDoll;
import com.bn.util.SliderHelper;
import com.bn.view.CollectionView;
import com.bn.view.GameView;
import com.bulletphysics.linearmath.Transform;

public class PhysicsThread extends Thread {
    GameView gv; //游戏界面引用
    Vector3f origin;//用来储存滑动关节中箱子的位置

    public PhysicsThread(GameView gv) {
        super("PhysicsThread");
        this.gv = gv;

    }

    private boolean mIsStop = false;

    public void quitSync() {
        mIsStop = true;
        try {
            interrupt();
            join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (!mIsStop) {
            try {
                origin = SliderHelper.cubeBody.getMotionState().getWorldTransform(new Transform()).origin;//获取箱子位置

                // 物理世界模拟 (进行一次物理模拟)
                gv.dynamicsWorld.stepSimulation(TIME_STEP, MAX_SUB_STEPS);

                delDoll();// 调用删除娃娃刚体的方法

                // 游戏界面 前后左右按键的响应
                if ((gv.keyState & 0x01) != 0) {//点击向前按钮
                    if (origin.z <= 14.7f) {
                        istop = false; // isstop isbottom isright isleft 标记哪个方向已经到了尽头不能移动

                        // 机械手  沿Z轴正方向移动 moveBy调用了很多物理世界API
                        gv.claw.moveBy(new Vector3f(0, 0, 0.05f));
                        if (origin.z < 14.65f && origin.z > 14.6f) {
                            isbottom = true;//向前按钮消失标志为true
                        }
                    }

                } else if ((gv.keyState & 0x02) != 0) {//点击向后按钮

                    isbottom = false;
                    if (origin.z >= 11.4f) {
                        gv.claw.moveBy(new Vector3f(0, 0, -0.05f));//沿Z轴负方向移动
                        if (origin.z > 11.4 && origin.z < 11.49) {
                            istop = true;//向后按钮消失标志为true
                        }
                    }
                } else if ((gv.keyState & 0x04) != 0) {//点击向左按钮
                    isright = false;
                    if (origin.x >= -0.85f) {
                        gv.claw.moveBy(new Vector3f(-0.05f, 0, 0));//沿X轴负方向移动
                        if (origin.x > -0.8f && origin.x < -0.75f) {
                            isleft = true;//向左按钮消失标志为true
                        }
                    }
                } else if ((gv.keyState & 0x08) != 0) {//点击向右按钮
                    isleft = false;
                    if (origin.x <= 1.2f) {
                        gv.claw.moveBy(new Vector3f(0.05f, 0, 0));//沿X轴正方向移动
                        if (origin.x == 1.15f) {
                            isright = true;//向右按钮消失标志为true
                        }
                    }
                }
                Thread.sleep(20);    //当前线程睡眠20毫秒  hhl 定时物理模拟
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    // 从物理世界删除刚体
    private void delDoll() {
        if (isupdate){// 如果点击刷新按钮
            for (int i = 0; i < updatedoll.size(); i++) {
                // 从物理世界删除所有的娃娃
                gv.dynamicsWorld.removeRigidBody(updatedoll.get(i).RigidBodydoll);//从物理世界中删除刚体

            }
            updatedoll.clear();     // 清空娃娃刚体列表
            isupdate = false;       // 刷新标志位置为false
            gv.update();            // 调用刷新娃娃方法，重新生成娃娃
        }

        // 刷新之后 刚好有娃娃 掉到 收纳箱中(带有透明玻璃围栏的盒子)
        List<BNAbstractDoll> removedoll = new ArrayList<BNAbstractDoll>();//存放要删除的娃娃对象

        for (int i = 0; i < updatedoll.size(); i++) {

            // 获取娃娃刚体位置
            Transform posi2 = updatedoll.get(i).RigidBodydoll.getMotionState().getWorldTransform(new Transform());

            // 判断娃娃是否处于收纳盒内
            if (posi2.origin.z > 14.4f && posi2.origin.x > 0.5f && posi2.origin.y < 1.8f) {

                updatedoll.get(i).isInBox = true;

                // 相应获取到的娃娃数量加1
                int count = dollcount[updatedoll.get(i).bianhao] + 1;
                dollcount[updatedoll.get(i).bianhao] = count;

                // 从物理世界中删除刚体
                gv.dynamicsWorld.removeRigidBody(updatedoll.get(i).RigidBodydoll);

                // 获取到娃娃数量加1
                removedoll.add(updatedoll.get(i));
                getcount++;

                // 调用计算抓到娃娃奖励方法
                CollectionView.CalculateAward();

                // 是否抓取成功标志位置为true
                gv.isSuccess = true;

                // 记录当前娃娃编号
                gv.successId = updatedoll.get(i).bianhao;
                break;
            }
        }

        // 循环删除指定娃娃对象
        for (int i = 0; i < removedoll.size(); i++){
            updatedoll.remove(removedoll.get(i));
        }
    }


}
