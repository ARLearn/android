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
import com.actionbarsherlock.app.SherlockFragment;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.events.FeaturedGameEvent;
import org.celstec.arlearn2.android.viewWrappers.GameRowBig;

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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


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

        LinearLayout layout = (LinearLayout) v.findViewById(R.id.storeLinearLayout);
        ARL.store.downloadFeaturedGames();

//        GameRowBig big3 = new GameRowBig(inflater, layout);
//        big3.setGameTitle("Record game");
//        big3.setGameCategory("Music");
//        big3.setGameDescription("A location based game where the goal is to take as many pictures of music as possible.");
//
//        GameRowBig big1 = new GameRowBig(inflater, layout);
//        big1.setGameTitle("Get The picture");
//        big1.setGameCategory("Photography");
//        big1.setGameDescription("A location based game where the goal is to take as many pictures  as possible.");
//
//        GameRowBig big2 = new GameRowBig(inflater, layout);
//        big2.setGameTitle("Shop-a-holic");
//        big2.setGameCategory("Shopping");
//        big2.setGameDescription("A game that woman can play while their husbands are attending a football game");

        return v;
    }

    public void onEventMainThread(FeaturedGameEvent featuredGameEvent) {



        LinearLayout layout = (LinearLayout) v.findViewById(R.id.storeLinearLayout);
        GameRowBig big2 = new GameRowBig(inflater, layout);
        big2.setGameTitle(featuredGameEvent.getGameObject().getTitle());
        big2.setGameCategory("Shopping");
        big2.setGameDescription(featuredGameEvent.getGameObject().getDescription());
        byte[] data = featuredGameEvent.getGameObject().getIcon();
        if (data != null && data.length!=0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            big2.setIcon(bitmap);
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
