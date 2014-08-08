package cn.nju.zouxuan.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cn.nju.zouxuan.start.StartActivity;
import cn.nju.zouxuan.util.SysApplication;

import com.cooliris.media.R;

public class LoginSuccActivity extends Activity {
	
	private Button signout;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);
		Intent intent=getIntent();
		String name=intent.getStringExtra("name");
		setContentView(R.layout.login);
		signout=(Button)findViewById(R.id.button1);
		sp=this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
		TextView nameView=(TextView)findViewById(R.id.textView3);
		nameView.setText(name);
		signout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				sp.edit().putBoolean("ischeck", false).commit();
				sp.edit().putBoolean("auto_ischeck", false).commit(); 
				Intent intent=new Intent(LoginSuccActivity.this,LoginActivity.class);
				startActivity(intent);
				
			}
		});
		
		
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {  
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  
        	//返回到主界面
        	Intent intent=new Intent(LoginSuccActivity.this,StartActivity.class);
        	startActivity(intent);
            return false;  
        }  
        return false;  
    }  
	

}
