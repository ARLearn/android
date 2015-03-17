package org.celstec.arlearn2.android.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.SherlockListFragment;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.game.GameSplashScreen;
import org.celstec.arlearn2.android.listadapter.ListItemClickInterface;
import org.celstec.arlearn2.android.listadapter.impl.GamesLazyListAdapter;
import org.celstec.arlearn2.android.listadapter.impl.RunsLazyListAdapter;
import org.celstec.dao.gen.GameLocalObject;
import org.celstec.dao.gen.RunLocalObject;

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
public class GameRunsFragment extends SherlockListFragment implements ListItemClickInterface<RunLocalObject> {

    private RunsLazyListAdapter adapter;
    private GameLocalObject game;

    public GameRunsFragment() {
    }

//    public GameRunsFragment(GameLocalObject gameLocalObject) {
//        this.game = gameLocalObject;
//    }


    public void setGame(GameLocalObject game) {
        this.game = game;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTheme(R.style.ARLearn_schema1);
//        ARL.runs.syncRun(game);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            getActivity().getActionBar().setIcon(R.drawable.ic_ab_back);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.run_list, container, false);
        if (adapter == null) {
            adapter = new RunsLazyListAdapter(getActivity(), game.getId());
            adapter.setOnListItemClickCallback(this);
        }

        setListAdapter(adapter);
        return v;
    }

    @Override
    public void onListItemClick(View v, int position, RunLocalObject run) {
//        Intent gameIntent = new Intent(getActivity(), GameSplashScreen.class);
//        gameIntent.putExtra(GameLocalObject.class.getName(), game.getId());
//        gameIntent.putExtra(RunLocalObject.class.getName(), run.getId());
//        getActivity().startActivity(gameIntent);
        GameSplashScreen.startActivity(getActivity(), game.getId(), run.getId());
    }

    @Override
    public boolean setOnLongClickListener(View v, int position, RunLocalObject object) {
        return false;
    }

}
