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
import org.celstec.arlearn2.android.events.FeaturedGameEvent;
import org.celstec.arlearn2.android.events.GameEvent;
import org.celstec.arlearn2.android.listadapter.LazyListAdapter;
import org.celstec.dao.gen.StoreGameLocalObject;
import org.celstec.dao.gen.StoreGameLocalObjectDao;

/**
 * Created by str on 20/03/15.
 */
//TODO delete this one
public class FeaturedGamesLazyListAdapter extends LazyListAdapter<StoreGameLocalObject> {

    private Query query;

    public FeaturedGamesLazyListAdapter(Context context) {
        super(context);
        StoreGameLocalObjectDao dao = DaoConfiguration.getInstance().getStoreGameLocalObjectDao();
//        qb = dao.queryBuilder().orderAsc(GameLocalObjectDao.Properties.Title);
        QueryBuilder qb = dao.queryBuilder().where(StoreGameLocalObjectDao.Properties.Featured.eq(true)).orderAsc(StoreGameLocalObjectDao.Properties.FeaturedRank);
        ARL.eventBus.register(this);
        setLazyList(query.listLazy());
    }

    public void onEventMainThread(FeaturedGameEvent event) {
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
        return inflater.inflate(R.layout.store_game_list_entry_big, parent, false);
    }

    @Override
    public void bindView(View view, Context context,  StoreGameLocalObject item) {
        TextView firstLineView =(TextView) view.findViewById(R.id.gameTitleId);
        firstLineView.setText(item.getTitle());
        ((TextView) view.findViewById(R.id.gameDescriptionId)).setText(item.getDescription());
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