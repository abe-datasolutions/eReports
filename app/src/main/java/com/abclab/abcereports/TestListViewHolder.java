package com.abclab.abcereports;

import android.view.View;
import android.widget.TextView;

import com.abclab.abcereports.ExternalDB.DatabaseAccess;

public class TestListViewHolder {
	private TextView rowTestName;
	TestListViewHolder(View row){
		rowTestName = (TextView) row.findViewById(R.id.rowTestListName);
	}
	void Populate(DBTestInfo.TestListData data){
		rowTestName.setText(data.Name);
	}
	void Populate(DatabaseAccess.TestListData data){
		rowTestName.setText(data.Name);
	}
}
