package org.celstec.arlearn2.android.whitelabel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import authentication.LoginActivity;
import daoBase.DaoConfiguration;
import org.celstec.arlearn.delegators.QuestionDelegator;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.events.SplashScreenLoaded;
import org.celstec.arlearn2.android.game.DelayedGameLauncher;
import org.celstec.arlearn2.android.game.InitWhiteLabelDatabase;
import org.celstec.arlearn2.android.game.MyGamesActivity;
import org.celstec.arlearn2.android.game.messageViews.GameMessages;
import org.celstec.arlearn2.android.game.messageViews.MessageViewLauncher;
import org.celstec.arlearn2.android.util.BitmapWorkerTask;
import org.celstec.arlearn2.android.util.ImageUtil;
import org.celstec.arlearn2.android.util.MediaFolders;
import org.celstec.arlearn2.beans.run.GeneralItemVisibility;
import org.celstec.dao.gen.*;

import java.io.File;
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
public class SplashScreen extends Activity {

    DelayedGameLauncher delayedGameLauncher;


    private boolean databaseInitReady = false;
    private Long gameIdToUseForMainSplashScreen;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_splash_screen);
        ARL.init(this);
//        DaoConfiguration.getInstance().getActionLocalObjectDao().deleteAll();
//        DaoConfiguration.getInstance().getGeneralItemVisibilityLocalObjectDao().deleteByKey("5832556599574528:5816404838187008");

//        long time = System.currentTimeMillis();
//        while(time + 3000> System.currentTimeMillis()) {
//
//        };
      gameIdToUseForMainSplashScreen = getGameIdToUseForMainSplashScreen();
        delayedGameLauncher = new DelayedGameLauncher(System.currentTimeMillis() + 3000) {

            @Override
            public void runNextActivity() {
                if (ARL.config.getBooleanProperty("show_info_screen")){
                    Intent infoscreenIntent = new Intent(SplashScreen.this, InfoScreen.class);
                    startActivity(infoscreenIntent);
                } else if (ARL.config.getBooleanProperty("white_label_login") && !ARL.accounts.isAuthenticated()) {
                    Intent loginIntent = new Intent(SplashScreen.this, LoginActivity.class);
                    startActivity(loginIntent);
                } else
                    if (isGroupOfGames()) {
                    Intent gameIntent = new Intent(SplashScreen.this, MyGamesActivity.getMyGamesActivityClass());
                    startActivity(gameIntent);
                } else {

                    long gameId = InitWhiteLabelDatabase.getGameIds().get(0);
                        new MessageViewLauncher(gameId).launchMessageView(SplashScreen.this);

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

    public void onEventAsync(SplashScreenLoaded event) {
        final String splashScreenPath = "/gameSplashScreen";
        if (event.getPath().equals(splashScreenPath)){
            runOnUiThread(new Runnable() {
                public void run() {
                    SplashScreen.setBackgroundImage(SplashScreen.this, splashScreenPath);
                }
            });
        }

    }

    static long getGameIdToUseForMainSplashScreen() {
        if (!ARL.config.containsKey("gameIdToUseForMainSplashScreen")) return 0l;
            Long gameIdToUseForMainSplashScreen = null;
        try {
            gameIdToUseForMainSplashScreen = Long.parseLong((String) ARL.config.get("gameIdToUseForMainSplashScreen_" + Locale.getDefault().getLanguage()));
        } catch (NumberFormatException e ) {}

        if (gameIdToUseForMainSplashScreen == null) gameIdToUseForMainSplashScreen = Long.parseLong((String) ARL.config.get("gameIdToUseForMainSplashScreen"));
        return  gameIdToUseForMainSplashScreen;
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
            if (setBackgroundImage(activity, "/gameSplashScreen")) {
                activity.findViewById(R.id.downloadStatus).setVisibility(View.GONE);
            }
        }
    }

    public static boolean setBackgroundImage(Activity ctx, String path) {
//        long gameIdToUseForMainSplashScreen = getGameIdToUseForMainSplashScreen();
//        Bitmap background = GameFileLocalObject.getBitmapFullScreen(ctx, gameIdToUseForMainSplashScreen, imageId);
//        if (android.os.Build.VERSION.SDK_INT >= 11)
//            if (background != null) {
//                ((ImageView) ctx.findViewById(R.id.main_backgroundImage)).setImageBitmap(background);
//
//                return true;
//            }
//        return false;
        long gameIdToUseForMainSplashScreen = getGameIdToUseForMainSplashScreen();
        String key = gameIdToUseForMainSplashScreen+path;
        final Bitmap bitmap = ARL.imageCache.getBitmapFromMemCache(key);
        ImageView imageView = ((ImageView) ctx.findViewById(R.id.main_backgroundImage));
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            Display display = ctx.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            GameFileLocalObject gameFileLocalObject =GameFileLocalObject.getGameFileLocalObject(gameIdToUseForMainSplashScreen, path);
            if (gameFileLocalObject != null){
                BitmapWorkerTask task = new BitmapWorkerTask(imageView, key, size.x, size.y);
                task.execute(gameFileLocalObject);
            }

        }

//        ImageUtil.setBackgroundImage(ctx, key, getGameIdToUseForMainSplashScreen(), R.id.main_backgroundImage);

        return false;
    }


    private boolean databaseNotLoaded() {
        return (DaoConfiguration.getInstance().getGameLocalObjectDao().loadAll().isEmpty());
    }

    private void initDatabase() {
        if (DaoConfiguration.getInstance().getGeneralItemLocalObjectDao().loadAll().isEmpty()
                ||
                DaoConfiguration.getInstance().getGameLocalObjectDao().load(InitWhiteLabelDatabase.getGameIds().get(0))== null) {
            ARL.eventBus.register(this);
            InitWhiteLabelDatabase initWhiteLabelDatabase = InitWhiteLabelDatabase.getWhiteLabelDatabaseIniter(this);
            initWhiteLabelDatabase.init();
//            new HtmlSplashScreenInitiationScript(this);

        } else {
            findViewById(R.id.downloadStatus).setVisibility(View.GONE);
         databaseInitReady = true;
        }
    }

    public void onEventMainThread(InitWhiteLabelDatabase.SyncReady ready) {
        if (ready.isSuccess()) {
            databaseInitReady = true; GameFileLocalObject gameFileLocalObject = GameFileLocalObject.getGameFileLocalObject(gameIdToUseForMainSplashScreen, "/map.zip");
            if (gameFileLocalObject!= null) {
                InitWhiteLabelDatabase.writeFileToOSM(this, gameFileLocalObject.getLocalFile().toString());
            }
            ARL.eventBus.unregister(this);
        } else {
            this.finish();
        }
    }

    static boolean isGroupOfGames() {
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
