package cn.nju.zouxuan.forum.detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cooliris.media.R;

public class DetailAdapter extends BaseAdapter {

	private String[] namelist;
	private String[] timelist;
	private String[] datalist;
	private int count;
	private LayoutInflater mInflater;

	public DetailAdapter(String[] namelist, String[] timelist,
			String[] datalist,int length, Context context) {
		this.namelist = namelist;
		this.timelist = timelist;
		this.datalist = datalist;
		if (namelist == null) {
			count = 0;
		} else {
			count = length;
		}
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return count;
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertview, ViewGroup parent) {
		DetailHolder detailHolder = null;
		if (convertview == null) {
			detailHolder = new DetailHolder();
			convertview = mInflater.inflate(R.layout.detail_item, null);
			detailHolder.nameView = (TextView) convertview
					.findViewById(R.id.textView1);
			detailHolder.timeView = (TextView) convertview
					.findViewById(R.id.textView2);
			detailHolder.dataView = (TextView) convertview
					.findViewById(R.id.textView3);
			convertview.setTag(detailHolder);
			detailHolder.nameView.setText(namelist[position]);
			detailHolder.timeView.setText(timelist[position]);
			detailHolder.dataView.setText(datalist[position]);
		} else {
			detailHolder = (DetailHolder) convertview.getTag();
			detailHolder.nameView.setText(namelist[position]);
			detailHolder.timeView.setText(timelist[position]);
			detailHolder.dataView.setText(datalist[position]);

		}
		return convertview;
	}
}
