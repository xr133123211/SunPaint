package cn.nju.zouxuan.forum.detail;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.nju.zouxuan.util.Client;
import cn.nju.zouxuan.util.SysApplication;

import com.cooliris.media.R;

public class DetailActivity extends Activity {

	private static final int REQUIRE_HEIGHT = 200;
	private static final int REQUIRE_WIDTH = 400;
	private JSONArray jsonArray;
	private String[] namelist;
	private String[] timelist;
	private String[] datalist;
	private ListView listView;
	private DetailAdapter adapter;
	private Button comment;
	private String review;
	private String name; // 原作者的时间
	private String time;
	private ImageView imageView;
	private Bitmap bitmap;
	private String review_time;
	private int review_length;
	private int reviewer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);
		setContentView(R.layout.detail);
		comment = (Button) findViewById(R.id.forum_button);
		TextView textView_name = (TextView) findViewById(R.id.textview_name);
		TextView textView_time = (TextView) findViewById(R.id.textView_time);
		imageView = (ImageView) findViewById(R.id.imageView1);
		TextView textView_data = (TextView) findViewById(R.id.textView1);
		Intent intent = getIntent();
		name = intent.getStringExtra("name");
		time = intent.getStringExtra("time");
		String data = intent.getStringExtra("data");
		final String image = intent.getStringExtra("image");
		textView_name.setText(name);
		textView_time.setText(time);
		textView_data.setText(data);
		new Thread() {
			public void run() {
				bitmap = loadImageFromInternet(image);
				HttpResponse response = http_get(name, time);
				getJson(response);
				handler.sendEmptyMessage(0);
			}
		}.start();

		comment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(DetailActivity.this,
						ReviewActivity.class);
				startActivityForResult(intent, 0);
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (resultCode == Activity.RESULT_OK && requestCode == 0) {
			review = intent.getStringExtra("data");
			HttpResponse response = sendReview(review);
			try {
				JSONObject jsonObject = Client.jsonDecode(response);
				review_time = jsonObject.getString("time");
				name = jsonObject.getString("name");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			namelist[review_length] = name;
			timelist[review_length] = time;
			datalist[review_length] = review;
			setupViews(review_length + 1);

		}
		super.onActivityResult(requestCode, resultCode, intent);
	}

	private void setupViews(int length) {
		listView = (ListView) findViewById(R.id.listView1);
		adapter = new DetailAdapter(namelist, timelist, datalist, length, this);
		listView.setAdapter(adapter);
		imageView.setImageBitmap(bitmap);
	}

	private void getJson(HttpResponse httpResponse) {
		HttpEntity entity = httpResponse.getEntity();
		try {
			InputStream is = entity.getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is,
					"iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
//			if (br.readLine()==null) {
//				System.out.println("!!!!!!!!!!!!");
//			}
			sb.append(br.readLine() + "\n");
			String line = "0";
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			String result = sb.toString();
			jsonArray = new JSONArray(result);
			review_length = jsonArray.length();
			System.out.println("reviewLength: " + review_length);
			namelist = new String[review_length + 1];
			timelist = new String[review_length + 1];
			datalist = new String[review_length + 1];

			for (int i = 0; i < jsonArray.length(); i++) { // 把读取到数据库表的数据通过for循环添加到数组里
				JSONObject json_data = jsonArray.getJSONObject(i);
				namelist[i] = json_data.getString("reviewer");
				timelist[i] = json_data.getString("reviewer_time");
				datalist[i] = json_data.getString("data");
			}

		} catch (JSONException e) {
			review_length=0;
			namelist=new String[1];
			timelist=new String[1];
			datalist=new String[1];
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			setupViews(review_length);
		}
	};

	protected HttpResponse http_get(String owner, String owner_time) {

		String url = new String(
				"http://1.zouxuan2.sinaapp.com/detail.php?owner=" + owner
						+ "&ownertime=" + owner_time).replace(" ", "+");
		HttpGet request = new HttpGet(url);
		try {
			HttpResponse response = Client.getHttpClient().execute(request);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public HttpResponse sendReview(String data) {
		try {
			return Client.http_post("http://1.zouxuan2.sinaapp.com/review.php",
					new BasicNameValuePair("owner", name),
					new BasicNameValuePair("ownertime", time),
					new BasicNameValuePair("data", data));

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public Bitmap loadImageFromInternet(String url) {
		System.out.println(url);
		Bitmap bitmap = null;
		HttpClient client = Client.getHttpClient();
		HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 3000);
		HttpConnectionParams.setSocketBufferSize(params, 3000);
		HttpResponse response = null;
		InputStream inputStream = null;
		HttpGet httpGet = null;
		try {

			httpGet = new HttpGet(url);

			response = client.execute(httpGet);
			int stateCode = response.getStatusLine().getStatusCode();
			if (stateCode != HttpStatus.SC_OK) {
				return bitmap;
			}
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				try {
					inputStream = entity.getContent();
					final BitmapFactory.Options options = new BitmapFactory.Options();
					options.inJustDecodeBounds = true;
					byte[] data = inputStream2ByteArr(inputStream);
					Bitmap b = BitmapFactory.decodeByteArray(data, 0,
							data.length, options);
					options.inSampleSize = calculateInSampleSize(options,
							REQUIRE_WIDTH, REQUIRE_HEIGHT);
					// 设置为false，确保可以得到bitmap != null
					options.inJustDecodeBounds = false;
					bitmap = BitmapFactory.decodeByteArray(data, 0,
							data.length, options);
					return bitmap;
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (ClientProtocolException e) {
			httpGet.abort();
			e.printStackTrace();
		} catch (IOException e) {
			httpGet.abort();
			e.printStackTrace();
		}
		return bitmap;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// 源图片的高度和宽度
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			// 计算出实际宽高和目标宽高的比率
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			// 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
			// 一定都会大于等于目标的宽和高。
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	private byte[] inputStream2ByteArr(InputStream inputStream)
			throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buff = new byte[1024];
		int len = 0;
		while ((len = inputStream.read(buff)) != -1) {
			outputStream.write(buff, 0, len);
		}
		inputStream.close();
		outputStream.close();
		return outputStream.toByteArray();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
		}
		return false;
	}

}
