package org.celstec.arlearn2.android.store.map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.RectF;
import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.events.SearchResultList;
import org.celstec.arlearn2.android.store.NearMeActivity;
import org.celstec.arlearn2.android.store.NearMeFragment;
import org.celstec.arlearn2.android.views.DrawableUtil;
import org.celstec.arlearn2.beans.game.Game;
import org.celstec.dao.gen.StoreGameLocalObject;
import org.celstec.dao.gen.StoreGameLocalObjectDao;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.ItemizedIconOverlay;

import java.util.ArrayList;
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
public class GameOverlay extends ItemizedIconOverlay<GameOverlayItem> {

    HashMap<Long, GameOverlayItem> games = new HashMap<Long, GameOverlayItem>();
    Context ctx;
    NearMeFragment nearMeFragment;

    public GameOverlay(NearMeFragment nearMeFragment) {
        super(nearMeFragment.getActivity(), new ArrayList<GameOverlayItem>(), null);
//        ARL.eventBus.register(this);
        this.ctx = nearMeFragment.getActivity();
        this.nearMeFragment = nearMeFragment;
        setGameList();
    }

    public GameOverlay(Activity ctx) {
        super(ctx, new ArrayList<GameOverlayItem>(), null);
//        ARL.eventBus.register(this);
        this.ctx = ctx;

        setGameList();
    }


    public void resume(){
        ARL.eventBus.register(this);
    }

    public void close() {
        ARL.eventBus.unregister(this);
    }

    public void onEventMainThread(SearchResultList event) {
        for (Game game: event.getGamesList().getGames()){
            GameOverlayItem overlayItem = new GameOverlayItem(DaoConfiguration.getInstance().getStoreGameLocalObjectDao().load(game.getGameId()),ctx);
            games.put(game.getGameId(), overlayItem);
        }
        setGameList();
        ((NearMeActivity)ctx).invalidateMap();
//        notifyAll();
    }

    private void setGameList() {
        removeAllItems();
        for (GameOverlayItem item: games.values().toArray(new GameOverlayItem[0])){
            addItem(item);
        }

    }

    @Override
    protected boolean onTap(int index) {
        if (nearMeFragment != null) {
            nearMeFragment.openGame(getItem(index).getGame().getGameBean());

        } else {
            ((NearMeActivity) ctx).openGame(getItem(index).getGame().getGameBean());
        }
        return true;
    }
//    @Override
//    public void draw(Canvas canvas, org.osmdroid.views.MapView mapView, boolean shadow) {
//        // TODO Auto-generated method stub
//        super.draw(canvas, mapView, shadow);
//        // go through all OverlayItems and draw title for each of them
//        for (int i = 0; i< size(); i++){
//            GameOverlayItem item = (GameOverlayItem) getItem(i);
//            GeoPoint point = item.getPoint();
//            Point markerBottomCenterCoords = new Point();
//            mapView.getProjection().toPixels(point, markerBottomCenterCoords);
//
////            /* Find the width and height of the title*/
////            TextPaint paintText = new TextPaint();
////            Paint paintRect = new Paint();
////
////            Rect rect = new Rect();
////            paintText.setTextSize(FONT_SIZE);
////            int itemTitleLength = 0;
////            String itemTitle = "";
////            if (item.getTitle() != null) itemTitleLength = item.getTitle().length();
////            if (item.getTitle() != null) itemTitle = item.getTitle();
////            paintText.getTextBounds(itemTitle, 0, itemTitleLength, rect);
////
////            rect.inset(-TITLE_MARGIN, -TITLE_MARGIN);
////            int markerHeight = item.getMarkerHeight() /2;
////            DrawableUtil util = new DrawableUtil(R.id.)
//            int markerHeight = 30; //DrawableUtil.dipToPixels(20);
////            rect.offsetTo(markerBottomCenterCoords.x - rect.width()/2, markerBottomCenterCoords.y + markerHeight ); //- rect.height()
////            rect.offsetTo(markerBottomCenterCoords.x - rect.width()/2, markerBottomCenterCoords.y - 5 ); //- rect.height()
//
////            paintText.setTextAlign(Paint.Align.CENTER);
////            paintText.setTextSize(FONT_SIZE);
////            paintText.setARGB(255, 255, 255, 255);
////            paintRect.setARGB(130, 0, 0, 0);
//
////            canvas.drawRoundRect( new RectF(rect), 2, 2, paintRect);
////            canvas.drawText(itemTitle, rect.left + rect.width() / 2,
////                    rect.bottom - TITLE_MARGIN, paintText);
//        }
//    }
}