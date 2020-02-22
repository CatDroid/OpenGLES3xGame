package com.bn.util;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.content.res.Resources;
import android.opengl.GLES30;
import android.util.Log;

/**
 * 本类是编译并管理着色器脚本资源的工具类。色器器脚本需要放在assets文件夹下。使用时：<br>
 * 1.将要用到的着色器脚本文件名放入shaderName的二维数组中。<br>
 * 2.然后再在GL的上下文中调用 ShaderManager.loadShaderScriptAndCompiled()方法即可编译好所需要的着色器程序。<br>
 * 3.使用时通过调用ShaderManager.getShaderProgram(int i)获得编译好的program ID。<br>
 *
 * @author fuxp
 */
public class ShaderManager {
    public static final String SHADER_SCRIPT_1 = "vertex.glsl";
    public static final String SHADER_SCRIPT_2 = "frag.glsl";
    public static final String SHADER_SCRIPT_3 = "vertexlight.glsl";
    public static final String SHADER_SCRIPT_4 = "fraglight.glsl";

    private ShaderManager() {
    }

    /**
     * 用二维数组表示所有需要用到的着色器脚本
     */
    private static String[][] shaderName =
            {
                    {"vertex.sh", "frag.sh"},
                    {"vertexlight.sh", "fraglight.sh"},
//		{getInternalScript("vertex.sh"),getInternalScript("frag.sh")},//0
//		{getInternalScript("vertexlight.sh"),getInternalScript("fraglight.sh")},//1
            };

    private final static int shaderCount = shaderName.length;
    private final static int[] program = new int[shaderCount];
    private final static String[] mVertexShader = new String[shaderCount];
    private final static String[] mFragmentShader = new String[shaderCount];

    /**
     * 获得编译好的shader program 的id
     */
    public static int getShaderProgram(int i) {
        return program[i];
    }

    /**
     * 加载内嵌着色器并比编译好着色程序
     */
    public static void loadShaderScriptAndCompiled(Resources r) {
        loadCodeFromFile(r);
        compileShader();
    }

    /**
     * 加载并比编译好着色程序
     */
    public static void loadShaderScriptAndCompiled(Resources r, String[][] script) {
        shaderName = script;
        loadCodeFromFile(r);
        compileShader();
    }

    /**
     * 加载shader
     */
    private static void loadCodeFromFile(Resources r) {
        for (int i = 0; i < shaderCount; i++) {
            mVertexShader[i] = loadFromAssetsFile(shaderName[i][0], r);
            mFragmentShader[i] = loadFromAssetsFile(shaderName[i][1], r);
        }
    }

    /**
     * 编译shader
     */
    private static void compileShader() {
        for (int i = 0; i < shaderCount; i++) {
            program[i] = createProgram(mVertexShader[i], mFragmentShader[i]);
        }
    }

    /**
     * 创建shaderProgram程序的方法
     *
     * @param vertexSource   顶点脚本
     * @param fragmentSource 片元脚本
     * @return shaderProgram的id
     */
    public static int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }
        int pixelShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }
        int program = GLES30.glCreateProgram();
        if (program != 0) {
            GLES30.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");
            GLES30.glAttachShader(program, pixelShader);
            checkGlError("glAttachShader");
            GLES30.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES30.GL_TRUE) {
                Log.e("ES30_ERROR", "Could not link program: ");
                Log.e("ES30_ERROR", GLES30.glGetProgramInfoLog(program));
                GLES30.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    /**
     * 创建shader的方法
     *
     * @param shaderType shader的类型  GLES30.GL_VERTEX_SHADER   GLES30.GL_FRAGMENT_SHADER
     * @param source     shader的脚本字符串
     * @return shader的id
     */
    private static int loadShader(int shaderType, String source) {
        int shader = GLES30.glCreateShader(shaderType);
        if (shader != 0) {
            GLES30.glShaderSource(shader, source);
            GLES30.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Log.e("ES30_ERROR", "Could not compile shader " + shaderType + ":");
                Log.e("ES30_ERROR", GLES30.glGetShaderInfoLog(shader));
                GLES30.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    /**
     * 检查每一步操作是否有错误的方法
     *
     * @param op
     */
    private static void checkGlError(String op) {
        int error;
        while ((error = GLES30.glGetError()) != GLES30.GL_NO_ERROR) {
            Log.e("ES30_ERROR", op + ": glError " + error);

            throw new RuntimeException(op + ": glError " + error);
        }
    }

    /**
     * 用IO从Assets目录下读取文件(读取*.sh字符串)
     *
     * @param fname 文件名
     * @param r     资源
     * @return *.sh字符串
     */
    public static String loadFromAssetsFile(String fname, Resources r) {
        String result = null;
        try {
            InputStream in = r.getAssets().open(fname);
            int ch = 0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((ch = in.read()) != -1) {
                baos.write(ch);
            }
            byte[] buff = baos.toByteArray();
            baos.close();
            in.close();
            result = new String(buff, "UTF-8");
            result = result.replaceAll("\\r\\n", "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 读取脚本字符串
     *
     * @param name {"frag.sh","vertex.sh"}
     *             {"fraglight.sh","vertexlight.sh"}
     * @return
     */
    public static String getInternalScript(String name) {
        try {
            InputStream in = ClassLoader.getSystemResourceAsStream("com/fuxp/openwater/resource/" + name);
            int ch = 0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((ch = in.read()) != -1) {
                baos.write(ch);
            }
            byte[] buff = baos.toByteArray();
            baos.close();
            in.close();
            String result = new String(buff, "UTF-8");
            result = result.replaceAll("\\r\\n", "\n");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
