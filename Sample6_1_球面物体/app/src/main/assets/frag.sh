#version 300 es
precision mediump float;
uniform float uR;
in vec2 mcLongLat;//接收从顶点着色器过来的参数
in vec3 vPosition;//接收从顶点着色器过来的顶点位置
out vec4 fragColor;//输出的片元颜色
void main()                         
{
   // 棋盘纹理 片元着色器
   vec3 color;
   float n = 8.0;//外接立方体每个坐标轴方向切分的份数
   float span = 2.0*uR/n;//每一份的尺寸（小方块的边长）   球的外边 接的 立方体 的 边长是 2.0*uR  切分成8*8*8个
   int i = int((vPosition.x + uR)/span);//当前片元位置小方块的行数   + uR 相当于 把整个球移到 直角坐标系x,y,z都是正轴区 确保计算出来的都是正数
   int j = int((vPosition.y + uR)/span);//当前片元位置小方块的层数
   int k = int((vPosition.z + uR)/span);//当前片元位置小方块的列数
    //计算当前片元行数、层数、列数的和并对2取模
   int whichColor = int(mod(float(i+j+k),2.0));
   if(whichColor == 1) {//奇数时为红色
   		color = vec3(0.678,0.231,0.129);//红色
   }
   else {//偶数时为白色
   		color = vec3(1.0,1.0,1.0);//白色
   }
	//将计算出的颜色传递给管线
   fragColor=vec4(color,0);
}     