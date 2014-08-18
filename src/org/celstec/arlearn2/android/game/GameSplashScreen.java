package org.celstec.arlearn2.android.game;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.delegators.game.GameDownloadEventInterface;
import org.celstec.arlearn2.android.delegators.game.GameDownloadManager;
import org.celstec.arlearn2.android.game.messageViews.GameMessages;
import org.celstec.arlearn2.android.views.DownloadViewManager;
import org.celstec.dao.gen.GameLocalObject;
import org.celstec.dao.gen.RunLocalObject;

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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_splash_screen);
        if (!ARL.isInit()) ARL.init(this);
        downloadViewManager = new DownloadViewManager(findViewById(R.id.downloadStatus)){

            @Override
            public void onDismiss() {
                downloadViewManager.setGone();
                startGeneralItemListActivity();
            }
        };
    }

    private void startGeneralItemListActivity(){
        Intent gameIntent = new Intent(this, GameMessages.class);
        gameIntent.putExtra(GameLocalObject.class.getName(), gameLocalObject.getId());
        gameIntent.putExtra(RunLocalObject.class.getName(), runLocalObject.getId());
        ARL.actions.createAction(runLocalObject.getId(), "startRun");
        ARL.actions.uploadActions(runLocalObject.getId());
        ARL.actions.downloadActions(runLocalObject.getId());
        this.startActivity(gameIntent);
        this.finish();
    }

    private void whiteLabelMetadata() {
        Long gameId = Long.parseLong(ARL.config.getProperty("white_label_gameId"));
        Long runId = Long.parseLong(ARL.config.getProperty("white_label_runId"));
        gameLocalObject = DaoConfiguration.getInstance().getGameLocalObjectDao().load(gameId);
        runLocalObject = DaoConfiguration.getInstance().getRunLocalObjectDao().load(runId);
        if (ARL.config.getBooleanProperty("white_label_online")) {
            onlineTest();
        }
    }


    private void nativeArlearnMetadata(){
        Long gameId = getIntent().getLongExtra(GameLocalObject.class.getName(), 0l);
        Long runId = getIntent().getLongExtra(RunLocalObject.class.getName(), 0l);
        gameLocalObject = DaoConfiguration.getInstance().getGameLocalObjectDao().load(gameId);
        runLocalObject = DaoConfiguration.getInstance().getRunLocalObjectDao().load(runId);
        onlineTest();
    }

    private void onlineTest() {
        gameDownloadManager = new GameDownloadManager(gameLocalObject.getId());
        gameDownloadManager.setDownloadEventListener(downloadViewManager);

        if (ARL.isOnline()) {
            ARL.eventBus.post(new NetworkTest());
        } else {
            notOnline();
        }

    }

    private void onEventAsync(NetworkTest networkTest) {
        networkTest.executeTest();
    }

    private void onEventMainThread(NetworkTest.NetworkResult result) {
        if (result.isResult()) {
            syncGameContent();
        } else {
            notOnline();
        }
    }

    private void notOnline() {
        if (DaoConfiguration.getInstance().getGameLocalObjectDao().load(gameLocalObject.getId()).getLastSyncGeneralItemsDate() == null){
            displayAlert(R.string.noInternet);
            return;
        }
        if (!gameDownloadManager.contentIsDownloaded()) {
//            System.out.println("error message, switch on network to sync first");
            displayAlert(R.string.contentMissingNoInternet);
        }
    }

    private void displayAlert(int messageId) {
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
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        GameSplashScreen.this.finish();
                    }
                });
        builder.create();
        builder.show();
    }

    private void syncGameContent() {
        downloadViewManager.setVisible();
        ARL.eventBus.post(gameDownloadManager);
        ARL.generalItemVisibility.syncGeneralItemVisibilities(runLocalObject);

        //TODO
        //ARL.responses.syncResponses(runId);
        //ARL.actions.sync


    }



    @Override
    public void onResume() {
        super.onResume();

        ARL.eventBus.register(this);
        if (ARL.config.getBooleanProperty("white_label")) {
            whiteLabelMetadata();
        } else {
            nativeArlearnMetadata();
        }
//        new DelayedGameLauncher(gameLocalObject.getId(), runLocalObject.getId(), this, 2000);
        gameDownloadManager.register();
        //TODO nullpointerexception
//        Caused by: java.lang.NullPointerException
//        at org.celstec.arlearn2.android.game.GameSplashScreen.onResume(GameSplashScreen.java:86)
//        at android.app.Instrumentation.callActivityOnResume(Instrumentation.java:1185)
//        at android.app.Activity.performResume(Activity.java:5182)
//        at android.app.ActivityThread.performResumeActivity(ActivityThread.java:2732)
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameDownloadManager.unregister();
        ARL.eventBus.unregister(this);
    }


    public static void startActivity(Context ctx, long gameId, long runId){
        Intent gameIntent = new Intent(ctx, GameSplashScreen.class);
        gameIntent.putExtra(GameLocalObject.class.getName(), gameId);
        gameIntent.putExtra(RunLocalObject.class.getName(), runId);
        ctx.startActivity(gameIntent);
    }

}
