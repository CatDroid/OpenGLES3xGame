package com.bn.Sample12_2;

import static com.bn.Sample12_2.MySurfaceView.*;
import java.util.ArrayList;
import java.util.List;

public class TreeGroup
{
	TreeForDraw tfd;
	List<SingleTree> alist=new ArrayList<SingleTree>();
	
	public TreeGroup(MySurfaceView mv)
	{
		tfd=new TreeForDraw(mv);
		alist.add(new SingleTree(0,0,0,this));
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
    {
    	//计算列表中每个树木的朝向
    	for(int i=0;i<alist.size();i++)
    	{
    		alist.get(i).calculateBillboardDirection();
    	}
    }
    
    public void drawSelf(int texId)
    {//绘制列表中的每个树木
    	for(int i=0;i<alist.size();i++)
    	{
    		alist.get(i).drawSelf(texId);
    	}
    }
}