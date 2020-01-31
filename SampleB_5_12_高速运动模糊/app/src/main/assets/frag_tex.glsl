#version 300 es
precision mediump float;
in vec2 vTextureCoord; //接收从顶点着色器过来的参数

uniform sampler2D sTexture;//纹理内容数据
uniform sampler2D depthTexture;//纹理内容数据
uniform mat4 uViewProjectionInverseMatrix;
uniform mat4 uPreviousProjectionMatrix;
uniform int g_numSamples;

out vec4 fragColor;
void main()
{
    // 得到像素处的深度缓存值
    vec2 textureCoord = vTextureCoord;
    float zOverW = texture(depthTexture, textureCoord).r;

    // H为像素所在的标准空间位置，范围为-1~1   这里做了y轴镜像???
    // vec4 H = vec4(textureCoord.x*2.0 - 1.0, (1.0 - textureCoord.y)*2.0 - 1.0,  zOverW, 1.0);
    highp vec4 H = vec4(textureCoord.x*2.0 - 1.0,  textureCoord.y*2.0 - 1.0,  zOverW, 1.0);

    //通过观察-投影矩阵的逆阵进行变换
    highp vec4 D = uViewProjectionInverseMatrix*H;
    highp vec4 worldPos= D/D.w;

    // 使用世界位置，并通过前一个观察-投影矩阵进行变换
    highp vec4 previousPos = uPreviousProjectionMatrix*worldPos;
    // 通过除以w转换到非齐次点(-1,1)
    previousPos = previousPos/previousPos.w;

    // 当前视口位置
    vec4 currentPos = H;
    // 使用当前帧和前一帧中的位置来计算像素速度
    vec2 velocity = ( (previousPos - currentPos) / float(g_numSamples) ).xy;

    // 得到此像素位置的初始颜色
    vec4 color = texture(sTexture, textureCoord) ;

    textureCoord += velocity;

    for(int i=1; i< g_numSamples ; i++, textureCoord+=velocity)
    {
         // 沿速度向量对颜色缓存进行采样
         vec4 currentColor = texture(sTexture, textureCoord);
         // 将当前颜色累加到颜色和中
         color += currentColor;
   }
   //对采样结果取平均，得到最终的模糊颜色
   fragColor = color / float(g_numSamples);

    //textureCoord += vec2(0.001, 0.001);
    //textureCoord += velocity ;
//    textureCoord = vec2( (previousPos.x + 1.0)*0.5, (previousPos.y + 1.0)*0.5);
//    vec4 color = texture(sTexture, textureCoord) ;
//    fragColor = color ;
}