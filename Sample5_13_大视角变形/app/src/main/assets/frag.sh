#version 300 es
precision mediump float;
in  vec4 vColor; //接收从顶点着色器过来的参数
in vec3 vPosition;//接收从顶点着色器过来的顶点位置
out vec4 fragColor;//输出到的片元颜色
void main() {                       
   vec4 finalColor=vColor;
   //绕z轴转20度的旋转变换矩阵
   mat4 mm=mat4(0.9396926,  -0.34202012,  0.0,  0.0,
                0.34202012, 0.9396926,    0.0,  0.0,
   			    0.0,        0.0,          1.0,  0.0,
   			    0.0,        0.0,          0.0,  1.0);
   vec4 tPosition=mm*vec4(vPosition,1);//将顶点坐标绕z轴转20度
   if(mod(  tPosition.x ,  0.4 )>0.3) {   //计算X方向在不在红光色带范围内       hhl 如果不加这个的话 那么整个正方体都是红色的了
                                            // hhl 对0.4求余  0~0.4 超过0.3的部分(也就是只有0.3~0.4之间 占1/4的区域是加上红色) 改变0.3到0.2可以加宽红条带
                                            // hhl +100.0 是为了不会低于0.4?? 不加也可以 mod(  tPosition.x + 100.0f  ,  0.4 )
     finalColor=vec4(0.4,0.0,0.0,1.0)+finalColor;//若在给最终颜色加上淡红色  hhl 两种颜色相加 并没有用透明
     // hhl 这里是光栅化后的每个片元(候选像素) vPosition经过线性插值的物体上每个点的世界坐标
   }
   fragColor = finalColor;//给此片元颜色值 hhl 最终实现 条纹灯的效果
}