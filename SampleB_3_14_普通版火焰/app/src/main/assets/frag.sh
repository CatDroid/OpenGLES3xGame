#version 300 es
precision mediump float;        // 给出默认浮点精度
uniform vec4 startColor;        // 起始颜色
uniform vec4 endColor;          // 终止颜色
uniform float bj;               // 纹理矩形半径
uniform sampler2D sTexture;     // 纹理内容数据

in vec2 vTextureCoord;          // 接收从顶点着色器传过来的纹理坐标
in vec4 vPosition;              // 接收从顶点着色器传过来的片元位置属性(物体坐标系)
in float sjFactor;              // 接收从顶点着色器传过来的总衰减因子

out vec4 fragColor;             // 输出到的片元颜色

void main(){
	if(vPosition.w==10.0){              // 该片元的生命期为10.0时，处于未激活状态，不绘制
		fragColor=vec4(0.0,0.0,0.0,0.0);// 舍弃此片元
	}else{                              // 该片元的生命期不为10.0时，处于活跃状态，绘制

		vec4 colorTL = texture(sTexture, vTextureCoord);        // 进行纹理采样(输入的纹理贴图主要是用来控制粒子形状)

    	float disT=distance(vPosition.xyz,vec3(0.0,0.0,0.0));   // 计算当前片元/粒子 与 火盘中心点(物体坐标系坐标是vec3(0,0,0))的距离，这样越远离火盘底部，火苗颜色越小
    	float tampFactor=(1.0-disT/bj)*sjFactor;                // 计算片元颜色插值因子(当前片元距离粒子中心*剩余生命占比)

        //if(disT < 1.5f) discard ; // hhl 这样会看到底部一个圆的范围内 都没有火焰 因为计算的距离是当前片元/粒子 距离火炬盘中心点(物体坐标系坐标是vec3(0.,0.,0.))

    	vec4 factor4=vec4(tampFactor,tampFactor,tampFactor,tampFactor);

    	vec4 colorT;                                    // 颜色变量
    	//colorT = clamp(factor4, endColor,startColor); // 进行颜色插值
    	colorT = mix( endColor,startColor, factor4);
    	colorT = colorT * colorTL.a;                    // 结合采样出的透明度计算最终颜色
    	fragColor = colorT;                             // 将计算出来的片元颜色传给渲染管线
	}  
}              