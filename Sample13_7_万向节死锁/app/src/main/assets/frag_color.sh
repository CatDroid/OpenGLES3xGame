#version 300 es
precision mediump float;//给出默认浮点精度
in  vec3 vPosition;  //从顶点着色器接收的顶点位置
out vec4 fragColor;//最终片元颜色
void main() {
   vec4 bColor=vec4(0.678,0.231,0.129,0);//条纹的颜色(深红色)
   vec4 mColor=vec4(0.763,0.657,0.614,0);//间隔区域的颜色(淡红色)
   float y=vPosition.y;//提取顶点的y坐标值
   y=mod((y+100.0)*4.0,4.0);//折算出区间值
   if(y>1.8) {//当区间值大于指定值时
     fragColor = bColor;//设置片元颜色为条纹的颜色
   } else {//当区间值不大于指定值时
     fragColor = mColor;//设置片元颜色为间隔区域的颜色
}} 