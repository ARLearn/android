package authentication;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.actionbarsherlock.app.SherlockFragment;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;

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
public abstract class WebViewFragment extends SherlockFragment {
    private WebView webView;
    @Override
    public void onResume() {
        super.onResume();
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            getActivity().getActionBar().setIcon(R.drawable.ic_ab_back);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v =  inflater.inflate(R.layout.login_webview, container, false);
        webView = (WebView) v.findViewById(R.id.loginWebview);
        WebSettings web_sett=webView.getSettings();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebClient());

        webView.loadUrl(getUrl());
        return v;

    }

    protected abstract String getUrl();

    private class MyWebClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            webView.loadUrl(url);
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            System.out.println("URL "+url);
            if (url.contains("#/oauth/")) {
                String token = url.substring(url.indexOf("oauth/")+6);
                token = token.substring(0, token.indexOf("/"));
                ARL.properties.setAuthToken(token);
                ARL.accounts.syncMyAccountDetails();
                getActivity().getSupportFragmentManager().popBackStack();
                getActivity().getSupportFragmentManager().popBackStack();
//            } else
//            if (url.contains("oauth.html?accessToken=")) {
//                String token = url.substring(url.indexOf("?")+1);
//                token = token.substring(token.indexOf("=")+1, token.indexOf("&"));
//                ARL.properties.setAuthToken(token);
//                ARL.accounts.syncMyAccountDetails();
//                getActivity().getSupportFragmentManager().popBackStack();
//                getActivity().getSupportFragmentManager().popBackStack();
            } else  if (url.endsWith("oauth.html") || url.contains("twitter?denied")) {
                getActivity().getSupportFragmentManager().popBackStack();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        }

    }
}
