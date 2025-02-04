package com.abclab.abcereports;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class CacheArrayAdapter extends ArrayAdapter<CacheData>{
	private GlobalClass gc = null;
	Context curContext;
	
	public CacheArrayAdapter(Context context, ArrayList<CacheData> data) {
		super(context, android.R.layout.simple_list_item_1,data);
		curContext=context;
		gc = ((GlobalClass) curContext.getApplicationContext());
	}

	@SuppressLint("InflateParams") @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		CacheViewHolder holder;
		
		if (convertView == null){
			LayoutInflater inflater =(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.cache_row, null);
			holder = new CacheViewHolder(convertView, gc.getDateTimeFormat());
			convertView.setTag(holder);
		} else {
			holder = (CacheViewHolder) convertView.getTag();
		}
		CacheData data = getItem(position);
		holder.Populate(data);
		return convertView;
	}		
}
