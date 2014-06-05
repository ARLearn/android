package org.celstec.arlearn2.android;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.events.SearchResultList;

/**
 * Created by str on 27/05/14.
 */
public class NearMeFragment extends SherlockFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        ARL.eventBus.register(this);
    }
    @Override
    public void onDestroy() {
        ARL.eventBus.unregister(this);
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

        final View v = inflater.inflate(R.layout.store_nearme, container, false);

        FragmentManager fragmgr = getFragmentManager();
        Fragment fragment = fragmgr.findFragmentById(R.id.map);
        GoogleMap map = ((SupportMapFragment) fragment).getMap();
        map.setMyLocationEnabled(true);

        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                System.out.println("new position" +cameraPosition.target.latitude +" "+cameraPosition.target.latitude);
                ARL.games.search(cameraPosition.target.latitude, cameraPosition.target.longitude, 200000l);
            }
        });

        return v;
    }
}
