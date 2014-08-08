package cn.nju.zouxuan.forum.post;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import cn.nju.zouxuan.forum.list.ListActivity;
import cn.nju.zouxuan.util.SysApplication;

import com.cooliris.media.R;

/**
 * 说明：主要用于选择文件和上传文件操作
 */
public class PostActivity extends Activity implements OnClickListener {
	private static final String TAG = "uploadImage";
	public static final int UPLOAD_SUCC = 1;
	public static final int UPLOAD_FAIL = 2;

	/**
	 * 去上传文件
	 */
	protected static final int TO_UPLOAD_FILE = 1;
	/**
	 * 上传文件响应
	 */
	protected static final int UPLOAD_FILE_DONE = 2; //
	/**
	 * 选择文件
	 */
	public static final int TO_SELECT_PHOTO = 3;
	/**
	 * 上传初始化
	 */
	private static final int UPLOAD_INIT_PROCESS = 4;
	/**
	 * 上传中
	 */
	private static final int UPLOAD_IN_PROCESS = 5;
	/***
	 * 这里的这个URL是我服务器的javaEE环境URL
	 */
	private Button selectButton, uploadButton, recordButton;
	private ImageView imageView;
	private EditText editText;
	private String picPath = null;
	private ProgressDialog progressDialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);
		setContentView(R.layout.post);
		initView();
	}

	/**
	 * 初始化数据
	 */
	private void initView() {
		selectButton = (Button) this.findViewById(R.id.selectImage);
		uploadButton = (Button) this.findViewById(R.id.uploadImage);
		selectButton.setOnClickListener(this);
		uploadButton.setOnClickListener(this);
		imageView = (ImageView) this.findViewById(R.id.imageView);
		editText = (EditText) this.findViewById(R.id.editText1);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.selectImage:
			Intent intent = new Intent(this, SelectPicActivity.class);
			startActivityForResult(intent, TO_SELECT_PHOTO);
			break;
		case R.id.uploadImage:
			if (picPath != null) {
				handler.sendEmptyMessage(TO_UPLOAD_FILE);
			} else {
				Toast.makeText(this, "上传的文件路径出错", Toast.LENGTH_LONG).show();
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && requestCode == TO_SELECT_PHOTO) {
			picPath = data.getStringExtra(SelectPicActivity.KEY_PHOTO_PATH);
			Log.i(TAG, "最终选择的图片=" + picPath);
			Bitmap bm = BitmapFactory.decodeFile(picPath);
			imageView.setImageBitmap(bm);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void toUploadFile() {
		UploadUtil uploadUtil = UploadUtil.getInstance();
		uploadUtil.uploadFile(picPath, editText.getText().toString());
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case TO_UPLOAD_FILE:
				new Thread() {
					public void run() {
						toUploadFile();
						handler_2.sendEmptyMessage(UPLOAD_SUCC);
					}
				}.start();
				break;
			case UPLOAD_INIT_PROCESS:
				break;
			case UPLOAD_IN_PROCESS:
				break;
			case UPLOAD_FILE_DONE:
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	public Handler handler_2 = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == UPLOAD_FAIL) {
				Toast toast = Toast.makeText(PostActivity.this, "失败",
						Toast.LENGTH_LONG);
				toast.show();
			} else if (msg.what == UPLOAD_SUCC) {
				Toast toast = Toast.makeText(PostActivity.this, "发帖成功",
						Toast.LENGTH_LONG);
				toast.show();
				imageView.setImageBitmap(null);
				editText.setText("");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Intent intent = new Intent(PostActivity.this,
						ListActivity.class);
				startActivity(intent);
				
			}
		}
	};

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent(PostActivity.this,
					ListActivity.class);
			startActivity(intent);
			return false;
		}
		return false;
	}

}