package org.celstec.arlearn2.android.store;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.events.SearchResultList;
import org.celstec.arlearn2.android.store.map.GameOverlay;
import org.celstec.arlearn2.android.viewWrappers.GameRowBig;
import org.celstec.arlearn2.android.viewWrappers.GameRowSmall;
import org.celstec.arlearn2.beans.game.Game;
import org.celstec.dao.gen.StoreGameLocalObject;
import org.osmdroid.events.DelayedMapListener;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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
public class NearMeActivity extends Activity {

    private MapView mMapView;
    private GameOverlay gameOverlay;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_nearme);
        mMapView = (MapView) findViewById(R.id.map);
        ARL.mapContext.applyContext(mMapView);
        mMapView.setMapListener(new DelayedMapListener(new MapListener() {
            public boolean onZoom(final ZoomEvent e) {

                return false;
            }

            public boolean onScroll(final ScrollEvent e) {
                issueQuery();
                return true;
            }
        }, 1000 ));
        issueQuery();
        gameOverlay =new GameOverlay(this);
        mMapView.getOverlays().add(gameOverlay);

        if (android.os.Build.VERSION.SDK_INT >= 11) {
            getActionBar().setHomeButtonEnabled(true);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
//        gameOverlay.resume();
        ARL.eventBus.register(this);
        mMapView.invalidate();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (itemsOverlay != null) itemsOverlay.close();
//        gameOverlay.close();
        ARL.eventBus.unregister(this);
        ARL.mapContext.saveContext(mMapView);
    }

    private HashMap<StoreGameLocalObject, GameRowSmall> gameToRowMap = new HashMap<>();
    private HashSet<StoreGameLocalObject> displayingRows = new HashSet<>();

    public void onEventMainThread(SearchResultList event) {
        gameOverlay.onEventMainThread(event);
        redrawResults(event.getStoreGameList());
    }

    private void redrawResults(ArrayList<StoreGameLocalObject> storeGameList){
        LinearLayout layout = (LinearLayout) findViewById(R.id.gamesList);
        for (final StoreGameLocalObject storeGameLocalObject: storeGameList){
            if (!gameToRowMap.containsKey(storeGameLocalObject)) {
                GameRowSmall small = new GameRowSmall(getLayoutInflater(), layout, storeGameLocalObject);
                small.getView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openGame(storeGameLocalObject);
                    }
                });
                gameToRowMap.put(storeGameLocalObject, small);
            }
        }
        synchronized (displayingRows) {
            for (StoreGameLocalObject storeGameLocalObject : gameToRowMap.keySet()) {
                if (storeGameLocalObject.isContainedWithin(mMapView.getProjection().getBoundingBox())) {
                    System.out.println("We need to draw " + storeGameLocalObject);
                    if (!displayingRows.contains(storeGameLocalObject)) {
                        displayingRows.add(storeGameLocalObject);
                        layout.addView(gameToRowMap.get(storeGameLocalObject).getView());
                    }
                } else {
                    if (displayingRows.contains(storeGameLocalObject)) {
                        displayingRows.remove(storeGameLocalObject);
                        layout.removeView(gameToRowMap.get(storeGameLocalObject).getView());
                    }
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;

    }

    private void issueQuery(){
        GeoPoint center  = mMapView.getProjection().getBoundingBox().getCenter();
        int lengthInMeters =mMapView.getProjection().getBoundingBox().getDiagonalLengthInMeters();
        ARL.store.search(center.getLatitude(), center.getLongitude(), (long) lengthInMeters);
        redrawResults(new ArrayList<StoreGameLocalObject>());
    }

    public void openGame(StoreGameLocalObject game){
        if (game != null) {
            Intent gameIntent = new Intent(this, GameActivity.class);
            gameIntent.putExtra(StoreGameLocalObject.class.getName(), game.getId());
            startActivity(gameIntent);
        }
    }

    public void invalidateMap() {
        mMapView.invalidate();
    }
}
