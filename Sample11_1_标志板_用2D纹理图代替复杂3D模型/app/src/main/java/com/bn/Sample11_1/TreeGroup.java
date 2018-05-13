package com.bn.Sample11_1;

import static com.bn.Sample11_1.MySurfaceView.*;
import java.util.ArrayList;
import java.util.List;

public class TreeGroup
{
	TreeForDraw tfd;
	List<SingleTree> alist=new ArrayList<SingleTree>();//创建SingleTree对象列表
	
	public TreeGroup(MySurfaceView mv)
	{
		tfd=new TreeForDraw(mv);//创建TreeForDraw对象
		alist.add(new SingleTree(0,0,0,this));//创建一个SingleTree对象并加入列表
		alist.add(new SingleTree(8*UNIT_SIZE,0,0,this));
		alist.add(new SingleTree(5.7f*UNIT_SIZE,5.7f*UNIT_SIZE,0,this));
		alist.add(new SingleTree(0,-8*UNIT_SIZE,0,this));
		alist.add(new SingleTree(-5.7f*UNIT_SIZE,5.7f*UNIT_SIZE,0,this));
		alist.add(new SingleTree(-8*UNIT_SIZE,0,0,this));
		alist.add(new SingleTree(-5.7f*UNIT_SIZE,-5.7f*UNIT_SIZE,0,this));
		alist.add(new SingleTree(0,8*UNIT_SIZE,0,this));
		alist.add(new SingleTree(5.7f*UNIT_SIZE,-5.7f*UNIT_SIZE,0,this));
	}
	public void calculateBillboardDirection()
    {//计算列表中每个树木的朝向
    	for(int i=0;i<alist.size();i++)
    	{
    		alist.get(i).calculateBillboardDirection();//计算每个植物纹理矩形的朝向
    	}
    }
    
    public void drawSelf(int texId)
    {//绘制列表中的植物
    	for(int i=0;i<alist.size();i++)
    	{
    		alist.get(i).drawSelf(texId);//调用SingleTree对象的drawSelf方法绘制植物
    	}
    }
}