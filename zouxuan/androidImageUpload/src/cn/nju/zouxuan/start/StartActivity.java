package cn.nju.zouxuan.start;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONObject;

import cn.nju.zouxuan.R;
import cn.nju.zouxuan.R.string;
import cn.nju.zouxuan.forum.list.ListActivity;
import cn.nju.zouxuan.login.LoginActivity;
import cn.nju.zouxuan.util.Client;
import cn.nju.zouxuan.util.SysApplication;
import android.R.bool;
import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class StartActivity extends Activity {
	Button forumButton;
	Button loginButton;
	private static final int NOLOGIN = 1;
	private static final int LOGIN = 2;
	String name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
		SysApplication.getInstance().addActivity(this);
		forumButton = (Button) findViewById(R.id.button1);
		loginButton=(Button)findViewById(R.id.button2);
		forumButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				testlogin();
			}
		});
		
		loginButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent(StartActivity.this,LoginActivity.class);
				startActivity(intent);
				
			}
		});
	}

	public void testlogin() {
		new Thread() {

			public void run() {
				HttpGet request = new HttpGet(
						"http://1.zouxuan2.sinaapp.com/testlogin.php");
				try {
					HttpResponse response = Client.getHttpClient().execute(
							request);
					JSONObject jsonObject = Client.jsonDecode(response);
					name = jsonObject.getString("result");
					//TODO如果登录，那返回名字，如果没有登录，返回unlogin
					if (name.equals("unlogin")) {
						handler.sendEmptyMessage(NOLOGIN);
					} else {
						handler.sendEmptyMessage(LOGIN);
					}
//					handler.sendEmptyMessage(LOGIN);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}.start();
	}

	
	private void requestlogin() {
		Toast toast=Toast.makeText(this, "登录！", Toast.LENGTH_LONG);
		toast.show();
	}
	
	
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NOLOGIN:
				requestlogin();
				break;
			case LOGIN:
				Intent intent = new Intent(StartActivity.this,
						ListActivity.class);
				intent.putExtra("isfirst", true);
				
				startActivity(intent);
				break;

			default:
				break;
			}
		}
	};

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// 返回到主界面
			SysApplication.getInstance().exit();
			return false;
		}
		return false;
	}

}
