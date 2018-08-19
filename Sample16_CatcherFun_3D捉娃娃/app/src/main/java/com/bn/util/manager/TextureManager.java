 package com.bn.util.manager;
 import com.bn.catcherFun.MySurfaceView;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLUtils;

public class TextureManager
{
	static String[] texturesName={
		"claw.png","gan.png","hengtiao.png","floor.jpg","showscore.png",//5
		"jb.png","down.png","up.png","tod.png","tou.png",//10
		"tol.png","tor.png","doll1.png","doll2.png","hole.png",//15
		"doll0.png","dollbox.png","holebox.png","niu.png","dunId.png",//20
		"tv.png","catch.png","MainView_Background.png","Button_StartDown.png","Button_Start.png",//25
		
		"load.png","config_collectionsDown.png","config_collections.png","Button_Tutorail.png","Button_TutorailDown.png",//30
		"Box1.png","MainGame_start.png","MainGame_startDown.png","Tex_MoneyBox.png","Tex_Money.png"//35
		,"ganbox.png","HB.png","shuaxin.png","shuaxin_Down.png","parrot.png",//40
		"menu.png","set.png","off.png","0.png","1.png",//45
		"2.png","3.png","4.png","5.png","6.png",//50
		"7.png","8.png","9.png","x.png","background.png",//55
		"shutiao.png","back.png","salebackground.png","xing1.png","xing2.png",//60
		"sell.png","sell_down.png","car.png","camera.png","robot.png",//65
		"page0.png","page1.png","page2.png","page3.png","page4.png",//70
		"button_back.png","Game_AboutDown.png","aboutText.png","Game_About.png","Button_Config.png",//75
		"Button_ConfigDown.png","button_score.png","button_score_Down.png","score_background.png","%.png",//80
		"lock.png","message.png","Box2.png","catchbackground.png","stars.png",//85
		"stars2.png","fire.png","lu.png","button_quit_Down.png","button_quit.png",//90
		"lu1.png","backText.png",//93
		
		};//纹理图的名称
	
	static HashMap<String,Integer> texList=new HashMap<String,Integer>();//放纹理图的列表
	public static int initTexture(MySurfaceView mv,String texName,boolean isRepeat)//生成纹理id
	{
		int[] textures=new int[1];
		GLES30.glGenTextures
		(
				1,//产生的纹理id的数量
				textures,//纹理id的数组
				0//偏移量
		);
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[0]);//绑定纹理id
		//设置MAG时为线性采样
		GLES30.glTexParameterf
		(
				GLES30.GL_TEXTURE_2D,
				GLES30.GL_TEXTURE_MAG_FILTER,
				GLES30.GL_LINEAR
		);
		//设置MIN时为最近点采样
		GLES30.glTexParameterf
		(
				GLES30.GL_TEXTURE_2D,
				GLES30.GL_TEXTURE_MIN_FILTER, 
				GLES30.GL_NEAREST
		);
		if(isRepeat)
		{
			//设置S轴的拉伸方式为重复拉伸
			GLES30.glTexParameterf
			(
					GLES30.GL_TEXTURE_2D,
					GLES30.GL_TEXTURE_WRAP_S, 
					GLES30.GL_REPEAT
			);
			//设置T轴的拉伸方式为重复拉伸
			GLES30.glTexParameterf
			(
					GLES30.GL_TEXTURE_2D,
					GLES30.GL_TEXTURE_WRAP_T, 
					GLES30.GL_REPEAT
			);
		}else
		{
			//设置S轴的拉伸方式为截取
			GLES30.glTexParameterf
			(
					GLES30.GL_TEXTURE_2D,
					GLES30.GL_TEXTURE_WRAP_S, 
					GLES30.GL_CLAMP_TO_EDGE
			);
			//设置T轴的拉伸方式为截取
			GLES30.glTexParameterf
			(
					GLES30.GL_TEXTURE_2D,
					GLES30.GL_TEXTURE_WRAP_T, 
					GLES30.GL_CLAMP_TO_EDGE
			);
		}
		String path="pic/"+texName;//定义图片路径
		InputStream in = null;
		try {
			in = mv.getResources().getAssets().open(path);
		}catch (IOException e) {
			e.printStackTrace();
		}
		Bitmap bitmap=BitmapFactory.decodeStream(in);//从流中加载图片内容
		GLUtils.texImage2D
		(
				GLES30.GL_TEXTURE_2D,//纹理类型，在OpenGL ES中必须为GL10.GL_TEXTURE_2D
				0,//纹理的层次，0表示基本图像层，可以理解为直接贴图
				bitmap,//纹理图像
				0//纹理边框尺寸
		);
		bitmap.recycle();//纹理加载成功后释放内存中的纹理图
		return textures[0];
	}
	
	public static void loadingTexture(MySurfaceView mv,int start,int picNum)//加载所有纹理图
	{
		for(int i=start;i<start+picNum;i++)
		{
			int texture=0;
			if((texturesName[i].equals("claw.png"))||(texturesName[i].equals("gan.png"))
					||(texturesName[i].equals("f6.png"))||(texturesName[i].equals("floor.jpg"))
					||(texturesName[i].equals("doll1.png"))||(texturesName[i].equals("doll2.png"))
					||(texturesName[i].equals("hole.png"))||(texturesName[i].equals("doll0.png"))
					||(texturesName[i].equals("dollbox.png"))
					||(texturesName[i].equals("holebox.png"))||(texturesName[i].equals("ganbox.png"))
					||(texturesName[i].equals("car.png"))||(texturesName[i].equals("camera.png"))
					||(texturesName[i].equals("robot.png"))||(texturesName[i].equals("jb.png"))
					||(texturesName[i].equals("floor1.png"))
					)
			{
				texture=initTexture(mv,texturesName[i],true);
			}else
			{
				texture=initTexture(mv,texturesName[i],false);
			}
			texList.put(texturesName[i],texture);//将数据加入到列表中
		}
	}
	public static int getTextures(String texName)//获得纹理图
	{
		int result=0;
		if(texList.get(texName)!=null)//如果列表中有此纹理图
		{
			result=texList.get(texName);//获取纹理图
		}else
		{
			result=-1;
		}
		return result;
	}
}
