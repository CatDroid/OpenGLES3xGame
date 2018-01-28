#ifndef Triangle__h//防止重复定义
#define Triangle__h

#include <GLES3/gl3.h>//导入需要的头文件
#include <GLES3/gl3ext.h>

class Triangle {
    GLuint mProgram;//自定义着色器程序id
    GLuint muMVPMatrixHandle;//总变换矩阵引用
    GLuint maPositionHandle;//顶点位置属性引用
    GLuint maColorHandle;//顶点颜色属性引用
    const GLvoid* pCoords;//顶点坐标数据
    const GLvoid* pColors;//顶点颜色数据
    int vCount;//顶点数量
public:
    Triangle();//构造函数
    void initVertexData();//初始化顶点数据和着色数据的函数
    void initShader();//初始化着色器的函数
    void drawSelf();//绘制函数
};

const float vertices[]=//顶点坐标数据
{
    -0.8    ,0      ,0,
    0       ,0.8    ,0,
    0.8     ,0      ,0
};


const float colors[] = //顶点着色数据
{
    0,0,1,0,
    1,0,0,0,
    0,1,0,0
};

#endif
