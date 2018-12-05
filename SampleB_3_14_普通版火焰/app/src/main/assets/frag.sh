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

    	float disT=distance(vPosition.xyz,vec3(0.0,0.0,0.0));   // 计算当前片元与中心点的距离
    	float tampFactor=(1.0-disT/bj)*sjFactor;                // 计算片元颜色插值因子(当前片元距离粒子中心*剩余生命占比)
    	vec4 factor4=vec4(tampFactor,tampFactor,tampFactor,tampFactor);

    	vec4 colorT;                                    // 颜色变量
    	//colorT = clamp(factor4, endColor,startColor);   // 进行颜色插值
    	colorT = mix( endColor,startColor, factor4);
    	colorT = colorT * colorTL.a;                    // 结合采样出的透明度计算最终颜色
    	fragColor = colorT;                             // 将计算出来的片元颜色传给渲染管线
	}  
}              