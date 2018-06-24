package com.bn.Sample11_9;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Typeface;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FontUtil 
{
	private static final String TAG = "FontUtil";
	static int cIndex=0;//文本内容索引
	static final float textSize=30;//字体的大小
	static final float LINE_INTERVAL = 5 ; // 行间隔
	static int R=255;//画笔红色通道的值
	static int G=255;//画笔绿色通道的值
	static int B=255;//画笔蓝色通道的值

	// 使用Paint画到(Canvas.drawText)到对应的Bitmap
	public static Bitmap generateWLT(String[] str,int width,int height)
	{//生成文本纹理图的方法
		Paint paint=new Paint();					// 创建画笔对象
		paint.setARGB(255, R, G, B);				// 设置画笔颜色
		paint.setTextSize(textSize);				// 设置字体大小 单位是像素 可能超过画布Bitmap的尺寸/宽高
		paint.setTypeface(Typeface.DEFAULT_BOLD); 	// 粗体
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);		// 打开抗锯齿，使字体边缘光滑
		Bitmap bmTemp=Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);// 创建指定大小的bitmap
		Canvas canvasTemp = new Canvas(bmTemp);// 根据指定的位图创建画布
		for(int i=0;i<str.length;i++)// 绘制当前纹理图对应的每行文字
		{
			// hhl. drawText需要设定x,y坐标，是baseLine的位置
			// hhl. 行间隔为5 注意i=0间隔为0
			canvasTemp.drawText(str[i], 0,
					textSize*(i+1) + (i==0?0:(i-1)*LINE_INTERVAL),
					paint);
		}




		if(str.length == content.length){
			try {
				FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory()+"/fontUtil.png");
				bmTemp.compress(Bitmap.CompressFormat.PNG,100,fos);
				fos.close();
				Log.w(TAG,"save bitmap done");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return bmTemp;//返回绘制的作为纹理图的位图
	}

	// 使用 TextView(Layout)画到Bitmap(Canvas)上
	// getSuitable(context,R.layout.layout_bitmap_text , R.id.textView, R.string.bitmapTexture )
	static public Bitmap getSuitable(Context context, int textString,
							  int viewWidth, int viewHeight ){ // viewWidth = 400 viewHeight = 58

		final int FONT_SIZE_IN_PIXEL = 40 ;
		final int CONFIG_USE_LAYOUT = 2 ; // 1 = linear 2 = frame

		if(CONFIG_USE_LAYOUT == 2){
			int textlayout = com.bn.Sample11_9.R.layout.framelayout_bitmap_text;
			int textViewInLayout = com.bn.Sample11_9.R.id.textView;


			FrameLayout layout = (FrameLayout) LayoutInflater.from(context).inflate(textlayout , null);
			FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams)layout.getLayoutParams();
			Log.w(TAG,"layoutParams2 = " +layoutParams2 ); // 这个是空的!!

			TextView textView = (TextView)layout.findViewById(textViewInLayout);
			// 获取TextView的内部Layout 设置为 水平右对齐 垂直居中
			FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) textView.getLayoutParams();
			layoutParams.gravity = Gravity.CENTER_VERTICAL|Gravity.RIGHT|Gravity.END;
			//layoutParams.setMargins(0,0,0,0);
			textView.setLayoutParams(layoutParams);
			//textView.setGravity(Gravity.CENTER_VERTICAL);
			//textView.setPadding(0,0,0,0);
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, FONT_SIZE_IN_PIXEL);
			textView.setText(textString);
			textView.setTextColor(Color.argb(200, 255, 0, 0));




			// MeasureSpec通过将SpecMode和SpecSize打包成一个int值来避免过多的对象内存分配
			// MeasureSpec的值由specSize和specMode共同组成的，其中specSize记录的是大小，specMode记录的是规格
			// MeasureSpec是父控件提供给子View的一个参数，作为设定自身大小参考，只是个参考，要多大，还是View自己说了算
			int width = View.MeasureSpec.makeMeasureSpec(viewWidth, View.MeasureSpec.EXACTLY);  // 宽度是屏幕View的宽度 固定
			int height = View.MeasureSpec.makeMeasureSpec(viewHeight, View.MeasureSpec.EXACTLY); 		// 高度跟着字体大小
			//layout.setPadding(0,0,0,0);
			layout.measure(width, height); // 参数是 int widthMeasureSpec, int heightMeasureSpec 不只是宽高
			layout.layout(0, 0, viewWidth, viewHeight);

			// public void layout(int l, int t, int r, int b);
			// 该方法是View的放置方法，在View类实现。
			// 调用该方法需要传入放置View的矩形空间左上角left、top值和右下角right、bottom值。这四个值是相对于父控件而言的。
			// 例如 传入的是（10, 10, 100, 100），则该View在距离父控件的左上角位置(10, 10)处显示，显示的大小是宽高是90(参数r,b是相对左上角的)



			Bitmap result = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888); // 带有透明通道
			if (result == null) {
				return null;
			}
			Canvas canvasL = new Canvas(result);
			canvasL.setDrawFilter(new PaintFlagsDrawFilter(
					Paint.ANTI_ALIAS_FLAG,
					Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
			layout.draw(canvasL); // 实际是view.draw(canvas)  TextView(Layout)画到Bitmap(Canvas)上
			return result;

		}else{  // LinearLayout目前无法做到居中

			int textlayout = com.bn.Sample11_9.R.layout.layout_bitmap_text;
			int textViewInLayout = com.bn.Sample11_9.R.id.textView;

			LinearLayout layout = (LinearLayout) LayoutInflater.from(context).inflate(textlayout , null);

			TextView textView = (TextView)layout.findViewById(textViewInLayout);

			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
			layoutParams.gravity = Gravity.CENTER_VERTICAL|Gravity.RIGHT|Gravity.END;
			layoutParams.setMargins(0,0,0,0);
			textView.setLayoutParams(layoutParams);
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, FONT_SIZE_IN_PIXEL);
			textView.setText(textString);
			textView.setTextColor(Color.argb(200, 255, 0, 0));


			int width = View.MeasureSpec.makeMeasureSpec(viewWidth, View.MeasureSpec.EXACTLY);
			int height = View.MeasureSpec.makeMeasureSpec(viewHeight, View.MeasureSpec.EXACTLY);
			layout.setPadding(0,0,0,0);
			layout.measure(width, height);
			layout.layout(0, 0, viewWidth, viewHeight);




			Bitmap result = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);
			if (result == null) {
				return null;
			}
			Canvas canvasL = new Canvas(result);
			canvasL.setDrawFilter(new PaintFlagsDrawFilter(
					Paint.ANTI_ALIAS_FLAG,
					Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
			layout.draw(canvasL);

			return result;
		}

	}


	static String[] content=
	{
		"赵客缦胡缨，吴钩霜雪明。",
		"银鞍照白马，飒沓如流星。",
		"十步杀一人，千里不留行。",
		"事了拂衣去，深藏身与名。",
		"闲过信陵饮，脱剑膝前横。",
		"将炙啖朱亥，持觞劝侯嬴。",
		"三杯吐然诺，五岳倒为轻。",
		"眼花耳热后，意气素霓生。",
		"救赵挥金槌，邯郸先震惊。",
		"千秋二壮士，煊赫大梁城。",
		"纵死侠骨香，不惭世上英。",
		"谁能书閤下，白首太玄经。",
	};
	
	public static String[] getContent(int length,String[] content)
	{//获取指定行数字符串数组的方法
		String[] result=new String[length+1];//创建字符串数组
		for(int i=0;i<=length;i++)
		{
			result[i]=content[i];//将当前需要的内容填入数组
		}
		return result;
	}
	
	public static void updateRGB()//随机产生画笔颜色值的方法
	{
		R=(int)(255*Math.random());//随机产生画笔红色通道值
		G=(int)(255*Math.random());//随机产生画笔绿色通道值
		B=(int)(255*Math.random());//随机产生画笔蓝色通道值
	}
}