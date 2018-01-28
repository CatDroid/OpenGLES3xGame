#include "Mountain.h"
#include "util/ShaderUtil.h"
#include "util/MatrixState.h"
#include "util/FileUtil.h"
#include <math.h>
#include <malloc.h>

#define STRINGIFY(A) #A

Mountain::Mountain()
{
    initVertexData();
    initShader();
}

void Mountain::initVertexData()
{
	int vsize = sizeof(mHigh); // mHigh来自头文件 一个64*64的二维数组  代表每个点的高度?
	int rows = sqrt((double)vsize/sizeof(float))-1; // rows = 63
	int cols = rows; // cols = 63

    vCount = cols*cols*6; // 64*64的点  构成了 63*63个正方形格子  每个格子用6个顶点来表示

	float* vertices = (float*)malloc(vCount*3*sizeof(float));

    int count=0;//顶点计数器
     for(int j=0;j<rows;j++)
     {
     	for(int i=0;i<cols;i++) // 遍历每个格子  设置每个格子的 两个三角形 共6个顶点的坐标
     	{
    		//计算当前格子左上侧点坐标
     		float zsx=-UNIT_SIZE*cols/2+i*UNIT_SIZE;//zs_x  以整个山地平面中间的格子 对应原点 ; 如果以原点为整个山地平面的左上角 那么山地平面就会往z轴正方向延伸很多...
     		float zsz=-UNIT_SIZE*rows/2+j*UNIT_SIZE;//zs_z

     		vertices[count++]=zsx;
     		vertices[count++]=mHigh[j][i]*LAND_HIGHEST/255-LAND_HIGH_ADJUST;// 高度
     		vertices[count++]=zsz;

     		vertices[count++]=zsx;
     		vertices[count++]=mHigh[j+1][i]*LAND_HIGHEST/255-LAND_HIGH_ADJUST;
     		vertices[count++]=zsz+UNIT_SIZE; // UNIT_SIZE = 3  一个格子  UNIT_SIZE*UNIT_SIZE  可以认为是3pix*3pix

     		vertices[count++]=zsx+UNIT_SIZE;
     		vertices[count++]=mHigh[j][i+1]*LAND_HIGHEST/255-LAND_HIGH_ADJUST;
     		vertices[count++]=zsz;

     		vertices[count++]=zsx+UNIT_SIZE;
     		vertices[count++]=mHigh[j][i+1]*LAND_HIGHEST/255-LAND_HIGH_ADJUST;
     		vertices[count++]=zsz;

     		vertices[count++]=zsx;
     		vertices[count++]=mHigh[j+1][i]*LAND_HIGHEST/255-LAND_HIGH_ADJUST;
     		vertices[count++]=zsz+UNIT_SIZE;

     		vertices[count++]=zsx+UNIT_SIZE;
     		vertices[count++]=mHigh[j+1][i+1]*LAND_HIGHEST/255-LAND_HIGH_ADJUST;
     		vertices[count++]=zsz+UNIT_SIZE;
     	}
     }

     mVertexBuffer = &vertices[0];
     float* tex = (float*)malloc(vCount*2*sizeof(float));

     generateTexCoor(cols,cols,tex);
     mTexCoorBuffer = tex;
}

void Mountain::initShader()
{
	string vertexShader=FileUtil::loadShaderStr("shader/vert.sh");
	string fragmentShader=FileUtil::loadShaderStr("shader/frag.sh");

	mProgram = ShaderUtil::createProgram(vertexShader.c_str(), fragmentShader.c_str());
    maPositionHandle = glGetAttribLocation(mProgram, "aPosition");
    //获取程序中顶点纹理坐标属性引用
	maTexCoorHandle= glGetAttribLocation(mProgram, "aTexCoor");
	//获取程序中总变换矩阵引用
	muMVPMatrixHandle = glGetUniformLocation(mProgram, "uMVPMatrix");
	//纹理
	//草地
	sTextureGrassHandle=glGetUniformLocation(mProgram, "sTextureGrass");
	//石头
	sTextureRockHandle=glGetUniformLocation(mProgram, "sTextureRock");
	// landStartY 低于这个高度 用grass.png纹理     目前 是 0 和 10
	landStartYYHandle=glGetUniformLocation(mProgram, "landStartY");
	// landYSpan 过渡带高度 过渡带用线性插值混合grass和rock     超过landStartY+landYSpan的高度   用rock.png纹理
	landYSpanHandle=glGetUniformLocation(mProgram, "landYSpan");
}

void Mountain::drawSelf(const GLint texId,const GLint rock_textId)
{
    glUseProgram(mProgram);
    
    glUniformMatrix4fv(muMVPMatrixHandle, 1, 0, MatrixState::getFinalMatrix());

	//传送顶点位置数据进渲染管线
	glVertexAttribPointer
	(
		maPositionHandle,
		3,
		GL_FLOAT,
		GL_FALSE,
		3*4,
		mVertexBuffer
	);
	//传送顶点纹理坐标数据进渲染管线
	glVertexAttribPointer
	(
		maTexCoorHandle,
		2,
		GL_FLOAT,
		GL_FALSE,
		2*4,
		mTexCoorBuffer
	);

	//允许顶点位置数据数组
	glEnableVertexAttribArray(maPositionHandle);
	glEnableVertexAttribArray(maTexCoorHandle);

	//绑定纹理
	glActiveTexture(GL_TEXTURE0);
	glBindTexture(GL_TEXTURE_2D, texId);
	glActiveTexture(GL_TEXTURE1);
	glBindTexture(GL_TEXTURE_2D, rock_textId);
	glUniform1i(sTextureGrassHandle, 0);//使用0号纹理
	glUniform1i(sTextureRockHandle, 1); //使用1号纹理

	//传送相应的x参数
	glUniform1f(landStartYYHandle, 0);
	glUniform1f(landYSpanHandle, 10);

	//绘制纹理矩形
	glDrawArrays(GL_TRIANGLES, 0, vCount);

	glDisableVertexAttribArray(maPositionHandle);
	glDisableVertexAttribArray(maTexCoorHandle);
}

//自动切分纹理产生纹理数组的方法
void Mountain::generateTexCoor(int bw,int bh,float* tex){
    float sizew=16.0f/bw;//列数
    float sizeh=16.0f/bh;//行数
    int c=0;
    for(int i=0;i<bh;i++){
        for(int j=0;j<bw;j++){
            //每行列一个矩形，由两个三角形构成，共六个点，12个纹理坐标
            float s=j*sizew;
            float t=i*sizeh;
            tex[c++]=s;
            tex[c++]=t;
            tex[c++]=s;
            tex[c++]=t+sizeh;
            tex[c++]=s+sizew;
            tex[c++]=t;
            tex[c++]=s+sizew;
            tex[c++]=t;
            tex[c++]=s;
            tex[c++]=t+sizeh;
            tex[c++]=s+sizew;
            tex[c++]=t+sizeh;
    }}
}
