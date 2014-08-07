package cn.nju.zouxuan.forum.list;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import cn.nju.zouxuan.forum.detail.DetailActivity;
import cn.nju.zouxuan.forum.post.PostActivity;
import cn.nju.zouxuan.start.StartActivity;
import cn.nju.zouxuan.util.Client;
import cn.nju.zouxuan.util.SysApplication;

import com.cooliris.media.R;

public class ListActivity extends Activity {

	protected static final String TAG = "ListViewPerformaceActivity";
	private ListView mListview;
	ImageLoader mImageLoader = new ImageLoader();
	MyAdapter adapter;
	private JSONArray jArray;
	private String result = null;
	private InputStream is = null;
	private StringBuilder sb = null;
	private JSONObject json_data = null;
	private String[] datalist;
	private String[] imagelist;
	private String[] namelist;
	private String[] timelist;
	private Button post;
	private TextView name;
	private int count;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filelist);
		SysApplication.getInstance().addActivity(this);
		Intent intent = getIntent();
		Log.i("on", "inActivity");
		post = (Button) findViewById(R.id.button1);
		name = (TextView) findViewById(R.id.textView1);
		name.setText(Client.name);
		post.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(ListActivity.this,
						PostActivity.class);
				startActivity(intent);
			}
		});

		new Thread() {

			public void run() {
				// TODO http get
				try {
					HttpClient httpclient = Client.getHttpClient();
					HttpGet httpget = new HttpGet(
							"http://1.zouxuan2.sinaapp.com/filelist.php");// IP为自己的服务器的IP因为手机连接局域网的WIFI，所以只用自己的电脑作为服务器也可以测试效果。
					HttpResponse response = httpclient.execute(httpget);
					HttpEntity entity = response.getEntity();
					is = entity.getContent();
				} catch (Exception e) {
					Log.e("log_tag",
							"2013Error in http connection" + e.toString());
				}
				// convert response to string
				try {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(is, "iso-8859-1"), 8);
					sb = new StringBuilder();
					sb.append(reader.readLine() + "\n");

					String line = "0";
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
					is.close();
					result = sb.toString();
				} catch (Exception e) {
					e.printStackTrace();
					Log.e("log_tag", "Error converting result " + e.toString());
				}

				try {
					jArray = new JSONArray(result);
					Log.i("on", "----->>>" + jArray.length());
					int length = jArray.length();
					namelist = new String[length];
					timelist = new String[length];
					datalist = new String[length];
					imagelist = new String[length];
					count = length;
					for (int i = 0; i < jArray.length(); i++) { // 把读取到数据库表的数据通过for循环添加到数组里
						json_data = jArray.getJSONObject(i);
						namelist[i] = json_data.getString("name");
						timelist[i] = json_data.getString("time");
						datalist[i] = json_data.getString("data");
						imagelist[i] = json_data.getString("imagepath");
					}

				} catch (JSONException e1) {
					e1.printStackTrace();
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
				handler.sendEmptyMessage(0);
			}
		}.start();


	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			setupViews();

		}

	};

	private void setupViews() {
		mListview = (ListView) findViewById(R.id.listView1);
		adapter = new MyAdapter(imagelist, namelist, timelist, datalist, count,
				this);// 这里把从数据库读取出来的图片URL传给Adapter
		mListview.setAdapter(adapter);
		mListview.setOnScrollListener(mScrollListener);
		// 添加ListView中Item的点击事件，针对整个Item，如果Item布局不同的组件，对不同的组件添加不同的事件，刚要另外处理，我实现了这个功能，这里就不多说，可以自己再找资料研究。
		mListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String name = namelist[arg2];
				String time = timelist[arg2];
				String data = datalist[arg2];
				String image = imagelist[arg2];
				Intent intent = new Intent(ListActivity.this,
						DetailActivity.class);
				intent.putExtra("name", name);
				intent.putExtra("time", time);
				intent.putExtra("data", data);
				intent.putExtra("image", image);
				startActivity(intent);
			}
		});
	}

	OnScrollListener mScrollListener = new OnScrollListener() {// 加载图片的缓存处理

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_FLING:
				adapter.setFlagBusy(true);
				break;
			case OnScrollListener.SCROLL_STATE_IDLE:
				adapter.setFlagBusy(false);
				break;
			case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				adapter.setFlagBusy(false);
				break;
			default:
				break;
			}
			adapter.notifyDataSetChanged();
		}

		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

		}
	};

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// 返回到主界面
			Intent intent = new Intent(ListActivity.this, StartActivity.class);
			startActivity(intent);
			return false;
		}
		return false;
	}

}
