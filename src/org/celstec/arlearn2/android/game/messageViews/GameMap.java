package org.celstec.arlearn2.android.game.messageViews;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.*;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.game.messageViews.map.OsmGeneralItemizedIconOverlay;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.ResourceProxyImpl;

import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.MyLocationOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

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
public class GameMap extends Activity {
    GameActivityFeatures gameActivityFeatures;
    ActionBarMenuController actionBarMenuController;

    double lat;
    double lng;
    private MyLocationOverlay myLocation;

    private MapView mv;
    private IMapController control;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameActivityFeatures = new GameActivityFeatures(this);

        setTheme(R.style.ARLearn_schema2);
        setContentView(R.layout.game_mapview);

        gameActivityFeatures = new GameActivityFeatures(this);
        actionBarMenuController = new ActionBarMenuController(this, gameActivityFeatures);

        MapView mv = (MapView) findViewById(R.id.map);
        mv.setTileSource(TileSourceFactory.MAPNIK);

        mv.setClickable(true);
        mv.setBuiltInZoomControls(true);

        mv.getController().setZoom(5);
        mv.setBuiltInZoomControls(false);
        control = mv.getController();
//        myLocation = new MyLocationOverlay(this, mv) {
//            public synchronized void onLocationChanged(Location location) {
//                super.onLocationChanged(location);
//                lat = location.getLatitude();
//                lng = location.getLongitude();
//            }
//        };
        ItemizedIconOverlay.OnItemGestureListener<OverlayItem> gestureListener = new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {

            @Override
            public boolean onItemLongPress(int index, OverlayItem arg1) {
                return false;
            }

            @Override
            public boolean onItemSingleTapUp(int index, OverlayItem arg1) {
                return false;
            }
        };

        OsmGeneralItemizedIconOverlay itemsOverlay = new OsmGeneralItemizedIconOverlay(this, new ArrayList<OverlayItem>(), gestureListener);
        mv.getOverlays().add(itemsOverlay);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        actionBarMenuController.inflateMenu(menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return actionBarMenuController.onOptionsItemSelected(item);

    }
}
