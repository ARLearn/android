package org.celstec.arlearn2.android.listadapter.impl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import daoBase.DaoConfiguration;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.delegators.StoreDelegator;
import org.celstec.arlearn2.android.events.GameEvent;
import org.celstec.arlearn2.android.listadapter.LazyListAdapter;
import org.celstec.dao.gen.*;

import java.util.HashMap;

import static org.celstec.arlearn2.android.delegators.StoreDelegator.*;

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
public class CategoryGamesLazyListAdapter extends LazyListAdapter<StoreGameLocalObject> {

    private Query query;

    public CategoryGamesLazyListAdapter(Context context, long category) {
        super(context);
        StoreGameLocalObjectDao dao = DaoConfiguration.getInstance().getStoreGameLocalObjectDao();
//        qb = dao.queryBuilder().orderAsc(GameLocalObjectDao.Properties.Title);
        query = dao.queryRawCreate(
                ", GAME_CATEGORY_LOCAL_OBJECT G WHERE G.CATEGORY_ID=? AND T._ID=G.GAME_ID", ""+category);

        ARL.eventBus.register(this);
        setLazyList(query.listLazy());
    }

    public void onEventMainThread(GameEvent event) {
        if (lazyList != null) lazyList.close();
        setLazyList(query.listLazy());
        notifyDataSetChanged();
    }

    public void close() {
        if (lazyList != null)lazyList.close();
        ARL.eventBus.unregister(this);
    }

    @Override
    public View newView(Context context, StoreGameLocalObject item, ViewGroup parent) {
        if (item == null) return null;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.store_game_list_entry_small, parent, false);

    }

    @Override
    public void bindView(View view, Context context,  StoreGameLocalObject item) {
        TextView firstLineView =(TextView) view.findViewById(R.id.gameTitleId);
        firstLineView.setText(item.getTitle());
        if (item != null) {
            byte[] data = item.getIcon();
            if (data != null && data.length!=0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                ((ImageView) view.findViewById(R.id.icon)).setImageBitmap(bitmap);
            } else {
                ((ImageView) view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_default_game);
            }
            if (data == null){
                ARL.store.syncStoreGame(item.getId());
            }
        }
    }


    @Override
    public long getItemId(int position) {
        if (dataValid && lazyList != null) {
            StoreGameLocalObject item = lazyList.get(position);
            if (item != null) {
                return item.getId();
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }



}

