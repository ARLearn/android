package org.celstec.arlearn2.android.delegators.game;

import com.google.android.gms.games.Game;
import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.delegators.GameDelegator;
import org.celstec.arlearn2.android.events.FileDownloadStatus;
import org.celstec.arlearn2.android.events.GeneralItemEvent;
import org.celstec.arlearn2.beans.game.GameFile;
import org.celstec.arlearn2.beans.game.GameFileList;
import org.celstec.arlearn2.beans.generalItem.GeneralItem;
import org.celstec.arlearn2.beans.generalItem.GeneralItemList;
import org.celstec.arlearn2.client.GameClient;
import org.celstec.dao.gen.GameFileLocalObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by str on 10/03/15.
 */
public class GameDownloadManager2 {

    private long[] gameIds;
    private int amountOfMessages=0;
    ArrayList<Long> storedMessages = new ArrayList<Long>();
    HashMap<Long,GeneralItemList> generalItemListMap = new HashMap<Long,GeneralItemList> ();
    HashMap<Long, List<GameFile>> gameFilesMap = new HashMap<Long, List<GameFile>>();
    private GameDownloadEventInterface gameDownloadEventListener;

    public GameDownloadManager2(long gameId){
        gameIds = new long[1];
        gameIds[0] = gameId;
    }

    public GameDownloadManager2(long[] gameIds){
        gameIds = gameIds;
    }

    public void setGameDownloadEventListener(GameDownloadEventInterface gameDownloadEventListener) {
        this.gameDownloadEventListener = gameDownloadEventListener;
    }

    public GameDownloadManager2(List<Long> gameIdsLong) {
        gameIds = new long[gameIdsLong.size()];
        for (int i = 0;i<gameIdsLong.size(); i++) {
            gameIds[i] = gameIdsLong.get(i);
        }
    }

    public void register() {
        ARL.eventBus.register(this);
    }

    public void unregister() {
        ARL.eventBus.unregister(this);
    }

    public void downloadGames(){
        ARL.eventBus.post(new DownloadGamesEvent());
    }

    public void onEventAsync(DownloadGamesEvent g) {
        syncDownloadGames();
    }

    public void syncDownloadGames() {
        for (long gameId: gameIds) {
            downloadMessages(gameId);
            retrieveGameFilesMetadata(gameId);
        }
        gameDownloadEventListener.setAmountOfMessages(amountOfMessages);
        if (gameIds.length == generalItemListMap.keySet().size())
            gameDownloadEventListener.setAmountOfMessages(getTotalAmountOfMessages());
        if (gameIds.length == gameFilesMap.keySet().size())
            gameDownloadEventListener.setAmountOfContentInBytes(getTotalAmountOfBytes());
        downloadFiles();
        if (!ARL.games.getUnSyncedFiles().isEmpty()) {
            ARL.eventBus.post(new Dismiss(false));
        }
        storeMessagesInDatabase();
        ARL.eventBus.post(new Dismiss(true));
    }




    private void downloadMessages(long gameId) {
        GeneralItemList list = ARL.generalItems.asyncRetrieveItems(gameId);

        if (list != null && list.getError() == null) {
            generalItemListMap.put(gameId, list);
            amountOfMessages +=generalItemListMap.size();
        }
    }

    private void retrieveGameFilesMetadata(long gameId) {

        GameFileList list = ARL.games.asyncRetrieveGameFiles(gameId);//GameClient.getGameClient().getGameFileList(null, gameId);
        if (list != null && list.getError() == null) {
            gameFilesMap.put(gameId, list.getGameFiles());
        }
    }

    public int getTotalAmountOfMessages(){
        int total = 0;
        for (GeneralItemList list: generalItemListMap.values()) {
            total += list.getGeneralItems().size();
        }
        return  total;
    }

    public long getTotalAmountOfBytes(){
        long total = 0;
        for (List<GameFile> list: gameFilesMap.values()) {
            for (GameFile gameFile: list) {
                total += gameFile.getSize();
            }
        }
        return  total;
    }

    private void downloadFiles() {
        for (long gameId: gameIds) {
            System.out.println("download messages for gameid "+gameId);
            ARL.games.asyncDownloadGameContent(gameId);
        }
    }

    private void storeMessagesInDatabase() {
        for(Long gameId: generalItemListMap.keySet()){
            GeneralItemList list= generalItemListMap.get(gameId);
            ARL.generalItems.storeItemsInDatabase(list, gameId);
        }
    }


    public class DownloadGamesEvent{}

    public void onEventMainThread(FileDownloadStatus fds) {
        if (gameDownloadEventListener != null) {
            gameDownloadEventListener.addContentDownloadedInBytes(fds.bytesDownloaded);
        }
    }

    public void onEventMainThread(GeneralItemEvent generalItemEvent) {
        storedMessages.add(generalItemEvent.getGeneralItemId());
        gameDownloadEventListener.setAmountOfMessages(storedMessages.size());

    }

    public void onEventMainThread(Dismiss dismiss) {
        gameDownloadEventListener.onDismiss();
    }

    public class Dismiss {
        private boolean successful;

        public Dismiss(boolean successful) {
            this.successful = successful;
        }

        public boolean isSuccessful() {
            return successful;
        }

        public void setSuccessful(boolean successful) {
            this.successful = successful;
        }
    }
}
