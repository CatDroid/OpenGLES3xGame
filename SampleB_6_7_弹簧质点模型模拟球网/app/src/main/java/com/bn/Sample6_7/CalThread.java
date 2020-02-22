package com.bn.Sample6_7;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.bn.util.Vector3f;

public class CalThread extends Thread
{
    boolean flag=true;
    ParticleControl pc;
    Vector3f ballP;
    FloatBuffer mVertexBuffer;
    float[] vdata;
    
    public CalThread(ParticleControl pc)
    {
    	this.pc=pc;
    }
    
    public void run()
    {
    	while(flag)
    	{
    		synchronized(Constant.lockB)
    		{
    		    pc.stepSimulation(0.010f);//进行物理计算
    		}

    		vdata = pc.getVerties(); // 获取布料/网络的数据

    		ballP = pc.getBall();    // 获得球的位置

            ByteBuffer vbb = ByteBuffer.allocateDirect(vdata.length * 4);
            vbb.order(ByteOrder.nativeOrder());
            mVertexBuffer = vbb.asFloatBuffer();
            mVertexBuffer.put(vdata);
            mVertexBuffer.position(0);

    		synchronized(Constant.lockA)
    		{
    			Constant.mVertexBufferForFlag = mVertexBuffer;//将顶点缓冲引用指向新的数据
    			Constant.ballP = ballP;
    		}
    	}
    }
}

