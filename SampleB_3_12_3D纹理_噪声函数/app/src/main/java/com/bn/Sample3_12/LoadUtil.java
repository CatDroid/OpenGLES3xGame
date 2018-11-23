package com.bn.Sample3_12;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.res.Resources;
import android.util.Log;

public class LoadUtil {

    //从obj文件中加载携带顶点信息的物体，并自动计算每个顶点的平均法向量
    public static LoadedObjectVertexNormal loadFromFile(String fname, Resources r, MySurfaceView mv) {

        //加载后物体的引用
        LoadedObjectVertexNormal lo = null;

        // HHL 没有使用纹理坐标  只加载模型 和 法向量(关照)


        //原始顶点坐标列表--直接从obj文件中加载
        ArrayList<Float> alv = new ArrayList<Float>();
        //结果顶点坐标列表--按面组织好
        ArrayList<Float> alvResult = new ArrayList<Float>();


        //原始法向量列表
        ArrayList<Float> aln = new ArrayList<Float>();
        //法向结果量列表
        ArrayList<Float> alnResult = new ArrayList<Float>();

        float max_x = 0;
        float min_x = 0;
        float max_y = 0;
        float min_y = 0;
        float max_z = 0;
        float min_z = 0;


        try {
            InputStream in = r.getAssets().open(fname);
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(isr);
            String temps = null;

            //扫面文件，根据行类型的不同执行不同的处理逻辑
            while ((temps = br.readLine()) != null) {
                //用空格分割行中的各个组成部分
                String[] tempsa = temps.split("[ ]+");
                if (tempsa[0].trim().equals("v")) {//此行为顶点坐标
                    //若为顶点坐标行则提取出此顶点的XYZ坐标添加到原始顶点坐标列表中
                    float x = Float.parseFloat(tempsa[1]);
                    float y = Float.parseFloat(tempsa[2]);
                    float z = Float.parseFloat(tempsa[3]); //查看模型的尺寸
                    alv.add(x);
                    alv.add(y);
                    alv.add(z);
                    if( y < 0 ){ // 模型是 都在y轴正方向
                        android.util.Log.e("TOM", "y axiz is negative ");
                    }
                    max_x = x > max_x ? x:max_x;
                    min_x = x < min_x ? x:min_x ;

                    max_y = y > max_y ? y:max_y;
                    min_y = y < min_y ? y:min_y ;

                    max_z = z > max_z ? x:max_z;
                    min_z = z < min_z ? x:min_z ;

                } else if (tempsa[0].trim().equals("vt")) {//此行为纹理坐标行
                } else if (tempsa[0].trim().equals("vn")) {//此行为法向量行
                    //若为纹理坐标行则提取ST坐标并添加进原始纹理坐标列表中
                    aln.add(Float.parseFloat(tempsa[1]));
                    aln.add(Float.parseFloat(tempsa[2]));
                    aln.add(Float.parseFloat(tempsa[3]));
                } else if (tempsa[0].trim().equals("f")) {//此行为三角形面
                    //计算第0个顶点的索引，并获取此顶点的XYZ三个坐标
                    int index = Integer.parseInt(tempsa[1].split("/")[0]) - 1;
                    float x0 = alv.get(3 * index);
                    float y0 = alv.get(3 * index + 1);
                    float z0 = alv.get(3 * index + 2);
                    alvResult.add(x0);
                    alvResult.add(y0);
                    alvResult.add(z0);

                    //计算第1个顶点的索引，并获取此顶点的XYZ三个坐标
                    index = Integer.parseInt(tempsa[2].split("/")[0]) - 1;
                    float x1 = alv.get(3 * index);
                    float y1 = alv.get(3 * index + 1);
                    float z1 = alv.get(3 * index + 2);
                    alvResult.add(x1);
                    alvResult.add(y1);
                    alvResult.add(z1);

                    //计算第2个顶点的索引，并获取此顶点的XYZ三个坐标
                    index = Integer.parseInt(tempsa[3].split("/")[0]) - 1;
                    float x2 = alv.get(3 * index);
                    float y2 = alv.get(3 * index + 1);
                    float z2 = alv.get(3 * index + 2);
                    alvResult.add(x2);
                    alvResult.add(y2);
                    alvResult.add(z2);

                    //=================================================
                    //计算第0个顶点的索引，并获取此顶点的XYZ三个坐标
                    int indexN = Integer.parseInt(tempsa[1].split("/")[2]) - 1;
                    float nx0 = aln.get(3 * indexN);
                    float ny0 = aln.get(3 * indexN + 1);
                    float nz0 = aln.get(3 * indexN + 2);
                    alnResult.add(nx0);
                    alnResult.add(ny0);
                    alnResult.add(nz0);

                    //计算第1个顶点的索引，并获取此顶点的XYZ三个坐标
                    indexN = Integer.parseInt(tempsa[2].split("/")[2]) - 1;
                    float nx1 = aln.get(3 * indexN);
                    float ny1 = aln.get(3 * indexN + 1);
                    float nz1 = aln.get(3 * indexN + 2);
                    alnResult.add(nx1);
                    alnResult.add(ny1);
                    alnResult.add(nz1);

                    //计算第2个顶点的索引，并获取此顶点的XYZ三个坐标
                    indexN = Integer.parseInt(tempsa[3].split("/")[2]) - 1;
                    float nx2 = aln.get(3 * indexN);
                    float ny2 = aln.get(3 * indexN + 1);
                    float nz2 = aln.get(3 * indexN + 2);
                    alnResult.add(nx2);
                    alnResult.add(ny2);
                    alnResult.add(nz2);
                }
            }

            //生成顶点数组
            int size = alvResult.size();
            float[] vXYZ = new float[size];
            for (int i = 0; i < size; i++) {
                vXYZ[i] = alvResult.get(i);
            }

            //生成法向量数组
            size = alnResult.size();
            float[] nXYZ = new float[size];
            for (int i = 0; i < size; i++) {
                nXYZ[i] = alnResult.get(i);
            }

            //创建3D物体对象
            lo = new LoadedObjectVertexNormal(mv, vXYZ, nXYZ);
        } catch (Exception e) {
            Log.d("load error", "load error");
            e.printStackTrace();
        }

        android.util.Log.w("TOM",
                String.format("x=[%f~%f],y=[%f~%f],z=[%f~%f]",
                min_x,max_x,min_y,max_y,min_z,max_z));
        // x=[-0.450000~0.515100],y=[0.000000~0.472500],z=[-0.240000~0.255000]
        return lo;
    }
}
