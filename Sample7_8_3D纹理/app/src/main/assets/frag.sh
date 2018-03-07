#version 300 es
precision mediump float;
uniform mediump sampler3D sTexture;//纹理内容数据
//接收从顶点着色器过来的参数
in vec4 ambient;
in vec4 diffuse;
in vec4 specular;
in vec3 vPosition;
out vec4 fragColor;
void main()
{
   //根据片元的位置折算出3D纹理坐标
   vec3 texCoor=vec3(       ((vPosition.x/0.2)+1.0)/2.0, // 移位正值 并归一化
                                    vPosition.y/0.4,
                                    vPosition.z/0.4);
   //进行3D纹理采样
   vec4 noiseVec=texture(sTexture,texCoor);
   //给此片元颜色值
   fragColor = noiseVec*ambient+noiseVec*specular+noiseVec*diffuse;
}   