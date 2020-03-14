* 每次创建rigid刚体实例，都要设置DefaultMotionState，包含质心在世界坐标系中的位置，保存到每个rigid里面，以后每次渲染时候从rigid取出

```
Transform startTransform = new Transform();         //  创建刚体的初始变换对象 (整个组合形状对象)
startTransform.setIdentity();                       //  初始化变换对象
startTransform.origin.set(new Vector3f(cx, cy, cz));//  设置刚体的初始位置 (整个刚体的初始位置,渲染的时候直接从刚体拿到这个坐标/世界坐标系)
DefaultMotionState myMotionState = new DefaultMotionState(startTransform);//创建刚体的运动状态对象

RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo//创建刚体描述信息对象
                (
                        mass, myMotionState, comShape, localInertia
                );
body = new RigidBody(rbInfo);       // 创建刚体对象
  
  
渲染时候

获取到 从模型坐标到 世界坐标系的  平移和旋转

Transform trans = body.getMotionState().getWorldTransform(new Transform());//获取这个物体对应刚体的的变换信息对象
MatrixState.translate(trans.origin.x, trans.origin.y, trans.origin.z);//进行平移变换
Quat4f ro = trans.getRotation(new Quat4f());//获取当前旋转变换的信息进入四元数
if (ro.x != 0 || ro.y != 0 || ro.z != 0)//若四元数3个轴的分量都不为0
{
     if (!Float.isNaN(ro.x) && !Float.isNaN(ro.y) && !Float.isNaN(ro.z)
                    && !Float.isInfinite(ro.x) && !Float.isInfinite(ro.y) && !Float.isInfinite(ro.z)) {
         float[] fa = SYSUtil.fromSYStoAXYZ(ro);//将四元数转换成AXYZ的形式
         MatrixState.rotate(fa[0], fa[1], fa[2], fa[3]);
     }
} 

  
```

* 组合形状碰撞，每个addChildShape进去的子形状，都要设置 Transform，代表子形状在整个组合形状中的位置变化， 相当于每个子模型在子模型的坐标系中，(如何旋转和位移)变换到，组合模型的模型坐标系中

```
CompoundShape comShape = new CompoundShape(); //创建组合形状对象

 // 组合形状 中 每一个形状 增加都要加上一个 变换+形状???
Transform localTransform = new Transform();                     // 创建变换对象
localTransform.setIdentity();                                   // 初始化变换
localTransform.origin.set(new Vector3f(0, 0, 0));      // 设置变换的平移参数  !!! 这个应该是在组合形状模型坐标系中 !!!
comShape.addChildShape(localTransform, csa[0]);//添加子形状（箱子）
comShape.addChildShape(localTransform, csa[1]);//添加子形状（围绕x轴的圆柱）

localTransform = new Transform();//创建变换对象
localTransform.basis.rotX((float) Math.toRadians(90));//绕x旋转90
comShape.addChildShape(localTransform, csa[2]);//添加子形状（围绕z轴的圆柱）
        
```



