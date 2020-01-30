#version 300 es
precision highp float;

uniform float uUsingRGBATexture ;   // 使用RGBA纹理中r通道传递距离值 归一化到-1到1 同时不存在超过[-1,1]范围的
uniform mat4 uMVPMatrix;            // 总变换矩阵
uniform highp vec3 uLightLocation;	// 光源位置

in vec4 vPosition;                  // 接收从顶点着色器过来的参数
out vec4 fragColor;                // 输出到的片元颜色

void main()
{
    if (uUsingRGBATexture == 1.0)
    {
        vec4 clip_space_position = uMVPMatrix * vPosition ;             // 裁剪空间坐标
        fragColor.r = clip_space_position.z / clip_space_position.w ;   // 透视除法
    }
    else
    {
        float dis = distance(vPosition.xyz, uLightLocation);
        fragColor.r = dis;
    }

}