package org.celstec.arlearn2.android.game.messageViews;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivity;
import org.celstec.arlearn2.android.game.messageViews.map.OSMOverlayItem;
import org.celstec.arlearn2.android.game.messageViews.map.OsmGeneralItemizedIconOverlay;
import org.celstec.dao.gen.GeneralItemLocalObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

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
    private MyLocationNewOverlay myLocation;

    private MapView mv;
//    private IMapController control;

    private OsmGeneralItemizedIconOverlay itemsOverlay;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameActivityFeatures = new GameActivityFeatures(this);

        setTheme(R.style.ARLearn_schema2);
        setContentView(R.layout.game_mapview);
        getActionBar().setIcon(R.drawable.ic_ab_back);

        gameActivityFeatures = new GameActivityFeatures(this);
        actionBarMenuController = new ActionBarMenuController(this, gameActivityFeatures);




//        mv.setTileSource(TileSourceFactory.MAPNIK);
//
//        mv.setClickable(true);
//        mv.setBuiltInZoomControls(true);
//
//        mv.getController().setZoom(5);
//        mv.setBuiltInZoomControls(false);
//        control = mv.getController();
        mv = (MapView) findViewById(R.id.map);
        myLocation = new MyLocationNewOverlay(this, mv) {

            @Override
            public void onLocationChanged(Location location, IMyLocationProvider source) {
                super.onLocationChanged(location, source);
                lat = location.getLatitude();
                lng = location.getLongitude();
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();
        mv = (MapView) findViewById(R.id.map);
        ARL.mapContext.applyContext(mv);
        itemsOverlay = new OsmGeneralItemizedIconOverlay(this, gameActivityFeatures.getRunId(), gameActivityFeatures.getGameId());
        mv.getOverlays().add(itemsOverlay);
        mv.invalidate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (itemsOverlay != null) itemsOverlay.close();
        ARL.mapContext.saveContext(mv);

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

    public void openGeneralItem(OverlayItem overlayItem){
        Intent intent = new Intent(GameMap.this, GeneralItemActivity.class);
        gameActivityFeatures.addMetadataToIntent(intent);
        intent.putExtra(GeneralItemLocalObject.class.getName(), ((OSMOverlayItem)overlayItem).getGeneralItemId());
        startActivity(intent);
    }
}
