#version 300 es
uniform mat4 uMVPMatrix; //总变换矩阵
in vec3 aPosition;  //顶点位置
in vec2 aTexCoor;    //顶点纹理坐标
out vec2 vTexPosition; //转换后传递给片元着色器的纹理坐标
void main() {                            		
   gl_Position = uMVPMatrix * vec4(aPosition,1); //根据总变换矩阵计算此次绘制此顶点位置
   vTexPosition = (aTexCoor-0.5)*5.0;//将纹理坐标转换后传递给片元着色器
}