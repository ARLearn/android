package org.celstec.arlearn2.android.listadapter;

import android.content.Context;
import daoBase.DaoConfiguration;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.events.GeneralItemEvent;
import org.celstec.dao.gen.GeneralItemLocalObject;
import org.celstec.dao.gen.GeneralItemLocalObjectDao;
import org.celstec.dao.gen.GeneralItemVisibilityLocalObject;
import org.celstec.dao.gen.GeneralItemVisibilityLocalObjectDao;

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
public abstract class AbstractGeneralItemsVisibilityAdapter extends LazyListAdapter<GeneralItemVisibilityLocalObject> {

    private QueryBuilder qb;
    protected long runId;
    protected long gameId;

    public AbstractGeneralItemsVisibilityAdapter(Context context, long runId, long gameId) {
        super(context);
        this.runId = runId;
        this.gameId = gameId;
        qb = getQueryBuilder(runId, gameId);
//        ARL.eventBus.register(this);
        setLazyList(qb.listLazy());
    }

    public AbstractGeneralItemsVisibilityAdapter(Context context, long runId, long gameId, boolean messagsOnly){
        super(context);
        this.runId = runId;
        this.gameId = gameId;
        qb = getQueryBuilder(runId, gameId, messagsOnly);

        setLazyList(qb.listLazy());
    }

    public void register(){
        ARL.eventBus.register(this);
    }

    public void unregister(){
        ARL.eventBus.unregister(this);
    }

    public static QueryBuilder<GeneralItemVisibilityLocalObject> getQueryBuilder(long runId, long gameId, boolean messagsOnly){
        GeneralItemVisibilityLocalObjectDao dao = DaoConfiguration.getInstance().getGeneralItemVisibilityLocalObjectDao();
        QueryBuilder<GeneralItemVisibilityLocalObject> qb =dao.queryBuilder();
        String condition = "";
        if (messagsOnly){
//            condition = "status = 1 and run_id = "+runId+" and general_item_id in (select _id from general_item_local_object where game_id = "+gameId+" and deleted = 0 and lat is null)";
            condition = "status = 1 and " +
                    "run_id = "+runId+" and " +
                    "NOT EXISTS (SELECT * from general_item_visibility_local_object where run_id = "+runId+" and " +
                    "status = 2 and general_item_id = T.general_item_id  and time_stamp < "+ARL.time.getServerTime()+") and " +
                    "general_item_id in (select _id from general_item_local_object where game_id = "+gameId+" and deleted = 0 and lat is null)";
       } else {
            condition = "status = 1 and " +
                    "run_id = "+runId+" and " +
                    "NOT EXISTS (SELECT * from general_item_visibility_local_object where run_id = "+runId+" and status = 2 and general_item_id = T.general_item_id  and time_stamp < "+ARL.time.getServerTime()+") and " +
                    "general_item_id in (select _id from general_item_local_object where game_id = "+gameId+" and deleted = 0)";

        }

        qb.where(new WhereCondition.StringCondition(
                condition
        )).orderDesc(GeneralItemVisibilityLocalObjectDao.Properties.TimeStamp);
        return qb;
    }

    public static QueryBuilder<GeneralItemVisibilityLocalObject> getQueryBuilder(long runId, long gameId){
        GeneralItemVisibilityLocalObjectDao dao = DaoConfiguration.getInstance().getGeneralItemVisibilityLocalObjectDao();
        QueryBuilder<GeneralItemVisibilityLocalObject> qb =dao.queryBuilder();
        String condition = "status = 1 and run_id = "+runId+" and general_item_id in (select _id from general_item_local_object where game_id = "+gameId+" and deleted = 0)";
        qb.where(new WhereCondition.StringCondition(
                condition
        )).orderDesc(GeneralItemVisibilityLocalObjectDao.Properties.TimeStamp);
        return qb;
    }

//    public static QueryBuilder<GeneralItemVisibilityLocalObject> getQueryBuilder(long runId, long gameId, boolean asc){
//        GeneralItemVisibilityLocalObjectDao dao = DaoConfiguration.getInstance().getGeneralItemVisibilityLocalObjectDao();
//        QueryBuilder<GeneralItemVisibilityLocalObject> qb =dao.queryBuilder();
//        qb.where(new WhereCondition.StringCondition("status = 1 and run_id = "+runId+" and general_item_id in (select _id from general_item_local_object where game_id = "+gameId+" and deleted = 0)"));
//        if (asc) {
//            qb = qb.orderAsc(GeneralItemVisibilityLocalObjectDao.Properties.TimeStamp);
//        } else {
//            qb = qb.orderDesc(GeneralItemVisibilityLocalObjectDao.Properties.TimeStamp);
//        }
//
//        return qb;
//    }


    public void onEventMainThread(GeneralItemEvent event) {

        qb = getQueryBuilder(runId, gameId, ARL.config.getProperty("message_view_messages").equals("messages_only"));
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        if (lazyList != null) lazyList.close();
        setLazyList(qb.listLazy());
        super.notifyDataSetChanged();
    }

    public void close() {
        if (lazyList != null)lazyList.close();
        ARL.eventBus.unregister(this);
    }
}
