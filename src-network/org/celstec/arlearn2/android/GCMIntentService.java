package org.celstec.arlearn2.android;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.gcm.NotificationListenerInterface;

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
public class GCMIntentService extends IntentService {


    static final String TAG = "GCMDemo";


    public GCMIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ARL.init(this);
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        HashMap<String, String> map = new HashMap<String, String>();
        for (String key: intent.getExtras().keySet()) {
            if (intent.getExtras().get(key) instanceof String) map.put(key, intent.getExtras().getString(key));
            if (intent.getExtras().get(key) instanceof Integer) map.put(key, ""+intent.getExtras().getInt(key));
            if (intent.getExtras().get(key) instanceof Long) map.put(key, ""+intent.getExtras().getLong(key));
        }


        for (NotificationListenerInterface listener: ARL.notificationListenerInterfaces) {
            if (listener.acceptNotificationType(map.get("type"))) {
                listener.handleNotification(map);
            }
        }


        GCMWakefulReceiver.completeWakefulIntent(intent);

    }

}
