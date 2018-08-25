package com.bn.util.screenscale;

//计算缩放情况的工具类
public class ScreenScaleUtil {
    static final float sHpWidth = 1920;//原始横屏的宽度
    static final float sHpHeight = 1080;//原始横屏的高度
    static final float whHpRatio = sHpWidth / sHpHeight;//原始横屏的宽高比


    static final float sSpWidth = 1080;//原始竖屏的宽度
    static final float sSpHeight = 1920;//原始竖屏的高度
    static final float whSpRatio = sSpWidth / sSpHeight;//原始竖屏的宽高比


    public static ScreenScaleResult calScale(
            float targetWidth,    //目标宽度
            float targetHeight    //目标高度
    ) {
        ScreenScaleResult result = null;
        ScreenOrien so = null;

        //首先判断目标是横屏还是竖屏
        if (targetWidth > targetHeight) {
            so = ScreenOrien.HP;
        } else {
            so = ScreenOrien.SP;
        }
        System.out.println(so);

        // FIXME(hhl) 横屏布局 不确认
        //进行横屏结果的计算
//        if (so == ScreenOrien.HP) {
//            //计算目标的宽高比
//            float targetRatio = targetWidth / targetHeight;
//
//            if (targetRatio > whHpRatio) // targetWidth/sHpWidth >  targetHeight/sHpHeight
//            {
//                //若目标宽高比大于原始宽高比则以目标的高度计算结果
//                float ratio = targetHeight / sHpHeight;
//                float realTargetWidth = sHpWidth * ratio;
//                float lcuX = (targetWidth - realTargetWidth) / 2.0f;
//                float lcuY = 0;
//                result = new ScreenScaleResult((int) lcuX, (int) lcuY, ratio, so);
//            } else {
//                //若目标宽高比小于原始宽高比则以目标的宽度计算结果
//                float ratio = targetWidth / sHpWidth;
//                float realTargetHeight = sHpHeight * ratio;
//                float lcuX = 0;
//                float lcuY = (targetHeight - realTargetHeight) / 2.0f;
//                result = new ScreenScaleResult((int) lcuX, (int) lcuY, ratio, so);
//            }
//        }

        //进行竖屏结果的计算
//        if (so == ScreenOrien.SP) {
            //计算目标的宽高比
//            float targetRatio = targetWidth / targetHeight;

            // FIXME(hhl) 目前总是竖屏  修改后这里的转换 才跟Constant.fromScreenXToNearX一样，总是以高为最长边
            // FIXME(hhl) 触摸事件坐标转换 可以看成触摸在缩放后的屏幕上按下，只是坐标原点是屏幕做上角,而不是(lcuX,lucY)
            // FiXME(hhl)               先加上-lucX，得到以缩放后屏幕做上角为原点的坐标，然后除以ratio，得到标准屏幕1080x1920上的坐标(1080x1920的左上角为原点)
            // FIXME(hhl)  物理屏幕坐标 --> 缩放后的屏幕坐标 -->  1080x1920标准屏幕的坐标
            float ratio = targetHeight / sSpHeight;
            float realTargetWidth = sSpWidth * ratio;
            float lcuX = (targetWidth - realTargetWidth) / 2.0f;
            float lcuY = 0;
            result = new ScreenScaleResult((int) lcuX, (int) lcuY, ratio, so);

//            if (targetRatio > whSpRatio) {
//                //若目标宽高比大于原始宽高比则以目标的高度计算结果
//                float ratio = targetHeight / sSpHeight;
//                float realTargetWidth = sSpWidth * ratio;
//                float lcuX = (targetWidth - realTargetWidth) / 2.0f;
//                float lcuY = 0;
//                result = new ScreenScaleResult((int) lcuX, (int) lcuY, ratio, so);
//            } else
//            {
//                //若目标宽高比小于原始宽高比则以目标的宽度计算结果
//                float ratio = targetWidth / sSpWidth;
//                float realTargetHeight = sSpHeight * ratio;
//                float lcuX = 0;
//                float lcuY = (targetHeight - realTargetHeight) / 2.0f;
//                result = new ScreenScaleResult((int) lcuX, (int) lcuY, ratio, so);
//            }
//        }
        return result;
    }
}