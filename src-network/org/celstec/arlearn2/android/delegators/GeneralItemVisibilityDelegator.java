package org.celstec.arlearn2.android.delegators;

import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.events.GeneralItemBecameVisibleEvent;
import org.celstec.arlearn2.android.events.GeneralItemEvent;
import org.celstec.arlearn2.beans.run.GeneralItemVisibility;
import org.celstec.arlearn2.beans.run.GeneralItemVisibilityList;
import org.celstec.arlearn2.client.GeneralItemVisibilityClient;
import org.celstec.dao.gen.*;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
public class GeneralItemVisibilityDelegator extends AbstractDelegator {

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

    public void calculateInVisibility(Long runId, Long gameId) {
        ARL.eventBus.post(new CalculateINVisibility(runId, gameId));
    }

    public void onEventAsync(CalculateINVisibility calculateVisibility) {
        Long runId = calculateVisibility.getRunId();
        Long gameId = calculateVisibility.getGameId();
        if (runId == null || gameId == null) return;
        RunLocalObject run = DaoConfiguration.getInstance().getRunLocalObjectDao().load(runId);
        GameLocalObject game = DaoConfiguration.getInstance().getGameLocalObjectDao().load(gameId);
        if (run == null || game == null) return;
        GeneralItemEvent newInVisibilityStatementDetected = null;
        for (GeneralItemLocalObject generalItemLocalObject : game.getGeneralItems()) {
//            generalItemLocalObject.getDependencyDisappearLocalObject()
            long disappearAt = Long.MAX_VALUE;
            if (generalItemLocalObject.getDependencyDisappearLocalObject() != null) {
                disappearAt = generalItemLocalObject.getDependencyDisappearLocalObject().disappearAt(run);
            }
            if (disappearAt != Long.MAX_VALUE) {

                String id = GeneralItemVisibilityLocalObject.generateId(generalItemLocalObject.getId(), runId, GeneralItemVisibilityLocalObject.INVISIBLE);
                GeneralItemVisibilityLocalObject generalItemVisibilityLocalObject = DaoConfiguration.getInstance().getGeneralItemVisibilityLocalObjectDao().load(id);

                if (generalItemVisibilityLocalObject == null) {
                    generalItemVisibilityLocalObject = new GeneralItemVisibilityLocalObject();
                    generalItemVisibilityLocalObject.setId(id);
                    generalItemVisibilityLocalObject.setGeneralItemLocalObject(generalItemLocalObject);
                    generalItemLocalObject.resetVisibilities();
                    generalItemVisibilityLocalObject.setStatus(GeneralItemVisibilityLocalObject.INVISIBLE);
                    generalItemVisibilityLocalObject.setRunId(runId);
                    generalItemVisibilityLocalObject.setAccount(ARL.accounts.getLoggedInAccount().getFullId());
                    generalItemVisibilityLocalObject.setTimeStamp(disappearAt);
                    DaoConfiguration.getInstance().getGeneralItemVisibilityLocalObjectDao().insertOrReplace(generalItemVisibilityLocalObject);

                    newInVisibilityStatementDetected = new GeneralItemEvent(generalItemLocalObject.getId());

                    ARL.eventBus.postSticky(newInVisibilityStatementDetected);


                } else {
                    if (generalItemVisibilityLocalObject.getStatus() != GeneralItemVisibilityLocalObject.VISIBLE) {
                        if (generalItemVisibilityLocalObject.getTimeStamp() > disappearAt) {
                            generalItemVisibilityLocalObject.setTimeStamp(disappearAt);
                            DaoConfiguration.getInstance().getGeneralItemVisibilityLocalObjectDao().insertOrReplace(generalItemVisibilityLocalObject);
                        }
                    }
                }
                if (disappearAt > ARL.time.getServerTime()) {

//                } else {

                    ARL.visibilityHandler.scheduleInVisibilityEvent(disappearAt, run, game, generalItemLocalObject.getId());
                }
                //todo
            }
        }

    }

    public void calculateVisibility(Long runId) {
        if (runId == null) return;
        RunLocalObject run = DaoConfiguration.getInstance().getRunLocalObjectDao().load(runId);
        calculateVisibility(runId, run.getGameId());
    }

    public void calculateVisibility(Long runId, Long gameId) {
        ARL.eventBus.post(new CalculateVisibility(runId, gameId));
    }

    public void onEventAsync(CalculateVisibility calculateVisibility) {
        Long runId = calculateVisibility.getRunId();
        Long gameId = calculateVisibility.getGameId();
        if (runId == null || gameId == null) return;
        RunLocalObject run = DaoConfiguration.getInstance().getRunLocalObjectDao().load(runId);
        GameLocalObject game = DaoConfiguration.getInstance().getGameLocalObjectDao().load(gameId);
        if (run == null || game == null) return;
        GeneralItemEvent newVisibilityStatementDetected = null;
        for (GeneralItemLocalObject generalItemLocalObject : game.getGeneralItems()) {
            long satisfiedAt = 0l;
            Set<String> userRoles = run.getUserRoles();
            if (generalItemLocalObject.getGeneralItemBean().getRoles() != null && !generalItemLocalObject.getGeneralItemBean().getRoles().isEmpty()) {
                boolean containsRole = false;
                for (String targetRole : generalItemLocalObject.getGeneralItemBean().getRoles()) {
                    if (userRoles.contains(targetRole)) containsRole = true;
                }
                if (!containsRole) {
                    satisfiedAt = -1l;
                    String id = GeneralItemVisibilityLocalObject.generateId(generalItemLocalObject.getId(), runId, GeneralItemVisibilityLocalObject.VISIBLE);
                    DaoConfiguration.getInstance().getGeneralItemVisibilityLocalObjectDao().deleteByKey(id);
                }
            }
            if (satisfiedAt != -1 && generalItemLocalObject.getDependencyLocalObject() != null) {
                satisfiedAt = generalItemLocalObject.getDependencyLocalObject().satisfiedAt(run);
            }
            if (satisfiedAt != -1)
                if (satisfiedAt < ARL.time.getServerTime()) {
                    String id = GeneralItemVisibilityLocalObject.generateId(generalItemLocalObject.getId(), runId, GeneralItemVisibilityLocalObject.VISIBLE);
                    GeneralItemVisibilityLocalObject generalItemVisibilityLocalObject = DaoConfiguration.getInstance().getGeneralItemVisibilityLocalObjectDao().load(id);
                    if (generalItemVisibilityLocalObject == null) {

                        generalItemVisibilityLocalObject = new GeneralItemVisibilityLocalObject();
                        generalItemVisibilityLocalObject.setId(id);
                        generalItemVisibilityLocalObject.setGeneralItemLocalObject(generalItemLocalObject);
                        generalItemLocalObject.resetVisibilities();
                        generalItemVisibilityLocalObject.setStatus(GeneralItemVisibilityLocalObject.VISIBLE);
                        generalItemVisibilityLocalObject.setRunId(runId);
                        generalItemVisibilityLocalObject.setAccount(ARL.accounts.getLoggedInAccount().getFullId());
                        generalItemVisibilityLocalObject.setTimeStamp(satisfiedAt);
                        DaoConfiguration.getInstance().getGeneralItemVisibilityLocalObjectDao().insertOrReplace(generalItemVisibilityLocalObject);
                        if (satisfiedAt != 0) {
                            GeneralItemBecameVisibleEvent event = new GeneralItemBecameVisibleEvent(generalItemLocalObject.getId());
                            if (generalItemVisibilityLocalObject.getGeneralItemLocalObject().getAutoLaunch() != null && generalItemVisibilityLocalObject.getGeneralItemLocalObject().getAutoLaunch()) {
                                event.setAutoLaunch(true);
                            }
                            event.setShowStroken(true);
                            ARL.eventBus.postSticky(event);
                        } else {
                            newVisibilityStatementDetected = new GeneralItemEvent(generalItemLocalObject.getId());
                        }
//                    System.out.println("LOG postSticky "+System.currentTimeMillis());
                    } else {
                        if (generalItemVisibilityLocalObject.getStatus() != GeneralItemVisibilityLocalObject.INVISIBLE) {
                            if (generalItemVisibilityLocalObject.getTimeStamp() > satisfiedAt) {
                                generalItemVisibilityLocalObject.setTimeStamp(satisfiedAt);
                                DaoConfiguration.getInstance().getGeneralItemVisibilityLocalObjectDao().insertOrReplace(generalItemVisibilityLocalObject);
                            }
                        }
                    }
                } else {

                    ARL.visibilityHandler.scheduleVisibilityEvent(satisfiedAt, run, game);
                }
            if (newVisibilityStatementDetected != null) {
                ARL.eventBus.post(newVisibilityStatementDetected);
            }
//            else {
//                ARL.visibilityHandler
//            }
//            System.out.println(generalItemLocalObject.getTitle()+" "+ satisfiedAt);
        }
    }

     /*
    Implementation
     */

    public void onEventAsync(SyncGeneralItemVisibilities syncGeneralItemVisibilities) {
        if (!ARL.config.getBooleanProperty("white_label_online")) return;
        String token = returnTokenIfOnline();
        if (token != null) {
            long lastSyncDate = 0l;
            if (syncDates.containsKey(syncGeneralItemVisibilities.getRun().getId())) {
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
        for (GeneralItemVisibility vi : generalItemsVisibility) {

            String id = GeneralItemVisibilityLocalObject.generateId(vi.getGeneralItemId(), vi.getRunId(), vi.getStatus());
            GeneralItemVisibilityLocalObject object = DaoConfiguration.getInstance().getGeneralItemVisibilityLocalObjectDao().load(id);
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

    public void deleteVisibilities(Long runId) {
        DaoConfiguration.getInstance().getGeneralItemVisibilityLocalObjectDao().queryBuilder().where(GeneralItemVisibilityLocalObjectDao.Properties.RunId.eq(runId)).buildDelete().executeDeleteWithoutDetachingEntities();
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

    private class CalculateVisibility{
        long runId;
        long gameId;

        public CalculateVisibility(long runId, long gameId) {
            this.runId = runId;
            this.gameId = gameId;
        }

        public long getRunId() {
            return runId;
        }

        public void setRunId(long runId) {
            this.runId = runId;
        }

        public long getGameId() {
            return gameId;
        }

        public void setGameId(long gameId) {
            this.gameId = gameId;
        }
    }
    private class CalculateINVisibility{
        long runId;
        long gameId;

        public CalculateINVisibility(long runId, long gameId) {
            this.runId = runId;
            this.gameId = gameId;
        }

        public long getRunId() {
            return runId;
        }

        public void setRunId(long runId) {
            this.runId = runId;
        }

        public long getGameId() {
            return gameId;
        }

        public void setGameId(long gameId) {
            this.gameId = gameId;
        }
    }
}
