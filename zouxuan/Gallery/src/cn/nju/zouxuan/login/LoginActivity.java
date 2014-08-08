package cn.nju.zouxuan.login;

import org.apache.http.HttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;
import cn.nju.zouxuan.util.Client;
import cn.nju.zouxuan.util.SysApplication;

import com.cooliris.media.R;

public class LoginActivity extends Activity {
	private static final int LOGIN_SUCC = 0;
	private static final int LOGIN_FAIL = 1;
	LoginService service;
	EditText name;
	EditText password;
	private CheckBox rememberBox, autoBox;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		SysApplication.getInstance().addActivity(this);
		sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
		name = (EditText) findViewById(R.id.editText1);
		password = (EditText) findViewById(R.id.editText2);
		final Button login = (Button) findViewById(R.id.forum_button);
		final Button register = (Button) findViewById(R.id.login_button);
		rememberBox = (CheckBox) findViewById(R.id.checkBox1);
		autoBox = (CheckBox) findViewById(R.id.checkBox2);

		if (sp.getBoolean("ischeck", false)) {
			// 设置默认是记录密码状态
			System.out.println("~~~~~~~~~~");
			rememberBox.setChecked(true);
			name.setText(sp.getString("username", ""));
			password.setText(sp.getString("password", ""));
			// 判断自动登陆多选框状态
			if (sp.getBoolean("auto_ischeck", false)) {
				// 设置默认是自动登录状态
				autoBox.setChecked(true);
				// 跳转界面
				userLogin(sp.getString("username", ""),
						sp.getString("password", ""));

			}
		}

		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (arg0 == login) {
					String nameString = name.getText().toString();
					String passwordString = password.getText().toString();
					if (nameString.equals("") || passwordString.equals("")) {
						Toast toast = Toast.makeText(LoginActivity.this,
								"有内容为空", Toast.LENGTH_SHORT);
						toast.show();
					} else {
						userLogin(name.getText().toString(), password.getText()
								.toString());
					}
				} else if (arg0 == register) {
					Intent intent = new Intent(LoginActivity.this,
							RegisterActivity.class);
					startActivity(intent);
				}
			}
		};
		login.setOnClickListener(listener);
		register.setOnClickListener(listener);
		rememberBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (rememberBox.isChecked()) {
					sp.edit().putBoolean("ischeck", true).commit();
				} else {
					sp.edit().putBoolean("ischeck", false).commit();
				}
			}
		});

		autoBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (autoBox.isChecked()) {
					sp.edit().putBoolean("auto_ischeck", true).commit();
				} else {
					sp.edit().putBoolean("auto_ischeck", false).commit();
				}
			}
		});

	}

	private void userLogin(final String name, final String password) {
		new Thread() {
			public void run() {
				service = new LoginService();
				String s = null;
				JSONObject result = null;
				try {
					HttpResponse response = service.http_post(
							"http://1.zouxuan2.sinaapp.com/login.php",
							new BasicNameValuePair("username", name),
							new BasicNameValuePair("password", password));
					result = service.jsonDecode(response);
					s = result.getString("result");
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (s.equals("success")) {
					handler.sendEmptyMessage(LOGIN_SUCC);
					Client.name = name;
				} else {
					handler.sendEmptyMessage(LOGIN_FAIL);
				}
			}
		}.start();
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == LOGIN_SUCC) {
				Toast toast = Toast.makeText(LoginActivity.this, "success",
						Toast.LENGTH_SHORT);
				toast.show();
				if (rememberBox.isChecked()) {
					Editor editor = sp.edit();
					editor.putString("username", name.getText().toString());
					editor.putString("password", password.getText().toString());

					editor.commit();
				}

				Intent intent = new Intent(LoginActivity.this,
						LoginSuccActivity.class);
				intent.putExtra("name", name.getText().toString());
				startActivity(intent);

			} else if (msg.what == LOGIN_FAIL) {
				name.setText("");
				password.setText("");
				Toast toast = Toast.makeText(LoginActivity.this, "fail",
						Toast.LENGTH_SHORT);
				toast.show();
			}
		}

	};

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// 返回到主界面
			finish();
			return false;
		}
		return false;
	}

}
