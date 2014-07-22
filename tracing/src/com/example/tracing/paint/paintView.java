package com.example.tracing.paint;

import java.util.ArrayList;

import com.example.tracing.paint.myPainter.simplePainter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class paintView extends ImageView{
	
	
	private paintStack redo;
	private paintStack undo;
	private float x1,y1;
	private paintPath path ;
	
	Matrix matrix;
	
	
	 
	
	public paintView(Context context) {
		super(context);
		init();
	}
	
	
	public paintView(Context context, AttributeSet attrs){
		super(context,attrs);
		init();
	}
	
	public void init(){
		redo = new paintStack();
		undo = new paintStack();
		matrix = new Matrix();
		path = new paintPath();
		
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()&MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			x1 = event.getX();
			y1 = event.getY();
			path = new paintPath();
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
		
			break;

		case MotionEvent.ACTION_MOVE:
			onTouchMove(event);
			break;
		
		case MotionEvent.ACTION_POINTER_UP:
			
			
			break;
		
		case MotionEvent.ACTION_UP:
			undo.Push(path);
			break;

		}
		
		return true;	
	}
	
	public void onTouchMove(MotionEvent event){
		float currentx = event.getX();
		float currenty = event.getY();
		if (x1!=currentx||y1!=currenty)
		{
			path.addPosition(new paintPosition(currentx,currenty));
		}
		invalidate();
	}
	
	
	public void onDraw(Canvas canvas)  {  
        super.onDraw(canvas);  
        for (paintPath drawPath : undo.getList())
        	paintLine(canvas,drawPath);
        paintLine(canvas,path);

	}
	
	public void paintLine(Canvas canvas,paintPath path){
		ArrayList<paintPosition> list = path.getList();
		int size  = list.size();
		paintPosition p1,p2;
		simplePainter paint = new simplePainter();
		
		
		for (int i=0;i<size-1;i++ ){
			
			p1 = list.get(i);
			p2 = list.get(i+1);
			canvas.drawLine(p1.getX(), p1.getY(), p2.getX(),p2.getY(), paint);
			
		}
	}
	


}
