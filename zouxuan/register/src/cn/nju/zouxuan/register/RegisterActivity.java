package cn.nju.zouxuan.register;

import org.apache.http.HttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity{
	
	private LoginService service;
	private static final int REGISTER_SUCC=0;
	private static final int REGISTER_SAMENAME=1;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		final Button register=(Button)findViewById(R.id.button3);
		final EditText name=(EditText)findViewById(R.id.editText1);
		final EditText password1=(EditText)findViewById(R.id.editText2);
		final EditText password2=(EditText)findViewById(R.id.editText3);
		
		OnClickListener listener=new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (arg0==register) {
					String namesString=name.getText().toString();
					String passwordString1=password1.getText().toString();
					String passwordString2=password2.getText().toString();
					if (namesString.equals("")||passwordString1.equals("")||passwordString2.equals("")) {
						Toast toast = Toast.makeText(RegisterActivity.this, "有内容未填写",
								Toast.LENGTH_SHORT);
						toast.show();
					}
					else if (!passwordString1.equals(passwordString2)) {
						password1.setText("");
						password2.setText("");
						Toast toast = Toast.makeText(RegisterActivity.this, "两次输入的密码不同",
								Toast.LENGTH_SHORT);
						toast.show();
					}
					else{
						userResgister(namesString, passwordString1);
					}
				}
			}
		};
		register.setOnClickListener(listener);
	}
	
	private void userResgister(final String name, final String password) {
		new Thread(){
			public void run(){
				service=new LoginService();
				String string=null;
				JSONObject result=null;
				try {
					HttpResponse response=service.http_post("http://1.zouxuan2.sinaapp.com/register.php", 
							new BasicNameValuePair("username",name),new BasicNameValuePair("password", password));
					result = service.jsonDecode(response);
					string= result.getString("result");
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					service.shut();
				}
				if (string.equals("success")) {
					handler.sendEmptyMessage(REGISTER_SUCC);
				} else if(string.equals("invalid")){
					handler.sendEmptyMessage(REGISTER_SAMENAME);
				}
			}
		}.start();
	}


	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what==REGISTER_SUCC) {
				Toast toast = Toast.makeText(RegisterActivity.this, "注册成功",
						Toast.LENGTH_SHORT);
				toast.show();
			}
			else if (msg.what==REGISTER_SAMENAME) {
				Toast toast = Toast.makeText(RegisterActivity.this, "用户名已被使用",
						Toast.LENGTH_SHORT);
				toast.show();
			}
		}

	};

	
	

}
