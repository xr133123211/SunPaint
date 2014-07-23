package com.example.tracing.paint;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.example.tracing.paint.myPainter.simplePainter;

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
			invalidate();
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
	//	System.out.println("size: "+size);
		
		paintPosition p1,p2;
		simplePainter paint = new simplePainter();
		paintPosition A,B,C,D;
		A=new paintPosition(0, 0);
		B=new paintPosition(0, 0);
		C=new paintPosition(0, 0);
		D=new paintPosition(0, 0);
		graphicVector direction,prep;
		float length;
		long time;
		float v;
		if (size==0) return;
		if (size==1)
		{
			p1=list.get(0);
			canvas.drawLine(p1.getX(), p1.getY(), p1.getX(), p1.getY(), paint);
			return;
		}
		if (size==2)
		{
			p1=list.get(0);
			p2=list.get(1);
			canvas.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY(), paint);
			return;
		}
		
		boolean flag=true;
		for (int i=0;i<size-1;i++ ){
			
			p1 = list.get(i);
			p2 = list.get(i+1);
			//canvas.drawLine(p1.getX(), p1.getY(), p2.getX(),p2.getY(), paint);
			direction=new graphicVector(p2.getX()-p1.getX(), p2.getY()-p1.getY());
			length=direction.length();
			time=p2.getTime().getTimeInMillis()-p1.getTime().getTimeInMillis();
			v=length/time;
			if (v==0) v=1;
			else if (v>10) v = 10;
			
			prep=direction.prep();
			if (flag)
			{
				A.SetXY(p1.x+prep.getX()*v,p1.getY()+prep.getY()*v);
				B.SetXY(p1.x-prep.getX()*v,p1.getY()-prep.getY()*v);
				flag=false;
			}
			else 
			{
				A.SetXY(C.getX(), C.getY());
				B.SetXY(D.getX(), D.getY());		
			}
			C.SetXY(p2.x+prep.getX()*v,p2.getY()+prep.getY()*v);
			D.SetXY(p2.x-prep.getX()*v,p2.getY()-prep.getY()*v);
	
			
			paint.setColor(Color.BLACK);
			Path temppath = new Path();  
			temppath.moveTo(A.getX(), A.getY());// 姝ょ偣涓哄杈瑰舰鐨勮捣鐐�  
			temppath.lineTo(B.getX(), B.getY());  
			temppath.lineTo(C.getX(), C.getY());  
			temppath.close(); // 浣胯繖浜涚偣鏋勬垚灏侀棴鐨勫杈瑰舰  
		    canvas.drawPath(temppath, paint); 
		    temppath.reset();
		    paint.setColor(Color.RED);
		    temppath.moveTo(B.getX(), B.getY());// 姝ょ偣涓哄杈瑰舰鐨勮捣鐐�  
			temppath.lineTo(C.getX(), C.getY());  
			temppath.lineTo(D.getX(), D.getY());  
			temppath.close(); // 浣胯繖浜涚偣鏋勬垚灏侀棴鐨勫杈瑰舰  
		    canvas.drawPath(temppath, paint); 
		}
		
		
	}
	


}
