#version 300 es
uniform mat4 uMVPMatrix; 							//总变换矩阵
uniform mat4 uMMatrix; 								//基本变换矩阵
in vec3 aPosition;  									//顶点位置
in vec2 aTexCoor;   								//顶点纹理坐标
in vec3 aNormal;   									//法向量
out vec3 fNormal;    								//用于传递给片元着色器的法向量
out vec2 vTextureCoord;  							//用于传递给片元着色器的纹理坐标
out vec4 vPosition;						//用于传递给片元着色器的基本变换后的顶点位置
out vec3 mvPosition;  								//用于传递给片元着色器的顶点位置
out mat4 vMMatrix;						//用于传递给片元着色器的基本变换矩阵
void main(){
	gl_Position = uMVPMatrix * vec4(aPosition,1); 		// 根据总变换矩阵计算此次绘制此顶点位置
	vPosition=uMMatrix*vec4(aPosition,1);		        // 计算出变换后的顶点位置并传递给片元着色器
	vTextureCoord=aTexCoor;					            // 将顶点的纹理坐标传递给片元着色器
   	mvPosition=aPosition;						        // 将顶点坐标传递给片元着色器
   	fNormal=aNormal;  							        // 将顶点的法向量传给片元着色器
   	vMMatrix=uMMatrix;							        // 将基本变换矩阵传给片元着色器
}
                 