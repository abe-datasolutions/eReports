package com.abclab.abcereports;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.abclab.abcereports.ExternalDB.DatabaseAccess;


//public class TestListArrayAdapter extends ArrayAdapter<DBTestInfo.TestListData> {
public class TestListArrayAdapter extends ArrayAdapter<DatabaseAccess.TestListData> {
	Context curContext;
	
//	public TestListArrayAdapter(Context context, ArrayList<DBTestInfo.TestListData> data) {
//		super(context, android.R.layout.simple_list_item_1,data);
//		curContext=context;
//	}
	public TestListArrayAdapter(Context context, ArrayList<DatabaseAccess.TestListData> data) {
		super(context, android.R.layout.simple_list_item_1,data);
		curContext=context;
	}

	@SuppressLint("InflateParams") @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		TestListViewHolder holder;
		
		if (convertView == null){
			LayoutInflater inflater =(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.test_list_row, null);
			holder = new TestListViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (TestListViewHolder) convertView.getTag();
		}
//		DBTestInfo.TestListData data = getItem(position);
		DatabaseAccess.TestListData data = getItem(position);

		holder.Populate(data);
		return convertView;
	}		
}
