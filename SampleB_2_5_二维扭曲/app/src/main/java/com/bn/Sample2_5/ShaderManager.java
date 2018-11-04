package com.bn.Sample2_5;

import android.content.res.Resources;
/*
 * 该shader管理器主要是用于加载shader和编译shader
 */
public class ShaderManager
{
	final static String[][] shaderName=
	{
		{"vertex_tex.sh","frag_tex.sh"},
	};
	static String[]mVertexShader=new String[shaderName.length];//顶点着色器字符串数组
	static String[]mFragmentShader=new String[shaderName.length];//片元着色器字符串数组
	static int[] program=new int[shaderName.length];//程序数组
	//加载shader字符串
	public static void loadCodeFromFile(Resources r)
	{
		for(int i=0;i<shaderName.length;i++)
		{
			//加载顶点着色器的脚本内容       
	        mVertexShader[i]=ShaderUtil.loadFromAssetsFile(shaderName[i][0],r);
	        //加载片元着色器的脚本内容 
	        mFragmentShader[i]=ShaderUtil.loadFromAssetsFile(shaderName[i][1], r);
		}	
	}
	//编译shader
	public static void compileShader()
	{
		for(int i=0;i<shaderName.length;i++)
		{
			program[i]=ShaderUtil.createProgram(mVertexShader[i], mFragmentShader[i]);
		}
	}
	//这里返回三角形shader
	public static int getTrangleShaderProgram()
	{
		return program[0];
	}
}
