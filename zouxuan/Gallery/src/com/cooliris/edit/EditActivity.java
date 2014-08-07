package com.cooliris.edit;

import com.cooliris.media.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class EditActivity extends Activity {
	ImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit);
		imageView=(ImageView)findViewById(R.id.imageView1);
		Intent intent=getIntent();
		String string=intent.getStringExtra("path");
		Bitmap bitmap=BitmapFactory.decodeFile(string);
		imageView.setImageBitmap(bitmap);
		
	}
	

}
