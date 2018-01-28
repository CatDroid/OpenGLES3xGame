#ifndef ShaderUtil_hpp
#define ShaderUtil_hpp
#include <GLES3/gl3.h>
#include <GLES3/gl3ext.h>

class ShaderUtil
{
public:
	//创建着色器程序的函数
    static GLuint createProgram(const char* vertexShaderSource,
                                          const char* fragmentShaderSource);
    static GLuint loadShader(const char* source, GLenum shaderType);
};

#endif
