package com.matburt.mobileorg;

import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class SimpleTextDisplay extends Activity
{
    private WebView orgDisplay;
    public static final String LT = "MobileOrg";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simpletext);
        this.orgDisplay = (WebView) this.findViewById(R.id.orgTxt);
        orgDisplay.setWebViewClient( new InternalWebViewClient() );
        orgDisplay.setWebChromeClient( new InternalWebChromeClient() );
        this.populateDisplay();
    }

    public void populateDisplay() {
        Intent txtIntent = getIntent();
        String srcText = txtIntent.getStringExtra("txtValue");
        srcText = convertToHtml( srcText );
        this.orgDisplay.loadData( srcText,  "text/html", "UTF-8" );
    }

	private String convertToHtml(String srcText) {
		int i1 = 0, i2 = 0;
		while ( true ) {
			i1 = srcText.indexOf("[[", i2+1);
			if ( i1 < 0 ) break;
			i2 = srcText.indexOf("]]", i1);
			if ( i2 < 0 ) break;
			int i3 = srcText.indexOf("][", i1);
			String linkUrl;
			String linkText;
			if ( i3 >= 0 && i3 < i2 ) {
				linkUrl = srcText.substring( i1+2, i3 );
				linkText = srcText.substring( i3+2, i2 );
			} else {
				linkUrl = srcText.substring( i1+2, i2 );
				linkText = linkUrl;
				
			}
			srcText = srcText.substring( 0, i1 ) +
				"<a href=\"" + linkUrl + "\">" + linkText + "</a>" +
				srcText.substring( i2+2 );
		}
		String result = "<html><body><pre>" + srcText.replaceAll("\\n", "<br/>\n") + "</pre></body></html>";
		return result;
	}
	
	
	private static class InternalWebViewClient extends WebViewClient {
	
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String urlStr) {
			try {
				Log.d(LT, urlStr);
				URL url = new URL( urlStr );
				if ( "file".equals( url.getProtocol()) ) {
					return true;
				}
			} catch (MalformedURLException e) {
				Log.e( LT, "Could not parse url " + urlStr, e );
				// ignore?
			}
			return false;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			Log.d(LT, description + ": " + failingUrl );
		}
		
	}
	
	
	private static class InternalWebChromeClient extends WebChromeClient {
		// TODO empty; unused right now...
	}
	
}