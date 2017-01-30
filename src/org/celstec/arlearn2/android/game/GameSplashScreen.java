package org.celstec.arlearn2.android.game;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.delegators.game.GameDownloadManager;
import org.celstec.arlearn2.android.events.SplashScreenLoaded;
import org.celstec.arlearn2.android.game.messageViews.GameMessages;
import org.celstec.arlearn2.android.util.ImageUtil;
import org.celstec.arlearn2.android.views.DownloadViewManager;
import org.celstec.arlearn2.android.whitelabel.SplashScreen;
import org.celstec.dao.gen.GameFileLocalObject;
import org.celstec.dao.gen.GameLocalObject;
import org.celstec.dao.gen.RunLocalObject;

import java.util.HashMap;

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
public class GameSplashScreen extends Activity {

    GameLocalObject gameLocalObject;
    RunLocalObject runLocalObject;
    private GameDownloadManager gameDownloadManager;
    private DownloadViewManager downloadViewManager;

    DelayedGameLauncher delayedGameLauncher;
    boolean offline = false;

    private static HashMap<Long, Boolean> dataSynced = new HashMap<Long, Boolean>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_splash_screen);
        ARL.init(this);

        final Long gameId = getIntent().getLongExtra(GameLocalObject.class.getName(), 0l);
        Boolean sync = getIntent().getBooleanExtra("syncContent", true);
        if (sync && (!dataSynced.containsKey(gameId))) {
            downloadViewManager = new DownloadViewManager(findViewById(R.id.downloadStatus)) {
                @Override
                public void onDismiss() {
                    super.onDismiss();
                    dataSynced.put(gameId, true);
                }
            };
        } else {
            findViewById(R.id.downloadStatus).setVisibility(View.GONE);
            dataSynced.put(gameId, true);
        }

        GameFileLocalObject background = GameFileLocalObject.getGameFileLocalObject(gameId, "/gameSplashScreen");
        if (background != null) {
            ImageUtil.setBackgroundImage(this, background.getPath(), gameId, R.id.main_backgroundImage);
        } else {
            int gameSplashScreenDrawable = R.drawable.game_splash_screen;
            ImageUtil.setBackgroundImage(this, gameSplashScreenDrawable ,gameId,R.id.main_backgroundImage);
        }




        delayedGameLauncher = new DelayedGameLauncher(System.currentTimeMillis() + 3000) {

            @Override
            public void runNextActivity() {
                launchGame();
            }

            @Override
            public boolean additionalCondition() {
                return offline || (dataSynced.containsKey(gameId) && dataSynced.get(gameId));
            }
        };
    }

    private void launchGame() {
        Intent gameIntent = new Intent(GameSplashScreen.this, GameMessages.class);
        gameIntent.putExtra(GameLocalObject.class.getName(), gameLocalObject.getId());
        gameIntent.putExtra(RunLocalObject.class.getName(), runLocalObject.getId());
        ARL.actions.createAction(runLocalObject.getId(), "startRun");
        ARL.actions.syncActions(runLocalObject.getId());
        ARL.responses.syncResponses(runLocalObject.getId());
        GameSplashScreen.this.startActivity(gameIntent);
        GameSplashScreen.this.finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        ARL.eventBus.register(this);
        Long gameId = getIntent().getLongExtra(GameLocalObject.class.getName(), 0l);
        Long runId = getIntent().getLongExtra(RunLocalObject.class.getName(), 0l);
        gameLocalObject = DaoConfiguration.getInstance().getGameLocalObjectDao().load(gameId);
        runLocalObject = DaoConfiguration.getInstance().getRunLocalObjectDao().load(runId);
        SplashScreen.setSplashScreen(this, gameId);
        ARL.generalItemVisibility.calculateVisibility(runId, gameId);
        onlineTest();
        if (downloadViewManager!= null) {
            gameDownloadManager.register();
            ARL.eventBus.post(gameDownloadManager);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gameDownloadManager != null) gameDownloadManager.unregister();
        ARL.eventBus.unregister(this);
    }

    public void onEventAsync(SplashScreenLoaded event) {
        ImageUtil.setBackgroundImage(this, "/gameSplashScreen",gameLocalObject.getId(),R.id.main_backgroundImage);
    }


//    private void startGeneralItemListActivity() {
//
//    }

    private void onlineTest() {

        gameDownloadManager = new GameDownloadManager(gameLocalObject.getId());
        gameDownloadManager.setDownloadEventListener(downloadViewManager);
        if (ARL.isOnline()) {
            offline = false;
            ARL.eventBus.post(new NetworkTest());
        } else {

            notOnline();
        }

    }

    public void onEventAsync(NetworkTest networkTest) {
        networkTest.executeTest();
    }

    public void onEventMainThread(NetworkTest.NetworkResult result) {
        if (result.isResult()) {
            offline = false;
            if (downloadViewManager!= null) syncGameContent();
        } else {
            notOnline();
        }
    }

    private void syncGameContent() {
        downloadViewManager.setVisible();
        ARL.eventBus.post(gameDownloadManager);
        ARL.generalItemVisibility.syncGeneralItemVisibilities(runLocalObject);
    }

    private void notOnline() {
        offline = true;
        if (DaoConfiguration.getInstance().getGameLocalObjectDao().load(gameLocalObject.getId()).getLastSyncGeneralItemsDate() == null) {
            displayAlert(R.string.noInternet, R.string.operatingOffline);
            return;
        } else if (!gameDownloadManager.contentIsDownloaded()) {
            displayAlert(R.string.contentMissingNoInternet, R.string.operatingOffline);
        }
//        else {
//            startGeneralItemListActivity();
//        }
    }

    private void displayAlert(int messageId, int cancelMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(messageId)
                .setPositiveButton(R.string.wireless, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.setClassName("com.android.settings", "com.android.settings.wifi.WifiSettings");
                        startActivity(intent);
                        dialog.cancel();
                        GameSplashScreen.this.finish();

                    }
                })
                .setNegativeButton(cancelMessage, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        launchGame();
//                        GameSplashScreen.this.finish();
//                        startGeneralItemListActivity();
//                        dataSynced.put(gameId, true);
                    }
                });
        builder.create();
        builder.show();
    }



    public static void startActivity(Context ctx, long gameId, long runId) {
        startActivity(ctx,gameId,runId,true);
    }

    public static void startActivity(Context ctx, long gameId, long runId, boolean sync) {
        Intent gameIntent = new Intent(ctx, GameSplashScreen.class);
        gameIntent.putExtra(GameLocalObject.class.getName(), gameId);
        gameIntent.putExtra(RunLocalObject.class.getName(), runId);
        gameIntent.putExtra("syncContent", sync);
        ctx.startActivity(gameIntent);
    }

}
