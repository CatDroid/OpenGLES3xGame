package com.bn.Sample10_3;

/**
 * Created by Hanlon on 2018/4/22.
 */

public class Config {

    enum FOG_MODE {
        MODE_LINEAR ,
        MODE_SMOOTH,
        MODE_SMOOTHER,
        MODE_EXP,
    }

    public final static FOG_MODE CONFIG_USING_FOG_MODE = FOG_MODE.MODE_EXP ;

    public final static String FOG_MODE_VERTEX_SHARDER ;
    static{
        switch(CONFIG_USING_FOG_MODE ){
            case MODE_LINEAR:
                FOG_MODE_VERTEX_SHARDER = "vertex_light.sh";
                break;
            case MODE_SMOOTH:
                FOG_MODE_VERTEX_SHARDER = "vertex_light_smoothstep.sh";
                break;
            case MODE_SMOOTHER:
                FOG_MODE_VERTEX_SHARDER = "vertex_light_smootherstep.sh";
                break;
            case MODE_EXP:
                FOG_MODE_VERTEX_SHARDER = "vertex_light_exp.sh";
                break;
            default:
                FOG_MODE_VERTEX_SHARDER = "vertex_light.sh";
                break;
        }

    }
}
