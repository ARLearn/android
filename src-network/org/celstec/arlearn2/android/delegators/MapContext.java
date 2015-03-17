package org.celstec.arlearn2.android.delegators;

import android.content.Context;
import android.location.Location;
import org.celstec.arlearn2.android.util.GPSUtils;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
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
public class MapContext {
    private GeoPoint location;
    private int zoom;
    
    public MapContext(Context ctx) {
        Location mLocation = GPSUtils.getLastKnownLocation();
        if (location == null) {
            double lat =  50.87786336d;
            double lng = 5.958559513f;
            location = new GeoPoint(lat, lng);
        } else {
            location = new GeoPoint(mLocation);
        }
        zoom = 10;
    }

    public void applyContext(MapView mapView) {
        mapView.getController().setZoom(zoom);
        mapView.getController().animateTo(ARL.mapContext.getLocation());
        mapView.setMaxZoomLevel(19);

        mapView.setTileSource(TileSourceFactory.MAPNIK);

        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
    }

    public void saveContext(MapView mapView) {
        zoom = mapView.getZoomLevel();
        location = mapView.getProjection().getBoundingBox().getCenter();
    }

    public GeoPoint getLocation() {
        return location;
    }
}
