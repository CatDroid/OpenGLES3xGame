#ifndef __FileUtil_H__
#define __FileUtil_H__

#include "android/asset_manager.h"
#include "android/asset_manager_jni.h"
#include <string>

using namespace std;//ָ��ʹ�õ������ռ�
class FileUtil
{
  public:
	static AAssetManager* aam;//ָ��AAssetManager�����ָ��
	static void setAAssetManager(AAssetManager* aamIn);//��ʼ��AAssetManager����
	static string loadShaderStr(string fname);//������ɫ��
};

#endif
