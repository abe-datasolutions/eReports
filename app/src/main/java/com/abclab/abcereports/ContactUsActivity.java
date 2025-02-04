package com.abclab.abcereports;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ContactUsActivity extends AppCompatActivity {
	private WebView wv;
	private GlobalClass gc = null;
	ImageView imgLogo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		gc = ((GlobalClass) this.getApplicationContext());
		setContentView(R.layout.contact_us);
		wv = (WebView) findViewById(R.id.contactUsView);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setLogo(R.mipmap.ic_launcher_foreground);
		getSupportActionBar().setDisplayUseLogoEnabled(true);
		getSupportActionBar().setTitle(getTitle());
        imgLogo = (ImageView) findViewById(R.id.contactLogo);
        if (gc.getBranchId() == 2) {
        	imgLogo.setImageResource(R.drawable.logo_jkt);
        }
		
		wv.loadData(gc.getContact(), "text/html", "utf-8");
	}
}
