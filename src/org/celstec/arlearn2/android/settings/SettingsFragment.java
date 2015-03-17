package org.celstec.arlearn2.android.settings;

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
import android.widget.LinearLayout;
import com.actionbarsherlock.app.SherlockFragment;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.events.FeaturedGameEvent;
import org.celstec.arlearn2.android.store.CategoryFragment;
import org.celstec.arlearn2.android.store.NearMeActivity;
import org.celstec.arlearn2.android.store.SearchFragment;
import org.celstec.arlearn2.android.store.TopGamesFragment;
import org.celstec.arlearn2.android.viewWrappers.GameRowBig;

/**
 * Created by str on 28/01/15.
 */
public class SettingsFragment  extends SherlockFragment {
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
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        v = inflater.inflate(R.layout.settings, container, false);

        SettingsEntry logout = new LogoutSetting(getActivity());

        ((LinearLayout)v.findViewById(R.id.settingsPane)).addView(logout.inflate(inflater, v));

        return v;
    }



}
