package com.zane.moodpainter.view;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class BmpView extends View implements ImageAction{
	
	Matrix savedMatrix = new Matrix();	
	Matrix matrix = new Matrix();;
	
	float startX,startY;
	float secondX,secondY;
	float endX,endY;
	boolean second = false;
	
	Bitmap bitmap;

	public BmpView(Context context, AttributeSet attrs) {

		super(context, attrs);
		
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent e){
		
		
		float dx,dy;
			
		switch(e.getAction()& MotionEvent.ACTION_MASK){
		case MotionEvent.ACTION_DOWN:
			startX = e.getX();
			startY = e.getY();	
			second = false;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			secondX = e.getX(1);
			secondY = e.getY(1);	
			second = true;
			//System.out.println(23233);
			break;
		case MotionEvent.ACTION_MOVE:
			matrix = new Matrix(savedMatrix);
			if(!second){
				endX = e.getX();
				endY = e.getY();
				dx = endX-startX;
				dy = endY-startY;						
				translate(dx,dy);
			}else{
				
				float sx = Math.abs((e.getX(1)-e.getX(0))/(secondX-startX));
				float sy = Math.abs((e.getY(1)-e.getY(0))/(secondY-startY));
				zoom(sx,sy);
				
				
				PointF s = new PointF(startX,startY);
				PointF e1 = new PointF(secondX,secondY);
				PointF e2 = new PointF(e.getX(1),e.getY(1));
				double a = distancePointF(s,e1 );  
                double c = distancePointF(s,e2);  
                double b = distancePointF(e1,e2);  
                  
                double cosb = (a * a + c * c - b * b) / (2 * a * c);  
                  
                if (cosb >= 1) {  
                    cosb = 1f;  
                }  
                  
                float degrees = (float) (Math.acos(cosb) * 180 / Math.PI);  
				
				PointF centerToProMove = new PointF(secondX - startX, secondY - startY);  
				PointF centerToCurMove = new PointF(e.getX(1) - startX, e.getY(1) - startY);
				float result = centerToProMove.x * centerToCurMove.y - centerToProMove.y * centerToCurMove.x; 
				
				if(result<0)  degrees = -degrees;  
				
				rotate(startX, startY, degrees);
			}
			
			break;
		case MotionEvent.ACTION_UP:
			savedMatrix = matrix;
			break;
		case MotionEvent.ACTION_POINTER_UP:
			second = false;
			savedMatrix = matrix;
			break;
		
		}	
		invalidate();
		return true;
	}

	
	
	public void setBitmap(Bitmap bmp){
		this.bitmap = bmp;
		invalidate();
		
	}
	
	@Override
	public void onDraw(Canvas canvas){
		//super.onDraw(canvas);
		
		if(bitmap!=null){			
			canvas.drawBitmap(bitmap, matrix, null);
		}
	} 
	
	
	@Override
	public boolean rotate(float centerx, float centery, float degrees) {
		 
		matrix.postRotate(degrees, centerx, centery);		
		return true;
	}

	@Override
	public boolean translate(float dx, float dy) {
		
		matrix.postTranslate(dx, dy);				
		return true;
	}

	@Override
	public boolean zoom(float scaleX,float scaleY) {
		if(scaleX<=0||scaleY<=0) return false;
		matrix = new Matrix(savedMatrix);
		matrix.postScale(scaleX, scaleY);
		return true;
	}
	
	public float distancePointF(PointF pf1, PointF pf2){
		 float disX = pf2.x - pf1.x;  
	        float disY = pf2.y - pf1.y;  
	        return (float) Math.sqrt(disX * disX + disY * disY);  
	}

}
