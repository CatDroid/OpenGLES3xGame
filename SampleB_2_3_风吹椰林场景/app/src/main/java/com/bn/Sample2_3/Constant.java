package com.bn.Sample2_3;
import java.io.IOException;
import java.io.InputStream;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLES30;
import android.opengl.GLUtils;
//常量类
public class Constant 
{
	//系统常量
	//屏幕的宽高
	public static int SCREEN_WIDTH;
	public static int SCREEN_HEIGHT;
	//线程标志
	public static boolean flag_go=true;
	//单位宽度值
	public static final float UNIT_SIZE=100;
	//太阳光的位置
	public static final float[] sunPosition=
	{
		UNIT_SIZE*31*1.5f, 1000, UNIT_SIZE*31*1.2f
	};
	//--风吹的属性
	public static int wind=5;//当前的风力
	public static float wind_force_init=-4.0f;
	public static float wind_force=wind_force_init*1.006f;;
	public static float wind_speed_init=200;
	public static float wind_speed=wind_speed_init;
	public static float bend_R_max=5000;
	public static float bend_R=bend_R_max;//这里指的是节点圆柱中心到弯曲中心处的长度
	public static float wind_direction=0;//这里指的是风向的角度  
	public static void setWindForce(int windForce)//根据风力级数设置加速度和线程休眠时间
	{
		wind=windForce;
		switch(windForce)
		{
		case 0:
			bend_R=10000;
			break;
		case 1:
			bend_R=bend_R_max;
			wind_speed=wind_speed_init;
			wind_force=wind_force_init*1.1f;
			break;
		case 2:
			bend_R=bend_R_max;
			wind_speed=wind_speed_init;
			wind_force=wind_force_init*1.08f;
			break;
		case 3:
			bend_R=bend_R_max;
			wind_speed=wind_speed_init;
			wind_force=wind_force_init*1.05f;
			break;
		case 4:
			bend_R=bend_R_max;
			wind_speed=wind_speed_init;
			wind_force=wind_force_init*1.03f;
			break;
		case 5:
			bend_R=bend_R_max;
			wind_speed=wind_speed_init;
			wind_force=wind_force_init*1.006f;
			break;
		case 6:
			bend_R=bend_R_max;
			wind_speed=wind_speed_init;
			wind_force=wind_force_init*1.004f;
			break;
		case 7:
			bend_R=bend_R_max;
			wind_speed=wind_speed_init;
			wind_force=wind_force_init;
			break;
		case 8:
			bend_R=bend_R_max;
			wind_speed=wind_speed_init;
			wind_force=wind_force_init*0.999f;
			break;
		case 9:
			bend_R=bend_R_max;
			wind_speed=wind_speed_init;
			wind_force=wind_force_init*0.998f;
			break;
		case 10:
			bend_R=bend_R_max;
			wind_speed=wind_speed_init;
			wind_force=wind_force_init*0.997f;
			break;
		case 11:
			bend_R=bend_R_max;
			wind_speed=wind_speed_init;
			wind_force=wind_force_init*0.996f;
			break; 
		case 12:
			bend_R=bend_R_max;
			wind_speed=wind_speed_init;
			wind_force=wind_force_init*0.9952f;
			break;
		}
	}
	//摄像机参数
	public static float DISTANCE=3000.0f;//摄像机位置距离观察目标点的距离4700--600
	public static final float CAMERA_X=UNIT_SIZE*31*1.5f;//摄像机的观察点
	public static final float CAMERA_HEIGHT=80;//摄像机的高度
	public static final float CAMERA_Z=UNIT_SIZE*31*1.5f;//摄像机的观察点
	public static float camera_direction=225;//摄像机的观察方向//摄像机的方向角初始方向是Z州的负方向,逆时针旋转
	public static final float MOVE_SPAN=20f;//摄像机每次移动的位移
	//海底的相关参数
	public static final float FLOOR_WIDTH=UNIT_SIZE*31;//海底的宽度
	public static final float FLOOR_HEIGHT=UNIT_SIZE*31;//海底的高度
	public static final float[][] floor_array=//绘制山地的区域0表示海底1表示高山
	{
		{0,0,0},
		{0,1,0},
		{0,0,0}
	};
	//树干的属性
	public static float bottom_radius=15f;//树的底端半径
	public static float joint_height=15f;//每个节点的高度
	public static int joint_num=20;//节点的总共数目
	public static int joint_available_num=14;//节点的有效数目
	//------------叶子的属性
	public static float leaves_width=60f;//叶子的宽度;
	public static float leaves_height=60f;//叶子的高度
	public static float leaves_offset=-leaves_height/1.4f;//叶子相对于XZ平面的偏移量
	public static float leaves_absolute_height=joint_height*joint_available_num;//叶子的绝对高度
	public static float tree_YOffset=39.21f;
	//表示椰子树位置的矩阵
	public static final float[][] MAP_TREE=
	{
		{FLOOR_WIDTH*1.49f,tree_YOffset,FLOOR_WIDTH*1.54f},
		{FLOOR_WIDTH*1.48f,tree_YOffset,FLOOR_WIDTH*1.5f},
		{FLOOR_WIDTH*1.47f,tree_YOffset,FLOOR_WIDTH*1.4f},
		{FLOOR_WIDTH*1.5f,tree_YOffset,FLOOR_WIDTH*1.49f},
		{FLOOR_WIDTH*1.52f,tree_YOffset,FLOOR_WIDTH*1.4f},
		{FLOOR_WIDTH*1.48f,tree_YOffset,FLOOR_WIDTH*1.3f},
		{FLOOR_WIDTH*1.56f,tree_YOffset,FLOOR_WIDTH*1.35f},
		{FLOOR_WIDTH*1.48f,tree_YOffset,FLOOR_WIDTH*1.2f},
	};	
	//----------水面的属性
	public static final int ROWS=31*3;//水面的行数
	public static final int COLS=31*3;//水面的列数
	public static final float WATER_SPAN=UNIT_SIZE;//水面格子的单位间隔
	//加载纹理的方法
	public static int initTexture(Resources r,int drawableId)//textureId
	{
		//生成纹理ID
		int[] textures = new int[1];
		GLES30.glGenTextures
		(
				1,          //产生的纹理id的数量
				textures,   //纹理id的数组
				0           //偏移量
		);    
		int textureId=textures[0];    
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_REPEAT);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_REPEAT);
        //通过输入流加载图片===============begin===================
        InputStream is = r.openRawResource(drawableId);
        Bitmap bitmapTmp;
        try 
        {
        	bitmapTmp = BitmapFactory.decodeStream(is);
        } 
        finally 
        {
            try 
            {
                is.close();
            } 
            catch(IOException e) 
            {
                e.printStackTrace();
            }
        }
        //实际加载纹理
        GLUtils.texImage2D
        (
        		GLES30.GL_TEXTURE_2D,   //纹理类型，在OpenGL ES中必须为GL10.GL_TEXTURE_2D
        		0, 					  //纹理的层次，0表示基本图像层，可以理解为直接贴图
        		bitmapTmp, 			  //纹理图像
        		0					  //纹理边框尺寸
        );
        bitmapTmp.recycle(); 		  //纹理加载成功后释放图片
        return textureId;
	}
	//-----------灰度图相关属性
	public static final float LAND_HIGH_ADJUST=0f;//陆地的高度调整值
	public static final float LAND_HIGHEST=100f;//陆地最大高差
	public static float[][] LAND_ARRAY;//加载的灰度图数据的高度
	public static final float LAND_SPAN=UNIT_SIZE ;//陆地的单位宽度
	public static void initLand(Resources r,int texId)
	{  
		//从灰度图片中加载陆地上每个顶点的高度
		LAND_ARRAY=loadLandforms(r,texId);
	}
	//从灰度图片中加载陆地上每个顶点的高度
	public static float[][] loadLandforms(Resources resources,int texId)
	{
		Bitmap bt=BitmapFactory.decodeResource(resources, texId);
		int colsPlusOne=bt.getWidth();
		int rowsPlusOne=bt.getHeight(); 
		float[][] result=new float[rowsPlusOne][colsPlusOne];
		for(int i=0;i<rowsPlusOne;i++)
		{   
			for(int j=0;j<colsPlusOne;j++)
			{
				int color=bt.getPixel(j,i);
				int r=Color.red(color);
				int g=Color.green(color); 
				int b=Color.blue(color);
				int h=(r+g+b)/3;
				result[i][j]=h*LAND_HIGHEST/255-LAND_HIGH_ADJUST;  
			}
		}		
		return result;
	}
	//天空球的相关属性
	public static final float SKY_BALL_RADIUS=1.49F*FLOOR_WIDTH;//天空球的半径
	public static float sky_rotation=0;
}
