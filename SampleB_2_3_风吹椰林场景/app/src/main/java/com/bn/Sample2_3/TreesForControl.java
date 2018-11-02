package com.bn.Sample2_3;
import java.util.ArrayList;
import java.util.Collections;

import android.opengl.GLES30;

import static com.bn.Sample2_3.Constant.*;
//表示树的控制类,用于控制所有的树
public class TreesForControl 
{
	float height;
	//存储所有树的列表
    ArrayList<TreeTrunkControl> treeTrunkList=new ArrayList<TreeTrunkControl>();
    ArrayList<TreeLeavesControl> treeLeavesList=new ArrayList<TreeLeavesControl>();
    public TreesForControl(TreeTrunk treeTrunk,TreeLeaves[] treeLeaves)
    {
    	//扫描地图生成各个位置的椰子树
    	for(int i=0;i<MAP_TREE.length;i++)
    	{
    		//创建所有的树干
			treeTrunkList.add(new TreeTrunkControl(MAP_TREE[i][0],MAP_TREE[i][1],MAP_TREE[i][2],treeTrunk));
			//创建所有的树叶
			for(TreeLeaves tempLeaves:treeLeaves) // 每一颗树木的叶子和树干的整体位移是一样的，都是(MAP_TREE[i][0],MAP_TREE[i][1],MAP_TREE[i][2])
			{
				treeLeavesList.add(new TreeLeavesControl(MAP_TREE[i][0],MAP_TREE[i][1],MAP_TREE[i][2],tempLeaves));
			} // treeLeavesList 包含了6棵数目的所有的叶子  用于排序
    	}
    }
    //绘制列表中的每一颗树
    public void drawSelf(int tex_leavesId,int tex_treejointId,float bend_R,float wind_direction)
    {
    	//绘制树干
    	for(TreeTrunkControl tempTrunk:treeTrunkList)
    	{
    		tempTrunk.drawSelf(tex_treejointId, bend_R, wind_direction);
    	}
    	//对所有的叶子进行排序( hhl 由于叶子是带有透明的，所以要排序)
    	Collections.sort(treeLeavesList);
    	//开启混合 
        GLES30.glEnable(GLES30.GL_BLEND);
        //设置混合因子
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA);
        //关闭背面剪裁
        GLES30.glDisable(GLES30.GL_CULL_FACE);
    	//绘制所有的叶子
    	for(TreeLeavesControl tempLeaves:treeLeavesList)
    	{
    		tempLeaves.drawSelf(tex_leavesId,bend_R,wind_direction);
    	}
    	//打开背面剪裁
		GLES30.glEnable(GLES30.GL_CULL_FACE);
    	GLES30.glDisable(GLES30.GL_BLEND);

    }
}
