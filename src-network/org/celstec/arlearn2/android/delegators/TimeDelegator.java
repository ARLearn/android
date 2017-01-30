package org.celstec.arlearn2.android.delegators;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import org.celstec.arlearn2.client.VersionClient;

import java.text.SimpleDateFormat;
import java.util.Date;

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
public class TimeDelegator {
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static TimeDelegator instance;
    private long timeDifWithServer;

    private TimeDelegator(Context ctx) {
        timeDifWithServer = ARL.properties.getTimeDifferenceWithServer();
        ARL.eventBus.register(this);
//        mHandler.postDelayed(timeTask, 100);
        ARL.eventBus.post(this);
//        new Thread(timeTask).run();
    }

    public static TimeDelegator getInstance(Context ctx)  {

        if (instance == null) {
            instance = new TimeDelegator(ctx);
        }
        return instance;
    }

    public long getServerTime() {
        return System.currentTimeMillis() + timeDifWithServer;
    }

    public void printTime(){
        Log.i("TIME", "Server: "+format.format(new Date(getServerTime())));
        Log.i("TIME", "Local : "+format.format(new Date(System.currentTimeMillis())));
    }

    private Handler mHandler = new Handler();

    public void onEventAsync(TimeDelegator td){
//    protected Runnable timeTask = new Runnable() {
//        public void run() {
//            Log.i("TIME", "getting time from server");

            if (ARL.isOnline()){
//                Log.i("TIME", "getting time from server : online");
                try {
                    Long serverTime = VersionClient.getVersionClient().getTime();
//                    Log.i("TIME", "getting time from server : "+serverTime);
                if  (serverTime!= 0l)  {
                    timeDifWithServer = serverTime - System.currentTimeMillis();
                    ARL.properties.setTimeDifferenceWithServer(timeDifWithServer);
//                    mHandler.removeCallbacks(timeTask);
                    return;
                }
                }catch (Exception e) {
                    Log.e("TIME", e.getMessage(), e);
                }
            }
//            mHandler.postDelayed(timeTask, 60000);
//        }
    }

    public void printTime(String s, Long time) {
        Log.i("TIME", s+ " "+format.format(new Date(getServerTime())));
    }
}
