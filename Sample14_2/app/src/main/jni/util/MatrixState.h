#ifndef MatrixState_hpp
#define MatrixState_hpp

#include "util/Matrix.h"

class MatrixState
{
private:
    static float currMatrix[16];//��ǰ�任����
	static float mProjMatrix[16];//ͶӰ����
    static float mVMatrix[16];//���������
    static float mMVPMatrix[16];//�ܱ任����
public:
    static float mStack[10][16];//�����任�����ջ
    static int stackTop;//ջ��λ��
    
    static void setInitStack();//��ʼ������
    
    static void pushMatrix();//�����任����
    
    static void popMatrix();//�ָ��任����
    
    static void translate(float x,float y,float z);
    
    static void rotate(float angle,float x,float y,float z);
    
    static void scale(float x,float y,float z);
    static void setCamera//���������
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

    static void setProjectFrustum//����͸��ͶӰ����
    (
     float left,
     float right,
     float bottom,
     float top,
     float near,
     float far
     );
    
    static float* getFinalMatrix();//��ȡ���վ���
};


#endif
