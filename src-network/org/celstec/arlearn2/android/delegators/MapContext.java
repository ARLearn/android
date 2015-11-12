package org.celstec.arlearn2.android.delegators;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import org.celstec.arlearn2.android.util.GPSUtils;
import org.celstec.arlearn2.android.util.MediaFolders;
import org.celstec.arlearn2.beans.game.Config;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.IRegisterReceiver;
import org.osmdroid.tileprovider.MapTileProviderArray;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.modules.*;
import org.osmdroid.tileprovider.tilesource.*;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.TilesOverlay;

import java.io.File;
import java.io.IOException;

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
public class MapContext implements LocationListener {
    private GeoPoint location;
    private int zoom;
    
    public MapContext(Context ctx) {
        Location mLocation = GPSUtils.getLastKnownLocation();
        LocationManager locationManager = (LocationManager) ARL.ctx.getSystemService(ARL.ctx.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        if (location == null) {
            double lat =  50.87786336d;
            double lng = 5.958559513f;
            location = new GeoPoint(lat, lng);
        } else {
            location = new GeoPoint(mLocation);
        }
        zoom = 10;
    }
//    public static final OnlineTileSourceBase MIJNEN = new XYTileSource("Mijnen",
//            ResourceProxy.string.mapnik, 0, 18, 256, ".png", new String[] {
//            "http://a.tile.openstreetmap.org/",
//            "http://b.tile.openstreetmap.org/",
//            "http://c.tile.openstreetmap.org/" });

    public void applyContext(MapView mapView) {
        mapView.getController().setZoom(zoom);
//        mapView.getController().animateTo(ARL.mapContext.getLocation());
        mapView.getController().setCenter(ARL.mapContext.getLocation());
        mapView.getController().setZoom(zoom);

//        mapView.getTileProvider().rescaleCache(zoom, zoom, mapView.getScreenRect(null));

        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

//        mapView.setTileSource(TileSourceFactory.MAPNIK);

    }

    public void applyContext(MapView mapView, Config config) {
        applyContext(mapView);
        if (config.getTileSource() != null) {
            final Context applicationContext = ARL.ctx.getApplicationContext();
            final IRegisterReceiver registerReceiver = new SimpleRegisterReceiver(applicationContext);

            final ITileSource tileSource = new XYTileSource(config.getTileSource(), ResourceProxy.string.mapnik, config.getMinZoomLevel(), config.getMaxZoomLevel(), 512, ".png", new String[]{});



            final MapTileProviderArray tileProviderArray = new MapTileProviderArray(tileSource, registerReceiver, new MapTileModuleProviderBase[]{});
            tileProviderArray.setUseDataConnection(false);

            mapView.setTileSource(tileSource);
            mapView.setUseDataConnection(false);
            if (config.getBoundingBoxWest()!=null && config.getBoundingBoxSouth()!=null && config.getBoundingBoxEast()!=null&&config.getBoundingBoxNorth()!=null) {
                BoundingBoxE6 boundingBoxE6 = new BoundingBoxE6((int)(config.getBoundingBoxNorth() * 1E6),(int)(config.getBoundingBoxEast() * 1E6),(int)(config.getBoundingBoxSouth() * 1E6),(int)(config.getBoundingBoxWest() * 1E6));
                mapView.setScrollableAreaLimit(boundingBoxE6);
            }

        }

        mapView.setMaxZoomLevel(config.getMaxZoomLevel());
        mapView.setMinZoomLevel(config.getMinZoomLevel());

    }

    public void saveContext(MapView mapView) {
        zoom = mapView.getZoomLevel();
        location = mapView.getProjection().getBoundingBox().getCenter();
    }

    public GeoPoint getLocation() {
        if (location != null) {
            ((LocationManager) ARL.ctx.getSystemService(ARL.ctx.LOCATION_SERVICE)).removeUpdates(this);
        }
        return location;
    }


    @Override
    public void onLocationChanged(Location location) {
        this.location = new GeoPoint(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
