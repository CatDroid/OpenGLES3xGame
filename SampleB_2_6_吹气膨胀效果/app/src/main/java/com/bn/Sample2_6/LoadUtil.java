package com.bn.Sample2_6;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import android.content.res.Resources;
import android.util.Log;

public class LoadUtil 
{	
	//求两个向量的叉积
	public static float[] getCrossProduct(float x1,float y1,float z1,float x2,float y2,float z2)
	{		
		//求出两个矢量叉积矢量在XYZ轴的分量ABC
        float A=y1*z2-y2*z1;
        float B=z1*x2-z2*x1;
        float C=x1*y2-x2*y1;
		
		return new float[]{A,B,C};
	}
	
	//向量规格化
	public static float[] vectorNormal(float[] vector)
	{
		//求向量的模
		float module=(float)Math.sqrt(vector[0]*vector[0]+vector[1]*vector[1]+vector[2]*vector[2]);
		return new float[]{vector[0]/module,vector[1]/module,vector[2]/module};
	}
	//从obj文件中加载携带顶点信息的物体
    public static LoadedObjectVertexNormalTexture loadFromFile
    (String fname, Resources r,MySurfaceView mv)
    {
    	//加载后物体的引用
    	LoadedObjectVertexNormalTexture lo=null;
    	//原始顶点坐标列表--直接从obj文件中加载
    	ArrayList<Float> alv=new ArrayList<Float>();
    	//结果顶点坐标列表--按面组织好
    	ArrayList<Float> alvResult=new ArrayList<Float>(); 
    	//原始纹理坐标列表
    	ArrayList<Float> alt=new ArrayList<Float>();  
    	//纹理坐标结果列表
    	ArrayList<Float> altResult=new ArrayList<Float>();  
    	//原始面法向量列表
    	ArrayList<Float> aln=new ArrayList<Float>();    
    	//结果面法向量列表
    	ArrayList<Float> alnResult=new ArrayList<Float>();    
    	
    	try
    	{
    		InputStream in=r.getAssets().open(fname);
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
		      	{//此行为纹理坐标行
		      		//若为纹理坐标行则提取ST坐标并添加进原始纹理坐标列表中
		      		alt.add(Float.parseFloat(tempsa[1]));
		      		alt.add(1-Float.parseFloat(tempsa[2])); 
		      	}
		      	else if(tempsa[0].trim().equals("vn"))
		      	{//此行为法向量行
		      		//若为纹理坐标行则提取ST坐标并添加进原始纹理坐标列表中
		      		aln.add(Float.parseFloat(tempsa[1]));
		      		aln.add(Float.parseFloat(tempsa[2])); 
		      		aln.add(Float.parseFloat(tempsa[3])); 
		      	}
		      	else if(tempsa[0].trim().equals("f")) 
		      	{//此行为三角形面	      		
		      		//计算第0个顶点的索引，并获取此顶点的XYZ三个坐标	      		
		      		int index=Integer.parseInt(tempsa[1].split("/")[0])-1;
		      		float x0=alv.get(3*index);
		      		float y0=alv.get(3*index+1);
		      		float z0=alv.get(3*index+2);
		      		alvResult.add(x0);
		      		alvResult.add(y0);
		      		alvResult.add(z0);		
		      		
		      	    //计算第1个顶点的索引，并获取此顶点的XYZ三个坐标	  
		      		index=Integer.parseInt(tempsa[2].split("/")[0])-1;
		      		float x1=alv.get(3*index);
		      		float y1=alv.get(3*index+1);
		      		float z1=alv.get(3*index+2);
		      		alvResult.add(x1);
		      		alvResult.add(y1);
		      		alvResult.add(z1);
		      		
		      	    //计算第2个顶点的索引，并获取此顶点的XYZ三个坐标	
		      		index=Integer.parseInt(tempsa[3].split("/")[0])-1;
		      		float x2=alv.get(3*index);
		      		float y2=alv.get(3*index+1);
		      		float z2=alv.get(3*index+2);
		      		alvResult.add(x2);
		      		alvResult.add(y2); 
		      		alvResult.add(z2);			      		
		      		
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
		      		
		      	    //=================================================
		      		//计算第0个顶点的索引，并获取此顶点的XYZ三个坐标	
		      		int indexN=Integer.parseInt(tempsa[1].split("/")[2])-1;
		      		float nx0=aln.get(3*indexN);
		      		float ny0=aln.get(3*indexN+1);
		      		float nz0=aln.get(3*indexN+2);
		      		alnResult.add(nx0);
		      		alnResult.add(ny0);
		      		alnResult.add(nz0);		
		      		
		      	    //计算第1个顶点的索引，并获取此顶点的XYZ三个坐标	  
		      		indexN=Integer.parseInt(tempsa[2].split("/")[2])-1;
		      		float nx1=aln.get(3*indexN);
		      		float ny1=aln.get(3*indexN+1);
		      		float nz1=aln.get(3*indexN+2);
		      		alnResult.add(nx1);
		      		alnResult.add(ny1);
		      		alnResult.add(nz1);
		      		
		      	    //计算第2个顶点的索引，并获取此顶点的XYZ三个坐标	
		      		indexN=Integer.parseInt(tempsa[3].split("/")[2])-1;
		      		float nx2=aln.get(3*indexN);
		      		float ny2=aln.get(3*indexN+1);
		      		float nz2=aln.get(3*indexN+2);
		      		alnResult.add(nx2);
		      		alnResult.add(ny2); 
		      		alnResult.add(nz2);	
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
		    	tST[i]=altResult.get(i);
		    }
		    
		    //生成法向量数组
		    size=alnResult.size();
		    float[] nXYZ=new float[size];
		    for(int i=0;i<size;i++)
		    {
		    	nXYZ[i]=alnResult.get(i);
		    }
		    
		    //创建3D物体对象
		    lo=new LoadedObjectVertexNormalTexture(mv,vXYZ,nXYZ,tST);
    	}
    	catch(Exception e)
    	{
    		Log.d("load error", "load error");
    		e.printStackTrace();
    	}    	
    	return lo;
    }
}
