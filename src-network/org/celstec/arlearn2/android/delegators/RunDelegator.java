package org.celstec.arlearn2.android.delegators;

import android.util.Log;
import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.db.PropertiesAdapter;
import org.celstec.arlearn2.android.events.RunEvent;
import org.celstec.arlearn2.beans.run.Run;
import org.celstec.arlearn2.beans.run.RunList;

import org.celstec.arlearn2.beans.run.User;
import org.celstec.arlearn2.client.RunClient;
import org.celstec.arlearn2.client.UserClient;
import org.celstec.dao.gen.*;

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
public class RunDelegator extends AbstractDelegator{

    private static RunDelegator instance;

    private static long lastSyncDateParticipate = 0l;
    private static long lastSyncDate = 0l;
    private static long lastLocalSyncDateParticipate = 0l;

    private RunDelegator() {
        ARL.eventBus.register(this);
    }

    public static RunDelegator getInstance() {
        if (instance == null) {
            instance = new RunDelegator();
        }
        return instance;
    }

    /*
    Public API
     */

    public void syncRunsParticipate() {
        if ((System.currentTimeMillis() - lastLocalSyncDateParticipate ) > 240000) {
            lastLocalSyncDateParticipate = System.currentTimeMillis();
            ARL.eventBus.post(new SyncRunsEventParticipate());
        }

    }
    public void syncRunsParticipate(Long gameId) {
        SyncRunsEventParticipate sge = new SyncRunsEventParticipate();
        sge.setGameId(gameId);
        ARL.eventBus.post(sge);
    }

    public void asyncRunsParticipate() {
        onEventAsync(new SyncRunsEventParticipate());
    }

    public void syncRun(long runId) {
        ARL.eventBus.post(new SyncRun(runId));
    }

    public void selfRegister(long runId) {
        ARL.eventBus.post(new SelfRegister(runId));
    }

//    public void syncRun(GameLocalObject game) {
//        SyncRun sr = new SyncRun(game);
//    }

    public void onEventAsync(SelfRegister selfRegister) {
        selfRegisterForRun(selfRegister.getRunId());
    }

    /*
    Implementation
     */

    public void onEventAsync(SyncRunsEventParticipate sge) {
        String token = returnTokenIfOnline();
        if (token != null) {
            RunList rl = null;
            if (sge.getGameId() != null) {
                rl = RunClient.getRunClient().getRunsForGameParticipate(token, sge.gameId);
                if (rl.getError() == null) {
                    process(rl);
                }
            } else {
                rl = RunClient.getRunClient().getRunsParticipate(token, lastSyncDateParticipate);
                if (rl.getError() == null) {
                    process(rl);
                    lastSyncDateParticipate = rl.getServerTime();
                }
            }


        }
    }

    public static void deleteRuns() {
        lastSyncDateParticipate = 0l;
        lastSyncDate = 0l;
        DaoConfiguration.getInstance().getRunLocalObjectDao().deleteAll();
    }

    public void onEventAsync(SyncRun syncRun) {
        if (syncRun.getRunId()!=null) {
            asyncRun(syncRun.runId);
        } else if (syncRun.getGameId() != null) {
//            asyncGame(syncRun.getGameId());
        }

    }

    public void onEventAsync(CreateRun syncRun) {
        String token = returnTokenIfOnline();
        if (token != null) {
            Run run = new Run();
            run.setGameId(syncRun.getGameId());
            run.setTitle("Default Run");
            Run newRun = RunClient.getRunClient().createRun(token, run);
            selfRegisterForRun(newRun.getRunId());
            ARL.eventBus.post(new RunEvent(newRun.getRunId()));
//            asyncRun(newRun);
        }
    }

    public void asyncRun(long runId) {
        String token = returnTokenIfOnline();
        if (token != null) {
            Log.i(SYNC_TAG, "Sync run : " + runId);
            Run run =RunClient.getRunClient().getRun(runId, token);
            asyncRun(run);
        }
    }

    public void asyncRun(Run run) {
        if (run.getError() == null) {
            RunList rl = new RunList();
            rl.addRun(run);
            process(rl);
        }
    }

    public Run asyncRunBean(long runId) {
//        String token = returnTokenIfOnline();
        if (ARL.isOnline()) {
            return RunClient.getRunClient().getRun(runId, null);
        }
        return null;
    }

    private void process(RunList rl) {
        RunEvent runEvent = null;
        for (Run rBean: rl.getRuns()) {
            RunLocalObject databaseRun = DaoConfiguration.getInstance().getRunLocalObjectDao().load(rBean.getRunId());
            if (!(databaseRun != null && databaseRun.getDeleted() == true && rBean.getDeleted() == true)) {
                RunLocalObject newRun = toDaoLocalObject(rBean);
                String token = returnTokenIfOnline();
                if (token != null){
                    User user = UserClient.getUserClient().getUser(token, rBean.getRunId(), ARL.accounts.getLoggedInAccount().getFullId());
                    if (user.getRoles() != null && !user.getRoles().isEmpty()){
                        String userRoles = user.getRoles().get(0);
                        for (int i= 1; i< user.getRoles().size();i++) {
                            userRoles += ";"+user.getRoles().get(i);
                        }
                        newRun.setRoles(userRoles);
                    }


//                }
                }
                DaoConfiguration.getInstance().getRunLocalObjectDao().insertOrReplace(newRun);
                if (newRun.getGameLocalObject()!=null) {
                    newRun.getGameLocalObject().resetRuns();
                    newRun.getGameLocalObject().resetGeneralItems();
                    for (GeneralItemLocalObject object : newRun.getGameLocalObject().getGeneralItems()) {
                        object.removeRun(newRun.getId());
                        //Clear is necessary because GeneralItemLocalObject contains a cache
                    }
                }
                if (newRun.getDeleted()) {
                    ResponseDelegator.getInstance().deleteResponses(newRun.getId());
                    GeneralItemVisibilityDelegator.getInstance().deleteVisibilities(newRun.getId());
                    ActionsDelegator.getInstance().deleteActions(newRun.getId());
//                DaoConfiguration.getInstance().getGeneralItemLocalObjectDao().getSession().refresh(new GeneralItemLocalObject());
                    DaoConfiguration.getInstance().getRunLocalObjectDao().deleteByKey(newRun.getId());
                    ARL.eventBus.postSticky(new RunEvent(newRun.getId(), newRun.getDeleted()));
                    ARL.proximityEvents.deleteEvents(newRun);
                } else {
                    ARL.proximityEvents.createEvents(newRun);
                    runEvent = new RunEvent(newRun.getId());
                }
            }



            //ARL.eventBus.post(new RunEvent(newRun.getId(), newRun.getDeleted()));
            //todo generates to many events
        }
        if (runEvent != null) {
            ARL.eventBus.postSticky(runEvent);
        }
    }

    private RunLocalObject toDaoLocalObject(Run rBean) {
        RunLocalObject runDao = new RunLocalObject();
        runDao.setId(rBean.getRunId());
        runDao.setDeleted(rBean.getDeleted());
        runDao.setTitle(rBean.getTitle());
        runDao.setGameId(rBean.getGameId());
//        GameLocalObject gameLocalObject = DaoConfiguration.getInstance().getGameLocalObjectDao().load(rBean.getGameId());
//        if (gameLocalObject != null)
//            runDao.setGameLocalObject(gameLocalObject);
        return runDao;
    }


    private void sync() {
        PropertiesAdapter pa = PropertiesAdapter.getInstance();
        if (pa != null) {
            String token = pa.getAuthToken();
            if (token != null) {
                RunList rl = RunClient.getRunClient().getRunsParticipate(token);
                for (Run runBean : rl.getRuns()) {
                    RunLocalObject runDao = new RunLocalObject();
                    runDao.setId(runBean.getRunId());
                    runDao.setTitle(runBean.getTitle());
                    runDao.setDeleted(runBean.getDeleted());
                    runDao.setGameId(runBean.getGameId());

                    DaoConfiguration.getInstance().getRunLocalObjectDao().insertOrReplace(runDao);
                }
            }
        }

    }

    public void selfRegisterForRun(long runId) {
        String token = returnTokenIfOnline();
        if (token != null) {
            asyncRun(RunClient.getRunClient().selfRegister(token, runId));
        }
    }

    public void createRun(long gameId) {
        ARL.eventBus.post(new CreateRun(gameId));
    }

//    public void resetRun(long runId) {
//        ARL.eventBus.post(new ResetRunsEvent(runId));
//    }


    private class SyncRunsEventParticipate {
        private Long gameId;

        public Long getGameId() {
            return gameId;
        }

        public void setGameId(Long gameId) {
            this.gameId = gameId;
        }
    }

//    private class ResetRunsEvent {
//        long runId;
//
//        public ResetRunsEvent(long runId) {
//            this.runId = runId;
//        }
//
//        public long getRunId() {
//            return runId;
//        }
//
//        public void setRunId(long runId) {
//            this.runId = runId;
//        }
//    }

    private class SyncRunsEvent {
    }

    private class CreateRun{

        private  long gameId;

        public CreateRun(long gameId) {
            this.gameId = gameId;
        }

        public long getGameId() {
            return gameId;
        }

        public void setGameId(long gameId) {
            this.gameId = gameId;
        }
    }

    private class SyncRun {
        private  Long runId;
        private  Long gameId;

        public SyncRun(long runId) {
            this.runId = runId;
        }

        private SyncRun() {
        }

        public SyncRun(GameLocalObject game) {
            this.gameId = game.getId();
        }

        public Long getRunId() {
            return runId;
        }

        public void setRunId(Long runId) {
            this.runId = runId;
        }

        public Long getGameId() {
            return gameId;
        }

        public void setGameId(Long gameId) {
            this.gameId = gameId;
        }
    }

    private class SelfRegister{
        private Long runId;

        private SelfRegister(Long runId) {
            this.runId = runId;
        }

        public Long getRunId() {
            return runId;
        }

        public void setRunId(Long runId) {
            this.runId = runId;
        }
    }
}
