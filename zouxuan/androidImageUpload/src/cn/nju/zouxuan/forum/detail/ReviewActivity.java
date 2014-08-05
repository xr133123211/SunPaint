package cn.nju.zouxuan.forum.detail;

import com.spring.sky.image.upload.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ReviewActivity extends Activity {
	Intent lastIntent;
	EditText editText;
	Button button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.review);
		editText=(EditText)findViewById(R.id.editText1);
		button=(Button)findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				review();
			}
		});
		lastIntent=getIntent();
	}
	
	
	public void review(){
		String data=editText.getText().toString();
		lastIntent.putExtra("data", data);
		setResult(Activity.RESULT_OK, lastIntent);
		finish();
	}
	
	
	
	
	
	
	

}
