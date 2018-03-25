package com.bn.Sample8_5;
public class MyMathUtil{
	static double a[][];
	//通过doolittle分解解n元一次线性方程组的工具方法
	static double[] doolittle(double a[][]){ // hhl  3*4   三个方程 * ( 系数A | 值b )
		MyMathUtil.a=a;
		int rowNum = a.length ;//获得未知数的个数       hhl 方程的个数 就认为是 参数的个数
		int xnum = a[0].length-rowNum;// 所求解的组数  hhl 4-3=1
		
		double AugMatrix[][]=new double[10][20];//拓展的增广矩阵
		
		readData(a,rowNum,xnum,AugMatrix);
		/*
		*  就是把 a的数据拷贝到 AugMatrix 的 1,1 开始    这样是为了跟算法表述一样???? 都是从1开始???
		*
		*  a:
		*  a11, a12, a13,  b1
		*  a21, a22, a23,  b2
		*  a31, a32, a33,  b3
		*
		*  ---> AugMatrix
		*	0     0    0     0     0
		*   0	  a11, a12, a13,  b1
			0	  a21, a22, a23,  b2
			0	  a31, a32, a33,  b3
		*         ^         ^
		*         i=1       = rowNum
		* */
		
		for(int i=1;i<=rowNum;i++)
		{
			prepareChoose(i,rowNum,AugMatrix);	// hhl 计算剩下系数矩阵中的第一列的u  a[i][k=i~rowNum]
			//android.util.Log.d("TOM", " " + AugMatrix[1][0] );
			choose(i,rowNum,xnum,AugMatrix);	// hhl 选主元  哪一个计算出来的u是最大的
			resolve(i,rowNum,xnum,AugMatrix);	// hhl 分解当前 i的Li Ui
		}

		findX(rowNum,xnum,AugMatrix);
		
		double[] result=new double[rowNum];
		for(int i=0;i<rowNum;i++)
		{
			result[i]=AugMatrix[i+1][rowNum+1];
		}

		return result;
	}
	
	static void readData(double a[][],int rowNum,int xnum,double AugMatrix[][])
	{
		for(int i=0;i<=rowNum;i++) // 增广矩阵的拓展
		{
			AugMatrix[i][0]=0;
		}
		for(int i=0;i<=rowNum+xnum;i++)
		{
			AugMatrix[0][i]=0;
		}
		for(int i=1;i<=rowNum;i++)
			for(int j=1;j<=rowNum+xnum;j++)
				AugMatrix[i][j]=a[i-1][j-1];
	}
	
	static void prepareChoose(int times,int rowNum,double AugMatrix[][])
	{//计算准备选主元
		for(int i=times;i<=rowNum;i++)
		{
			for(int j=times-1;j>=1;j--)
			{
				AugMatrix[i][times]=AugMatrix[i][times]-AugMatrix[i][j]*AugMatrix[j][times];
			}
		}
		/*
		*  ---> AugMatrix
		*	0     0    0     0     0
		*   0	  u11, u12, u13,  b1
			0	  l21,[a22], a23,  b2  < times
			0	  l31, a32, a33,  b3
		*              ^
		*              times
		*
		*   这里算出 剩下的行中 分别作为主元时候，计算出的u22的值，假设分别是 a22_to_u22 和 a32_to_u22
		*
		*   然后在choose中选择平方值最大的一个作为主元，对应的a2x_to_u22 就最为 u22
		*
		*
		*
		*   计算出 a22 a32 作为u22的值   因为u22后面计算l32...li2(i = 2...n) 时候做除法的 a32 = l31*u12 + l32*u22   l32 = (a32 - l31*u12)/u22
		*/
	}
	static void choose(int times,int rowNum,int xnum,double AugMatrix[][])
	{//选主元
		int line=times;
		for(int i=times+1;i<=rowNum;i++)//选最大行
		{
			/*
				*  ---> AugMatrix
				*	0     0    0     									0     0
				*   0	  u11, u12, 									u13,  b1
					0	  l21, a22_to_u22[若这行作为主元 计算出u22的值], 	a23,  b2
					0	  l31, a32_to_u22[若这行作为主元 计算出u22的值], 	a33,  b3
				*              ^
				*              line
				*
				*  假如第一行第一列已经确定，现在到第二行第二列
				*
				*  prepare_a22^2 和 prepare_a32^2  哪个比较大  也可以用Math.abs绝对值大的
			* */
			if(AugMatrix[i][times]*AugMatrix[i][times]>AugMatrix[line][times]*AugMatrix[line][times])
				line=i;
		}
		if(AugMatrix[line][times]==0)//最大数等于零
		{
			System.out.println("doolittle fail !!!"); // hhl LU分解失败  没有解??奇异??
			
		}
		if(line!=times)//交换
		{
			double temp;
			for(int i=1;i<=rowNum+xnum;i++)  // hhl 包括值的一列 一起交换 相当于  Ax=b   PAx = Pb   P是置换矩阵
			{
				temp=AugMatrix[times][i];
				AugMatrix[times][i]=AugMatrix[line][i];
				AugMatrix[line][i]=temp;
			}
		}
	}
	static void resolve(final int times,int rowNum,int xnum,double AugMatrix[][])
	{//分解



		for(int i=times+1;i<=rowNum;i++) 		// update L
		{
			AugMatrix[i][times]=AugMatrix[i][times]/AugMatrix[times][times];
			/*
				l[i,times] 依赖 u[times,times] 的值  这个值在choose选主元的时候确定了哪一行 , 这一行的l[times,times]=1

				后面的几行 l[i,times] (i from times+1 to rowNum ) 都需要除以u[times,times]

				a[i,j] = Σ(k=1 k<=min{i,j}) l[i,k] * u[k,j]

				对于下三角矩阵 i > j

				a[i,j] = (Σ(k=1 k<=j-1) l[i,k] * u[k,j])   +   l[i,j] * u[j,j]

				l[i,j] = {   a[i,j] - (Σ(k=1 k<=j-1) l[i,k] * u[k,j])   } / u[j,j]

				注意{}里面的计算 已经在prepare时候 已经计算好了

			 */

		}

		for(int i=times+1;i<=rowNum+xnum;i++) 	// update U 和 b  b会成为y的!!!  最后求x的时候 就只要 Ux=y了 !!!
		{													//因为y的求法 跟u的求法一样 都是依赖该行的l[i][0~i]和 y列的 y[0~i]  和 b[i]
			for(int j=times-1;j>=1;j--)						// Σ{k 1~i-1 }L[k,1]*y[k] + y[i]  = b[i]
															// y[i] = b[i] -  Σ{k 1~i-1 }L[k,1]*y[k]  形式上跟
															// u[i,j] = a[i,j] - Σ{k 1~i-1 }L[i,k] * u[k,j] 一样的 所以可以同时算
			{
				//AugMatrix[times][i]=AugMatrix[times][i]-AugMatrix[times][j]*AugMatrix[j][i];
				AugMatrix[times][i]-=AugMatrix[times][j]*AugMatrix[j][i];
			}

			/*
			   	a[i,j] = Σ(k=1 k<=min{i,j}) l[i,k] * u[k,j]

			   	对于上三角矩阵 j >= i

			   	a[i,j] = ( Σ(k=1 k<=i-1 ) l[i,k] * u[k,j] )  +  l[i,i] u[i,j]   其中 l[i,i] == 1

			 */
		}

	}
	static void findX(int rowNum,int xnum,double AugMatrix[][])
	{//求解
		// 如果 Ax=b  b是两列 x也是两列的话  那么可以看成 x=[x1,x2] Ax=b --> Ax1 = b1 Ax2 = b2
		// 注意 这里的 AugMatrix[rowNum][rowNum+k] 其实是 y 了  也就是 Ax=b LUx=b  Ly=b  Ux=y  中的y 在前面分解的时候 已经把y的值同时得到了
		for(int k=1;k<=xnum;k++)
		{

			AugMatrix[rowNum][rowNum+k]=AugMatrix[rowNum][rowNum+k]/AugMatrix[rowNum][rowNum]; // 最后一个的解

			for(int i=rowNum-1;i>=1;i--)
			{
				for(int j=rowNum;j>i;j--)
				{
					AugMatrix[i][rowNum+k] -= AugMatrix[i][j]*AugMatrix[j][rowNum+k];
				}
				AugMatrix[i][rowNum+k]=AugMatrix[i][rowNum+k]/AugMatrix[i][i];
			}
		}
	}
}

/*


void DirectLU(double a[N][N+1],double x[])    //列主元LU分解函数
{
	int i,r,k,j;
	double s[N],t[N];//={-20,8,14,-0.8};
	double max;
	for(r=0;r<N;r++)
	{
		max=0;
		j=r;
		for(i=r;i<N;i++) //求是s[i]的绝对值,选主元
		{
			s[i]=a[i][r];
			for(k=0;k<r;k++)
				s[i]-=a[i][k]*a[k][r];
			s[i]=s[i]>0?s[i]:-s[i]; //s[i]取绝对值
			if(s[i]>max){
				j=i;
				max=s[i];
			}
		}

		if(j!=r) //选出的主元所在行j若不是r,则对两行相应元素进行调换
		{
			for(i=0;i<N+1;i++)
				swap(a[r][i],a[j][i]);
		}

		for(i=r;i<N+1;i++) //记算第r行的元素
			for(k=0;k<r;k++){
				a[r][i]-=a[r][k]*a[k][i];
			}

		for(i=r+1;i<N;i++) //记算第r列的元素
		{
			for(k=0;k<r;k++)
				a[i][r]-=a[i][k]*a[k][r];
			a[i][r]/=a[r][r];
		}
	}


	for(i=0;i<N;i++)
		t[i]=a[i][N];

	for(i=N-1;i>=0;i--) //利用回代法求最终解
	{
		for(r=N-1;r>i;r--)
			t[i]-=a[i][r]*x[r];
		x[i]=t[i]/a[i][i];
	}
}


void swap(double &a,double &b)      //交换函数
{
	a=a+b;
	b=a-b;
	a=a-b;
}

int main()
{
	double x[N];
	int i,j;
	double a[N][N+1]={
	1,-1,2,-1,	-8,
	2,-2,3,-3,	-20,
	1,1,1,0,	-2,
	1,-1,4,3,	4
	 };//输入系数矩阵和右侧矩阵y放在一个二维数组里面

	cout<<"系数矩阵为: \n";
	for(i=0;i<N;i++){
		for(j=0;j<N;j++)
			cout<<setw(10)<<a[i][j];
		cout<<endl;
	}
	cout<<"矩阵 y 为: \n";
	for(i=0;i<N;i++)
	  cout<<setw(10)<<a[i][N];
	cout<<endl;
	cout<<"----------------------------------------"<<endl;

	DirectLU(a,x);        //调用LU分解函数

	cout<<"LU矩阵为: \n";
	for(i=0;i<N;i++){
		for(j=0;j<N;j++)
			cout<<setw(10)<<a[i][j];
		cout<<endl;
	}

	cout<<"变换后的右侧矩阵 y 为: \n";
	for(i=0;i<N;i++)
		cout<<setw(10)<<a[i][N];
	cout<<endl;
	cout<<"----------------------------------------"<<endl;

	cout<<"方程的解为: \n";
	for(i=0;i<N;i++)
		cout<<"x["<<i<<"]= "<<x[i]<<endl;
	cout<<"----------------------------------------"<<endl;

	return 0;
}

线性方程组 数值解法两类
https://www.51wendang.com/doc/51e52c241054f54768121754/7
a. 直接法  稠密矩阵为系数矩阵的中低阶线性方程组
	http://www.doc88.com/p-6711178473717.html

	高斯消元法    https://www.51wendang.com/doc/51e52c241054f54768121754/8
		顺序消元法
		列选主元消元法
		全选主元消元法
	直接三角解法
		矩阵的LU分解 (一般矩阵的直接三角分解法（LU分解法/Doolittle分解法))
		Doolittle/杜利特尔分解法
		Crout/克劳特分解法
		特殊矩阵:
		 	三对角方程组的追赶法 (针对特殊的稀疏矩阵 三对角矩阵 对角线和相邻两条次对角线)
 			平方根法(针对对称正定矩阵方程组 工程技术实际问题可以归结为线性方程组 其系数矩阵常具有对称正定性)

b. 迭代法  稀疏矩阵为系数矩阵的高阶线性方程组



* */