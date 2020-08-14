package com.example.apppermission;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.textclassifier.TextLinks;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.apppermission.databutton.DataActivity;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        /*TextView paragraph = (TextView) findViewById(R.id.about_paragraph);
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append("AppManager app and ");
        ssb.setSpan(new ImageSpan(this, R.drawable.ic_home_header_logo),
                ssb.length()-1,
                ssb.length(),
                0);
        ssb.append("Logo are the trademarks of SoftClouds LLC All rights reserved");
        paragraph.setText(ssb);*/

        WebView webView = findViewById(R.id.softcloud_links);
        webView.loadUrl("file:///android_asset/ABOUT_US.html");


        ImageView backArrow = (ImageView) findViewById(R.id.about_back_arrow);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}