package org.celstec.arlearn2.android.game;

import android.app.Activity;
import android.content.Context;
import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.delegators.GameDelegator;
import org.celstec.arlearn2.android.delegators.game.GameDownloadManager2;
import org.celstec.arlearn2.android.views.DownloadViewManager;
import org.celstec.dao.gen.GameFileLocalObject;

import java.util.List;

/**
 * Created by str on 12/03/15.
 */
public class InitWhiteLabelDatabaseOnlineSync extends InitWhiteLabelDatabase {
    public InitWhiteLabelDatabaseOnlineSync(Context context) {
        super(context);
    }

    @Override
    protected void loadGameScript() {
        for (Long gameIdLong:gameIdsLong) {
            ARL.games.asyncGame(gameIdLong, false);
        }
    }

    @Override
    protected void loadGameFiles() {

        List<GameFileLocalObject> gameFiles = DaoConfiguration.getInstance().getGameFileDao().loadAll();
        System.out.println("amount of files to reset is " + gameFiles.size());
        for (GameFileLocalObject gameFileLocalObject : gameFiles) {
            if (gameFileLocalObject.getSyncStatus() == GameFileLocalObject.FILE_IS_DOWNLOADING) {
                gameFileLocalObject.setSyncStatus(GameFileLocalObject.FILE_TO_DOWNLOAD);
                DaoConfiguration.getInstance().getGameFileDao().insertOrReplace(gameFileLocalObject);
            }
        }

        DownloadViewManager downloadViewManager = new DownloadViewManager(((Activity) context).findViewById(R.id.downloadStatus)) {
            public void onDismiss() {

            }

        };

        GameDownloadManager2 gameDownloadManager = new GameDownloadManager2(gameIdsLong);
        gameDownloadManager.setGameDownloadEventListener(downloadViewManager);
        gameDownloadManager.register();
        gameDownloadManager.syncDownloadGames();

    }
}
