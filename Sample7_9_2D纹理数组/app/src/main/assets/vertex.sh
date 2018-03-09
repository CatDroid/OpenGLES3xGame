#version 300 es
uniform mat4 uMVPMatrix; //总变换矩阵
in vec3 aPosition;  //顶点位置
out float vid;//顶点编号

void main()  
{                            		
   gl_Position = uMVPMatrix * vec4(aPosition,1); //根据总变换矩阵计算此次绘制此顶点位置
   gl_PointSize=64.0;//设置点精灵对应点的大小
   //vid=float(1);//将顶点编号传递给片元着色器
   //vid= -2.0f ;
   vid = float(gl_VertexID);
}

/*

Mark
gl_VertexID
            OpenGL ES3.0 新增
            顶点的整数索引
            glDrawArrays 第二个参数作为起始值 每次加1 直到glDrawArrays第三个参数给定值

gl_InstanceID
            采用实例绘制时候当前图元对应的实例号  如果图元不是实例绘制则为0
            glDrawArraysInstanced  实例对象instancing object


*/