package org.celstec.arlearn2.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import authentication.LoginFragment;
import com.actionbarsherlock.app.SherlockFragment;
import org.celstec.arlearn.delegators.INQ;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.game.MyGamesFragment;
import org.celstec.arlearn2.android.qrCodeScanning.ScannerFragment;
import org.celstec.arlearn2.android.settings.SettingsFragment;
import org.celstec.arlearn2.android.store.StoreFragment;

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
public class MainMenu extends SherlockFragment {
    private static final String TAG = "MainMenu";

    private View button1;
    private View storeButton;
    private View button3;
    private View button4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        INQ.init(getActivity());
        ARL.accounts.syncMyAccountDetails();
        ARL.store.syncCategories();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            getActivity().getActionBar().setIcon(R.drawable.ic_ab_menu);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.main_menu, container, false);

        button1 = v.findViewById(R.id.games);
        storeButton =  v.findViewById(R.id.store);
        button3 =  v.findViewById(R.id.scan);
        button4 =  v.findViewById(R.id.settings);

        button1.setOnClickListener(new GamesButton());
        storeButton.setOnClickListener(new StoreButton());
        button3.setOnClickListener(new ScanButton());
        button4.setOnClickListener(new SettingsButton());

        return v;
    }


    private class GamesButton implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            FragmentManager fm = getActivity().getSupportFragmentManager();
            Bundle args = new Bundle();
            Fragment frag;
            if (ARL.accounts.isAuthenticated()){
                frag = new MyGamesFragment();
            } else {
                frag = new LoginFragment();
            }

            frag.setArguments(args);
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left, R.anim.slide_out_right)
                    .replace(R.id.right_pane, frag).addToBackStack(null).commit();
        }



    }

    private class StoreButton implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
             Bundle args = new Bundle();
            StoreFragment frag = new StoreFragment();
            frag.setArguments(args);
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left, R.anim.slide_out_right)
                    .replace(R.id.right_pane, frag).addToBackStack(null).commit();

        }
    }

    private class ScanButton implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            Bundle args = new Bundle();


            ScannerFragment frag = new ScannerFragment();
            frag.setArguments(args);
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left, R.anim.slide_out_right)
                    .replace(R.id.right_pane, frag).addToBackStack(null).commit();
        }
    }

    private class SettingsButton implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            Bundle args = new Bundle();
            SettingsFragment frag = new SettingsFragment();
            frag.setArguments(args);
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left, R.anim.slide_out_right)
                    .replace(R.id.right_pane, frag).addToBackStack(null).commit();

        }
    }
}
