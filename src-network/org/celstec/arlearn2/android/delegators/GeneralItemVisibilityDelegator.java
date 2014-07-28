package org.celstec.arlearn2.android.delegators;

import daoBase.DaoConfiguration;
import org.celstec.arlearn2.beans.run.GeneralItemVisibility;
import org.celstec.arlearn2.beans.run.GeneralItemVisibilityList;
import org.celstec.arlearn2.client.GeneralItemVisibilityClient;
import org.celstec.dao.gen.GeneralItemVisibilityLocalObject;
import org.celstec.dao.gen.RunLocalObject;

import java.util.HashMap;
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
public class GeneralItemVisibilityDelegator extends AbstractDelegator{

    protected static GeneralItemVisibilityDelegator instance;
    private static HashMap<Long, Long> syncDates = new HashMap<Long, Long>();

    protected GeneralItemVisibilityDelegator() {
        ARL.eventBus.register(this);
    }

    public static GeneralItemVisibilityDelegator getInstance() {
        if (instance == null) {
            instance = new GeneralItemVisibilityDelegator();
        }
        return instance;
    }

    /*
       Public API
        */

    public void syncGeneralItemVisibilities(RunLocalObject run) {
        ARL.eventBus.post(new SyncGeneralItemVisibilities(run));
    }



     /*
    Implementation
     */

    private void onEventAsync(SyncGeneralItemVisibilities syncGeneralItemVisibilities) {
        String token = returnTokenIfOnline();
        if (token != null) {
            long lastSyncDate = 0l;
            if (syncDates.containsKey(syncGeneralItemVisibilities.getRun().getId())){
                lastSyncDate = syncDates.get(syncGeneralItemVisibilities.getRun().getId());
                syncDates.put(syncGeneralItemVisibilities.getRun().getId(), 0l);
            }
            GeneralItemVisibilityList visList = GeneralItemVisibilityClient.getGeneralItemClient().getGeneralItemVisibilities(token, syncGeneralItemVisibilities.getRun().getId(), lastSyncDate);
            if (visList.getError() == null) {
                process(visList.getGeneralItemsVisibility());

            }

        }
    }

    private void process(List<GeneralItemVisibility> generalItemsVisibility) {
        Long runId = null;
        for (GeneralItemVisibility vi: generalItemsVisibility) {

            String id = GeneralItemVisibilityLocalObject.generateId(vi);
            GeneralItemVisibilityLocalObject object =  DaoConfiguration.getInstance().getGeneralItemVisibilityLocalObjectDao().load(id);
            if (object == null) {
                object = new GeneralItemVisibilityLocalObject(vi);
                runId = vi.getRunId();
            } else {
                object.loadBean(vi);
            }
            DaoConfiguration.getInstance().getGeneralItemVisibilityLocalObjectDao().insertOrReplace(object);
        }
        if (runId != null) {
            DaoConfiguration.getInstance().getRunLocalObjectDao().load(runId).resetVisibilities();
        }

    }

    private class SyncGeneralItemVisibilities {
        private RunLocalObject run;

        private SyncGeneralItemVisibilities(RunLocalObject run) {
            this.run = run;
        }

        public RunLocalObject getRun() {
            return run;
        }

        public void setRun(RunLocalObject run) {
            this.run = run;
        }
    }
}
