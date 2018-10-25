##  多重渲染目标

1. OpenGL ES3.0支持 glDrawBuffers 指定输出到FrameBuffer的哪几个颜色附件(ES2.0没有glDrawBuffers)

2. 如果不设置framebuffer的glDrawBuffers，默认只会渲染到第一个颜色附件(location=0那个fragColor)，即使shader中有输出到fragColor0/1/2/3 也只会有一个有效果

3. 片元着色器代码

   ```
   layout (location=0)out vec4 fragColor0; // 这个location是跟FrameBuffer的第几个颜色附件 对应的
   layout (location=1)out vec4 fragColor1; // 颜色附件1 对应fragColor1
   layout (location=2)out vec4 fragColor2; 
   layout (location=3)out vec4 fragColor3;
   
   main
   {
   	....
      fragColor0=fragColor;
      fragColor1=vec4(fragColor.r,0.0,0.0,1.0);
      fragColor2=vec4(0.0,fragColor.g,0.0,1.0);
      fragColor3=vec4(0.0,0.0,fragColor.b,1.0);
   }
   ```

4. 获取最大颜色附件数目

   ```
   int[] maxColorAttachment = new int[1];
   GLES30.glGetIntegerv(GLES30.GL_MAX_COLOR_ATTACHMENTS,maxColorAttachment,0);
   Log.w("TOM","最大颜色附件数目是 " + maxColorAttachment[0]); //小米5(晓龙820)最大颜色附件数目为 8
   ```

   

## 正交投影

1. 如果摄像机位置移动，但望向的目标不变，那么正交投影的结果类似，从某个非正面来看物体，会变形的
2. 深度1是最大，投影时 near 映射为-1 ，far 映射为1 