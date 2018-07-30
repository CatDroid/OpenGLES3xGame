package com.bn.Sample9_1;//声明包
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.res.Resources;
import android.util.Log;

public class LoadUtil 
{
	//从obj文件中加载顶点数据并生成LoadedObjectVertexOnly类对象
    public static LoadedObjectVertexOnly loadFromFile(String fname, Resources r,MySurfaceView mv)
    {
    	LoadedObjectVertexOnly lo=null;//加载后物体的引用
    	
    	ArrayList<Float> alv=new ArrayList<Float>();//原始顶点坐标列表
    	ArrayList<Float> alvResult=new ArrayList<Float>();//结果顶点坐标列表
    	
    	try
    	{
    		InputStream in=r.getAssets().open(fname);//获取字节输入流
    		InputStreamReader isr=new InputStreamReader(in);//将字节流转换成字符流
    		BufferedReader br=new BufferedReader(isr);//将字符流进一步封装
    		String temps=null;
    		
		    while((temps=br.readLine())!=null)//读取一行文本
		    {
		    	String[] tempsa=temps.split("[ ]+");//将文本行用空格符切分
		      	if(tempsa[0].trim().equals("v"))//顶点坐标行
		      	{
		      		alv.add(Float.parseFloat(tempsa[1]));//将顶点的x、y、z坐标存入
		      		alv.add(Float.parseFloat(tempsa[2]));//原始顶点坐标列表
		      		alv.add(Float.parseFloat(tempsa[3]));
		      	}
		      	else if(tempsa[0].trim().equals("f"))//面数据行
		      	{
		      		int index=Integer.parseInt(tempsa[1].split("/")[0])-1;//得到顶点编号
		      		//将三角形第1个顶点的x、y、z坐标存入结果顶点坐标列表
		      		alvResult.add(alv.get(3*index));
		      		alvResult.add(alv.get(3*index+1));
		      		alvResult.add(alv.get(3*index+2));
		      		
		      		index=Integer.parseInt(tempsa[2].split("/")[0])-1; //得到顶点编号
		      		//将三角形第2个顶点的x、y、z坐标存入结果顶点坐标列表
		      		alvResult.add(alv.get(3*index));
		      		alvResult.add(alv.get(3*index+1));
		      		alvResult.add(alv.get(3*index+2));
		      		
		      		index=Integer.parseInt(tempsa[3].split("/")[0])-1;//得到顶点编号
		      		//将三角形第3个顶点的x、y、z坐标存入结果顶点坐标列表
		      		alvResult.add(alv.get(3*index));
		      		alvResult.add(alv.get(3*index+1));
		      		alvResult.add(alv.get(3*index+2));	
		      	}		      		
		    } 
		    
		    //生成顶点数组
		    int size=alvResult.size();//获取顶点坐标数量
		    float[] vXYZ=new float[size];//创建用于存储顶点坐标的数组
		    for(int i=0;i<size;i++)
		    {
		    	vXYZ[i]=alvResult.get(i);//将顶点坐标数据转存到数组中
		    }
		    
		    lo=new LoadedObjectVertexOnly(mv,vXYZ);//创建物体对象
    	}
    	catch(Exception e)
    	{
    		Log.d("load error", "load error");
    		e.printStackTrace();
    	}    	
    	return lo;//返回创建物体对象的引用
    }
}
