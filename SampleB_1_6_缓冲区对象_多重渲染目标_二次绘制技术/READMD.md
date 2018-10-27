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

8. VAO 顶点数组对象

   可以把__顶点属性的操作(glEnableVertexAttribArray glVertexAttribPointer)就会直接用VAO，一个gl指令就可以__

   但是__uniform的传入渲染管线(MVP，光源位置，M变换矩阵，摄像头位置)，还需要单独每个设置__，或者使用uniform一致块/GL_UNIFORM_BUFFER

   VAO 全称 Vertex Array Object，翻译过来叫顶点数组对象，但和Vertex Array（顶点数组）毫无联系！

   __VAO不是 buffer-object，所以不作数据存储；与顶点的绘制息息相关，即是说与VBO强相关。如上，VAO本质上是state-object（状态对象）,记录的是一次绘制所需要的信息，包括数据在哪，数据格式之类的信息__。如果抽象成数据结构，VAO 的数据结构如下：

   ```
    struct VertexAttribute  
       {  
           bool bIsEnabled = GL_FALSE;  
           int iSize = 4; //This is the number of elements in this attribute, 1-4.  
           unsigned int iStride = 0;  
           VertexAttribType eType = GL_FLOAT;  
           bool bIsNormalized = GL_FALSE;  
           bool bIsIntegral = GL_FALSE;  
           void * pBufferObjectOffset = 0;  
           BufferObject * pBufferObj = 0;  
       };  
       
       struct VertexArrayObject  
       {  
           BufferObject *pElementArrayBufferObject = NULL;  
           VertexAttribute attributes[GL_MAX_VERTEX_ATTRIB];  
       }  
   ```

   VAO里面存了一个__EBO的指针__以及__一个顶点属性数组__

   意味着上述一串操作的状态可以完全存储于VAO里面，而真正的数据依然在VBO里面。下面举一个示例代码：

   ```
   // 初始化
       unsigned int VAO;
       glGenVertexArrays(1, &VAO);  
       glBindVertexArray(VAO);
   
       glBindBuffer(GL_ARRAY_BUFFER, VBO);
       glBufferData(GL_ARRAY_BUFFER, sizeof(vertices), vertices, GL_STATIC_DRAW);
   
       glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
       glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(indices), indices, GL_STATIC_DRAW);
   
       glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 3 * sizeof(float), (void*)0);
       glEnableVertexAttribArray(0); 
   
       ...
   
       // 绘制
       glBindVertexArray(VAO);
       glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0)
       glBindVertexArray(0);
   ```

   VAO 记录的是：

   ​	a. __vertex attribute 的格式，由 glVertexAttribPointer 设置__

   ​	b.  __vertex attribute 对应的 VBO 的名字, 由一对 glBindBuffer 和  glVertexAttribPointer 设置。 __

   ​	c.  __#当前#绑定的 GL_ELEMENT_ARRAY_BUFFER 的名字，由 glBindBuffer 设置。(用于索引法draw) __

   ​	注意: VAO 中并不保存#当前#绑定的 GL_ARRAY_BUFFER，VBO 和 vertex attribute 的绑定是在glVertexAttribPointer 中完成的。

   VBO是为了均衡数据的传输效率与灵活修改性；

   VAO的本质是储存绘制状态，__简化绘制代码__

   ```
       // set up vertex data (and buffer(s)) and configure vertex attributes
       // ------------------------------------------------------------------
       float vertices[] = {
            0.5f,  0.5f, 0.0f,  // top right
            0.5f, -0.5f, 0.0f,  // bottom right
           -0.5f, -0.5f, 0.0f,  // bottom left
           -0.5f,  0.5f, 0.0f   // top left 
       };
       unsigned int indices[] = {  // note that we start from 0!
           0, 1, 3,  // first Triangle
           1, 2, 3   // second Triangle
       };
       unsigned int VBO, VAO, EBO;
       glGenVertexArrays(1, &VAO);
       glGenBuffers(1, &VBO);
       glGenBuffers(1, &EBO);
       // bind the Vertex Array Object first, then bind and set vertex buffer(s), and then configure vertex attributes(s).
       glBindVertexArray(VAO);
   
       glBindBuffer(GL_ARRAY_BUFFER, VBO);
       glBufferData(GL_ARRAY_BUFFER, sizeof(vertices), vertices, GL_STATIC_DRAW);
   
       glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
       glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(indices), indices, GL_STATIC_DRAW);
   
       glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 3 * sizeof(float), (void*)0);
       glEnableVertexAttribArray(0);
   
       // note that this is allowed, the call to glVertexAttribPointer registered VBO as the vertex attribute's bound vertex buffer object so afterwards we can safely unbind
       glBindBuffer(GL_ARRAY_BUFFER, 0); // 解绑VBO
   
       // remember: do NOT unbind the EBO while a VAO is active as the bound element buffer object IS stored in the VAO; keep the EBO bound.
       //glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
       //!!!! 在VAO里面可以解绑VBO，却不能解绑EBO!!!!
   
       // You can unbind the VAO afterwards so other VAO calls won't accidentally modify this VAO, but this rarely happens. Modifying other
       // VAOs requires a call to glBindVertexArray anyways so we generally don't unbind VAOs (nor VBOs) when it's not directly necessary.
       glBindVertexArray(0); 
   ```

   