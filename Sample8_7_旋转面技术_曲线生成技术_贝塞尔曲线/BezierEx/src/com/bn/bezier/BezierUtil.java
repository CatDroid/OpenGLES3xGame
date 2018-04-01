package com.bn.bezier;//������

import java.util.ArrayList;//����������

public class BezierUtil 
{
   static ArrayList<BNPosition> al=new ArrayList<BNPosition>();	//���Ƶ���б�
   
   public static ArrayList<BNPosition> getBezierData(float span)
   {//���ɱ����������ϵ����еķ���
	   ArrayList<BNPosition> result=new ArrayList<BNPosition>();//��Ž�������е��б�
	   
	   int n=al.size()-1;	//�õ����Ƶ��߶���
	   
	   if(n<1)	//�߶�������1���ޱ���������
	   {
		   return result;//���ؿ��б�
	   }
	   
	   int steps=(int) (1.0f/span);	//�����ֶܷ���
	   long[] jiechengNA=new long[n+1];	//����һ������Ϊn+1�Ľ׳�����
	   
	   for(int i=0;i<=n;i++){	//��0��n�Ľ׳�
		   jiechengNA[i]=jiecheng(i);//����jiecheng��������i�Ľ׳�
	   }
	   
	   for(int i=0;i<=steps;i++)
	   {//�ֶν���ѭ��
		   float t=i*span;
		   if(t>1)		//t��ֵ������0��1
		   {
			   t=1;
		   }
		   float xf=0;//�����������ϵ��x����
		   float yf=0;//�����������ϵ��y����
		   
		   float[] tka=new float[n+1];//�½�һ������Ϊn+1������
		   float[] otka=new float[n+1];//�½�һ������Ϊn+1������
		   for(int j=0;j<=n;j++)
		   {
			   tka[j]=(float) Math.pow(t, j); //����t��j����
			   otka[j]=(float) Math.pow(1-t, j); //����1-t��j����
		   }
		   
		   for(int k=0;k<=n;k++)
		   {//ѭ��n+1�μ��㱴���������ϸ����������
			   float xs=(jiechengNA[n]/(jiechengNA[k]*jiechengNA[n-k]))*tka[k]*otka[n-k];
			   xf=xf+al.get(k).x*xs;
			   yf=yf+al.get(k).y*xs;
		   }
		   result.add(new BNPosition(xf,yf));//���õ��ĵ�������б�
	   }
	   
	   return result;//���ر����������ϵ���б�
   }
   
 //��׳˵ķ���
   public  static long jiecheng(int n){
	   long result=1;	//����һ��long�͵ı���
	   if(n==0)			//0�Ľ׳�Ϊ1
	   {
		   return 1;
	   }
	   
	   for(int i=2;i<=n;i++){	//����ڵ���2�����Ľ׳�
		   result=result*i;
	   }
	   
	   return result;	//���ؽ׳˵Ľ��ֵ
   }
}
