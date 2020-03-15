package com.bn.Sample7_4;

public class Constant {
    //基本尺寸单元
    final static float UNIT_SIZE = 0.5f;

    final static float CAMERA_X = -4.5f;
    final static float CAMERA_Y = 4;
    final static float CAMERA_Z = 4.5f;

    final static float TARGET_X = 0;
    final static float TARGET_Y = 0;
    final static float TARGET_Z = 0;

    static float LIGHT_X;
    static float LIGHT_Y;
    static float LIGHT_Z;

    final static float DISTANCE = 100;

    public static void calculateLightPosition() {
        float[] dir = new float[3];
        dir[0] = CAMERA_X - TARGET_X;
        dir[1] = CAMERA_Y - TARGET_Y;
        dir[2] = CAMERA_Z - TARGET_Z;

        LIGHT_X = dir[0] * DISTANCE + CAMERA_X;
        LIGHT_Y = dir[1] * 100 + CAMERA_Y;
        LIGHT_Z = dir[2] * DISTANCE + CAMERA_Z;
    }
}
