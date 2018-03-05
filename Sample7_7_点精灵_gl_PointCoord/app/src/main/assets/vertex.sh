#version 300 es
uniform mat4 uMVPMatrix; //总变换矩阵
in vec3 aPosition;  //顶点位置
void main()  {                            		
   gl_Position = uMVPMatrix * vec4(aPosition,1); //根据总变换矩阵计算此次绘制此顶点位置
   gl_PointSize=256.0;//设置点精灵对应点的大小   hhl 图片的大小是64*64
}

/*
Mark.0 点精灵 使用场景: 绘制大量小的矩形

Mark.1 使用点精灵只需要1个顶点，但是用三角形绘制至少需要4/5个顶点，顶点数据减少，提供绘制效率



Mark.2 绘制点精灵，管线自动用指定大小的纹理矩形代替，用于需要绘制大量小的纹理矩形的情景

Mark.3 点精灵 是方形的~ 通过内置变量gl_PointSize设置点精灵对应矩形的尺寸 以像素为单位

Mark.4 gl_PointSize不同硬件的尺寸范围有不同 小米5高通820 范围是1.0~1023.0 通过glGetFloatv获取


Mark.5 点精灵，不需要传递纹理坐标，只需要纹理，然后在片元着色器使用内置变量gl_PointCoord作为纹理坐标采样(gl_PointCoord应该代表点的像素位置，由管线自动生成)

Mark.6 gl_PointCoord vec2内建变量 当前片元在图元中的位置 值从0.0~1.0 当前图元不是点精灵值不确定

Mark.7 gl_FragCoord vec4内建变量 当前片元在视口中的位置 左上角为原点

Mark.8 目前某些GPU,不可以直接使用内建变量gl_PointCoord进行纹理采样texture(unit,gl_PointCoord),需要先读到一个变量中


Mark.9 点精灵在使用纹理时候 GLES30.GL_LINEAR过大会边缘模糊 GLES30.GL_NEAREST会有锯齿

Mark.10 点精灵 有透明的纹理的话，需要先把靠远的点精灵先渲染，再把靠近摄像头的最后渲染

Mark.11 物体透明技术通常被叫做混合(Blending) 目前测试发现及时用开启Blending 点精灵要用alpha需要glEnable(GL_BLEND)

Mark.12 管线流程:  深度测试 --> 颜色混合  如果靠前的先渲染 背后的渲染时候就会在深度测试被抛弃 靠前的在渲染时候已经跟当前framebuffer的值混合alpha

*/