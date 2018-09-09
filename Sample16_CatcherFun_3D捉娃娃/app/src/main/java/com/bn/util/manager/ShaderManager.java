package com.bn.util.manager;

import com.bn.util.ShaderUtil;

import android.content.res.Resources;

public class ShaderManager {

    // 一共6个通用shader
    private final static int shaderCount = 6;
    private final static String[][] shaderName = {
            //0  顶点 只做MVP 传递纹理坐标到varying管线
            //   片元 只做texture2D,
            //   作用 最普通的program，用于所有用3dMax导出的.obj模型渲染(LoadedObjectVertexNormalTexture)
            //   用例 所有的3D物体如gan.obj dun.obj niu.obj tv.obj等
            {"vertex.glsl", "frag.glsl"},
            //1  顶点 做MVP 并把MVP后的x坐标给到frag
            //   片元 根据x坐标选择texture2d还是固定颜色
            //   作用 物体不同的位置使用不同的颜色
            //   用例 LoadView loadjm[1]进度条 BN2DObject spng=0 传入管线loadPosition 90+0*20 ~ 90+40*22.5 40*22.5=900
            {"vertex_load2d.glsl", "frag_load2d.glsl"},
            //2  顶点 只做MVP 传递纹理坐标到varying管线
            //   片元 只做texture2D,
            //   作用 最普通 跟0一样
            //   用例 所有的2D按钮 MainView GameVIew的按钮
            {"vertex_2d.glsl", "frag_2d.glsl"},
            //3  顶点 做MVP  片元 使用传入管线的alpha值
            {"holebox_vertex.glsl", "holebox_frag.glsl"},
            //4  顶点 做MVP 并把原来的顶点坐标和纹理坐标给到frag  片元 根据片元距离原点距离 计算颜色
            {"vertex_lz.glsl", "frag_lz.glsl"},
            //5  顶点 只做MVP
            //   片元 根据传入管线的uniform修改整个图片的alpha(动态修改整体的透明度)
            //   作用 做闪烁效果 时刻变大变小旋转闪烁
            //   用例 MainView的游戏标题的4个闪烁的星星 Clpng BN2DObject spng=1 传入管线step SwitchThread 5~30
            {"vertex_spng.glsl", "frag_spng.glsl"},
    };
    private static String[] mVertexShader = new String[shaderCount];
    private static String[] mFragmentShader = new String[shaderCount];
    private static int[] program = new int[shaderCount];

    // 从文件中加载所有shader
    public static void loadCodeFromFile(Resources r) {
        for (int i = 0; i < shaderCount; i++) {
            //加载顶点着色器的脚本内容
            mVertexShader[i] = ShaderUtil.loadFromAssetsFile(shaderName[i][0], r);
            //加载片元着色器的脚本内容
            mFragmentShader[i] = ShaderUtil.loadFromAssetsFile(shaderName[i][1], r);
        }
    }

    // 编译所有shader成gl程序
    public static void compileShader() {
        for (int i = 0; i < shaderCount; i++) {
            program[i] = ShaderUtil.createProgram(mVertexShader[i], mFragmentShader[i]);
            mVertexShader[i] = null;
            mFragmentShader[i] = null;
        }
    }

    //这里返回的是纹理的shader程序
    public static int getShader(int index) {
        return program[index];
    }

}
