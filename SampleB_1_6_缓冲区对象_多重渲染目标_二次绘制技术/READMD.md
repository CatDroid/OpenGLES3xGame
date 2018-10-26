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





## 顶点缓冲区

1. 两种类型的顶点缓冲区对象 GL_ARRAY_BUFFER GL_ELEMENT_ARRAY_BUFFER

2. GL_ARRAY_BUFFER 数组缓冲对象，可以存放顶点坐标，纹理坐标，法向量

3. GL_ELEMENT_ARRAY_BUFFER 索引数组缓冲对象，可以存放索引法绘制时索引数据

4. API：创建glGenBuffer，更新数据glBufferData,glBufferSubData，删除glDeleteBuffer，查询glGetBufferParameteriv

5. 一般glBindBuffer使用完之后，需要绑定到系统默认缓冲 glBindBuffer(target, 0 )

6. 缓冲区类型

   | target值                       | 说明         | 作用                                                         |
   | ------------------------------ | ------------ | ------------------------------------------------------------ |
   | GL_ARRAY_BUFFER                | 数组缓冲     | glVertexAttribPointer指定某个顶点属性(顶点坐标，纹理坐标 ，法向量)前，通过glBindBuffer，指定顶点属性对应缓冲区对象 |
   | GL_ELEMENT_ARRAY_BUFFER        | 索引数组缓冲 | void glDrawElements( GLenum mode, GLsizei count, GLenum type, const GLvoid *indices);                glDrawElement之前通过glBindBuffer指定，这样draw最后一个参数indices(索引)就作为偏移(offset) |
   | GL_COPY_READ_BUFFER            | 复制只读缓冲 | glCopyBufferSubData 的数据源                                 |
   | GL_COPY_WRITE_BUFFER           | 复制只写缓冲 | glCopyBufferSubData 的目标                                   |
   | GL_PIXEL_PACK_BUFFER           | 像素打包缓冲 | glReadPixels()和glGetTexImage() 是 "pack"像素操作， glDrawPixels(),  glTexImage2D() ,glTexSubImage2D() 是"unpack" 操作<br />e.g : <br /> glBindBuffer  <br /> glReadPixels(最后一个参数是偏移)  <br />glMapBufferRange(GL_PIXEL_PACK_BUFFER   映射得到一个指针/Buffer，可以在CPU操作数据<br />glUnmapBuffer(GL_PIXEL_PACK_BUFFER); |
   | GL_PIXEL_UNPACK_BUFFER         | 像素解包缓冲 |                                                              |
   | GL_TRANSFORM_  FEEDBACK_BUFFER | 变换反馈缓冲 |                                                              |
   | GL_UNIFORM_BUFFER              | 一致变量缓冲 | shader中使用了uniform 一致块，用这个更新数据                 |

   

7. 用途 glBufferData( int target，int size ，Buffer data，int usage)

   即使设置为GL_STATIC_DRAW，也可以进行修改

   STATIC 只设置一次  DYNAMIC 频繁更新(OpenGL或者应用程序)   STREAM 

   DRAW 用于绘制   READ应用程序读取    COPY拷贝到另外一个缓冲区?

   | 用途            | 说明                                                         |
   | --------------- | ------------------------------------------------------------ |
   | GL_STATIC_DRAW  | 缓冲区内容将由应用程序__设置一次__，并经常用于绘图或复制到其他图像 |
   | GL_STATIC_READ  | 缓冲区内容将被__设置一次__，作为 OpenGL 的输出，并被应用程序多次查询 |
   | GL_STATIC_COPY  | 缓冲区内容将被__设置一次__，作为 OpenGL 的输出，并经常用于绘制或复制到其他图像 |
   | GL_DYNAMIC_DRAW | 冲区内容将由__应用程序频繁更新__，并经常用于绘制或复制到其他图像 |
   | GL_DYNAMIC_READ | 缓冲区内容将作为 __OpenGL 的输出频繁更新__，并由应用程序多次查询 |
   | GL_DYNAMIC_COPY | 缓冲区内容将作为 __OpenGL 的输出频繁更新__ ，并经常用于绘图或复制到其他图像 |
   | GL_STREAM_DRAW  | 缓冲区内容将由应用程序设置一次，并且__不经常__用于绘图       |
   | GL_STREAM_READ  | 缓冲区内容将被设置一次，作为 OpenGL 的输出，并且__不经常__用于绘图 |
   | GL_STREAM_COPY  | 缓冲区内容将被设置一次，作为 OpenGL 的输出，并且__不经常__用于绘制或复制到其他图像 |

   