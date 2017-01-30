package org.celstec.arlearn2.android.delegators;

import android.util.Log;
import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.events.GeneralItemEvent;
import org.celstec.arlearn2.beans.generalItem.GeneralItem;
import org.celstec.arlearn2.beans.generalItem.GeneralItemList;
import org.celstec.arlearn2.client.GeneralItemClient;
import org.celstec.dao.gen.*;

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
public class GeneralItemDelegator extends AbstractDelegator{

    protected static GeneralItemDelegator instance;
//    private static HashMap<Long, Long> syncDates = new HashMap<Long, Long>();

    protected GeneralItemDelegator() {
        ARL.eventBus.register(this);
    }

    public static GeneralItemDelegator getInstance() {
        if (instance == null) {
            instance = new GeneralItemDelegator();
        }
        return instance;
    }

    /*
    Public API
     */

    public void syncGeneralItems(Long gameId) {
        syncGeneralItems(DaoConfiguration.getInstance().getGameLocalObjectDao().load(gameId));
    }

    public void syncGeneralItems(GameLocalObject game) {
        if (game == null) {
            Log.e(SYNC_TAG, "trying to sync game that does not exist ");
        } else {
            ARL.eventBus.post(new SyncGeneralItems(game));
        }
    }

    public void createGeneralItem(GeneralItem generalItem){
        ARL.eventBus.post(new CreateGeneralItem(generalItem));
    }

    /*
    Implementation
     */

    public void onEventAsync(CreateGeneralItem sgi) {
        String token = returnTokenIfOnline();
        if (token != null) {
            GeneralItem item = GeneralItemClient.getGeneralItemClient().postGeneralItem(token, sgi.getGi());
            onEventAsync(new SyncGeneralItems(DaoConfiguration.getInstance().getGameLocalObjectDao().load(item.getGameId())));
        }
    }

    public GeneralItemList asyncRetrieveItems(long gameId){
        String token = returnTokenIfOnline();
        if (token != null) {
            if ("".equals(token)) token = null;
            return GeneralItemClient.getGeneralItemClient().getGameGeneralItems(token, gameId, 0l);
        } else {
            System.out.println("token was null ");
        }
        return null;
    }

    public void storeItemsInDatabase(GeneralItemList list, long gameId){
            if (list.getError()== null) {
                process(list, DaoConfiguration.getInstance().getGameLocalObjectDao().load(gameId), true);
            } else {
                Log.e("ARLearn", "error returning list of gis" + list.getError());
            }
    }

    public void onEventAsync(SyncGeneralItems sgi) {
        String token = returnTokenIfOnline();
        if (token != null) {
            long gameId = sgi.getGame().getId();
//            Log.i(SYNC_TAG, "Syncing general items for "+sgi.getGame().getTitle()+" "+getLastSyncDate(gameId));
            Long lastSyncDate = sgi.getGame().getLastSyncGeneralItemsDate();
            if (lastSyncDate == null) lastSyncDate = 0l;
            GeneralItemList list = GeneralItemClient.getGeneralItemClient().getGameGeneralItems(token, gameId, lastSyncDate);
            if (list.getError()== null) {
                process(list, sgi.getGame(), false);
            } else {
                System.out.println("error "+list.getError());
                Log.e("ARLearn", "error returning list of gis"+list.getError());
            }
        } else {
            System.out.println("token was null ");
        }

    }

    public void process (GeneralItemList list, GameLocalObject gameLocalObject, boolean individualEvents) {
        GeneralItemEvent iEvent = null;
        for (GeneralItem giBean: list.getGeneralItems()) {
            if (giBean != null) {
                if (giBean.getDeleted()!=null &&giBean.getDeleted()) {
                    DaoConfiguration.getInstance().getGeneralItemLocalObjectDao().deleteByKey(giBean.getId());
                    iEvent = new GeneralItemEvent(giBean.getId());
                    if (individualEvents) ARL.eventBus.post(iEvent);
                } else {
                    GeneralItemLocalObject giDao = DaoConfiguration.getInstance().getGeneralItemLocalObjectDao().load(giBean.getId());

                    giDao = toDaoLocalObject(giBean, giDao);

                    giDao.setGameLocalObject(gameLocalObject);


                    if (giBean.getDependsOn() != null) {
                        if (giDao.getDependsOn() != null && !giDao.getDependencyLocalObject().recursiveEquals(giBean.getDependsOn())) {
                            giDao.getDependencyLocalObject().recursiveDelete();
                            giDao.setDependencyLocalObject(null);
                        }

                        if (giDao.getDependsOn() == null) {
                            DependencyLocalObject dependsOn = DependencyLocalObject.createDependencyLocalObject(giBean.getDependsOn());
                            giDao.setDependencyLocalObject(dependsOn);
                        }
                    } else {
                        if (giDao.getDependsOn() != null) {
                            giDao.getDependencyLocalObject().recursiveDelete();
                            giDao.setDependencyLocalObject(null);
                        }
                    }

                    if (giBean.getDisappearOn() != null) {
                        if (giDao.getDisappearAt() != null && !giDao.getDependencyDisappearLocalObject().recursiveEquals(giBean.getDisappearOn())) {
                            giDao.getDependencyDisappearLocalObject().recursiveDelete();
                            giDao.setDependencyDisappearLocalObject(null);
                        }
                        if (giDao.getDisappearAt() == null) {
                            DependencyDisappearLocalObject disappearOn = DependencyDisappearLocalObject.createDependencyDisappearLocalObject(giBean.getDisappearOn());
                            giDao.setDependencyDisappearLocalObject(disappearOn);
                        }
                    } else {
                        if (giDao.getDisappearAt() != null) {
                            giDao.getDependencyDisappearLocalObject().recursiveDelete();
                            giDao.setDependencyDisappearLocalObject(null);
                        }
                    }

                    DaoConfiguration.getInstance().getGeneralItemLocalObjectDao().insertOrReplace(giDao);

                    GiFileReferenceDelegator.getInstance().createReference(giBean, giDao);
                    iEvent = new GeneralItemEvent(giDao.getId());
                    if (individualEvents) ARL.eventBus.post(iEvent);
                }
            }
            if (iEvent != null && !individualEvents) {
                ARL.eventBus.post(iEvent);
            }
            gameLocalObject.setLastSyncGeneralItemsDate(list.getServerTime());
            DaoConfiguration.getInstance().getGameLocalObjectDao().update(gameLocalObject);
            if (iEvent != null) {
//        syncDates.put(gameLocalObject.getId(), list.getServerTime());
//
                if (gameLocalObject != null) {

                    gameLocalObject.resetGeneralItems();
                }
            }
        }
    }

    private GeneralItemLocalObject toDaoLocalObject (GeneralItem giBean, GeneralItemLocalObject giDao) {
        if (giDao == null)giDao = new GeneralItemLocalObject();
        giDao.setTitle(giBean.getName());
        giDao.setId(giBean.getId());
        giDao.setAutoLaunch(giBean.getAutoLaunch());
        giDao.setLastModificationDate(giBean.getLastModificationDate());
        giDao.setDescription(giBean.getDescription());
        giDao.setBean(giBean.toString());
        giDao.setDeleted(giBean.getDeleted());
        giDao.setType(giBean.getType());
        giDao.setLat(giBean.getLat());
        giDao.setLng(giBean.getLng());
        return giDao;
    }

//    private long getLastSyncDate(long gameId) {
//        if (syncDates.containsKey(gameId)) {
//            return syncDates.get(gameId);
//        }
//        return 0l;
//    }


    private void GeneralItemLocalObject(GeneralItem giBean) {}


    private class CreateGeneralItem {
        GeneralItem gi;

        private CreateGeneralItem(GeneralItem gi) {
            this.gi = gi;
        }

        public GeneralItem getGi() {
            return gi;
        }

        public void setGi(GeneralItem gi) {
            this.gi = gi;
        }
    }

    private class SyncGeneralItems {
        private GameLocalObject game;

        private SyncGeneralItems(GameLocalObject game) {
            this.game = game;
        }

        public GameLocalObject getGame() {
            return game;
        }

        public void setGame(GameLocalObject game) {
            this.game = game;
        }
    }

    private void loadGame(GameLocalObject gameLocalObject) {
        for (GeneralItemLocalObject generalItemLocalObject: gameLocalObject.getGeneralItems()){
            generalItemLocalObject.getResponses(); //todo for run only
            generalItemLocalObject.getActions(); //todo for run only
            generalItemLocalObject.getVisibilities();
            generalItemLocalObject.getGeneralItemMedia();

        }
    }
}
