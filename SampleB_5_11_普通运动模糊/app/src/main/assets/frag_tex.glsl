#version 300 es
precision mediump float;
in vec2 vTextureCoord; //接收从顶点着色器过来的参数
uniform sampler2D sTextureOne;//纹理内容数据
uniform sampler2D sTextureTwo;//纹理内容数据
uniform sampler2D sTextureThree;//纹理内容数据
uniform sampler2D sTextureFour;//纹理内容数据
uniform sampler2D sTextureFive;//纹理内容数据
out vec4 fragColor;
void main()
{
   vec4 finalColorOne= texture(sTextureOne, vTextureCoord);
   vec4 finalColorTwo= texture(sTextureTwo, vTextureCoord);
   vec4 finalColorThree= texture(sTextureThree, vTextureCoord);
   vec4 finalColorFour= texture(sTextureFour, vTextureCoord);
   vec4 finalColorFive= texture(sTextureFive, vTextureCoord);
   //给此片元从纹理中采样出颜色值
   fragColor =
          0.6f * finalColorOne    // 近
        + 0.1f * finalColorTwo
        + 0.1f * finalColorThree
        + 0.1f * finalColorFour
        + 0.1f * finalColorFive;  // 摄像机推远,所以树木会靠近,也会模糊点
}