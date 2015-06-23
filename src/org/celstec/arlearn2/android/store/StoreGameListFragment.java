package org.celstec.arlearn2.android.store;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockListFragment;
import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.listadapter.ListItemClickInterface;
import org.celstec.arlearn2.android.listadapter.impl.CategoryGamesLazyListAdapter;
import org.celstec.dao.gen.StoreGameLocalObject;

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
public class StoreGameListFragment extends SherlockListFragment implements ListItemClickInterface<StoreGameLocalObject> {

    private CategoryGamesLazyListAdapter adapter;

    private long categoryId;

    public StoreGameListFragment() {
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
        ARL.store.syncGamesForCategory(categoryId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ARL.games.syncGamesParticipate();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.store_category_gamelist, container, false);
        adapter = new CategoryGamesLazyListAdapter(getActivity(), categoryId);
        adapter.setOnListItemClickCallback(this);
        ((TextView)v.findViewById(R.id.categoryItemText)).setText(DaoConfiguration.getInstance().getCategoryLocalObjectDao().load(categoryId).getCategory());
        setListAdapter(adapter);
        return v;

    }

    @Override
    public void onListItemClick(View v, int position, StoreGameLocalObject game) {
        if (game != null) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            Bundle args = new Bundle();

            GameFragment frag = new GameFragment();
            args.putLong("gameId", game.getGameBean().getGameId());
            frag.setArguments(args);
            FragmentTransaction ft = fm.beginTransaction();

            ft.replace(R.id.right_pane, frag).addToBackStack(null).commit();
        }
    }

    @Override
    public boolean setOnLongClickListener(View v, int position, StoreGameLocalObject object) {
        return false;
    }

}
