#version 300 es
uniform mat4 uMVPMatrix;    // 总变换矩阵
uniform float uPointSize;   // 点尺寸
uniform float uTime;        // 粒子的累计运动时间
in vec3 aVelocity;          // 粒子初速度
void main()     
{

   float currTime = mod(uTime,10.0);    // 执行取模运算，相当于累计时间超过10则归0
   float px = aVelocity.x * currTime;	// 计算粒子此时的X坐标
   float py = aVelocity.y * currTime - 0.5 * 1.5 * currTime * currTime+3.0;; // 计算粒子此时的Y坐标
   float pz = aVelocity.z * currTime;   // 计算粒子此时的Z坐标

   // 根据总变换矩阵计算此次绘制此顶点位置
   gl_Position = uMVPMatrix * vec4(px,py,pz,1);


   // 设置粒子尺寸
   gl_PointSize=uPointSize;  
}