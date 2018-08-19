package com.bn.util.manager;

import com.bn.util.ShaderUtil;

import android.content.res.Resources;

public class ShaderManager
{
	final static int shaderCount=6;
	final static String[][] shaderName=
	{
		{"vertex.sh","frag.sh"},//0
		{"vertex_load2d.sh","frag_load2d.sh"},//1
		{"vertex_2d.sh","frag_2d.sh"},//2
		{"holebox_vertex.sh","holebox_frag.sh"},//3
		{"vertex_lz.sh","frag_lz.sh"},//4
		{"vertex_spng.sh","frag_spng.sh"},//5
		
	};
	static String[]mVertexShader=new String[shaderCount];
	static String[]mFragmentShader=new String[shaderCount];
	static int[] program=new int[shaderCount];
	
	public static void loadCodeFromFile(Resources r)
	{
		for(int i=0;i<shaderCount;i++)
		{
			//加载顶点着色器的脚本内容       
	        mVertexShader[i]=ShaderUtil.loadFromAssetsFile(shaderName[i][0],r);
	        //加载片元着色器的脚本内容 
	        mFragmentShader[i]=ShaderUtil.loadFromAssetsFile(shaderName[i][1], r);
		}	
	}
	
	//编译3D物体的shader
	public static void compileShader()
	{
		for(int i=0;i<shaderCount;i++)
		{
			program[i]=ShaderUtil.createProgram(mVertexShader[i], mFragmentShader[i]);
		}
	}
	//这里返回的是纹理的shader程序
	public static int getShader(int index)
	{
		return program[index];
	}
		
}
