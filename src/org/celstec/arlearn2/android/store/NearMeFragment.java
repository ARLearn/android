package org.celstec.arlearn2.android.store;


import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ZoomButtonsController;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockListFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.events.SearchResultList;
import org.celstec.arlearn2.android.listadapter.ListItemClickInterface;
import org.celstec.arlearn2.android.listadapter.impl.SearchResultsLazyListAdapter;
import org.celstec.arlearn2.android.store.map.GameOverlay;
import org.celstec.arlearn2.android.util.GPSUtils;
import org.celstec.arlearn2.beans.game.Game;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMap;
import org.osmdroid.api.IMapController;
import org.osmdroid.events.DelayedMapListener;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MyLocationOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import javax.xml.transform.sax.SAXSource;

/**
 * Created by str on 27/05/14.
 */
public class NearMeFragment extends SherlockListFragment implements ListItemClickInterface<Game> { //SherlockListFragment

    private SearchResultsLazyListAdapter adapter;
    private View v;

    private MapView mMapView;
    private GameOverlay gameOverlay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        gameOverlay =new GameOverlay(this);
//        ARL.eventBus.register(this);
    }

    @Override
    public void onPause() {
//        ARL.eventBus.unregister(this);
        super.onPause();
        ARL.mapContext.saveContext(mMapView);
        Fragment f = getFragmentManager().findFragmentById(R.id.map);
        if (f != null)
            getFragmentManager().beginTransaction().remove(f).commit();
        if (gameOverlay != null) gameOverlay.close();


    }

//    public void onEventMainThread(SearchResultList event) {
//        System.out.println("games found" + event.getGamesList().getGames().size());
//        for (Game game: event.getGamesList().getGames()){
//
//        }
//        //TODO work out
//    }

    @Override
    public void onResume() {
        super.onResume();
        ARL.mapContext.applyContext(mMapView);
//        gameOverlay =new GameOverlay(this);
//        gameOverlay.resume();
//        mMapView.getOverlays().
        mMapView.invalidate();



//        getActivity().getActionBar().setIcon(R.drawable.ic_ab_back);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (v != null) {
            ((ViewGroup) v.getParent()).removeView(v);
            return v;
        }
        v = inflater.inflate(R.layout.store_nearme, container, false);
        if (adapter == null) {
            adapter = new SearchResultsLazyListAdapter(getActivity());
            adapter.setOnListItemClickCallback(this);
        }
        setListAdapter(adapter);

        mMapView = (MapView) v.findViewById(R.id.map);
        ARL.mapContext.applyContext(mMapView);
        mMapView.setMapListener(new DelayedMapListener(new MapListener() {
            public boolean onZoom(final ZoomEvent e) {

                return false;
            }

            public boolean onScroll(final ScrollEvent e) {
                System.out.println("scrolled ! "+e.getX());
                issueQuery();
                return true;
            }
        }, 1000 ));
        mMapView.getOverlays().add(gameOverlay);
        mMapView.invalidate();
        issueQuery();
        return v;
    }


    private void issueQuery(){
        GeoPoint center  = mMapView.getProjection().getBoundingBox().getCenter();
        int lengthInMeters =mMapView.getProjection().getBoundingBox().getDiagonalLengthInMeters();
        System.out.println("length in meters "+lengthInMeters);
                ARL.store.search(center.getLatitude(), center.getLongitude(), (long) lengthInMeters);
    }

    @Override
    public void onListItemClick(View v, int position, Game game) {
        openGame(game);
    }

    public void openGame(Game game){
        if (game != null) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            Bundle args = new Bundle();

            GameFragment frag = new GameFragment(game);
            frag.setArguments(args);
            FragmentTransaction ft = fm.beginTransaction();

            ft.replace(R.id.right_pane, frag).addToBackStack(null).commit();
        }
    }

    @Override
    public boolean setOnLongClickListener(View v, int position, Game object) {
        return false;
    }

}
