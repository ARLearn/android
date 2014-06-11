package org.celstec.arlearn2.android;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockListFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.events.SearchResultList;
import org.celstec.arlearn2.android.listadapter.ListItemClickInterface;
import org.celstec.arlearn2.android.listadapter.impl.SearchResultsLazyListAdapter;
import org.celstec.arlearn2.beans.game.Game;

/**
 * Created by str on 27/05/14.
 */
public class NearMeFragment extends SherlockListFragment implements ListItemClickInterface<Game> {

    private SearchResultsLazyListAdapter adapter;
    private View v;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        ARL.eventBus.register(this);
    }
    @Override
    public void onDestroy() {
        ARL.eventBus.unregister(this);
        super.onDestroy();
        Fragment f =  getFragmentManager().findFragmentById(R.id.map);
        if (f != null)
            getFragmentManager().beginTransaction().remove(f).commit();
    }

    public void onEventMainThread(SearchResultList event) {
        System.out.println("games found"+ event.getGamesList().getGames().size());
    }

    @Override
    public void onResume() {
        super.onResume();
//        getActivity().getActionBar().setIcon(R.drawable.ic_ab_back);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (v!= null) {
            ((ViewGroup)v.getParent()).removeView(v);
            return v;
        }
        v = inflater.inflate(R.layout.store_nearme, container, false);
        if (adapter == null) {
            adapter = new SearchResultsLazyListAdapter(getActivity());
            adapter.setOnListItemClickCallback(this);
        }

        FragmentManager fragmgr = getFragmentManager();
        Fragment fragment = fragmgr.findFragmentById(R.id.map);
        final GoogleMap map = ((SupportMapFragment) fragment).getMap();
        map.setMyLocationEnabled(true);

        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                System.out.println("new position" +cameraPosition.target.latitude +" "+cameraPosition.target.latitude);

                LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;

                ARL.games.search(bounds.getCenter().latitude, bounds.getCenter().longitude, 2000000l);
            }
        });

        ListView lv = (ListView) v.findViewById(android.R.id.list);
        setListAdapter(adapter);

        return v;
    }

    @Override
    public void onListItemClick(View v, int position, Game game) {
        if (game != null) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            Bundle args = new Bundle();

            GameFragment frag = new GameFragment(game);
            frag.setArguments(args);
            FragmentTransaction ft = fm.beginTransaction();

            ft.replace(R.id.right_pane, frag).addToBackStack(null).commit();
//            Fragment f =  getFragmentManager().findFragmentById(R.id.map);
//            if (f != null)
//                getFragmentManager().beginTransaction().remove(f).commit();
        }
    }

    @Override
    public boolean setOnLongClickListener(View v, int position, Game object) {
        return false;
    }

}
