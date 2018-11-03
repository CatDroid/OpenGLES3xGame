package com.bn.Sample2_4;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import android.util.Log;
import static com.bn.Sample2_4.Constant.*;

public class LoadUtil 
{
	//从obj文件中加载携带顶点信息的物体，并自动计算每个顶点的平均法向量
    public static float[][] loadFromFileVertexOnly(String fname,MySurfaceView mv)
    {
    	float [][] gledeInfo=new float[2][];
    	
    	//原始顶点坐标列表--直接从obj文件中加载
    	ArrayList<Float> alv=new ArrayList<Float>();
    	//顶点组装面索引列表--根据面的信息从文件中加载
    	ArrayList<Integer> alFaceIndex=new ArrayList<Integer>();
    	//结果顶点坐标列表--按面组织好
    	ArrayList<Float> alvResult=new ArrayList<Float>();    	
    	//平均前各个索引对应的点的法向量集合Map
    	//原始纹理坐标列表
    	ArrayList<Float> alt=new ArrayList<Float>();  
    	//纹理坐标结果列表
    	ArrayList<Float> altResult=new ArrayList<Float>();  
    	
    	try
    	{
    		InputStream in=mv.getResources().getAssets().open(fname);
    		InputStreamReader isr=new InputStreamReader(in);
    		BufferedReader br=new BufferedReader(isr);
    		String temps=null;
    		
    		//扫面文件，根据行类型的不同执行不同的处理逻辑
		    while((temps=br.readLine())!=null) 
		    {
		    	//用空格分割行中的各个组成部分
		    	String[] tempsa=temps.split("[ ]+");
		      	if(tempsa[0].trim().equals("v"))
		      	{//此行为顶点坐标
		      	    //若为顶点坐标行则提取出此顶点的XYZ坐标添加到原始顶点坐标列表中
		      		alv.add(Float.parseFloat(tempsa[1]));
		      		alv.add(Float.parseFloat(tempsa[2]));
		      		alv.add(Float.parseFloat(tempsa[3]));
		      	}
		      	else if(tempsa[0].trim().equals("vt"))
		      	{
		      		alt.add(Float.parseFloat(tempsa[1])*MAX_S_GHXP);
		      		alt.add(Float.parseFloat(tempsa[2])*MAX_T_GHXP); 
		      	}
		      	else if(tempsa[0].trim().equals("f")) 
		      	{
		      		int[] index=new int[3];//三个顶点索引值的数组
		      		//计算第0个顶点的索引，并获取此顶点的XYZ三个坐标	      		
		      		index[0]=Integer.parseInt(tempsa[1].split("/")[0])-1; // hhl obj文件的顶点坐标和纹理坐标索引从1开始
		      		float x0=alv.get(3*index[0]);
		      		float y0=alv.get(3*index[0]+1);
		      		float z0=alv.get(3*index[0]+2);
		      		alvResult.add(x0);
		      		alvResult.add(y0);
		      		alvResult.add(z0);		
		      		
		      	    //计算第1个顶点的索引，并获取此顶点的XYZ三个坐标	  
		      		index[1]=Integer.parseInt(tempsa[2].split("/")[0])-1;
		      		float x1=alv.get(3*index[1]);
		      		float y1=alv.get(3*index[1]+1);
		      		float z1=alv.get(3*index[1]+2);
		      		alvResult.add(x1);
		      		alvResult.add(y1);
		      		alvResult.add(z1);
		      		
		      	    //计算第2个顶点的索引，并获取此顶点的XYZ三个坐标	
		      		index[2]=Integer.parseInt(tempsa[3].split("/")[0])-1;
		      		float x2=alv.get(3*index[2]);
		      		float y2=alv.get(3*index[2]+1);
		      		float z2=alv.get(3*index[2]+2);
		      		alvResult.add(x2);
		      		alvResult.add(y2); 
		      		alvResult.add(z2);	
		      		
		      		//记录此面的顶点索引
		      		alFaceIndex.add(index[0]);
		      		alFaceIndex.add(index[1]);
		      		alFaceIndex.add(index[2]);
		      		
		      		//将纹理坐标组织到结果纹理坐标列表中
		      		//第0个顶点的纹理坐标
		      		int indexTex=Integer.parseInt(tempsa[1].split("/")[1])-1;
		      		altResult.add(alt.get(indexTex*2));
		      		altResult.add(alt.get(indexTex*2+1));
		      	    //第1个顶点的纹理坐标
		      		indexTex=Integer.parseInt(tempsa[2].split("/")[1])-1;
		      		altResult.add(alt.get(indexTex*2));
		      		altResult.add(alt.get(indexTex*2+1));
		      	    //第2个顶点的纹理坐标
		      		indexTex=Integer.parseInt(tempsa[3].split("/")[1])-1;
		      		altResult.add(alt.get(indexTex*2));
		      		altResult.add(alt.get(indexTex*2+1));
		      	}		      		
		    } 
		    //生成顶点数组
		    int size=alvResult.size();
		    float[] vXYZ=new float[size];
		    for(int i=0;i<size;i++)
		    {
		    	vXYZ[i]=alvResult.get(i);
		    }
		    //生成纹理数组
		    size=altResult.size();
		    float[] tST=new float[size];
		    for(int i=0;i<size;i++)
		    {
		    	if(i%2==1){
		    		tST[i]=1-altResult.get(i);
		    	}else
		    	tST[i]=altResult.get(i);
		    }
		    
		    gledeInfo[0]=vXYZ;
		    gledeInfo[1]=tST;
		    
    	}
    	catch(Exception e)
    	{
    		Log.d("load error", "load error");
    		e.printStackTrace();
    	}   
    	return gledeInfo;
    }
}
