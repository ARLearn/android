package org.celstec.arlearn2.android.delegators;


import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.download.FileByteDownloader;
import org.celstec.arlearn2.android.events.*;
import org.celstec.arlearn2.beans.game.Game;
import org.celstec.arlearn2.beans.game.GamesList;
import org.celstec.arlearn2.beans.store.Category;
import org.celstec.arlearn2.beans.store.CategoryList;
import org.celstec.arlearn2.beans.store.GameCategory;
import org.celstec.arlearn2.client.GameClient;
import org.celstec.arlearn2.client.StoreClient;
import org.celstec.dao.gen.CategoryLocalObject;
import org.celstec.dao.gen.GameCategoryLocalObject;
import org.celstec.dao.gen.GameLocalObject;
import org.celstec.dao.gen.StoreGameLocalObject;

import java.util.List;
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
public class StoreDelegator extends AbstractDelegator{

    private static StoreDelegator instance;

    private static long lastDownloadFeaturedGamesSync = 0;
    private static long lastCategoriesSync = 0;

    private StoreDelegator() {
        ARL.eventBus.register(this);
    }

    public static StoreDelegator getInstance() {
        if (instance == null) {
            instance = new StoreDelegator();
        }
        return instance;
    }

    /*
   Public API
    */

    public void syncCategories() {
        if (lastCategoriesSync == 0 || lastCategoriesSync < System.currentTimeMillis()- 600000) {
            ARL.eventBus.post(new SyncCategories());
        }
    }

    public void syncGamesForCategory(Long categoryId) {
        ARL.eventBus.post(new SyncGames(categoryId));
    }

    public void syncTopGames() {
        ARL.eventBus.post(new SyncTopGames(Locale.getDefault().getLanguage()));
    }

    public void syncStoreGame(Long gameId) {
        ARL.eventBus.post(new SyncStoreGame(gameId));
    }

    public void downloadFeaturedGames() {
        if (lastDownloadFeaturedGamesSync == 0 || lastDownloadFeaturedGamesSync < System.currentTimeMillis()- 600000){
            ARL.eventBus.post(new DownloadFeaturedGames());
        }

    }

    public void search(Double lat, Double lng, Long distance) {
        ARL.eventBus.post(new LocationSearchGames(lat, lng, distance));
    }

    /*
   Implementation
    */

    public void onEventAsync(SyncGames syncGames) {
//        String token = returnTokenIfOnline();
        if (ARL.isOnline()) {
            for (GameCategory gameCategory: StoreClient.getStoreClient().getGameIdsForCategory(null, syncGames.getCategoryId()).getGameCategoryList()){
                GameCategoryLocalObject gameCategoryLocalObject = DaoConfiguration.getInstance().getGameCategoryDao().load(gameCategory.getId());
                GameCategoryEvent event = null;

                if (gameCategoryLocalObject == null) {
                    gameCategoryLocalObject = new GameCategoryLocalObject();
                    event = new GameCategoryEvent();
                    event.setCategoryId(gameCategory.getCategoryId());
                    event.setGameId(gameCategory.getGameId());
                }
                gameCategoryLocalObject.setId(gameCategory.getId());
                gameCategoryLocalObject.setDeleted(gameCategory.getDeleted());
                gameCategoryLocalObject.setGameId(gameCategory.getGameId());
                gameCategoryLocalObject.setCategoryId(gameCategory.getCategoryId());
                DaoConfiguration.getInstance().getGameCategoryDao().insertOrReplace(gameCategoryLocalObject);
                if (ARL.dao.getGameCategoryDao().load(gameCategory.getGameId())==null) {
                    StoreGameLocalObject storeGameLocalObject = asyncStoreGame(gameCategory.getGameId());
                    storeGameLocalObject.setCategoryId(gameCategory.getCategoryId());
                    DaoConfiguration.getInstance().getStoreGameLocalObjectDao().insertOrReplace(storeGameLocalObject);
                }
                if (event != null) {
                    ARL.eventBus.post(event);
                }
            }
        }
    }


    public void onEventAsync(DownloadFeaturedGames downloadFeaturedGames) {
        if (ARL.isOnline()) {
            lastDownloadFeaturedGamesSync = System.currentTimeMillis();
            GamesList gl =StoreClient.getStoreClient().getFeaturedGames(Locale.getDefault().getDisplayLanguage());
            for  (Game game: gl.getGames()) {

                StoreGameLocalObject storeGameLocalObject = asyncStoreGame(game.getGameId());


                FeaturedGameEvent event = new FeaturedGameEvent(game.getGameId(), game.getRank());
                CategoryList list = StoreClient.getStoreClient().getCategoriesByLangGame(ARL.getContext().getResources().getConfiguration().locale.getLanguage(), game.getGameId());
                storeGameLocalObject.setFeatured(true);
                storeGameLocalObject.setFeaturedRank(game.getRank());
                for (Category category:list.getCategoryList()) {
                    storeGameLocalObject.setCategoryId(category.getCategoryId());
                }
                DaoConfiguration.getInstance().getStoreGameLocalObjectDao().insertOrReplace(storeGameLocalObject);


//                event.setIcon(new FileByteDownloader(ARL.config.getProperty("arlearn_server")+"/game/"+game.getGameId()+"/gameThumbnail?thumbnail=200&crop=true").syncDownload());
//                GameClient.getGameClient().getGame(null, event.getGameId());
//                GameLocalObject gameLocalObject = ARL.games.asyncGame(event.getGameId(), false);
                event.setGameObject(storeGameLocalObject);

                ARL.eventBus.post(event);
            }

        }
    }

    public void onEventAsync(SyncTopGames syncTopGames) {
        if (ARL.isOnline()) {
            GamesList result = StoreClient.getStoreClient().getTopGames(syncTopGames.getLang());
            ARL.eventBus.post(new SearchResultList(result));
        }
    }
    public void onEventAsync(SyncCategories syncResponses) {
        if (ARL.isOnline()) {
            lastCategoriesSync = System.currentTimeMillis();
            CategoryList list = StoreClient.getStoreClient().getCategoriesByLang(null, ARL.getContext().getResources().getConfiguration().locale.getLanguage());
            CategoryEvent event = null;
            for (Category category:list.getCategoryList()) {
                CategoryLocalObject categoryLocalObject = DaoConfiguration.getInstance().getCategoryLocalObjectDao().load(category.getCategoryId());

                if (categoryLocalObject == null) {
                    categoryLocalObject = new CategoryLocalObject();
                    event = new CategoryEvent(category.getCategoryId());
                }
                categoryLocalObject.setId(category.getCategoryId());
                categoryLocalObject.setLang(category.getLang());
                categoryLocalObject.setCategory(category.getTitle());
                categoryLocalObject.setDeleted(category.getDeleted());
                categoryLocalObject.resetGames();
                DaoConfiguration.getInstance().getCategoryLocalObjectDao().insertOrReplace(categoryLocalObject);

            }
            if (event != null) {
                ARL.eventBus.post(event);
            }
        }
    }



    public void onEventAsync(SyncStoreGame g) {
        asyncStoreGame(g.getGameId());
    }

    public void onEventAsync(LocationSearchGames sg) {

        if (ARL.isOnline()) {
            GamesList result = GameClient.getGameClient().search(null, sg.getLat(), sg.getLng(), sg.getDistance());
            SearchResultList searchResultList = new SearchResultList();
            for (Game gameResult: result.getGames()) {
                searchResultList.addStoreGame(asyncStoreGame(gameResult.getGameId()));
            }
            ARL.eventBus.post(searchResultList);
        }
    }

    public StoreGameLocalObject asyncStoreGame(long gameId) {
        if (ARL.isOnline()) {
            Game game = GameClient.getGameClient().getGame(null, gameId);
            if (game.getError() == null) {
                return process(game);
            } else {
                ARL.eventBus.post(GameEvent.syncError());
            }
        }
        return null;
    }

    private StoreGameLocalObject process(Game gBean) {
        StoreGameLocalObject existingGame = DaoConfiguration.getInstance().getStoreGameLocalObjectDao().load(gBean.getGameId());
        StoreGameLocalObject newGame = toDaoLocalObject(gBean);
        newGame.setIcon(new FileByteDownloader(ARL.config.getProperty("arlearn_server")+"/game/"+gBean.getGameId()+"/gameThumbnail?thumbnail=200&crop=true").syncDownload());
        if ( (existingGame == null || newGame.getLastModificationDate() > existingGame.getLastModificationDate())) {
            DaoConfiguration.getInstance().getStoreGameLocalObjectDao().insertOrReplace(newGame);
            ARL.eventBus.post(new GameEvent(newGame.getId()));
//            ARL.eventBus.post(new SyncGameContributors(existingGame, newGame));
//                DaoConfiguration.getInstance().getGameLocalObjectDao().insertOrReplace(toDaoLocalObject(gBean));
            return newGame;
        }

        return existingGame;

    }

    private StoreGameLocalObject toDaoLocalObject(Game gBean) {
        StoreGameLocalObject gameDao = new StoreGameLocalObject();
        gameDao.setId(gBean.getGameId());
        gameDao.setTitle(gBean.getTitle());
        gameDao.setDeleted(gBean.getDeleted());
        gameDao.setDescription(gBean.getDescription());
        gameDao.setLicenseCode(gBean.getLicenseCode());
        gameDao.setLastModificationDate(gBean.getLastModificationDate());
        if (gameDao.getLastModificationDate() == null) gameDao.setLastModificationDate(0l);
        gameDao.setLat(gBean.getLat());
        gameDao.setLng(gBean.getLng());
        gameDao.setBean(gBean.toString());
        return gameDao;
    }




    private class SyncCategories{}

    private class DownloadFeaturedGames{}

    private class SyncGames{
        private Long categoryId;

        private SyncGames(Long categoryId) {
            this.categoryId = categoryId;
        }

        public Long getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(Long categoryId) {
            this.categoryId = categoryId;
        }
    }

    public List<CategoryLocalObject> getCategories() {
        return DaoConfiguration.getInstance().getCategoryLocalObjectDao().loadAll();
    }

    public class SyncStoreGame {
        private long gameId;

        private SyncStoreGame(long gameId) {
            this.gameId = gameId;
        }

        public long getGameId() {
            return gameId;
        }

        public void setGameId(long gameId) {
            this.gameId = gameId;
        }
    }

    private class SyncTopGames{
        private String lang;

        private SyncTopGames(String lang) {
            this.lang = lang;
        }

        public String getLang() {
            return lang;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }
    }

    private class LocationSearchGames {
        private Double lat;
        private Double lng;
        private Long distance;

        private LocationSearchGames(Double lat, Double lng, Long distance) {
            this.lat = lat;
            this.lng = lng;
            this.distance = distance;
        }

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Double getLng() {
            return lng;
        }

        public void setLng(Double lng) {
            this.lng = lng;
        }

        public Long getDistance() {
            return distance;
        }

        public void setDistance(Long distance) {
            this.distance = distance;
        }
    }
}
