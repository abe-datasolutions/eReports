package com.abclab.abcereports;

import java.util.Date;

import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;

public class CacheViewHolder {
	private TextView rowFileName;
	private TextView rowCacheFileDate;
	private String df;
	CacheViewHolder(View row, String dateFormat){
		rowFileName = (TextView) row.findViewById(R.id.rowCacheFileName);
		rowCacheFileDate = (TextView) row.findViewById(R.id.rowCacheFileDate);
		df = dateFormat;
	}
	void Populate(CacheData data){
		rowFileName.setText(data.fileName);
		Date dm = new Date(data.fileDate);
		rowCacheFileDate.setText(DateFormat.format(df,dm));
	}
}
