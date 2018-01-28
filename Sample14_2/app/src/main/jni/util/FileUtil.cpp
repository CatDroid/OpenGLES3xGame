#include "FileUtil.h"

AAssetManager* FileUtil::aam;

void FileUtil::setAAssetManager(AAssetManager* aamIn)//初始化AAssetManager对象
{
	aam=aamIn;//给AAssetManager对象赋值   可以全局使用!!!
}

string FileUtil::loadShaderStr(string fname)//加载着色器文件
{
	AAsset* asset =AAssetManager_open(aam,fname.c_str(),AASSET_MODE_UNKNOWN);//创建AAsset对象
	off_t fileSize = AAsset_getLength(asset);//获取AAsset对象的长度
	unsigned char* data = (unsigned char*) malloc(fileSize + 1);//分配内存
	data[fileSize] = '\0';//初始化
	int size = AAsset_read(asset, (void*)data, fileSize);//读取文件大小
	(void)size;
	std::string resultStr((const char*)data);//获得结果字符串

	return resultStr;
}
