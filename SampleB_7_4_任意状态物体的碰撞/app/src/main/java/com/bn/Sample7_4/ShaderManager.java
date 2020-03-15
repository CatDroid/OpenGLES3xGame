package com.bn.Sample7_4;

import android.content.res.Resources;

public class ShaderManager {

    private final static int shaderCount = 2;
    private final static String[][] shaderName =
            {
                    {"vertex.glsl", "frag.glsl"},
                    {"vertex_color.glsl", "frag_color.glsl"}
            };

    private static String[] mVertexShader = new String[shaderName.length];
    private static String[] mFragmentShader = new String[shaderName.length];
    private static int[] program = new int[shaderName.length];

    public static void loadCodeFromFile(Resources r)
    {
        for (int i = 0; i < shaderName.length; i++)
        {
            //加载顶点着色器的脚本内容
            mVertexShader[i] = ShaderUtil.loadFromAssetsFile(shaderName[i][0], r);
            //加载片元着色器的脚本内容
            mFragmentShader[i] = ShaderUtil.loadFromAssetsFile(shaderName[i][1], r);
        }
    }

    //编译3D物体的shader
    public static void compileShader()
    {
        for (int i = 0; i < shaderName.length; i++)
        {
            program[i] = ShaderUtil.createProgram(mVertexShader[i], mFragmentShader[i]);
            System.out.println("mProgram " + program[i]);
        }
    }

    //这里返回的是纹理带光照的shader程序
    public static int getTextureLightShaderProgram()
    {
        return program[0];
    }

    //这里返回的是颜色的shader程序
    public static int getColorShaderProgram()
    {
        return program[1];
    }
}
