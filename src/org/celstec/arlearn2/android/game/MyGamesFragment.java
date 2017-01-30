package org.celstec.arlearn2.android.game;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.SherlockListFragment;
import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.listadapter.ListItemClickInterface;
import org.celstec.arlearn2.android.listadapter.impl.GamesLazyListAdapter;
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
public class MyGamesFragment extends SherlockListFragment implements ListItemClickInterface<GameLocalObject> {

    private GamesLazyListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ARL.games.syncMyGames();
        ARL.games.syncGamesParticipate();
        ARL.runs.syncRunsParticipate();

    }

    @Override
    public void onResume() {
//        DaoConfiguration.getInstance().getGeneralItemVisibilityLocalObjectDao().deleteAll();
//        DaoConfiguration.getInstance().getActionLocalObjectDao().deleteAll();
        super.onResume();
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            getActivity().getActionBar().setIcon(R.drawable.ic_ab_back);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.mygames_list, container, false);
        if (adapter == null) {
            adapter = new GamesLazyListAdapter(getActivity());
            adapter.setOnListItemClickCallback(this);
        }

        setListAdapter(adapter);
        return v;
    }

    @Override
    public void onListItemClick(View v, int position, GameLocalObject game) {
//        Intent gameIntent = new Intent(getActivity(), GameSplashScreen.class);
//        gameIntent.putExtra(GameLocalObject.class.getName(), game.getId());
//        getActivity().startActivity(gameIntent);

        FragmentManager fm = getActivity().getSupportFragmentManager();
        Bundle args = new Bundle();
        Fragment frag = null;
        int amountOfRuns = 0;
        RunLocalObject lastRun = null;
        for (RunLocalObject run: game.getRuns()) {
            if (run.getDeleted() == null || !run.getDeleted()) {
                amountOfRuns++;
                lastRun = run;
            }
        }
        if (amountOfRuns ==1) {
            GameSplashScreen.startActivity(getActivity(), game.getId(), lastRun.getId());
            return;
        } else {
            frag = new GameRunsFragment();
            ((GameRunsFragment)frag).setGame(game);
        }

        frag.setArguments(args);
        fm.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.right_pane, frag).addToBackStack(null).commit();
    }

    @Override
    public boolean setOnLongClickListener(View v, int position, GameLocalObject object) {
        return false;
    }

}
