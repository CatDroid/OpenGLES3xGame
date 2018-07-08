## 裁剪测试
* 渲染管线流程  ... 光栅化 ... 片元着色器 ... 裁剪测试 .. 模板测试和深度测试 .. 混合 .. 去抖 ... 帧缓冲
* 限制绘制区域，在屏幕/帧缓冲指定一个矩形区域，绘制只会在这个区域
* 无论怎么绘制，剪裁窗口以外的像素将不会被修改
* glEnable( GL_SCISSOR_TEST)
* glScissor(x,y,width,height); 
* 与glViewPort一样，以左下角为原点


## 注意点
* 裁剪测试只是在原来的视口标准的绘制区域内开辟一块矩形区域来显示，而不是把内容放到裁剪的区域内来显示

* 在绘制时我们把视口设置为屏幕的宽和高，那么在启用裁剪测试后，想让绘制的内容正好显示在指定的矩形区域内，就必须保证即便没有启用裁剪测试，内容也是绘制在指定的区域内，否则裁剪区域内将不会显示绘制内容。

* 所以开发的时候，可以先不用设置裁剪测试，然后看调试效果，最后再加上裁剪窗口

## glViewPort与glScissor 
* glScissor 可以控制 glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);的区域,glViewPort不能,整个FB会清除
* glViewport 的宽高对应顶点的-1到1，而glScissor则不会，依旧按照viewport宽高作为顶点的-1到1


## 经验
### Matrix.setLookAtM(mVMatrix,0,  cx,cy,cz,  tx,ty,tz,  upx,upy,upz);
* upy 取反，可以导致图像左右上下都镜像，左上角变成右下角

### Matrix.scaleM(float[] m, int mOffset,  float x, float y, float z)
* x = 0 , z = 0 ,  y = -1 可以y轴 关于XoZ平面 上下镜像

### 透视除法
* 标准设备空间(立方体空间)中往原点远离或者靠近原点    
```
//根据总变换矩阵计算此次绘制此顶点位置
gl_Position = uMVPMatrix * vec4(aPosition,1); 

// 这样等效，管线做透视除法
gl_Position = gl_Position / gl_Position.w ; 

float new_w = 2.0 ; // 0.9
gl_Position = vec4(gl_Position.xyz, new_w);
// new_w > 1.0 , x,y,z(绝对值)都会变小, 并且由于原来的gl_Position.xyz已经在标准设备空间(-1,1) 所以结果就是在标准设备空间(立方体空间)中往原点靠近
// new_w < 1.0 , x,y,z(绝对值)都会变大, 所以就是在标准设备空间(立方体空间)中往原点远离，可能超出-1,1，导致没有显示
```

### 在摄像头前方不同位置的投影
*
![camera_position_project](camera_position_project.png)

### Android Studio GLSL插件 并关联.sh文件
*
![install_GLSL_plugin_associate_sh_file](install_GLSL_plugin_associate_sh_file.png)