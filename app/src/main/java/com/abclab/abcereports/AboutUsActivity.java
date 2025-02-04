package com.abclab.abcereports;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;

public class AboutUsActivity extends androidx.appcompat.app.AppCompatActivity {

	private WebView wv;
	private GlobalClass gc = null;
	ImageView imgLogo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		gc = ((GlobalClass) this.getApplicationContext());
		setContentView(R.layout.about_us);
		wv = (WebView) findViewById(R.id.aboutUsView);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setLogo(R.mipmap.ic_launcher_foreground);
		getSupportActionBar().setDisplayUseLogoEnabled(true);
		getSupportActionBar().setTitle(getTitle());
        imgLogo = (ImageView) findViewById(R.id.aboutUsLogo);
        if (gc.getBranchId() == 2) {
        	imgLogo.setImageResource(R.drawable.logo_jkt);
        }
		
		wv.loadData(gc.getAbout(), "text/html", "utf-8");
	}

}
