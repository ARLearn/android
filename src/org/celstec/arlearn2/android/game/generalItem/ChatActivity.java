package org.celstec.arlearn2.android.game.generalItem;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.util.DrawableUtil;
import org.celstec.arlearn2.android.util.StyleUtilInterface;

/**
 * ****************************************************************************
 * Copyright (C) 2013 Open Universiteit Nederland
 * <p/>
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * Contributors: Stefaan Ternier
 * ****************************************************************************
 */
public class ChatActivity extends Activity {


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StyleUtilInterface styleUtil = DrawableUtil.styleUtil;
        setTheme(styleUtil.getTheme());
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            getActionBar().hide();
        }
        setContentView(R.layout.game_general_item_chat);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        GradientDrawable shapeDrawable = (GradientDrawable) ((findViewById(R.id.content)).getBackground());
//        shapeDrawable.setColor(styleUtil.getBackgroundDark());


//        ((WebView) findViewById(R.id.chatWebView)).loadUrl("http://www.google.com");

//        ((GradientDrawable) findViewById(R.id.button).getBackground()).setColor(styleUtil.getPrimaryColor());

        WebView mWebview  = new WebView(this);

        mWebview.getSettings().setJavaScriptEnabled(true); // enable javascript

        final Activity activity = this;

//        mWebview.setWebViewClient(new WebViewClient() {
//            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
//            }
//
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
//                return false;
//            }
//
//            @Override
//            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                handler.proceed(); // Ignore SSL certificate errors
//            }
//        });
//        mWebview.clearSslPreferences();
        mWebview.setWebChromeClient(new WebChromeClient());

//        webView.setWebViewClient(new WebViewClient() {
//            @Override
//
//        });

        System.out.println("https://streetlearn.appspot.com/#/run/"+ getIntent().getLongExtra("runId", 0l)+"/chat/"+getIntent().getStringExtra("threadName")+"/"+ARL.properties.getAuthToken());
        mWebview.loadUrl("https://streetlearn.appspot.com/#/run/"+ getIntent().getLongExtra("runId", 0l)+"/chat/"+getIntent().getStringExtra("threadName")+"/"+ARL.properties.getAuthToken());
        setContentView(mWebview );
    }

}