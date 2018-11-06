## GLUtils

1. texImage2D 上传纹理，注意会受到 GL_UNPACK_ALIGNMENT 影响 

   ```
   * @param target 纹理目标 GL_TEXTURE_2D
   * @param level 	纹理级别(MIPMAP )
   * @param internalformat 内部格式 GL_RGBA
   * @param bitmap 图像 
   * @param type 	数据类型  GL_UNSIGNED_BYTE
   * @param border	边框，一般设为0
   public static void texImage2D(int target, int level, int internalformat,
               Bitmap bitmap, int type, int border)
   ```

   

2. getInternalFormat 与 getType

   ```
   返回给定bitmap对应的OpenGLES内部格式 
   public static int getInternalFormat(Bitmap bitmap) 
   返回给定bitmap对应的OpenGLES的数据格式 
   public static int getType(Bitmap bitmap)
   ```

   

## 膨胀

1. 模型从3dxMax导出的时候，就带有顶点坐标，纹理坐标和法向量(不用自己计算)
2. 根据传入管线的膨胀系数，每个顶点沿着法向量方向移动对应距离，__计算公式：在物体坐标系中，原来的坐标 + 法向量*膨胀系数__
3. 3dxMax导出head.obj时使用点平均向量法，zd.obj使用面向量法
4. 跟之前的"描边效果-沿法线挤出轮廓(利用关闭深度写入或者背面裁剪)"一样，都是顶点沿着法向量向外移动

