package com.zane.moodpainter.view;



public interface ImageAction {

	boolean rotate(float centerx,float centery,float degrees );
	
	boolean translate(float x,float y);
	
	boolean zoom(float scaleX ,float scaleY);
	
	
	
}
