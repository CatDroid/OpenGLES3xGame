package com.bn.Sample8_7;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by hl.he on 2018/3/30.
 */

public class MyBezierUtil {

    private static final String TAG = "MyBezierUtil";

    public static class BZPosition{
        public float x ;
        public float y ;
        public BZPosition(float _x , float _y){
            x = _x ; y = _y;
        }
        public BZPosition clone(){
            BZPosition p = new BZPosition(x,y);
            return p;
        }
    }


    // T 代表0~1划分的间隔
    public static ArrayList<BZPosition> generate(final ArrayList<BZPosition> points ,final int T ){

        float interval_t = 1.0f/T;
        final int N = points.size() ;// 控制点和起始点总数目


        ArrayList<BZPosition> result = new ArrayList<>(T);

        //for(float t = 0 ; t <= 1 ; t+= interval_t){
        // 存在浮点数精度 如果T=20 interval_t = 0.05  但是倒数的一个变成了 0.95000017

        for(int tt = 0 ; tt <= T ; tt++ ){

            float t = tt * interval_t;
            if( t > 1.0 ){
                Log.w("TOM","last one t= " + t);
                t = 1.0f;
            }

            Log.d("TOM","t="+t);
            ArrayList<BZPosition> cal_points = new ArrayList<BZPosition>();
            for(BZPosition p : points) cal_points.add(p.clone());


            for( int i= N-1 ; i > 0 ; i-- ){ // 计算N-1个点 N-1个点 ..... 到最后只需要算1个点   每一轮少一个点

                for( int j = 0 ; j < i ; j++ ){ // 计算 第0...i-1个点

                    BZPosition start = cal_points.get(j);
                    BZPosition end = cal_points.get(j+1);

                    start.x = start.x + (end.x - start.x) * t ;
                    start.y = start.y + (end.y - start.y) * t ;
                }


            }

            result.add( cal_points.get(0).clone() );


        }

        Log.d(TAG,"result size = " + result.size());
        return result;

    }


}
