package org.celstec.arlearn2.android.game.messageViews;

import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import org.celstec.arlearn2.android.R;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.ResourceProxyImpl;
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
public class GameMap extends Activity {
    GameActivityFeatures gameActivityFeatures;
    ActionBarMenuController actionBarMenuController;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.ARLearn_schema2);
        setContentView(R.layout.game_mapview);

        gameActivityFeatures = new GameActivityFeatures(this);
        actionBarMenuController = new ActionBarMenuController(this, gameActivityFeatures);

        MapView map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.getController().setZoom(5);
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
//    {
//        ResourceProxyImpl mResourceProxy = new ResourceProxyImpl(inflater.getContext().getApplicationContext());
//        MapView mMapView = new MapView(inflater.getContext(), 256, mResourceProxy);
//        return mMapView;
//    }

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
