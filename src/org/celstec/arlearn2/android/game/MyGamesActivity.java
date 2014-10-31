package org.celstec.arlearn2.android.game;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import org.celstec.arlearn2.android.GameRunsFragment;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.listadapter.ListItemClickInterface;
import org.celstec.arlearn2.android.listadapter.impl.GamesLazyListAdapter;
import org.celstec.dao.gen.GameLocalObject;

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
public class MyGamesActivity extends ListActivity implements ListItemClickInterface<GameLocalObject> {

    private GamesLazyListAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mygames_list);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        ARL.eventBus.register(this);
        adapter = new GamesLazyListAdapter(this);
        setListAdapter(adapter);
        adapter.setOnListItemClickCallback(this);
    }

    @Override
    public void onListItemClick(View v, int position, GameLocalObject game) {
            GameSplashScreen.startActivity(this, game.getId(), game.getRuns().get(0).getId());
    }

    @Override
    public boolean setOnLongClickListener(View v, int position, GameLocalObject object) {
        return false;
    }

}
