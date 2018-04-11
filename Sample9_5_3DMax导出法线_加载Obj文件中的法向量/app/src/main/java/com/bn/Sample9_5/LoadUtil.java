package com.bn.Sample9_5;//声明包
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import android.content.res.Resources;
import android.util.Log;

public class LoadUtil 
{		
	//从obj文件中加载携带顶点信息的物体
    public static LoadedObjectVertexNormalTexture loadFromFile
    (String fname, Resources r,MySurfaceView mv)
    {
    	//加载后物体的引用
    	LoadedObjectVertexNormalTexture lo=null;

    	ArrayList<Float> alv=new ArrayList<Float>();//原始顶点坐标列表--直接从obj文件中加载
    	ArrayList<Float> alt=new ArrayList<Float>();//原始纹理坐标列表
    	ArrayList<Float> aln=new ArrayList<Float>();//原始法向量列表


		ArrayList<Float> alvResult=new ArrayList<Float>();//结果顶点坐标列表--按面组织好
		ArrayList<Float> altResult=new ArrayList<Float>();//纹理坐标结果列表
		ArrayList<Float> alnResult=new ArrayList<Float>();//法向量结果列表

    	
    	try
    	{
    		InputStream in=r.getAssets().open(fname);
    		InputStreamReader isr=new InputStreamReader(in);
    		BufferedReader br=new BufferedReader(isr);
    		String temps=null;
    		
    		//扫面文件，根据行类型的不同执行不同的处理逻辑
		    while((temps=br.readLine())!=null) 
		    {//读取一行文本
		    	
		    	String[] tempsa=temps.split("[ ]+");//将文本行用空格符切分
		      	if(tempsa[0].trim().equals("v"))
		      	{//此行为顶点坐标行
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
		      		aln.add(Float.parseFloat(tempsa[1]));//放进aln列表中
		      		aln.add(Float.parseFloat(tempsa[2])); //放进aln列表中
		      		aln.add(Float.parseFloat(tempsa[3])); //放进aln列表中
		      	}
		      	else if(tempsa[0].trim().equals("f")) //此行为三角形面
		      	{
					String[] part1 = tempsa[1].split("/");// hhl  修改只split一次
					String[] part2 = tempsa[2].split("/");
					String[] part3 = tempsa[3].split("/");


					// 将纹理坐标 组织到 结果纹理坐标列表 中
		      		int index ;
					index=Integer.parseInt(part1[0])-1;
		      		alvResult.add(alv.get(3*index));
		      		alvResult.add(alv.get(3*index+1));
		      		alvResult.add(alv.get(3*index+2));

		      		index=Integer.parseInt(part2[0])-1;
		      		alvResult.add(alv.get(3*index));
		      		alvResult.add(alv.get(3*index+1));
		      		alvResult.add(alv.get(3*index+2));

		      		index=Integer.parseInt(part3[0])-1;
		      		alvResult.add(alv.get(3*index));
		      		alvResult.add(alv.get(3*index+1));
		      		alvResult.add(alv.get(3*index+2));


		      		// 将纹理坐标 组织到 结果纹理坐标列表 中
		      		int indexTex;
					indexTex=Integer.parseInt(part1[1])-1;//第0个顶点的纹理坐标
		      		altResult.add(alt.get(indexTex*2));
		      		altResult.add(alt.get(indexTex*2+1));

		      		indexTex=Integer.parseInt(part2[1])-1;//第1个顶点的纹理坐标
		      		altResult.add(alt.get(indexTex*2));
		      		altResult.add(alt.get(indexTex*2+1));

		      		indexTex=Integer.parseInt(part3[1])-1;//第2个顶点的纹理坐标
		      		altResult.add(alt.get(indexTex*2));
		      		altResult.add(alt.get(indexTex*2+1));		      		


					// 将法向量 组织到 结果法向量坐标列表中
		      		int indexN ;
					indexN =Integer.parseInt(part1[2])-1;//获取法向量编号
		      		alnResult.add(aln.get(3*indexN));	//获取法向量的x值
		      		alnResult.add(aln.get(3*indexN+1));	//获取法向量的y值
		      		alnResult.add(aln.get(3*indexN+2));	///获取法向量的z值

		      		indexN=Integer.parseInt(part2[2])-1;
		      		alnResult.add(aln.get(3*indexN));
		      		alnResult.add(aln.get(3*indexN+1));
		      		alnResult.add(aln.get(3*indexN+2));

		      		indexN=Integer.parseInt(part3[2])-1;
		      		alnResult.add(aln.get(3*indexN));
		      		alnResult.add(aln.get(3*indexN+1));
		      		alnResult.add(aln.get(3*indexN+2));
		      	}		      		
		    } 
		    

		    // hhl 下面将ArrayList<float>转成数组float[]

		    float[] vXYZ=new float[alvResult.size()];//生成顶点数组
		    for(int i=0;i<alvResult.size();i++)
		    {
		    	vXYZ[i]=alvResult.get(i);
		    } 

		    float[] tST=new float[altResult.size()]; //生成纹理数组
		    for(int i=0;i<altResult.size();i++)
		    {
		    	tST[i]=altResult.get(i);
		    }

		    float[] nXYZ=new float[alnResult.size()];//生成法向量数组
		    for(int i=0;i<alnResult.size();i++)
		    {
		    	nXYZ[i]=alnResult.get(i);
		    }
		    
		    //创建加载物体对象
		    lo=new LoadedObjectVertexNormalTexture(mv,vXYZ,nXYZ,tST);
    	}
    	catch(Exception e)
    	{
    		Log.d("load error", "load error");
    		e.printStackTrace();
    	}    	
    	return lo;//返回创建的物体对象的引用
    }
}
