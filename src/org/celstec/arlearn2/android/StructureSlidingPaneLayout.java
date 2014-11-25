package org.celstec.arlearn2.android;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.store.GameFragment;
import org.celstec.arlearn2.beans.game.Game;
import org.celstec.arlearn2.beans.run.Run;

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
 * Contributors: Angel Suarez
 * ****************************************************************************
 */

public class StructureSlidingPaneLayout extends SherlockFragmentActivity {
    private SlidingPaneLayout mSlidingLayout;
    private LinearLayout settings_list;
    private static final int SETTINGS_LIST = 1;
    private static final String TAG = "StructureSlidingPaneLayout";

    private ActionBarHelper mActionBar;
    private SherlockFragment frag;
//    private long gameToLoad = 0l;
    Uri dataUri;
    UserGameIntentAnalyser gameIntentAnalyser = new UserGameIntentAnalyser() {
        @Override
        public void scannedGame(Game game) {
//            FragmentManager fm = getSupportFragmentManager();
//            Bundle args = new Bundle();
//            FragmentTransaction ft = fm.beginTransaction();
//
////            game.setGameId(gameIntentAnalyser.getGameId());
////            GameFragment gf = new GameFragment(game);
////            if (gameIntentAnalyser.hasRunToLoad()){
////                gf.setRunId(gameIntentAnalyser.getRunId());
////            }
//            ft.replace(R.id.right_pane, gf).addToBackStack(null).commit();


//            FragmentManager fm = getSupportFragmentManager();
//            Bundle args = new Bundle();
//            frag = new GameFragment(game);
//            frag.setArguments(args);
//            FragmentTransaction ft = fm.beginTransaction();
//            ft.replace(R.id.right_pane, frag).addToBackStack(null).commit();

            GameFragment frag = new GameFragment(game);
            launchFragment(frag);
            StructureSlidingPaneLayout.this.frag = frag;
        }

        @Override
        public void scannedRun(Run run) {
            GameFragment frag = new GameFragment(run.getGame());
            frag.setRun(run);
            launchFragment(frag);
            System.out.println(run);
            StructureSlidingPaneLayout.this.frag = frag;
        }

        @Override
        public void scannedLoginToken(String loginToken) {

        }
    };

    private void launchFragment(Fragment frag) {
        FragmentManager fm = getSupportFragmentManager();
        Bundle args = new Bundle();

        frag.setArguments(args);
        FragmentTransaction ft = fm.beginTransaction();

        ft.replace(R.id.right_pane, frag).addToBackStack(null).commit();
    }
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Intent i = getIntent();

            dataUri = i.getData();

//            if (uri.toString().startsWith("http://streetlearn.appspot.com/game/")) {
//                gameToLoad = Long.parseLong(uri.toString().substring(uri.toString().lastIndexOf("/") + 1));
//            }
//            if (uri.toString().startsWith("http://streetlearn.appspot.com/oai/resolve/")) {
//                String gameIdAsString = uri.toString().substring(uri.toString().lastIndexOf("/") + 1);
//                if (gameIdAsString.contains("?")) {
//                    gameIdAsString = gameIdAsString.substring(0, gameIdAsString.indexOf("?"));
//                }
//                gameToLoad = Long.parseLong(gameIdAsString);
//            }
        } catch (NullPointerException e){

        }
        setContentView(R.layout.structure_sliding_pane);
//        Button logout = (Button) this.findViewById(R.id.logoutButton);
//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ARL.accounts.disAuthenticate();
//            }
//        });
        getSupportActionBar().setIcon(R.drawable.ic_ab_menu);

        // Action bar
        mActionBar = createActionBarHelper();
        mActionBar.init();


        getSupportActionBar().setHomeButtonEnabled(true);


        // Handle fragments. First time a default fragment is placed here.
        restoreFragment(savedInstanceState);
    }

    private void restoreFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null){


            if (!gameIntentAnalyser.analyze(dataUri)){
                FragmentManager fm = getSupportFragmentManager();
                Bundle args = new Bundle();
                frag = new MainMenu();
                frag.setArguments(args);
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.right_pane, frag).addToBackStack(null).commit();
            }


//            if (gameIntentAnalyser.hasGameToLoad()){
////            if (gameToLoad != 0l) {
//                ft.replace(R.id.right_pane, frag).addToBackStack(null);
//                Game game = new Game();
//                game.setGameId(gameIntentAnalyser.getGameId());
//                GameFragment gf = new GameFragment(game);
//                if (gameIntentAnalyser.hasRunToLoad()){
//                    gf.setRunId(gameIntentAnalyser.getRunId());
//                }
//                ft.replace(R.id.right_pane, gf).addToBackStack(null).commit();
//            } else {
//
//            }
        }
    }

//    private class SliderListener extends SlidingPaneLayout.SimplePanelSlideListener {
//        @Override
//        public void onPanelOpened(View panel) {
//            mActionBar.onPanelOpened();
//        }
//
//        @Override
//        public void onPanelClosed(View panel) {
//            mActionBar.onPanelClosed();
//        }
//    }


//    private class FirstLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
//        @Override
//        public void onGlobalLayout() {
//            mActionBar.onFirstLayout();
//            mSlidingLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//        }
//    }
    /**
     * Function to handle home option menu on action bar. By now it only close or open the pane.
     * This function will be available in further fragments.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (getSupportFragmentManager().getBackStackEntryCount() <=1) {
//            this.finish();
//        }
//        getSupportFragmentManager().getBackStackEntryAt(0).

        if (mSlidingLayout != null) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    if (mSlidingLayout.isOpen()) {
                        mSlidingLayout.closePane();
                    } else {
                        mSlidingLayout.openPane();
                    }
                    return true;
            }
        }
        onBackPressed();
        return true;
//        return super.onOptionsItemSelected(item);
    }

    private ActionBarHelper createActionBarHelper() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return new ActionBarHelperICS();
        } else {
            return new ActionBarHelper();
        }
    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() <=1) {
            this.finish();
        } else {
            super.onBackPressed();
        }

    }

    private class ActionBarHelper {
        public void init() {}
        public void onPanelClosed() {}
        public void onPanelOpened() {}
        public void onFirstLayout() {}
        public void setTitle(CharSequence title) {}
    }

    private class ActionBarHelperICS extends ActionBarHelper {
        private final ActionBar mActionBar;
        private CharSequence mDrawerTitle;
        private CharSequence mTitle;

        ActionBarHelperICS() {
            mActionBar = getSupportActionBar();
        }

        @Override
        public void init() {
            mActionBar.setDisplayHomeAsUpEnabled(false);
            mActionBar.setHomeButtonEnabled(false);
            mTitle = mDrawerTitle = getTitle();
        }

        @Override
        public void onPanelClosed() {
            super.onPanelClosed();
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeButtonEnabled(true);
            mActionBar.setTitle(mTitle);
        }

        @Override
        public void onPanelOpened() {
            super.onPanelOpened();
            mActionBar.setHomeButtonEnabled(false);
            mActionBar.setDisplayHomeAsUpEnabled(false);
            mActionBar.setTitle(mDrawerTitle);
        }

        @Override
        public void onFirstLayout() {
            if (mSlidingLayout.isSlideable() && !mSlidingLayout.isOpen()) {
                onPanelClosed();
            } else {
                onPanelOpened();
            }
        }

        @Override
        public void setTitle(CharSequence title) {
            mTitle = title;
        }
    }

    private class ClickOnSettingsList implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // TODO add functionality
        }
    }

}
