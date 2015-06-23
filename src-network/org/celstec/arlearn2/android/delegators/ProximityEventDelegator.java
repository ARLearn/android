package org.celstec.arlearn2.android.delegators;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.broadcast.ProximityIntentReceiver;
import org.celstec.arlearn2.beans.dependencies.ProximityDependency;
import org.celstec.arlearn2.beans.generalItem.GeneralItem;
import org.celstec.dao.gen.DependencyLocalObject;
import org.celstec.dao.gen.GeneralItemLocalObject;
import org.celstec.dao.gen.ProximityEventRegistryLocalObject;
import org.celstec.dao.gen.RunLocalObject;

import java.util.List;


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
public class ProximityEventDelegator {

    private static final long defaultExpireTime = 7200000l; //2 hours

    private static ProximityEventDelegator instance;

    private ProximityEventDelegator() {
        ARL.eventBus.register(this);
    }

    public static ProximityEventDelegator getInstance() {
        if (instance == null) {
            instance = new ProximityEventDelegator();
        }
        return instance;
    }

    public void deleteEvents(RunLocalObject newRun) {
        //todo
    }

    public void createEvents(RunLocalObject newRun) {
        ARL.eventBus.post(new CreateProximityEvents(newRun));
    }

    public synchronized void onEventAsync(CreateProximityEvents createProximityEvents) {
        ProximityFilter filter = new ProximityFilter();
        for (GeneralItemLocalObject generalItemLocalObject: createProximityEvents.getRunLocalObject().getGameLocalObject().getGeneralItems()) {
            if (generalItemLocalObject.getDependencyLocalObject() != null) {
                if (!generalItemLocalObject.getDeleted())
                    iterateDependencyStructure(createProximityEvents.getRunLocalObject(), generalItemLocalObject.getDependencyLocalObject(), filter);
            }
            //todo disappear
        }

    }

    private void iterateDependencyStructure(RunLocalObject runLocalObject,DependencyLocalObject dep, DepFilter depFilter) {
        depFilter.filter(runLocalObject,dep);
        if (dep.getType() == DependencyLocalObject.TIME_DEPENDENCY || dep.getType() == DependencyLocalObject.AND_DEPENDENCY || dep.getType() == DependencyLocalObject.OR_DEPENDENCY ) {
            iterateDependencyStructure(runLocalObject, dep.getChildDeps(), depFilter);
        }
    }
    private void iterateDependencyStructure(RunLocalObject runLocalObject,List<DependencyLocalObject> deps, DepFilter depFilter) {
        for (DependencyLocalObject dependencyLocalObject: deps) {
            iterateDependencyStructure(runLocalObject, dependencyLocalObject, depFilter);
        }
    }


    private interface DepFilter {
        public void filter(RunLocalObject runLocalObject, DependencyLocalObject dep);
    }

    private class ProximityFilter implements DepFilter {


        public void filter(RunLocalObject runLocalObject,DependencyLocalObject dep) {

            if (dep.getType() == DependencyLocalObject.PROXIMITY_DEPENDENCY) {
                if (dep.getLng() !=null && dep.getLng()!=null && dep.getRadius()!=null)
                    addProximityAlert(runLocalObject, dep.getLat(), dep.getLng(), dep.getRadius());
            }
        }
    }
    private static final String PROX_ALERT_INTENT = "org.celstec.arlearn.proximityAction";

    private void addProximityAlert(RunLocalObject runLocalObject, Double lat, Double lng, Long radius) {
        ProximityEventRegistryLocalObject oldProximityEvent = null;

        IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT);
        ARL.ctx.registerReceiver(new ProximityIntentReceiver(), filter);
        for (ProximityEventRegistryLocalObject event :runLocalObject.getProximityEvents() ){
            if (event.getLat().equals(lat) && event.getLng().equals(lng) && event.getRadius().equals(radius)) {
                oldProximityEvent = event;
            }
        }
        ProximityEventRegistryLocalObject proximityEventRegistryLocalObject = new ProximityEventRegistryLocalObject();
        boolean updateNecessary = false;
        if (oldProximityEvent == null) {
            updateNecessary = true;
            proximityEventRegistryLocalObject.setRunId(runLocalObject.getId());
            runLocalObject.resetProximityEvents();
            proximityEventRegistryLocalObject.setLat(lat);
            proximityEventRegistryLocalObject.setLng(lng);
            proximityEventRegistryLocalObject.setRadius(radius);
            proximityEventRegistryLocalObject.setExpires(System.currentTimeMillis() + defaultExpireTime);
            DaoConfiguration.getInstance().getProximityEventRegistryLocalObjectDao().insertOrReplace(proximityEventRegistryLocalObject);
        } else {
            if (oldProximityEvent.getExpires() < System.currentTimeMillis()) {
                updateNecessary = true;
                proximityEventRegistryLocalObject = oldProximityEvent;
                proximityEventRegistryLocalObject.setExpires(System.currentTimeMillis() + defaultExpireTime);
                DaoConfiguration.getInstance().getProximityEventRegistryLocalObjectDao().insertOrReplace(proximityEventRegistryLocalObject);
            }
        }
        if (updateNecessary) {
            Intent intent = new Intent(ARL.getContext(), ProximityIntentReceiver.class);
            intent.putExtra(ProximityIntentReceiver.ID, proximityEventRegistryLocalObject.getId());
            PendingIntent proximityIntent = PendingIntent.getBroadcast(ARL.ctx, (int) proximityEventRegistryLocalObject.getId().longValue() , intent, 0);
//            ARL.ctx.registerReceiver(new ProximityIntentReceiver())
            LocationManager locationManager = (LocationManager) ARL.ctx.getSystemService(Context.LOCATION_SERVICE);

            locationManager.removeProximityAlert(proximityIntent);


            locationManager.addProximityAlert(lat, lng, radius, proximityEventRegistryLocalObject.getExpires() - System.currentTimeMillis(), proximityIntent );
        }
        System.out.println("adding proximity event "+lat +" "+lng + "radius");
    }



    private class CreateProximityEvents {

        private RunLocalObject runLocalObject;

        public RunLocalObject getRunLocalObject() {
            return runLocalObject;
        }

        public void setRunLocalObject(RunLocalObject runLocalObject) {
            this.runLocalObject = runLocalObject;
        }

        public CreateProximityEvents(RunLocalObject runLocalObject) {

            this.runLocalObject = runLocalObject;
        }
    }
}
