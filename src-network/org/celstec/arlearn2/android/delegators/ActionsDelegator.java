package org.celstec.arlearn2.android.delegators;

import daoBase.DaoConfiguration;
import de.greenrobot.dao.query.QueryBuilder;
import org.celstec.arlearn2.beans.run.Action;
import org.celstec.arlearn2.beans.run.ActionList;
import org.celstec.arlearn2.client.ActionClient;
import org.celstec.dao.gen.AccountLocalObject;
import org.celstec.dao.gen.ActionLocalObject;
import org.celstec.dao.gen.ActionLocalObjectDao;

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
public class ActionsDelegator extends AbstractDelegator {

    public final static int READ = 0;
    public final static int START_RUN = 1;


    private static ActionsDelegator instance;

    private ActionsDelegator() {
        ARL.eventBus.register(this);
    }

    public static ActionsDelegator getInstance() {
        if (instance == null) {
            instance = new ActionsDelegator();
        }
        return instance;
    }

    /*
    Public API
    */

    public void issueAction(int action, Long runId, Long generalItemId, String generalItemType) {
        Action actionBean = new Action();
        switch (action) {
            case READ:
                actionBean.setAction("read");
                break;
            default:
                break;
        }
        if (actionDoesNotExist(runId, actionBean.getAction(), generalItemId, generalItemType, ARL.accounts.getLoggedInAccount())) {
            actionBean.setGeneralItemType(generalItemType);
            actionBean.setGeneralItemId(generalItemId);
            actionBean.setRunId(runId);
            actionBean.setTime(ARL.time.getServerTime());
            actionBean.setUserEmail(ARL.accounts.getLoggedInAccount().getFullId());
            ARL.actions.createAction(actionBean);
        }

    }



    public void createAction(long runId, String action) {
        if (actionDoesNotExist(runId, action, ARL.accounts.getLoggedInAccount())) {
            ARL.eventBus.post(new CreateAction(runId, action));
        }
    }
    private boolean actionDoesNotExist(Long runId, String action, Long generalItemId, String generalItemType, AccountLocalObject account) {
        ActionLocalObjectDao dao = DaoConfiguration.getInstance().getActionLocalObjectDao();
        QueryBuilder<ActionLocalObject> queryBuilder = dao.queryBuilder();
        queryBuilder.where(
                queryBuilder.and(
                        ActionLocalObjectDao.Properties.RunId.eq(runId),
                        ActionLocalObjectDao.Properties.Action.eq(action),
                        ActionLocalObjectDao.Properties.GeneralItem.eq(generalItemId),
                        ActionLocalObjectDao.Properties.GeneralItemType.eq(generalItemType),
                        ActionLocalObjectDao.Properties.Account.eq(account.getId())
                )
        );
        return queryBuilder.count() == 0;
    }

    private boolean actionDoesNotExist(long runId, String action, AccountLocalObject account) {
        ActionLocalObjectDao dao = DaoConfiguration.getInstance().getActionLocalObjectDao();
        QueryBuilder<ActionLocalObject> queryBuilder = dao.queryBuilder();
        queryBuilder.where(
                queryBuilder.and(
                        queryBuilder.and(ActionLocalObjectDao.Properties.RunId.eq(runId),
                                ActionLocalObjectDao.Properties.Action.eq(action)),
                        ActionLocalObjectDao.Properties.Account.eq(account.getId())
                )
        );
        return queryBuilder.count() == 0;
    }

    public void createAction(Action action) {
        ARL.eventBus.post(new CreateAction(action));

    }

    public void downloadActions(long runId) {
        ARL.eventBus.post(new DownloadActions(runId));
    }

    public void uploadActions(long runId) {
        ARL.eventBus.post(new UploadActions(runId));
    }

    public void syncActions(long runId) {
        downloadActions(runId);
        uploadActions(runId);
    }

   /*
   Implementation
   */

    private void onEventAsync(CreateAction createAction) {
        ActionLocalObject actionLocalObject = new ActionLocalObject();
        actionLocalObject.setTime(createAction.getAction().getTime());
        actionLocalObject.setAccount(createAction.account);
        if (createAction.getAction().getGeneralItemId() != null)
            actionLocalObject.setGeneralItem(createAction.getAction().getGeneralItemId());
        actionLocalObject.setAction(createAction.getAction().getAction());
        actionLocalObject.setRunId(createAction.getAction().getRunId());
        actionLocalObject.setGeneralItemType(createAction.getAction().getGeneralItemType());
        actionLocalObject.setIsSynchronized(false);
        DaoConfiguration.getInstance().getActionLocalObjectDao().insertOrReplace(actionLocalObject);
        if (actionLocalObject.getRunLocalObject() != null) actionLocalObject.getRunLocalObject().resetActions();
        if (actionLocalObject.getGeneralItemLocalObject() != null)
            actionLocalObject.getGeneralItemLocalObject().resetActions();
        ARL.generalItemVisibility.calculateVisibility(actionLocalObject.getRunId(), actionLocalObject.getRunLocalObject().getGameLocalObject().getId());

    }

    private synchronized void onEventAsync(UploadActions uploadActions) {
        String token = returnTokenIfOnline();
        if (token != null) {

            QueryBuilder<ActionLocalObject> queryBuilder = DaoConfiguration.getInstance().getActionLocalObjectDao().queryBuilder();
            List<ActionLocalObject> actionsList = queryBuilder.where(queryBuilder.and(
                    ActionLocalObjectDao.Properties.RunId.eq(uploadActions.getRunId()),
                    ActionLocalObjectDao.Properties.IsSynchronized.eq(0)
            )).list();

            for (ActionLocalObject actionLocalObject : actionsList) {
                uploadAction(token, actionLocalObject);
            }
        }
    }

    private void onEventAsync(DownloadActions downloadActions) {
        String token = returnTokenIfOnline();
        if (token != null) {
            ActionList list = ActionClient.getActionClient().getRunActions(token, downloadActions.getRunId(), 0l, null);
            storeActionsInLocalDatabase(list);
            while (list.getResumptionToken()!= null) {
                list = ActionClient.getActionClient().getRunActions(token, downloadActions.getRunId(), 0l, list.getResumptionToken());
                storeActionsInLocalDatabase(list);
            }
        }
    }

    private void storeActionsInLocalDatabase(ActionList list){
        for (Action action: list.getActions()) {
            storeActionsInLocalDatabase(action);
        }
    }

    private void storeActionsInLocalDatabase(Action action) {
        ActionLocalObject actionLocalObject = DaoConfiguration.getInstance().getActionLocalObjectDao().load(action.getIdentifier());
        if (actionLocalObject == null) {
            actionLocalObject = new ActionLocalObject(action);
            actionLocalObject.setIsSynchronized(true);
            DaoConfiguration.getInstance().getActionLocalObjectDao().insertOrReplace(actionLocalObject);
        }
    }

    private void uploadAction(String token, ActionLocalObject actionLocalObject) {
        Action resultBean = ActionClient.getActionClient().publishAction(token, actionLocalObject.getActionBean());
        DaoConfiguration.getInstance().getActionLocalObjectDao().delete(actionLocalObject);
        actionLocalObject.setId(resultBean.getIdentifier());
        actionLocalObject.setIsSynchronized(true);
        DaoConfiguration.getInstance().getActionLocalObjectDao().insertOrReplace(actionLocalObject);
//        actionLocalObject.setId(resultBean.get);
    }


    private class UploadActions {
        private long runId;

        private UploadActions(long runId) {
            this.runId = runId;
        }

        public long getRunId() {
            return runId;
        }

        public void setRunId(long runId) {
            this.runId = runId;
        }
    }

    private class DownloadActions {
        private long runId;

        private DownloadActions(long runId) {
            this.runId = runId;
        }

        public long getRunId() {
            return runId;
        }

        public void setRunId(long runId) {
            this.runId = runId;
        }
    }

    private class CreateAction {
        private Action action;
        private Long account;

        private CreateAction(Action action) {
            this.action = action;
            this.action.setTime(ARL.time.getServerTime());
            this.action.setUserEmail(ARL.accounts.getLoggedInAccount().getFullId());
            this.account = ARL.accounts.getAccount(action.getUserEmail()).getId();
        }

        private CreateAction(long runId, String action) {
            this.action = new Action();
            this.action.setRunId(runId);
            this.action.setAction(action);
            this.action.setTime(ARL.time.getServerTime());
            this.action.setUserEmail(ARL.accounts.getLoggedInAccount().getFullId());
            this.account = ARL.accounts.getLoggedInAccount().getId();
        }

        public Action getAction() {
            return action;
        }

        public void setAction(Action action) {
            this.action = action;
        }

        public Long getAccount() {
            return account;
        }

        public void setAccount(Long account) {
            this.account = account;
        }
    }
}
