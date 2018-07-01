#version 300 es
uniform mat4 uMVPMatrix; //总变换矩阵
in vec3 aPosition;  //顶点位置
in vec3 aNormal;    //顶点法向量
//out float vTest ;   // 测试某个值是多少
void main()     
{ 
	vec3 position=aPosition;//获取此顶点位置

	// hhl 将法向量 物体坐标空间 转化到 标准设备空间x,y,z(-1,1) ?????
	//vec4 ydskj      =   uMVPMatrix*vec4(0,0,0,1);
	//vec4 fxldskj    =   uMVPMatrix*vec4(aNormal.xyz,1.0); // 法向量的齐次坐标 w!=1.0
	//vec2 skjNormal  =   fxldskj.xy - ydskj.xy;  // hhl ??? 只用x y ??? 后面把z坐标直接设置为1.0 法向量全部指向z正半轴?
    vec2 skjNormal  =  mat2(uMVPMatrix)*vec2(aNormal.x,aNormal.y);
	skjNormal=normalize(skjNormal);

    //vTest = abs(aNormal.z) ;
    //vTest = abs(aNormal.x) ;
    //vTest = aNormal.w;  //  Swizzle field selector out of range aNormal只是vec3


	vec4 finalPosition=uMVPMatrix * vec4(position.xyz,1);
	finalPosition=finalPosition/finalPosition.w; // hhl MVP之后的顶点坐标是齐次坐标，要做透视除法，才能得到最后标准设备空间的坐标

    // 下面这两种会导致结果不一样 特别在重叠区域  实际两个球在世界坐标系中 应该是会发生一个球嵌入到另外一个球，所以应该会有一部分轮廓和球体没有了
    // 或者修改两个球的距离

   	// gl_Position =finalPosition+vec4(skjNormal.xy,1.0,1.0)*0.01;// 这个是错误的!!!

   	vec3 outline = finalPosition.xyz + vec3(skjNormal.xy,1.0)*0.01;//根据总变换矩阵计算此次绘制此顶点位置  扩展的长度是0.01
   	gl_Position = vec4(outline ,1.0); // 管线不用做透视除法，因为w=1
   	//gl_Position = vec4(outline ,0.96); // 这样轮廓会跟原点远离了一点
    //gl_Position = clamp(gl_Position,-0.99,0.99);
   	//gl_Position = vec4(outline ,1.02); // 这样轮廓会向原点靠近一点
}                    