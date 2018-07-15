# 任意裁剪平面原理
* 给出用于定义剪裁平面的4个参数，A B C D 这4个参数分别是，平面解析方程 Ax+By+Cz+D=0中的4个系数
* 正如二维上面任何一条线是y=ax+b(Ax+By+C=0)，所以三维是Ax+By+Cz+D=0，D控制平面到原点的距离，若(A,B,C)是单位向量，那么D就是原点到屏幕的距离，另外，特殊情况x=-D
* NAx + NBy + NCz + ND = 0 与 Ax + By + Cz + D = 0 表示的是同一个平面，但是 NAx + NBy + NCz + ND的值是Ax + By + Cz + D的N倍，如果只是区分正负数(大于小于0)的话，没有问题 
* 将剪裁平面的4个参数传入渲染管线，以备着色器使用,将顶点(x,y,z)代入平面方程Ax+By+Cz+D=0，完成计算后将得到的值传入‘片元着色器’，若Ax+By+Cz+D>0 则顶点在平面一侧，反之在平面的另一侧。这样就可以把某一侧的片元丢弃(直接在sharder中调用discard)
* OpenGL ES 1.x中提供<=6个任意剪裁平面的支持，只需要设置参数
* OpenGL ES 2/3不直接支持，没有glEnable的需要
* 可以在物体坐标系中对物体做任意面裁剪(只对场景中一个物体做任意面裁剪)
```
// aPosition是传入管线的物体顶点坐标(物体坐标系)
u_clipDist = dot(aPosition.xyz, u_clipPlane.xyz) +u_clipPlane.w;
```
* 也可以在世界坐标系中对物体做任意面裁剪(可以对场景上所有全部物体做同一个任意面裁剪)
```
// 在世界坐标系中做任意面剪裁测试
vec3 posInWorld = (uMMatrix*vec4(aPosition,1)).xyz;
u_clipDist = dot(posInWorld.xyz, u_clipPlane.xyz) +u_clipPlane.w;

```

# 用途
* 显示物体的剖面视图

# 其他
* 半空间(half space). 一个平面把空间分成二个半空间
* 图元装配的一个主要内容就是裁剪，它的任务是消除位于半空间（half-space）之外的那部分几何图元，这个半空间是由一个平面所定义的。


