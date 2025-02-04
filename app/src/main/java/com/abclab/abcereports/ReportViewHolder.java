package com.abclab.abcereports;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;

public class ReportViewHolder {
	public TextView rowReportNo;
	public TextView rowPatientName;
	public TextView rowReportDate;
	public TextView rowStatus;
	public TextView rowLoadMore;
	private View lastView;
	private String df;
	ReportViewHolder(View row, String dateFormat){
		rowReportNo = (TextView) row.findViewById(R.id.rowReportNo);
		rowPatientName = (TextView) row.findViewById(R.id.rowPatientName);
		rowReportDate = (TextView) row.findViewById(R.id.rowReportDate);
		rowStatus = (TextView) row.findViewById(R.id.rowStatus);
		rowLoadMore = (TextView) row.findViewById(R.id.rowLoadMore);
		lastView = row;
		df = dateFormat;
	}
	void Populate(ResultData data){
		rowReportNo.setText(data.reportNo);
		rowPatientName.setText(data.patientName);

		
		rowReportDate.setText(data.reportDate);
		SimpleDateFormat sf = (SimpleDateFormat) SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT,Locale.US);
		try {
			Date d = sf.parse(data.reportDate);
			rowReportDate.setText((data.status.equalsIgnoreCase("FINAL") ? "RPT Date: " : "SVC Date: ") + DateFormat.format(df,d));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		rowStatus.setText(data.status);
		int visible=View.VISIBLE;
		int showLoadMore=View.GONE;
		if (data.reportNo == "LAST") {
			visible = View.GONE;
			showLoadMore = View.VISIBLE;
			lastView.setEnabled(false);
			lastView.setClickable(false);
		}

		
		rowReportNo.setVisibility(visible);
		rowPatientName.setVisibility(visible);
		rowReportDate.setVisibility(visible);
		rowStatus.setVisibility(visible);
		rowLoadMore.setVisibility(showLoadMore);
	}
}
