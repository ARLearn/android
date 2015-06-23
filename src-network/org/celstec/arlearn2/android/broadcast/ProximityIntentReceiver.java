package org.celstec.arlearn2.android.broadcast;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.beans.run.Action;
import org.celstec.dao.gen.DependencyLocalObject;
import org.celstec.dao.gen.ProximityEventRegistryLocalObject;

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
public class ProximityIntentReceiver extends BroadcastReceiver {
    public static final String ID = "uniqueId";


    @Override
    public void onReceive(Context ctx, Intent intent) {
        System.out.println(intent.getExtras());
        long id = intent.getLongExtra(ID, 0);
        ProximityEventRegistryLocalObject proximityEventRegistryLocalObject = DaoConfiguration.getInstance().getProximityEventRegistryLocalObjectDao().load(id);
        if (proximityEventRegistryLocalObject != null) {
            Action action = new Action();
            action.setAction(DependencyLocalObject.createProximityActionString(proximityEventRegistryLocalObject.getLat(), proximityEventRegistryLocalObject.getLng(), proximityEventRegistryLocalObject.getRadius()));
            action.setRunId(proximityEventRegistryLocalObject.getRunId());

            action.setTime(ARL.time.getServerTime());
            action.setUserEmail(ARL.accounts.getLoggedInAccount().getFullId());
            ARL.actions.createAction(action);
        }
//			PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 12, intent, 0);
//        ProcessProximityAlert ppa = new ProcessProximityAlert();
//        ppa.setUniqueId(id);
//        ppa.run(ctx);
    }
}
