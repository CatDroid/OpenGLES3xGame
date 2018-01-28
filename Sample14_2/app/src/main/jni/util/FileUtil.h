#ifndef __FileUtil_H__
#define __FileUtil_H__

#include "android/asset_manager.h"
#include "android/asset_manager_jni.h"
#include <string>

using namespace std;//指定使用的命名空间
class FileUtil
{
  public:
	static AAssetManager* aam;//指向AAssetManager对象的指针
	static void setAAssetManager(AAssetManager* aamIn);//初始化AAssetManager对象
	static string loadShaderStr(string fname);//加载着色器
};

#endif
