 #include "ShaderUtil.h"
#include <android/log.h>
#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "native-activity", __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, "native-activity", __VA_ARGS__))


//加载顶点着色器和片元着色器
GLuint ShaderUtil::createProgram(const char* vertexShaderSource,
									  const char* fragmentShaderSource)
{
	//加载顶点着色器和片元着色器
	GLuint vertexShader = loadShader(vertexShaderSource, GL_VERTEX_SHADER);
	GLuint fragmentShader = loadShader(fragmentShaderSource, GL_FRAGMENT_SHADER);
	GLuint programHandle = glCreateProgram();//创建着色器程序
	glAttachShader(programHandle, vertexShader);//向着色器程序中加入顶点着色器
	glAttachShader(programHandle, fragmentShader);//向着色器程序中加入片元着色器
	glLinkProgram(programHandle);
	GLint linkSuccess;//声明链接是否成功标志变量
	glGetProgramiv(programHandle, GL_LINK_STATUS, &linkSuccess);
	if (linkSuccess == GL_FALSE) {//若连接失败获取获取错误信息
		GLchar messages[256];
		glGetProgramInfoLog(programHandle, sizeof(messages), 0, &messages[0]);
		messages[255]='\0';
		LOGE("Shader Link Error:");
        LOGE("%s",(char*)messages);
	}
	return programHandle;
}
//加载着色器的方法
GLuint ShaderUtil::loadShader(const char* source, GLenum shaderType)
{
	GLuint shaderHandle = glCreateShader(shaderType);
	glShaderSource(shaderHandle, 1, &source, 0);//加载着色器的脚本
	glCompileShader(shaderHandle);//编译着色器
	GLint compileSuccess;//声明编译是否成功标志变量
	glGetShaderiv(shaderHandle, GL_COMPILE_STATUS, &compileSuccess);
	if (compileSuccess == GL_FALSE) {//若编译失败
		GLchar messages[256];
		glGetShaderInfoLog(shaderHandle, sizeof(messages), 0, &messages[0]);//获取错误信息
		messages[255]='\0';
		LOGE("Shader Compile Error:");
		LOGE("%s",(char*)messages);
	}
	return shaderHandle;
}


