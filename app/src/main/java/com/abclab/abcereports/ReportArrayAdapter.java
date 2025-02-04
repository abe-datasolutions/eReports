package com.abclab.abcereports;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

class ReportArrayAdapter extends ArrayAdapter<ResultData>{

	private GlobalClass gc = null;
	Context curContext;
	public ReportArrayAdapter(Context context, ArrayList<ResultData> data) {
		super(context, android.R.layout.simple_list_item_1,data);
		curContext=context;
		gc = ((GlobalClass) curContext.getApplicationContext());
	}

	@SuppressLint("InflateParams") @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ReportViewHolder holder;
		
		if (convertView == null){
			LayoutInflater inflater =(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.report_row, null);
			holder = new ReportViewHolder(convertView,gc.getDateFormat());
			//Toast.makeText(curContext, "SETTINGS - " + gc.getSharedString(GlobalClass.PREF_COUNTRY) + " - " + gc.getDateFormat(), Toast.LENGTH_LONG).show();
			convertView.setTag(holder);
		} else {
			holder = (ReportViewHolder) convertView.getTag();
		}

		ResultData data = getItem(position);
		holder.Populate(data);
		return convertView;
	}		
}