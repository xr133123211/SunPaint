package cn.nju.zouxuan.forum.post;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONTokener;

import cn.nju.zouxuan.util.Client;
import android.R.integer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


/**
 * 
 * 上传工具类
 * 支持上传文件和参数
 */

public class UploadUtil {
	private static UploadUtil uploadUtil;
	private String string;
	private UploadUtil() {
	}

	/**
	 * 单例模式获取上传工具类
	 * @return
	 */
	public static UploadUtil getInstance() {
		if (null == uploadUtil) {
			uploadUtil = new UploadUtil();
		}
		return uploadUtil;
	}

	private static final String TAG = "UploadUtil";
	/***
	 * 请求使用多长时间
	 */
	

	
	/**
	 * android上传文件到服务器
	 * 
	 * @param filePath
	 *            需要上传的文件的路径
	 * @param fileKey
	 *            在网页上<input type=file name=xxx/> xxx就是这里的fileKey
	 * @param RequestURL
	 *            请求的URL
	 */
	public void  uploadFile(String filePath,String data) {
		if (filePath == null) {
			return;
		}
		try {
			File file = new File(filePath);
			uploadFile(file,data);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
	}

	/**
	 * android上传文件到服务器
	 * 
	 * @param file
	 *            需要上传的文件
	 * @param fileKey
	 *            在网页上<input type=file name=xxx/> xxx就是这里的fileKey
	 * @param RequestURL
	 *            请求的URL
	 */
	public void uploadFile(final File file,final String data) {
		if (file == null || (!file.exists())) {
			return ;
		}

		Log.i(TAG, "请求的fileName=" + file.getName());
		
		new Thread(new Runnable() {
			
			public void run() {
				toUpoadFile(file,data);
			}
		}).start();
		
	}
	
	
	protected JSONObject jsonDecode(HttpResponse response) throws Exception {
		try {
			String responseString = EntityUtils.toString(response.getEntity());
			JSONTokener tokener = new JSONTokener(responseString);
			JSONObject jsonObject = (JSONObject) tokener.nextValue();
			return jsonObject;
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	public void toUpoadFile(File file,String data){
		HttpPost httpPost=new HttpPost("http://1.zouxuan2.sinaapp.com/file.php");
		MultipartEntity entity = new MultipartEntity();
		ContentBody contentBody = new FileBody(file);
		entity.addPart("file", contentBody);
		Log.i(TAG, file.getName());
		try {
			entity.addPart("data", new StringBody(data,Charset
			        .forName(org.apache.http.protocol.HTTP.UTF_8)));
			Log.i(TAG, data);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		httpPost.setEntity(entity);
		try {
			HttpResponse response = Client.getHttpClient().execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				JSONObject jsonObject=jsonDecode(response);
				string=jsonObject.getString("result");
//				if (string.equals("success")) {
//					mainActivity.handler_2.sendEmptyMessage(MainActivity.UPLOAD_SUCC);
//				}
//				else {
//					mainActivity.handler_2.sendEmptyMessage(MainActivity.UPLOAD_FAIL);	
//				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	
	

	
	
	
	
}
