package org.celstec.arlearn2.android.game.generalItem.dataCollection;

import daoBase.DaoConfiguration;
import de.greenrobot.dao.query.LazyList;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import org.celstec.dao.gen.GeneralItemVisibilityLocalObject;
import org.celstec.dao.gen.GeneralItemVisibilityLocalObjectDao;
import org.celstec.dao.gen.ResponseLocalObject;
import org.celstec.dao.gen.ResponseLocalObjectDao;

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
public class LazyListAdapter {

    protected LazyList<ResponseLocalObject> lazyList;
    protected QueryBuilder<ResponseLocalObject> qb;
    protected long runId;
    protected long generalItemId;


    public LazyListAdapter(long runId, long generalItemId) {
        lazyList = getQueryBuilder(runId, generalItemId).listLazy();
        this.runId = runId;
        this.generalItemId = generalItemId;
    }

    public void updateList() {
        lazyList = getQueryBuilder(runId, generalItemId).listLazy();
    }

    public QueryBuilder<ResponseLocalObject> getQueryBuilder(long runId, long generalItemId) {
        ResponseLocalObjectDao dao = DaoConfiguration.getInstance().getResponseLocalObjectDao();
        qb = dao.queryBuilder();
        qb.where(
                qb.and(ResponseLocalObjectDao.Properties.Revoked.eq(0),
                qb.and(ResponseLocalObjectDao.Properties.RunId.eq(runId),
                        ResponseLocalObjectDao.Properties.GeneralItem.eq(generalItemId))))
                .orderAsc(ResponseLocalObjectDao.Properties.TimeStamp);
        return qb;
    }

    public int size(){
        return lazyList.size();
    }

    public boolean hasAudioResult() {
        for (ResponseLocalObject responseLocalObject: lazyList) {
            if (responseLocalObject.isAudio()) return true;
        }
        return false;
    }

    public boolean hasPictureResult() {
        for (ResponseLocalObject responseLocalObject: lazyList) {
            if (responseLocalObject.isPicture()) return true;
        }
        return false;
    }

    public boolean hasVideoResult() {
        for (ResponseLocalObject responseLocalObject: lazyList) {
            if (responseLocalObject.isVideo()) return true;
        }
        return false;
    }

    public boolean hasTextResult() {
        for (ResponseLocalObject responseLocalObject: lazyList) {
            if (responseLocalObject.isText()) return true;
        }
        return false;
    }

    public boolean hasValueResult() {
        for (ResponseLocalObject responseLocalObject: lazyList) {
            if (responseLocalObject.isValue()) return true;
        }
        return false;
    }

}
