package org.celstec.arlearn2.android.delegators.game;

import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.events.FileDownloadStatus;
import org.celstec.arlearn2.android.events.GeneralItemEvent;
import org.celstec.arlearn2.beans.game.GameFileList;
import org.celstec.arlearn2.beans.generalItem.GeneralItem;
import org.celstec.arlearn2.beans.generalItem.GeneralItemList;
import org.celstec.dao.gen.GameFileLocalObject;

import java.util.List;

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
public class GameDownloadManager {

    private long gameId;

    List<GeneralItem> generalItemList;
    List<GameFileLocalObject> gameFiles;
    private GameDownloadEventInterface gameDownloadEventListener;

    public GameDownloadManager(long gameId) {
        this.gameId = gameId;
//        ARL.eventBus.register(this);
    }

    public void register() {
        ARL.eventBus.register(this);
    }

    public void unregister() {
        ARL.eventBus.unregister(this);
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public void onEventAsync(GeneralItemEvent sgi) {
        if (gameDownloadEventListener != null) gameDownloadEventListener.newMessage();
    }

    public void onEventAsync(FileDownloadStatus fds) {
        if (gameDownloadEventListener != null) {
            if (fds.getStatus() == FileDownloadStatus.FINISHED) {
                gameDownloadEventListener.addContentDownloadedInBytes(fds.bytesDownloaded);
            }

        }
    }

    public void asyncDownloadGame() {
        ARL.games.asyncGame(gameId,true);

        GeneralItemList list = ARL.generalItems.asyncRetrieveItems(gameId);
        if (list.getError() == null) {
            generalItemList = list.getGeneralItems();
            if (gameDownloadEventListener != null)
                gameDownloadEventListener.setAmountOfMessages(this.generalItemList.size());
        }
        ARL.generalItems.storeItemsInDatabase(list, gameId);

        GameFileList filesList = ARL.games.asyncRetrieveGameFiles(gameId);

        gameFiles = DaoConfiguration.getInstance().getGameLocalObjectDao().load(gameId).getGameFiles();
        gameDownloadEventListener.setAmountOfContentInBytes(getAmountOfFileBytesToDownload());
        gameDownloadEventListener.setAmountOfContentDownloadedInBytes(getAmountOfDownloadedFileBytes());

        ARL.games.asyncDownloadGameContent(gameId);
        ARL.eventBus.post(new Dismiss());

    }

    private void onEventMainThread(Dismiss dismiss) {
        gameDownloadEventListener.onDismiss();
    }

    public int getAmountOfGeneralItems() {
        return generalItemList.size();
    }

    public int getAmountOfSyncedGeneralItems() {
        return 50;
    }

    public int getAmountOfFilesToDownload() {
        return gameFiles.size();
    }

    public long getAmountOfFileBytesToDownload() {
        long result = 0l;
        for (GameFileLocalObject fileLocalObject : gameFiles) {
            result += fileLocalObject.getSize();
        }
        return result;
    }

    public long getAmountOfDownloadedFileBytes() {
        long result = 0l;
        for (GameFileLocalObject fileLocalObject : gameFiles) {
            if (fileLocalObject.getSyncStatus() == GameFileLocalObject.FILE_DOWNLOADED)
                result += fileLocalObject.getSize();
        }
        return result;
    }

    public void setDownloadEventListener(GameDownloadEventInterface gameDownloadEventInterface) {
        this.gameDownloadEventListener = gameDownloadEventInterface;
    }

    public boolean contentIsDownloaded() {
        gameFiles = DaoConfiguration.getInstance().getGameLocalObjectDao().load(gameId).getGameFiles();
        for (GameFileLocalObject gameFileLocalObject : gameFiles) {
            if (!gameFileLocalObject.getLocalFile().exists()) return false;
        }

//        for (GeneralItemLocalObject generalItemLocalObject :DaoConfiguration.getInstance().getGameLocalObjectDao().load(gameId).getGeneralItems()){
//            System.out.println(generalItemLocalObject);
//        }

        return true;
    }

    private class Dismiss {
    }
}
