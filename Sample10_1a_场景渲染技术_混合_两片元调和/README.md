### 混合
#### gl管线功能 glEnable 
* GL_CULL_FACE 背面裁切 
* GL_DEPTH_TEST 深度测试 
* GL_BLEND 颜色混合 
* (管线也是按照这个顺序)

#### 深度缓冲
* 深度缓冲图 颜色越深 灰度值越小 代表深度越浅
* ![深度缓冲图](https://github.com/CatDroid/OpenGLES3xGame/blob/master/Sample10_1a_%E5%9C%BA%E6%99%AF%E6%B8%B2%E6%9F%93%E6%8A%80%E6%9C%AF_%E6%B7%B7%E5%90%88_%E4%B8%A4%E7%89%87%E5%85%83%E8%B0%83%E5%92%8C/%E6%B7%B1%E5%BA%A6%E7%BC%93%E5%86%B2%E5%9B%BE_%E9%A2%9C%E8%89%B2%E8%B6%8A%E6%B7%B1_%E4%BB%A3%E8%A1%A8%E7%81%B0%E5%BA%A6%E5%80%BC%E8%B6%8A%E5%B0%8F_%E4%BB%A3%E8%A1%A8%E6%B7%B1%E5%BA%A6%E8%B6%8A%E6%B5%85_.png) 

#### 常用两种因子组合(A:Alpha s:Source R/G/B:Red/Green/Blue)
* 源因子 GL_SRC_ALPHA 目标因子 GL_ONE_MINUS_SRC_ALPHA 
    * 源因子 (As,As,As,As) 目标因子 (1-As,1-As,1-As,1-As)
    * 需要源片元是有透明通道的 实现透明
* (滤光镜效果因子组合) 源因子 GL_SRC_COLOR 目标因子 GL_ONE_MINUS_SRC_COLOR 
    * 源因子 (Rs,Gs,Bs,As) 目标因子 (1-Rs,1-Bs,1-Bs,1-As)
    * 不要求源or纹理图片有alpha通道，要透明的话只要源图片是R/G/B都是0(也就是黑色)
    * 可实现透过有色玻璃看物体 
    * 注意，这样实现，会使目标颜色/背景是浅色的，那么最终颜色比源颜色(纹理图片)要浅，比如背景是白色 那么最终颜色会非常浅色
    * ![无Alpha混合](https://github.com/CatDroid/OpenGLES3xGame/blob/master/Sample10_1a_%E5%9C%BA%E6%99%AF%E6%B8%B2%E6%9F%93%E6%8A%80%E6%9C%AF_%E6%B7%B7%E5%90%88_%E4%B8%A4%E7%89%87%E5%85%83%E8%B0%83%E5%92%8C/%E4%BD%BF%E7%94%A8GL_SRC_COLOR_%E6%97%A0Alpha%E6%B7%B7%E5%90%88%E6%97%B6%E9%A2%9C%E8%89%B2%E5%8F%98%E6%B5%85.png)
                 
* 常因子 GL_CONSTANT_COLOR 和 GL_CONSTANT_ALPHA
    * 需要通过glBendColor设置 这样实现整个物体同一个alpha透明
    

#### API
* API 提供alpha和color一起和分开设置源因子和目标因子 glBlendFunc glBlendSeparate
* API 提供的混合方程式 glBlendEquation(int mode) 默认使用GL_FUNC_ADD 
  也可以color和alpha通道使用不同的混合方程式 glBlendSeperateEquation
  
#### 面向量法
* 面向量法中，每个三角面的法向量计算完后，要把同样的法向量值放到三个顶点对应的法向量

#### 3dMax
* 提供标准基本体，比如长方体 圆锥体 圆柱体 茶壶 平面 球体等
* 基本体可以设置部分参数，比如茶壶底部的半径，茶壶面分成多少段等
* 可以导出顶点 纹理 法向量坐标等obj文件