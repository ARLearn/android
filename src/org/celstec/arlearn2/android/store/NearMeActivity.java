package org.celstec.arlearn2.android.store;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.store.map.GameOverlay;
import org.celstec.arlearn2.beans.game.Game;
import org.celstec.dao.gen.StoreGameLocalObject;
import org.osmdroid.events.DelayedMapListener;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

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
//        getActionBar().setIcon(R.drawable.ic_ab_back);
        mMapView = (MapView) findViewById(R.id.map);
        ARL.mapContext.applyContext(mMapView);
        mMapView.setMapListener(new DelayedMapListener(new MapListener() {
            public boolean onZoom(final ZoomEvent e) {

                return false;
            }

            public boolean onScroll(final ScrollEvent e) {
                System.out.println("scrolled ! "+e.getX());
                issueQuery();
                return true;
            }
        }, 1000 ));
        issueQuery();
        gameOverlay =new GameOverlay(this);
        mMapView.getOverlays().add(gameOverlay);

    }

    @Override
    protected void onResume() {
        super.onResume();
//        ARL.mapContext.applyContext(mMapView);
//        itemsOverlay = new OsmGeneralItemizedIconOverlay(this, gameActivityFeatures.getRunId(), gameActivityFeatures.getGameId());
//        mMapView.getOverlays().add(gameOverlay);
        gameOverlay.resume();
        mMapView.invalidate();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (itemsOverlay != null) itemsOverlay.close();
        gameOverlay.close();
        ARL.mapContext.saveContext(mMapView);

    }

    private void issueQuery(){
        GeoPoint center  = mMapView.getProjection().getBoundingBox().getCenter();
        int lengthInMeters =mMapView.getProjection().getBoundingBox().getDiagonalLengthInMeters();
        System.out.println("length in meters "+lengthInMeters);
        ARL.store.search(center.getLatitude(), center.getLongitude(), (long) lengthInMeters);
    }

    public void openGame(Game game){
        if (game != null) {
            Intent gameIntent = new Intent(this, GameActivity.class);
            gameIntent.putExtra(StoreGameLocalObject.class.getName(), game.getGameId());
            startActivity(gameIntent);
        }
    }

    public void invalidateMap() {
        mMapView.invalidate();
    }
}
