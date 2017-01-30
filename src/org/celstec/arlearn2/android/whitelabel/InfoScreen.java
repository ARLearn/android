package org.celstec.arlearn2.android.whitelabel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import authentication.LoginActivity;
import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.game.DelayedGameLauncher;
import org.celstec.arlearn2.android.game.InitWhiteLabelDatabase;
import org.celstec.arlearn2.android.game.MyGamesActivity;
import org.celstec.arlearn2.android.game.messageViews.GameActivityFeatures;
import org.celstec.arlearn2.android.game.messageViews.GameMessages;
import org.celstec.arlearn2.android.game.messageViews.MessageViewLauncher;
import org.celstec.arlearn2.android.util.DrawableUtil;
import org.celstec.dao.gen.*;

import java.util.Locale;

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
public class InfoScreen extends Activity {



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARL.init(this);
        GameLocalObject gameLocalObject = DaoConfiguration.getInstance().getGameLocalObjectDao().load(InitWhiteLabelDatabase.getGameIds().get(0));
        setTheme(GameActivityFeatures.getTheme(gameLocalObject.getGameBean()));
        setContentView(R.layout.game_info_screen);
        ARL.getDrawableUtil(SplashScreen.getGameIdToUseForMainSplashScreen(), this);
        ((GradientDrawable)findViewById(R.id.button).getBackground()).setColor(DrawableUtil.styleUtil.getPrimaryColor());

        (findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ARL.config.getBooleanProperty("show_anonymous_registration")) {
                    if (ARL.accounts.getLoggedInAccount() == null) {
                        Intent anonymousRegistrationScreen = new Intent(InfoScreen.this, AnonymousRegistrationScreen.class);
                        startActivity(anonymousRegistrationScreen);
                    } else {
                        if (ARL.config.getBooleanProperty("white_label_login") && !ARL.accounts.isAuthenticated()) {
                            Intent loginIntent = new Intent(InfoScreen.this, LoginActivity.class);
                            startActivity(loginIntent);
                        } else if (SplashScreen.isGroupOfGames()) {
                            Intent gameIntent = new Intent(InfoScreen.this, MyGamesActivity.getMyGamesActivityClass());
                            startActivity(gameIntent);
                        } else {

                            long gameId = InitWhiteLabelDatabase.getGameIds().get(0);
                            new MessageViewLauncher(gameId).launchMessageView(InfoScreen.this);

                        }

                    }
                }
                InfoScreen.this.finish();
            }
        });

        String prefix = ARL.config.getProperty("message_html_prefix");
        String postfix = ARL.config.getProperty("message_html_postfix");
        if (prefix == null){
            prefix = "";
        }
        if (postfix == null){
            postfix = "";
        }

        WebView webView =
                ((WebView) findViewById(R.id.infoScreenWebView));
        webView.setBackgroundColor(0x00000000);
        webView.loadData(prefix+gameLocalObject.getDescription()+postfix, "text/html", "utf8");

        ((TextView)findViewById(R.id.title)).setText(gameLocalObject.getTitle());

        SplashScreen.setBackgroundImage(this, "/background");

//        switch (getResources().getDisplayMetrics().densityDpi) {
//            case DisplayMetrics.DENSITY_LOW:
//                System.out.println("Low density");
//                break;
//            case DisplayMetrics.DENSITY_MEDIUM:
//                System.out.println("DENSITY_MEDIUM");
//                break;
//            case DisplayMetrics.DENSITY_HIGH:
//                System.out.println("DENSITY_HIGH");
//                break;
//            case DisplayMetrics.DENSITY_XHIGH:
//                System.out.println("DENSITY_XHIGH");
//                break;
//        }

    }

}
