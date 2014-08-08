package cn.nju.zouxuan.forum.list;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cooliris.media.R;

public class MyAdapter extends BaseAdapter {

	private static final String TAG = "MyAdapter";
	private boolean mBusy = false;

	public void setFlagBusy(boolean busy) {
		this.mBusy = busy;
	}

	private ImageLoader mImageLoader;
	private int mCount;
	private Context mContext;
	String[] URLS;
	String[] namelist;
	String[] timelist;
	String[] datalist;
	

	public MyAdapter(String[] URLS,String[] namelist,String[] timelist,String[] datalist, int count, Context context) {
		this.URLS = URLS;
		this.namelist=namelist;
		this.timelist=timelist;
		this.datalist=datalist;
		this.mCount = count;
		this.mContext = context;
		mImageLoader = new ImageLoader();
	}

	@Override
	public int getCount() {
		return mCount;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d(TAG, "position=" + position + ",convertView=" + convertView);
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.list_item, null);// 这个过程相当耗时间
			viewHolder = new ViewHolder();
			viewHolder.mTextView = (TextView) convertView
					.findViewById(R.id.textView1);
			viewHolder.nameView=(TextView)convertView.findViewById(R.id.textview_name);
			viewHolder.timeView=(TextView)convertView.findViewById(R.id.textView_time);
			viewHolder.mImageView = (ImageView) convertView
					.findViewById(R.id.imageView1);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String url = "";
		url = URLS[position % URLS.length];
		if (!mBusy) {
			mImageLoader.loadImage(url, this, viewHolder);
			viewHolder.mTextView.setText(datalist[position]);
			viewHolder.nameView.setText(namelist[position]);
			viewHolder.timeView.setText(timelist[position]);
		} else {
			Bitmap bitmap = mImageLoader.getBitmapFromCache(url);
			if (bitmap != null) {
				viewHolder.mImageView.setImageBitmap(bitmap);
			} else {
				viewHolder.mImageView.setImageResource(R.drawable.icon);
			}
			viewHolder.mTextView.setText(datalist[position]);
			viewHolder.nameView.setText(namelist[position]);
			viewHolder.timeView.setText(timelist[position]);

		}
		return convertView;
	}
}
