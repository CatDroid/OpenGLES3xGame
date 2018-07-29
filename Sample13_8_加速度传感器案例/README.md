## Demo说明

* 按住屏幕上下左右，可以调整摄像头位置，摄像头始终看向原点

* 摄像头距离原点距离保持在 sightDis=26 

* 视椎体始终在z=4f~100f

* 光源位置始终在世界坐标系的(0 , 12 , 0)

* 底部只是一个长方形TextureRect，法向量都是z轴正方向(0,0,1)，在顶点着色器中，根据物体转换矩阵和视图转换矩阵得到在世界坐标系中的法向量，长宽是a \* scale 和 b \* scale

* 四边是个长方体，高度/厚度都是一样(width)，宽度都是b，长度有两个是c，两个是a-2*width，根据底部的矩形，还有有两个要少去2\*width 

* 修改原来Demo中Cube需要6个TextureRect的做法，改成只需要3个，长方体每个对面都一样

* 根据加速度传感器，获取x和y方向的大小，也就是手机屏幕XOY平面的加速度分量(direction[0],direction[1]) ，作为小球在木头盘(在世界坐标系中保持不动，只是摄像机在移动) 移动的方向向量，每次步进长度是0.08

* 小球的变化就是 自身旋转 + 按照方向向量位移

* BallGoThread 线程 只是 定时地执行 BallForControl.run 和退出或者后台时候暂停(onSurfaceChanged, surfaceDestroyed )

* frag_color_light.sh 和 frag_tex_light.sh 的区别，只是一个用纹理贴图(四边长方体Cube和底板TextureRect) ，一个用指定颜色(小球Ball)，关照使用 定点光(非定向光)

* MatrixState增加 matrix 方法 用于右乘 参数self矩阵

* 横屏状态，由于加速度传感器总是认为手机短边是X轴，所以需要调换一下

* 小球位置，每次都计算是否超过边界(-XBOUNDARY，+XBOUNDARY)，超过就不再做位移，否则在原来的位置(mRealTimeXOffset, mRealTimeZOffset) 上 加上 方向向量*0.08

* 小球旋转角度，根据移动的距离( 对‘方向向量*0.08’求模 )就是圆的弧度，和球的半径，得到一个旋转角度，而旋转轴，可以通过 '两个垂直向量点积为零'，特别地，y分量是0，相当于二维的垂直向量

  ```
  (tempSPANX,0,tempSPANZ)*(rotateX,0,rotateZ) = 0  = >
  tempSPANX * rotateX + tempSPANZ * rotateZ = 0 ; // 只是求方向向量的话，直接可看出:
  rotateX = tempSPANZ ;
  rotateZ = -tempSPANX; // 这个就是旋转轴的方向向量 ，符合在y轴正方向右手螺旋
                        // 这种方法在X0Y二维看就是顺时针方向上的向量
  ```

* 弧度，半径，角度的公式：

  ```
  设半径为r，弧长为I，圆心角为α，
  则α=I/r（单位：弧度）
  ```

* 3维空间两个垂直向量点积为0

  ```
  a=(ax,ay,az)  a≠0
  b=(bx,by,bz)  b≠0
  如果a，b垂直，那么:
  a*b = ax×bx + ay×by + az×bz = 0 ;
  ```

  

# Bug Fix

1. 解决恢复前台 或者开关屏幕后 越来越快 

   1. onSurfaceChanged 可能会回调多次
   2. 需要先判断原来是否已经启动了ballGoThread，否则会越来越多线程

2. GlSurfaceVIew必须调用surfaceDestroyed，会导致后台恢复前台没有显示

   1. 在MySurfaceVIew中 surfaceDestroyed 重写后没有调用父类GLSurfaceView的surfaceDestroyed

   2. 导致了后台时候surface已经destory了，但是还在swap，导致BAD_SURFACE 

      

## 补充

* GLSurfaceView的EGLContext会默认有深度buffer，但是不一定有模板buffer，可通过设置setEGLConfigChooser(r,g,b,a,depth,stencil)  一般除了depth是16bit 其他都是8bit

* Java反射静态类成员变量
  * 由于GLSurfaceView的LOG_THREADS等直接定义成false，所以会被编译器优化内联到使用处
  * 即使通过反射修改了静态final变量的值，获取到的也是已经修改的，但由于编译时候已经内联，使用处已经在编译时改成true了，所以不受到运行时的影响