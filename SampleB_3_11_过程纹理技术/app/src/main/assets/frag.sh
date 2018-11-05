#version 300 es
precision mediump float;
in vec2 mcLongLat;      // 接收从顶点着色器过来的参数 mcLongLat.x 水平角(0,360) mcLongLat.y 俯仰角(-90,90)
out vec4 fFragColor;    // 输出的片元颜色
void main()                         
{                       
   vec3 bColor=vec3(0.678,0.231,0.129);// 砖块的颜色
   vec3 mColor=vec3(0.763,0.657,0.614);// 水泥的颜色
   vec3 color;// 片元的最终颜色

   float ny = mod( mcLongLat.y + 90.0,12.0);    // 计算当前片元是否在此行区域1中的辅助变量
   int row = int( mod( ny , 2.0) );             // 计算当前位于奇数还是偶数行

   float oeoffset=0.0;                          // 每行的砖块偏移值，奇数行偏移半个砖块

   float nx;                                    // 当前片元是否在此行区域3中的辅助变量
   
   if( ny > 10.0){      // 位于此行的区域1中    // 垂直角按照12度为一行,0~10度是水泥
        color=mColor;   // 采用水泥色着色      // hhl 这里的12.0f和22.0f可以跟球顶点划分不一样
   } else{              // 不位于此行的区域1中
        if( row == 1){  // 若为奇数行则偏移半个砖块
            oeoffset=11.0;
        }
        nx = mod( mcLongLat.x+oeoffset, 22.0);// 计算当前片元是否在此行区域3中的辅助变量
        if(nx>20.0){    // 不位于此行的区域3中
            color=mColor;
        } else {        // 位于此行的区域3中
            color=bColor;// 采用砖块色着色
        }
   }
   fFragColor=vec4(color,0);// 将片元的最终颜色传递进渲染管线
}     