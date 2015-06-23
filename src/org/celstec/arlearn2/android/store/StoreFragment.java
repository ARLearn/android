package org.celstec.arlearn2.android.store;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import daoBase.DaoConfiguration;
import de.greenrobot.dao.query.QueryBuilder;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.events.FeaturedGameEvent;
import org.celstec.arlearn2.android.listadapter.impl.CategoryGamesLazyListAdapter;
import org.celstec.arlearn2.android.viewWrappers.GameRowBig;
import org.celstec.dao.gen.StoreGameLocalObject;
import org.celstec.dao.gen.StoreGameLocalObjectDao;

import java.util.HashMap;

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
public class StoreFragment extends SherlockFragment {
    private static final String TAG = "Store";

    private View searchButton;
    private View categoryButton;

    private View nearMeButton;
    private View topGamesButton;

    private LayoutInflater inflater;
    private View v;
    private HashMap<Long,GameRowBig> featuredGames = new HashMap<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ARL.store.syncCategories();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            getActivity().getActionBar().setIcon(R.drawable.ic_ab_back);
        }
        ARL.eventBus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        ARL.eventBus.unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        v = inflater.inflate(R.layout.store, container, false);

        searchButton = v.findViewById(R.id.search);
        searchButton.setOnClickListener(new SearchButton());

        categoryButton = v.findViewById(R.id.category);
        categoryButton.setOnClickListener(new CategoryButton());

        nearMeButton = v.findViewById(R.id.nearme);
        nearMeButton.setOnClickListener(new NearMeButton());

        topGamesButton = v.findViewById(R.id.topGames);
        topGamesButton.setOnClickListener(new TopGamesButton());

        ARL.store.downloadFeaturedGames();
        featuredGames = new HashMap<>();
        StoreGameLocalObjectDao dao = DaoConfiguration.getInstance().getStoreGameLocalObjectDao();
        QueryBuilder qb = dao.queryBuilder().where(StoreGameLocalObjectDao.Properties.Featured.eq(true)).orderAsc(StoreGameLocalObjectDao.Properties.FeaturedRank);
        for (Object o: qb.list()) {
            StoreGameLocalObject storeGameLocalObject = (StoreGameLocalObject) o;
            drawFeaturedGameDescription(storeGameLocalObject);

        }
        return v;
    }

    public void onEventMainThread(final FeaturedGameEvent featuredGameEvent) {

        drawFeaturedGameDescription(featuredGameEvent.getGameObject());


    }

    private void drawFeaturedGameDescription(final StoreGameLocalObject storeGameLocalObject) {
        synchronized (featuredGames) {
            LinearLayout layout = (LinearLayout) v.findViewById(R.id.storeLinearLayout);
            GameRowBig big2 = null;
            if (featuredGames.containsKey(storeGameLocalObject.getId())) {
                big2 = featuredGames.get(storeGameLocalObject.getId());
            } else {
                big2 = new GameRowBig(inflater, layout) {
                    @Override
                    public void onGameClick() {
//                if (featuredGameEvent.getGameObject() != null) {
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        Bundle args = new Bundle();

                        GameFragment frag = new GameFragment();
                        args.putLong("gameId", storeGameLocalObject.getId());
                        frag.setArguments(args);
                        FragmentTransaction ft = fm.beginTransaction();

                        ft.replace(R.id.right_pane, frag).addToBackStack(null).commit();
//                }
                    }
                };
                featuredGames.put(storeGameLocalObject.getId(), big2);
            }

            big2.setGameTitle(storeGameLocalObject.getTitle());
            big2.setGameCategory(storeGameLocalObject.getCategoryLocalObject());
            big2.setGameDescription(storeGameLocalObject.getDescription());
            byte[] data = storeGameLocalObject.getIcon();
            if (data != null && data.length != 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                big2.setIcon(bitmap);
            }
        }
    }


    private class SearchButton implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Log.e(TAG, "Click Search");
            FragmentManager fm = getActivity().getSupportFragmentManager();
            Bundle args = new Bundle();

            SearchFragment frag = new SearchFragment();
            frag.setArguments(args);
            FragmentTransaction ft = fm.beginTransaction();

//            ft.setCustomAnimations(android.R.anim.slide_out_right, android.R.anim.slide_in_left);
            ft.replace(R.id.right_pane, frag).addToBackStack(null).commit();
        }
    }

    private class CategoryButton implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Log.e(TAG, "Click Store");
//            startActivity(new Intent(MainMenu.this.getActivity(), StoreActivity.class));

            FragmentManager fm = getActivity().getSupportFragmentManager();
            Bundle args = new Bundle();

            CategoryFragment frag = new CategoryFragment();
            frag.setArguments(args);
            fm.beginTransaction().replace(R.id.right_pane, frag).addToBackStack(null).commit();
        }
    }

    private class NearMeButton implements View.OnClickListener {
        @Override
        public void onClick(View view) {
//            FragmentManager fm = getActivity().getSupportFragmentManager();
//            Bundle args = new Bundle();
//
//            NearMeFragment frag = new NearMeFragment();
//            frag.setArguments(args);
//            FragmentTransaction ft = fm.beginTransaction();
//
//            ft.replace(R.id.right_pane, frag).addToBackStack(null).commit();

            Intent gameIntent = new Intent(getActivity(), NearMeActivity.class);
//            gameIntent.putExtra(GameLocalObject.class.getName(), gameId);
//            gameIntent.putExtra(RunLocalObject.class.getName(), runId);
            getActivity().startActivity(gameIntent);
        }
    }

    private class TopGamesButton implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            Bundle args = new Bundle();

            TopGamesFragment frag = new TopGamesFragment();
            frag.setArguments(args);
            FragmentTransaction ft = fm.beginTransaction();

            ft.replace(R.id.right_pane, frag).addToBackStack(null).commit();
        }
    }

}
