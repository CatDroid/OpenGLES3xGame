#### NDK开发OpenGLES
* EGLContext可以在Java层创建，也可以在底层创建
* 底层可以通过AssetManager访问asset目录的shader文件，AAssetManager不需要NewGlobalRef
```
// 可以全局使用
AAssetManager* aamIn = AAssetManager_fromJava( env, assetManager ); 

 // 打开asset资源
AAsset* asset =AAssetManager_open(aam,fname.c_str(),AASSET_MODE_UNKNOWN);

// 获取文件长度
off_t fileSize = AAsset_getLength(asset)； 

// 读取文件
int size = AAsset_read(asset, (void*)data, fileSize); 

// 关闭文件
AAsset_close(asset)；

```
* 地形数组/地形灰度图，可以使用粒子沉积算法的PC工具来生成

#### 本例子
* GLSurfaceView在Java层创建EGLContext 
* GLSurfaceView通过setContentView作为Activity的界面，使用RENDERMODE_CONTINUOUSLY模式持续渲染
* MatrixState.h/cpp 作为转换矩阵状态的堆栈，支持最大深度为10个
* Matrix.h 提供矩阵运算 


#### 编译问题
* 二维数组 没有使用 { {}.{},{} ...} 的定义方式
  *  error: suggest braces around initialization of subobject
  * 通过 -Wno-missing-braces  解除错误警告
  * 见 build.gradle cppFlags 