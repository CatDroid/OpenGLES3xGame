#ifndef Mountain__h
#define Mountain__h

#include <GLES3/gl3.h>
#include <GLES3/gl3ext.h>


class Mountain {
    GLuint mProgram;            //  自定义渲染管线的id
    GLuint muMVPMatrixHandle;   //  总变换矩阵引用
    GLuint maPositionHandle;    //  顶点位置属性引用
    GLuint maTexCoorHandle;     //  顶点纹理坐标属性引用


    GLuint sTextureGrassHandle; //  草地的纹理引用
    GLuint sTextureRockHandle;  //  石头的纹理引用


    GLuint landStartYYHandle;   //  起始x值
    GLuint landYSpanHandle;     //  长度


    const GLvoid* mVertexBuffer;    //  顶点数据指针
    const GLvoid* mTexCoorBuffer;   //  纹理坐标数据指针

    int vCount;//顶点数量

public:
    Mountain();//构造函数
    void initVertexData();//初始化顶点坐标与纹理坐标数据的函数
    void initShader();//初始化着色器的函数
    void drawSelf(const GLint texId,const GLint rock_textId);//绘制函数
    void generateTexCoor(int bw,int bh,float* tex);//自动切分纹理产生纹理坐标数组的函数
};
const float UNIT_SIZE=3.0;
const float LAND_HIGH_ADJUST= 10;//  陆地的高度调整值
const float LAND_HIGHEST=60;    //  陆地最大高差
const float END_OF_FULL_GRASS = 20;
const float BEWTEEN_GRASS_AND_BLOCK = 10 ;


#endif
