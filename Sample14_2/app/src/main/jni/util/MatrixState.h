#ifndef MatrixState_hpp
#define MatrixState_hpp

#include "util/Matrix.h"

class MatrixState
{
private:
    static float currMatrix[16];//当前变换矩阵
	static float mProjMatrix[16];//投影矩阵
    static float mVMatrix[16];//摄像机矩阵
    static float mMVPMatrix[16];//总变换矩阵
public:
    static float mStack[10][16];//保护变换矩阵的栈
    static int stackTop;//栈顶位置
    
    static void setInitStack();//初始化矩阵
    
    static void pushMatrix();//保护变换矩阵
    
    static void popMatrix();//恢复变换矩阵
    
    static void translate(float x,float y,float z);
    
    static void rotate(float angle,float x,float y,float z);
    
    static void scale(float x,float y,float z);
    static void setCamera//设置摄像机
    (
     float cx,
     float cy,
     float cz,
     float tx,
     float ty,
     float tz,
     float upx,
     float upy,
     float upz
     );

    static void setProjectFrustum//设置透视投影参数
    (
     float left,
     float right,
     float bottom,
     float top,
     float near,
     float far
     );
    
    static float* getFinalMatrix();//获取最终矩阵
};


#endif
