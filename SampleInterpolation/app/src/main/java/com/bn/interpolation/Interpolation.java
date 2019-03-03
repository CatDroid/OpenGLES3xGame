package com.bn.interpolation;

import android.content.res.Resources;
import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Hanlon on 2019/3/2.
 */

public class Interpolation {

    static private String TAG = "Interpolation" ;


    final private static String sData = "331.632 610.8918 332.37842 630.79114 333.5992 650.7152 335.95996 " +
            "670.55634 339.36847 690.346 343.74146 709.7974 348.97797 729.1429 355.44202 748.04724 363.60437 " +
            "766.33215 373.73962 783.4349 385.8053 799.08606 399.52576 813.42163 414.46545 826.3425 430.69727 " +
            "837.62476 448.5349 846.1437 467.94513 850.997 487.99802 852.3558 508.8426 850.4663 529.09143 845.1653 " +
            "547.87256 836.33655 565.0301 824.5299 580.68494 810.97437 594.8679 795.8716 607.34753 779.3502 617.69684 " +
            "761.3562 625.51575 742.265 631.3269 722.3618 635.6334 701.94104 638.8778 681.40936 641.3535 " +
            "660.77783 642.60657 639.9847 642.8893 619.1963 642.505 598.4237 358.2827 576.4794 378.023 555.7844 " +
            "401.16992 553.1938 424.87018 555.8811 447.9417 561.45044 507.98407 557.57635 531.8628 549.54315 " +
            "556.9802 544.4453 582.2785 545.4065 605.14374 565.1127 479.14914 605.05505 479.59158 628.94855 " +
            "480.02197 652.9875 480.3855 677.1212 448.02686 704.5353 464.89404 705.10693 481.8874 706.0392 499.6214 " +
            "703.7306 517.536 702.4723 382.77536 612.3609 395.9447 603.88 426.75787 603.42474 438.54852 613.5122 " +
            "424.75092 617.91626 395.81808 618.34595 523.7461 609.42163 535.0886 598.11206 566.98505 595.534 581.5423 " +
            "602.5585 568.6013 610.0581 538.7199 612.6303 378.77844 573.9415 401.52942 572.266 424.86234 573.40393 447.78918 " +
            "576.2627 509.0805 572.2622 532.8613 567.21454 557.61487 563.73083 582.3461 563.59436 411.30713 600.7613 410.28293 " +
            "620.12024 410.72217 611.3173 550.673 593.9467 553.8784 613.4022 552.39984 604.45294 458.77448 609.232 501.40823 607.1986 " +
            "447.73538 669.65735 516.3704 667.42316 438.61908 690.9868 526.887 687.6448 436.3905 760.99207 451.13724 744.2077 " +
            "469.5058 735.2131 482.27075 736.3478 494.99683 734.6372 515.8176 741.9923 534.07104 757.5499 519.5548 768.63074 " +
            "502.86346 776.48804 484.44324 779.2816 466.8884 777.94666 450.44788 770.9487 442.30103 759.70935 " +
            "462.43875 753.0334 483.04736 752.2997 505.45294 751.58234 527.8855 756.944 505.5672 755.6013 483.16553 " +
            "757.079 462.54553 757.0591 410.7079 611.2878 552.4685 604.4771";

    final private static String sData2 = "331.632 610.8918 332.37842 630.79114 333.5992 650.7152 335.95996 " +
            "670.55634 339.36847 690.346 343.74146 709.7974 ";

    private static final float sWidth = 720 ;
    private static final float sHeight = 1280 ;
    private static float[] sFace106 = { // !!! 外部轮廓只有33个点!!
//            260f,540f,
//            300f,700f,
//            360f,840f,
//            420f,700f,
//            460f,540f

            331.632f,610.892f,
            332.378f,630.791f,
            333.599f,650.715f,
            335.960f,670.556f,
            339.368f,690.346f,
            343.741f,709.797f,
            348.978f,729.143f,
            355.442f,748.047f,
            363.604f,766.332f,
            373.740f,783.435f,
            385.805f,799.086f,
            399.526f,813.422f,
            414.465f,826.343f,
            430.697f,837.625f,
            448.535f,846.144f,
            467.945f,850.997f,
            487.998f,852.356f,
            508.843f,850.466f,
            529.091f,845.165f,
            547.873f,836.337f,
            565.030f,824.530f,
            580.685f,810.974f,
            594.868f,795.872f,
            607.348f,779.350f,
            617.697f,761.356f,
            625.516f,742.265f,
            631.327f,722.362f,
            635.633f,701.941f,
            638.878f,681.409f,
            641.354f,660.778f,
            642.607f,639.985f,
            642.889f,619.196f,
            642.505f,598.424f,
            // ---------
//            358.283f,576.479f,
//            378.023f,555.784f,
//            401.170f,553.194f,
//            424.870f,555.881f,
//            447.942f,561.450f,
//            507.984f,557.576f,
//            531.863f,549.543f,
//            556.980f,544.445f,
//            582.279f,545.406f,
//            605.144f,565.113f,
//            479.149f,605.055f,
//            479.592f,628.949f,
//            480.022f,652.987f,
//            480.385f,677.121f,
//            448.027f,704.535f,
//            464.894f,705.107f,
//            481.887f,706.039f,
//            499.621f,703.731f,
//            517.536f,702.472f,
//            382.775f,612.361f,
//            395.945f,603.880f,
//            426.758f,603.425f,
//            438.549f,613.512f,
//            424.751f,617.916f,
//            395.818f,618.346f,
//            523.746f,609.422f,
//            535.089f,598.112f,
//            566.985f,595.534f,
//            581.542f,602.558f,
//            568.601f,610.058f,
//            538.720f,612.630f,
//            378.778f,573.942f,
//            401.529f,572.266f,
//            424.862f,573.404f,
//            447.789f,576.263f,
//            509.081f,572.262f,
//            532.861f,567.215f,
//            557.615f,563.731f,
//            582.346f,563.594f,
//            411.307f,600.761f,
//            410.283f,620.120f,
//            410.722f,611.317f,
//            550.673f,593.947f,
//            553.878f,613.402f,
//            552.400f,604.453f,
//            458.774f,609.232f,
//            501.408f,607.199f,
//            447.735f,669.657f,
//            516.370f,667.423f,
//            438.619f,690.987f,
//            526.887f,687.645f,
//            436.391f,760.992f,
//            451.137f,744.208f,
//            469.506f,735.213f,
//            482.271f,736.348f,
//            494.997f,734.637f,
//            515.818f,741.992f,
//            534.071f,757.550f,
//            519.555f,768.631f,
//            502.863f,776.488f,
//            484.443f,779.282f,
//            466.888f,777.947f,
//            450.448f,770.949f,
//            442.301f,759.709f,
//            462.439f,753.033f,
//            483.047f,752.300f,
//            505.453f,751.582f,
//            527.885f,756.944f,
//            505.567f,755.601f,
//            483.166f,757.079f,
//            462.546f,757.059f,
//            410.708f,611.288f,
//            552.469f,604.477f,

    } ;

//    static {
//
//        String[] array = sData.split("\\s+");
//
//        if( array.length % 2 != 0 ){
//            Log.e(TAG,"sData is not even");
//        }
//
//        sFace106 = new float[ array.length/2 * 2 ];
//        for(int i = 0 ; i < array.length/2 ; i++ ){
//            sFace106[i*2]   = Float.parseFloat(array[i*2] );
//            sFace106[i*2+1] = Float.parseFloat(array[i*2 + 1] );
//            Log.e(TAG,String.format("%.3ff,%.3ff,",sFace106[i*2] , sFace106[i*2+1]));
//        }
//        Log.e(TAG,"sFace106.length is " + sFace106.length);
//
//    }


    public Interpolation(){

    }


    private int mProgram = 0 ;
    private int muHaltWidth  = 0 ;
    private int muHaltHeight = 0 ;
    private int maX = 0;
    private int maA1 = 0;
    private int maA2 = 0;
    private int maA3 = 0;

    public boolean init(Resources r){

        String vertex = ShaderUtil.loadFromAssetsFile("interPolationVertex.glsl", r);
        String flag = ShaderUtil.loadFromAssetsFile("interPolationFrag.glsl", r );
        mProgram = ShaderUtil.createProgram(vertex, flag);
        if(mProgram == 0 ){
            Log.e(TAG,"create Program Fail ");
        }

        maX  = GLES30.glGetAttribLocation(mProgram, "aX");
        if(maX < 0 ) Log.e(TAG,"glGetAttribLocation  aX");
        maA1 = GLES30.glGetAttribLocation(mProgram, "a1");
        if(maA1 < 0 ) Log.e(TAG,"glGetAttribLocation a1");
        maA2 = GLES30.glGetAttribLocation(mProgram, "a2");
        if(maA2 < 0 ) Log.e(TAG,"glGetAttribLocation a2");
        maA3 = GLES30.glGetAttribLocation(mProgram, "a3");
        if(maA3 < 0 ) Log.e(TAG,"glGetAttribLocation a3");


        muHaltWidth  = GLES30.glGetUniformLocation(mProgram, "halt_width");
        if(muHaltWidth < 0 ) Log.e(TAG,"glGetUniformLocation halt_width");

        muHaltHeight = GLES30.glGetUniformLocation(mProgram, "halt_height");
        if(muHaltWidth < 0 ) Log.e(TAG,"glGetUniformLocation halt_height");


        GLES30.glUseProgram(mProgram);

        GLES30.glUniform1f(muHaltWidth,sWidth/2);
        GLES30.glUniform1f(muHaltHeight,sHeight/2);

        GLES30.glUseProgram(0);

        return true ;
    }


    public void draw(){

        int basePoint = sFace106.length / 2;

        int fullPoint = basePoint + (basePoint-1) * 3 + 1 ; // basePoint基点  basePoint-1段*3插值点  +1顶点  = 总共的顶点数目

        float[] fullBuffer = new float[ fullPoint*(1+2+2+2)];

        int k = 0 ;

        fullBuffer[k++] = (sFace106[2*0]     + sFace106[2*(basePoint-1)])  /2.0f;
        fullBuffer[k++] = (sFace106[2*0]     + sFace106[2*(basePoint-1)])  /2.0f;
        fullBuffer[k++] = (sFace106[2*0 + 1] + sFace106[2*(basePoint-1)+1])/2.0f;

//        fullBuffer[k++] = 360f ;
//        fullBuffer[k++] = 360f ;
//        fullBuffer[k++] = 640f ;

        fullBuffer[k++] =  2 ; // Fake A2.x
        fullBuffer[k++] =  1 ; // Fake A2.y
        fullBuffer[k++] =  3 ; // Fake A3.x
        fullBuffer[k++] =  1 ; // Fake A3.y

        /*
            问题1:  如果用二次插值/抛物线插值, 对A C B 插值(与顺序无关) 会导致 不能从CBA形成上凸曲线，而是下凹曲线

              A .       . B
                 .     . C
                  .   .
                    .



             问题2:  A C B 三个点做抛物线插值,如果插值比较少点的话,由于比较曲会导致出现明显的折线
                .       .
                 .     .
               A  .   . B
                    . C


         */

//        Log.e(TAG,"center is = " + fullBuffer[1] + "," +  fullBuffer[2]);

        int i = 0;
        for(  ; i < (basePoint-1)  ; i++ ){

            int j = (i==basePoint-2)?i-1:i; // 用于提取基点; 倒数第二个点,基点使用前一个
            float baseA1x = sFace106[2*j];
            float baseA1y = sFace106[2*j+1];

            j = (j + 1);
            float baseA2x = sFace106[2*j];
            float baseA2y = sFace106[2*j+1];

            j = (j + 1);
            float baseA3x = sFace106[2*j];
            float baseA3y = sFace106[2*j+1];

            float start_x = baseA1x ;
            float end_x   = baseA2x ;
            if(i == basePoint - 2){
                start_x = sFace106[2*i];
                end_x   = sFace106[2*(i+1)];
            }
            float diff_x = (end_x- start_x)/3.0f;
            float inert1_x = start_x + diff_x*1.0f;
            float inert2_x = start_x + diff_x*2.0f;
            float inert3_x = start_x + diff_x*3.0f;

            fullBuffer[k++] = start_x ;              // 这一段 第一个顶点(这段开始基点)
            fullBuffer[k++] = baseA1x;
            fullBuffer[k++] = baseA1y;
            fullBuffer[k++] = baseA2x;
            fullBuffer[k++] = baseA2y;
            fullBuffer[k++] = baseA3x;
            fullBuffer[k++] = baseA3y;

            fullBuffer[k++] = inert1_x ;             // 这一段 第二个顶点(插入点3-1)
            fullBuffer[k++] = baseA1x;
            fullBuffer[k++] = baseA1y;
            fullBuffer[k++] = baseA2x;
            fullBuffer[k++] = baseA2y;
            fullBuffer[k++] = baseA3x;
            fullBuffer[k++] = baseA3y;

            fullBuffer[k++] = inert2_x;             // 这一段 第三个顶点(插入点3-2)
            fullBuffer[k++] = baseA1x;
            fullBuffer[k++] = baseA1y;
            fullBuffer[k++] = baseA2x;
            fullBuffer[k++] = baseA2y;
            fullBuffer[k++] = baseA3x;
            fullBuffer[k++] = baseA3y;

            fullBuffer[k++] = inert3_x;             // 这一段 第四个顶点(插入点3-3)
            fullBuffer[k++] = baseA1x;
            fullBuffer[k++] = baseA1y;
            fullBuffer[k++] = baseA2x;
            fullBuffer[k++] = baseA2y;
            fullBuffer[k++] = baseA3x;
            fullBuffer[k++] = baseA3y;
        }

        // 加上最后的一个基点  i=basePoint-1
        fullBuffer[k++] = sFace106[2*i];
        fullBuffer[k++] = sFace106[2*i];
        fullBuffer[k++] = sFace106[2*i+1];

        fullBuffer[k++] =  2 ; // Fake A2.x
        fullBuffer[k++] =  1 ; // Fake A2.y

        fullBuffer[k++] =  3 ; // Fake A3.x
        fullBuffer[k++] =  1 ; // Fake A3.y



        GLES30.glUseProgram(mProgram);
       // GLES30.glViewport(0,0,(int)sWidth,(int)sHeight);


        ByteBuffer bb = ByteBuffer.allocateDirect(k*4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(fullBuffer);
        vertexBuffer.flip();
        //Log.e(TAG,"vertexBuffer.remaining() = " + vertexBuffer.remaining());



        int[] buffIds = new int[1];
        GLES30.glGenBuffers(1, buffIds, 0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, buffIds[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertexBuffer.remaining() * 4, vertexBuffer, GLES30.GL_STATIC_DRAW);




        GLES30.glEnableVertexAttribArray(maX);
        GLES30.glVertexAttribPointer(maX, 1, GLES30.GL_FLOAT, false, (1+2+2+2) * 4 , 0);


        GLES30.glEnableVertexAttribArray(maA1);
        GLES30.glVertexAttribPointer(maA1, 2, GLES30.GL_FLOAT, false, (1+2+2+2) * 4, 1*4 );

        GLES30.glEnableVertexAttribArray(maA2);
        GLES30.glVertexAttribPointer(maA2, 2, GLES30.GL_FLOAT, false, (1+2+2+2) * 4, (1+2)*4);

        GLES30.glEnableVertexAttribArray(maA3);
        GLES30.glVertexAttribPointer(maA3, 2, GLES30.GL_FLOAT, false, (1+2+2+2) * 4, (1+2+2)*4);



        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, fullPoint );


        GLES30.glDeleteBuffers(1,buffIds,0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,0);


    }

}
