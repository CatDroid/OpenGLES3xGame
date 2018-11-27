#version 300 es
uniform mat4 uMVPMatrix; 		// 总变换矩阵
uniform mat4 uMMatrix; 		    // 基本变换矩阵
in vec3 aPosition;  		    // 顶点位置
in vec2 aTexCoor;    		    // 顶点纹理坐标
out vec2 vTextureCoord;  		// 用于传递给片元着色器的纹理坐标
out float currY;				// 用于传递给片元着色器的Y坐标 山在不同的高度本来对应石头纹理/过渡纹理/草地纹理
out vec4 pLocation;			    // 用于传递给片元着色器的顶点坐标

void main(){     
   vTextureCoord = aTexCoor;					// 将接收的纹理坐标传递给片元着色器
   currY=aPosition.y;						    // 将顶点的Y坐标传递给片元着色器
   gl_Position=uMVPMatrix * vec4(aPosition,1); 	// 根据总变换矩阵计算此次绘制此顶点的位置
   pLocation= uMMatrix* vec4(aPosition,1);      // 计算基本变换后顶点的位置
}     


                