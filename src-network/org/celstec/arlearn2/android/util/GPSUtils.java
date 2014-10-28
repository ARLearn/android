package org.celstec.arlearn2.android.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import org.celstec.arlearn2.android.delegators.ARL;

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
public class GPSUtils {
    public final static int METERS = 1;
    public final static int KILOMETERS = 2;

    public final static int MILES = 3;
    public final static int NAUTICAL = 4;

    public static double distance(double lat1, double lon1, double lat2, double lon2,
                                  int unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        switch (unit) {
            case METERS:
                dist = dist * 1.609344 * 1000;
                break;
            case KILOMETERS:
                dist = dist * 1.609344;
                break;
            case NAUTICAL:
                dist = dist * 0.8684;
                break;
            default:
                break;
        }

        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static Location getLastKnownLocation() {
        LocationManager locationManager = (LocationManager) ARL.ctx.getSystemService(ARL.ctx.LOCATION_SERVICE);

        String locationProviderNetwork = LocationManager.NETWORK_PROVIDER;
        String locationProviderGPS = LocationManager.GPS_PROVIDER;
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProviderGPS);
        if (lastKnownLocation == null) {
            lastKnownLocation =locationManager.getLastKnownLocation(locationProviderNetwork);
        }
        return lastKnownLocation;
    }

}
