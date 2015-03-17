package org.celstec.arlearn2.android.whitelabel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import daoBase.DaoConfiguration;
import org.celstec.arlearn.delegators.QuestionDelegator;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.game.DelayedGameLauncher;
import org.celstec.arlearn2.android.game.InitWhiteLabelDatabase;
import org.celstec.arlearn2.android.game.MyGamesActivity;
import org.celstec.arlearn2.android.game.messageViews.GameMessages;
import org.celstec.arlearn2.android.util.MediaFolders;
import org.celstec.dao.gen.DaoMaster;
import org.celstec.dao.gen.GameFileLocalObject;
import org.celstec.dao.gen.GameLocalObject;
import org.celstec.dao.gen.RunLocalObject;

import java.io.File;

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
public class SplashScreen extends Activity {

    DelayedGameLauncher delayedGameLauncher;


    private boolean databaseInitReady = false;
    private Long gameIdToUseForMainSplashScreen;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_splash_screen);
        ARL.init(this);
        gameIdToUseForMainSplashScreen = Long.parseLong((String) ARL.config.get("gameIdToUseForMainSplashScreen"));
        delayedGameLauncher = new DelayedGameLauncher(System.currentTimeMillis() + 3000) {

            @Override
            public void runNextActivity() {
                if (isGroupOfGames()) {
                    Intent gameIntent = new Intent(SplashScreen.this, MyGamesActivity.class);
                    startActivity(gameIntent);
                } else {

                    long gameId = InitWhiteLabelDatabase.getGameIds().get(0);
                    GameLocalObject gameLocalObject = DaoConfiguration.getInstance().getGameLocalObjectDao().load(gameId);
                    Intent gameIntent = new Intent(SplashScreen.this, GameMessages.class);
                    gameIntent.putExtra(GameLocalObject.class.getName(), gameId);
                    gameIntent.putExtra(RunLocalObject.class.getName(), gameLocalObject.getRuns().get(0).getId());
                    startActivity(gameIntent);
                }
                SplashScreen.this.finish();
            }

            @Override
            public boolean additionalCondition() {
                return databaseInitReady;
            }
        };
        initDatabase();
        ARL.accounts.syncMyAccountDetails();

        setSplashScreen(this, gameIdToUseForMainSplashScreen);
    }

    public static void setSplashScreen(Activity activity, long gameIdToUseForMainSplashScreen) {
        WebView webView = (WebView) activity.findViewById(R.id.splashWebView);
//        System.out.println(MediaFolders.getIncommingFilesDir()+" - "+ gameIdToUseForMainSplashScreen+"/htmlSplash");
//        System.out.println(new File(MediaFolders.getIncommingFilesDir(), gameIdToUseForMainSplashScreen+"/htmlSplash").exists());
        if (new File(MediaFolders.getIncommingFilesDir(), gameIdToUseForMainSplashScreen+"/htmlSplash").exists()) {
            webView.setVisibility(View.VISIBLE);
            activity.findViewById(R.id.main_backgroundImage).setVisibility(View.GONE);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.loadUrl("file://" + new File(MediaFolders.getIncommingFilesDir(), "htmlSplash/index.html").getAbsolutePath());
//            webView.loadDataWithBaseURL("file:///android_res/raw/", richText, "text/html", "UTF-8", null);

        } else {
            webView.setVisibility(View.GONE);
            activity.findViewById(R.id.main_backgroundImage).setVisibility(View.VISIBLE);
            Drawable splashDrawable = GameFileLocalObject.getDrawable(activity, gameIdToUseForMainSplashScreen, "/gameSplashScreen");
            if (splashDrawable != null) {
                Uri uri =GameFileLocalObject.getGameFileLocalObject(gameIdToUseForMainSplashScreen,"/gameSplashScreen").getLocalUri();
                ((ImageView) activity.findViewById(R.id.main_backgroundImage)).setImageURI(uri);

                activity.findViewById(R.id.downloadStatus).setVisibility(View.GONE);
            }
        }
    }


    private boolean databaseNotLoaded() {
        return (DaoConfiguration.getInstance().getGameLocalObjectDao().loadAll().isEmpty());
    }

    private void initDatabase() {
        if (DaoConfiguration.getInstance().getGeneralItemLocalObjectDao().loadAll().isEmpty()) {
            System.out.println("db was empty ");
            ARL.eventBus.register(this);
            InitWhiteLabelDatabase initWhiteLabelDatabase = InitWhiteLabelDatabase.getWhiteLabelDatabaseIniter(this);
            initWhiteLabelDatabase.init();
            new HtmlSplashScreenInitiationScript(this);

        } else {
            System.out.println("db was not empty ");
            findViewById(R.id.downloadStatus).setVisibility(View.GONE);
         databaseInitReady = true;
        }
    }

    public void onEventMainThread(InitWhiteLabelDatabase.SyncReady ready) {
        System.out.println("splashcreen ready "+ready.isSuccess());
        if (ready.isSuccess()) {
            databaseInitReady = true;
            ARL.eventBus.unregister(this);
        } else {
            this.finish();
        }
    }

    private boolean isGroupOfGames() {
        return InitWhiteLabelDatabase.getGameIds().size() > 1;
    }

//    public void run() {
//        if (timeForLaunchNextScreen < System.currentTimeMillis() && databaseInitReady) {
//            launchNextActivity();
//        } else {
//            handler.postDelayed(this, 500);
//        }
//    }

//    private void launchNextActivity() {
//        if (isGroupOfGames()) {
//            Intent gameIntent = new Intent(this, MyGamesActivity.class);
//            startActivity(gameIntent);
//        } else {
//
//            long gameId = InitWhiteLabelDatabase.getGameIds().get(0);
//            GameLocalObject gameLocalObject = DaoConfiguration.getInstance().getGameLocalObjectDao().load(gameId);
//            Intent gameIntent = new Intent(this, GameMessages.class);
//            gameIntent.putExtra(GameLocalObject.class.getName(), gameId);
//            gameIntent.putExtra(RunLocalObject.class.getName(), gameLocalObject.getRuns().get(0).getId());
//            startActivity(gameIntent);
////
////            ARL.actions.createAction(runLocalObject.getId(), "startRun");
//        }
//        this.finish();
//    }


}
