package org.celstec.arlearn2.android.game.messageViews;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.view.WindowCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.events.GeneralItemBecameVisibleEvent;
import org.celstec.arlearn2.android.events.RunEvent;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivity;
import org.celstec.arlearn2.android.game.messageViews.map.OSMOverlayItem;
import org.celstec.arlearn2.android.game.messageViews.map.OsmGeneralItemizedIconOverlay;
import org.celstec.arlearn2.android.util.BitmapWorkerTask;
import org.celstec.arlearn2.android.util.DrawableUtil;
import org.celstec.dao.gen.GameFileLocalObject;
import org.celstec.dao.gen.GeneralItemLocalObject;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

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
public class GameMap extends Activity {
    GameActivityFeatures gameActivityFeatures;
    ActionBarMenuController actionBarMenuController;
    private Menu menu;

    double lat;
    double lng;
    private MyLocationNewOverlay myLocation;

    private MapView mv;
//    private IMapController control;

    private OsmGeneralItemizedIconOverlay itemsOverlay;

    public void onCreate(Bundle savedInstanceState) {
        ARL.init(this);
        if (ARL.config.isGameMapActionBarTransparent()) {
            getWindow().requestFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY);
        }
        super.onCreate(savedInstanceState);
        gameActivityFeatures = new GameActivityFeatures(this);
        setTheme(gameActivityFeatures.getTheme());

        setContentView(R.layout.game_mapview);
        DrawableUtil drawableUtil = ARL.getDrawableUtil(gameActivityFeatures.getTheme(), this);

        boolean enabled = ARL.config.getBooleanProperty("game_map_show_home");
//                getActionBar().setDisplayHomeAsUpEnabled(enabled);
        if (android.os.Build.VERSION.SDK_INT >= 11) getActionBar().setHomeButtonEnabled(enabled);
        getActionBar().setDisplayShowHomeEnabled(enabled);
        getActionBar().setDisplayShowTitleEnabled(enabled);


        if (ARL.config.isGameMapActionBarTransparent()) {
            if (ARL.config.getGameMapActionBarTransparency() == ARL.config.TRANSPARENT) {
                getActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            if (ARL.config.getGameMapActionBarTransparency() == ARL.config.HALF) {
                getActionBar().setBackgroundDrawable(drawableUtil.getBackgroundDarkGradientTransparancy());
            }
        } else {
            getActionBar().setBackgroundDrawable(new ColorDrawable(DrawableUtil.styleUtil.getBackgroundDark()));
        }

        gameActivityFeatures = new GameActivityFeatures(this);
        actionBarMenuController = new ActionBarMenuController(this, gameActivityFeatures);
        setGameHeader(gameActivityFeatures.gameLocalObject.getId(), "/gameMessagesHeader");

        mv = (MapView) findViewById(R.id.map);
        ARL.time.printTime("end oncreate34", System.currentTimeMillis());
        new Thread(new Runnable() {
            public void run() {
                myLocation = new MyLocationNewOverlay(GameMap.this, mv) {

                    @Override
                    public void onLocationChanged(Location location, IMyLocationProvider source) {
                        super.onLocationChanged(location, source);
                        lat = location.getLatitude();
                        lng = location.getLongitude();
                    }
                };
                GameMap.this.runOnUiThread(new Runnable() {
                    public void run() {
                        if (gameActivityFeatures.getGameLocalObject().getGameBean().getConfig().getEnableMyLocation()) { //TODO invert
                            myLocation.enableFollowLocation();
                            myLocation.enableMyLocation();
                            myLocation.setOptionsMenuEnabled(true);
                            myLocation.setDrawAccuracyEnabled(true);
                            myLocation.runOnFirstFix(new Runnable() {
                                public void run() {
                                    mv.getController().animateTo(myLocation
                                            .getMyLocation());
                                }
                            });
                        }
                        mv.getOverlayManager().add(myLocation);
                    }
                });

            }
        }).start();


        ARL.time.printTime("end oncreate", System.currentTimeMillis());
    }

    private void setGameHeader(long gameId, String path) {
        String key = gameId + path;
        final Bitmap bitmap = ARL.imageCache.getBitmapFromMemCache(key);
        ImageView imageView = ((ImageView) findViewById(R.id.gameHeader));
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {

            GameFileLocalObject gameFileLocalObject = GameFileLocalObject.getGameFileLocalObject(gameId, path);
            if (gameFileLocalObject != null) {
                BitmapWorkerTask task = new BitmapWorkerTask(imageView, key, imageView.getMaxWidth(), imageView.getMaxHeight());
                task.execute(gameFileLocalObject);
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ARL.eventBus.register(this);
        if (ARL.properties.getAccount()==0 ||gameActivityFeatures.getRunLocalObject() == null) this.finish();
        if (gameActivityFeatures.getRunLocalObject() == null) this.finish();
        if (menu != null) actionBarMenuController.updateScore(menu);
        mv = (MapView) findViewById(R.id.map);
        ARL.mapContext.applyContext(mv, gameActivityFeatures.getGameLocalObject().getGameBean().getConfig());

        new Thread(new Runnable() {
            public void run() {
                itemsOverlay = new OsmGeneralItemizedIconOverlay(GameMap.this, gameActivityFeatures.getRunId(), gameActivityFeatures.getGameId());
                mv.getOverlays().add(itemsOverlay);

                GameMap.this.runOnUiThread(new Runnable() {
                    public void run() {
                        mv.invalidate();

                    }
                });

            }
        }).start();

        ARL.generalItems.syncGeneralItems(gameActivityFeatures.getGameLocalObject());
        ARL.generalItemVisibility.calculateVisibility(gameActivityFeatures.getRunId(), gameActivityFeatures.getGameId());

        GeneralItemBecameVisibleEvent event = (GeneralItemBecameVisibleEvent) ARL.eventBus.removeStickyEvent(GeneralItemBecameVisibleEvent.class);
        if (event != null) onEventMainThread(event);
    }

    public void onEventMainThread(final GeneralItemBecameVisibleEvent event) {
        event.processEvent(gameActivityFeatures, this, null);
    }

    public void onEventMainThread(RunEvent runEvent) {
        if (runEvent.getRunId() == gameActivityFeatures.getRunId() && runEvent.isDeleted()) {
            this.finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (itemsOverlay != null) itemsOverlay.close();
        ARL.mapContext.saveContext(mv);
        ARL.eventBus.unregister(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        actionBarMenuController.inflateMenu(menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return actionBarMenuController.onOptionsItemSelected(item);

    }

    public void openGeneralItem(OverlayItem overlayItem) {
        Intent intent = new Intent(GameMap.this, GeneralItemActivity.class);
        gameActivityFeatures.addMetadataToIntent(intent, false);
        intent.putExtra(GeneralItemLocalObject.class.getName(), ((OSMOverlayItem) overlayItem).getGeneralItemId());
        startActivity(intent);
    }
}
