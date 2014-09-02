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
    private AbstractGeneralItemsVisibilityAdapter adapter;
    protected long runId;

    public AbstractGeneralItemsVisibilityAdapter(Context context, long runId, long gameId) {
        super(context);
        this.runId = runId;
        qb = getQueryBuilder(runId, gameId);
        ARL.eventBus.register(this);
        setLazyList(qb.listLazy());
    }

    public static QueryBuilder<GeneralItemVisibilityLocalObject> getQueryBuilder(long runId, long gameId){
        GeneralItemVisibilityLocalObjectDao dao = DaoConfiguration.getInstance().getGeneralItemVisibilityLocalObjectDao();
        QueryBuilder<GeneralItemVisibilityLocalObject> qb =dao.queryBuilder();

//        qb.where(
//                qb.and(GeneralItemVisibilityLocalObjectDao.Properties.RunId.eq(runId)),
//                .orderAsc(GeneralItemVisibilityLocalObjectDao.Properties.TimeStamp);
        qb.where(new WhereCondition.StringCondition("status = 1 and run_id = "+runId+" and general_item_id in (select _id from general_item_local_object where game_id = "+gameId+" and deleted = 0)"))
//        qb.where(new WhereCondition.StringCondition("deleted = 0 and _id in (select general_item_id from general_item_visibility_local_object where run_id = "+runId+" and status = 1)"));
                .orderAsc(GeneralItemVisibilityLocalObjectDao.Properties.TimeStamp);
        return qb;
    }

    public void onEventMainThread(GeneralItemEvent event) {
        if (lazyList != null) lazyList.close();
        setLazyList(qb.listLazy());
        notifyDataSetChanged();
    }

    public void close() {
        if (lazyList != null)lazyList.close();
        ARL.eventBus.unregister(this);
    }
}
