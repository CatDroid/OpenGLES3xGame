# Activity和GLSurfaceView生命周期

* View会先Attach到Window，和最后Detach到Window
* 先是SurfaceView的Surface create和changed，在主线程回调，然后跑到GLSurfaceView中的GL-Thread线程中，再创建EGLSurface之后回调GLSurfaceView.Render的onSurfaceCreated和onSurfaceChanged（GL-Thread线程上回调）
* 但是GLSurfaceView是没有EGLSurface destroy的通告的的
* 在Activity切换到**后台，SurfaceView的Surface会Destroy**，但是GLSurfaceView的**EGLContext不会销毁，GL-Thread线程也不会退出**，除非手动调用了GLSurfaceView.onPause/onResume
* 在Activity切换到后台，SurfaceView的Surface会Destroy，在SurfaceVIew的surfaceDestroyed回调中（GLSurfaceView实现）会**等待GL-Thread把EGLSurface销毁才返回**
* 在SurfaceDestoryed之后，GL-Thread还是可以**跑queueEvent的event**，但是**不会响应requestRender**，可以获取**ableToDraw**得到是否可以render(EGLContext，EGLSurfac等是否有效)
* Activity，View，Surface ，EGLSurface（EGLContext）关系如下

| Object        | Life                 | Note                                                         |
| ------------- | -------------------- | ------------------------------------------------------------ |
| MainActivity  | onCreate             |                                                              |
| MainActivity  | onStart              |                                                              |
| MainActivity  | onResume             |                                                              |
| MySurfaceView | onAttachedToWindow   | 主线程回调 1,main ，  这时View才Attach到Window               |
| MySurfaceView | surfaceCreated       | Surface在SurfaceView中从系统中获取                           |
| MySurfaceView | surfaceChanged       |                                                              |
|               |                      |                                                              |
| MainActivity  | onPause              | 后台(按Home键)，可以在这里调用GLSurfaceView.onPause 释放GL-Thread的EGLContext |
| MySurfaceView | surfaceDestroyed     | Surface切换到后台会被回收                                    |
| MainActivity  | onStop               |                                                              |
|               |                      |                                                              |
| MainActivity  | onRestart            | 后台返回                                                     |
| MainActivity  | onStart              |                                                              |
| MainActivity  | onResume             | 可以在这里调用GLSurfaceView.onPause 重建GL-Thread的EGLContext |
| MySurfaceView | surfaceCreated       | Surface在SurfaceView中再从系统中获取                         |
| MySurfaceView | surfaceChanged       |                                                              |
|               |                      |                                                              |
| MainActivity  | onPause              | 后台                                                         |
| MySurfaceView | surfaceDestroyed     |                                                              |
| MainActivity  | onStop               |                                                              |
| MainActivity  | onDestroy            | 已在后台，然后到设置界面设置不保留活动 立刻被调用            |
| MySurfaceView | onDetachedFromWindow | 1,main 在主线程回调 View从Window分离，会等待Gl-Thread线程退出才返回，这时EGLContext也就销毁 |
|               |                      |                                                              |

