package com.example.tracing.show;



import com.example.tracing.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;

public class showView extends View{
	
	boolean debug = true;

	float x1,y1,x2,y2;
	int None = 0;
	int Drag = 1;
	int Zoom = 2;
	int Mode = None;
	
	int w,h;
	
	
	
	Matrix matrix ;
	Bitmap img ;
	Paint paint;
	
	
	public showView(Context context) {
		super(context);
		init();
	}
	
	public showView(Context context, AttributeSet attrs){
		super(context,attrs);
		init()	;
	}
	
	public void init(){
		float[] array = {1,0,0,0,1,0,0,0,1};
		matrix = new Matrix();
		matrix.setValues(array);
		Resources r = this.getContext().getResources();
		img = BitmapFactory.decodeResource(r,R.drawable.fx1);
		ImageProcess process = new ImageProcess(img);
		img = process.getAbstract();
		paint = new Paint();
		//if (debug) System.out.println(w+"       "+h);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()&MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			x1 = event.getX();
			y1 = event.getY();
			Mode = Drag;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			x2 = event.getX(1);
			y2 = event.getY(1);
			Mode=Zoom; 
			break;

		case MotionEvent.ACTION_MOVE:
			onTouchMove(event);
			break;
		
		case MotionEvent.ACTION_POINTER_UP:
			Mode = None;
			
			invalidate();
			break;
		
		case MotionEvent.ACTION_UP:
			Mode = None;
			
			invalidate();
			break;

		}
		
		//if (debug) System.out.println(x1+" "+y1);

		return true;	
	}
	
	//滑动过程的处理
	private void onTouchMove(MotionEvent event){
		


		if (Mode==Drag){
			float currentX = event.getX();
			float currentY = event.getY();
			float dx = currentX-x1;
			float dy = currentY-y1;
			
			matrix.postTranslate(dx, dy);
			
			x1 = currentX;
			y1 = currentY;
			
			if (debug) System.out.println(matrix); 
		}
		else if (Mode == Zoom){
			float currentX1 = event.getX(0);
			float currentY1 = event.getY(0);
			float currentY2 = event.getY(1);
			float currentX2 = event.getX(1);
			float l1 = getDistance(x1,y1,x2,y2);
			float l2 = getDistance(currentX1,currentY1,currentX2,currentY2);
			float m = l2/l1;//倍数
			//if (debug)System.out.println(l1+" "+l2+" "+m);
			//if (debug)System.out.println(matrix);
			matrix.postScale(m, m,(x1+x2)/2,(y1+y2)/2);
			
			x1 = currentX1;
			y1 = currentY1;
			x2 = currentX2;
			y2 = currentY2;
			//if (debug)System.out.println(l1-l2);
			
		}else 
		{
			
		}
		invalidate();
		
	}

	private float getDistance(float x1, float y1, float x2, float y2) {
		// TODO Auto-generated method stub
		float x = x1-x2;
		float y = y1-y2;
		return FloatMath.sqrt(x * x + y * y);
	}
	
	public void onDraw(Canvas canvas)  {  
	        super.onDraw(canvas);  
	        w = this.getWidth();
			h = this.getHeight();
	        canvas.drawBitmap(img, matrix, paint);
	        if (debug) System.out.println(Mode);
	}
	
	


}
